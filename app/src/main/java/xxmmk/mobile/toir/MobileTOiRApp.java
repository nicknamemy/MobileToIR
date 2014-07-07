package xxmmk.mobile.toir;

import android.app.Application;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import org.apache.http.HttpVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.impl.client.DefaultHttpClient;

import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
//import javax.net.ssl.SSLSocketFactory;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;

import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.http.conn.ssl.SSLSocketFactory;


public class MobileTOiRApp extends Application {
    private static MobileTOiRApp instance;
    public MobileTOiRDB getmDbHelper() {
        return mDbHelper;
    }

    private MobileTOiRDB mDbHelper;
    private String mDataBasicURL = "https://navigator.mmk.ru/getdata.aspx";
    private String mLoginURL = "https://navigator.mmk.ru/login_kis.aspx";

    public String getDataURL(String mCode) {
        return this.mDataBasicURL+"?s="+mCode+"&token="+this.getmHASH();
    }

    public String getObjectDataURL(String mCode,String mOrgId) {
        return this.mDataBasicURL+"?s="+mCode+"&token="+this.getmHASH()+"&org_id="+mOrgId;
    }

    public String getLoginDataURL(String login, String password) {
        return this.mLoginURL+"?user="+login+"&password="+password;
    }

    public String getmHASH() {
        if (mHASH == null || mHASH.isEmpty()) {
            mHASH = mDbHelper.getSettingValue("token");
        }
        return mHASH;
    }

    public void setmHASH(String mHASH) {
        mDbHelper.setSettingValue("token",mHASH);
        this.mHASH = mHASH;
    }

    public void saveUsername(String username) {
        mDbHelper.setSettingValue("username",username);
    }

    private String mHASH;

    public String getLOG_TAG() {
        return LOG_TAG;
    }

    final String LOG_TAG = "myLogs";

    @Override
    public void onCreate() {
        super.onCreate();
        mDbHelper = new MobileTOiRDB(getApplicationContext());
        mDbHelper.getWritableDatabase();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mDbHelper.close();
    }

    @Override
    public void onTerminate(){
        super.onTerminate();
        mDbHelper.close();
    }

    public static MobileTOiRApp getInstance() {
        return instance;
    }

    public MobileTOiRApp() {
        instance = this;
    }

    /*public static HttpClient wrapClient(HttpClient base) {
        try {
            javax.net.ssl.SSLContext ctx = SSLContext.getInstance("TLS");
            X509TrustManager tm = new X509TrustManager() {
                public void checkClientTrusted(X509Certificate[] xcs, String string) throws CertificateException { }

                public void checkServerTrusted(X509Certificate[] xcs, String string) throws CertificateException { }

                public X509Certificate[] getAcceptedIssuers() {
                    return null;
                }
            };
            ctx.init(null, new TrustManager[]{tm}, null);
            SSLSocketFactory ssf = new SSLSocketFactory(ctx);
            ssf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
            ClientConnectionManager ccm = base.getConnectionManager();
            SchemeRegistry sr = ccm.getSchemeRegistry();
            sr.register(new Scheme("https", ssf, 443));
            return new DefaultHttpClient(ccm, base.getParams());
        } catch (Exception ex) {
            return null;
        }
    }*/
    public class MySSLSocketFactory extends SSLSocketFactory {
        SSLContext sslContext = SSLContext.getInstance("TLS");

        public MySSLSocketFactory(KeyStore truststore) throws NoSuchAlgorithmException, KeyManagementException, KeyStoreException, UnrecoverableKeyException {
            super(truststore);

            TrustManager tm = new X509TrustManager() {
                public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                }

                public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                }

                public X509Certificate[] getAcceptedIssuers() {
                    return null;
                }
            };

            sslContext.init(null, new TrustManager[] { tm }, null);
        }

        @Override
        public Socket createSocket(Socket socket, String host, int port, boolean autoClose) throws IOException, UnknownHostException {
            return sslContext.getSocketFactory().createSocket(socket, host, port, autoClose);
        }

        @Override
        public Socket createSocket() throws IOException {
            return sslContext.getSocketFactory().createSocket();
        }
    }

    public HttpClient getNewHttpClient() {
        try {
            KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
            trustStore.load(null, null);

            SSLSocketFactory sf = new MySSLSocketFactory(trustStore);
            sf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);

            HttpParams params = new BasicHttpParams();
            HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
            HttpProtocolParams.setContentCharset(params, HTTP.UTF_8);

            SchemeRegistry registry = new SchemeRegistry();
            registry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
            registry.register(new Scheme("https", sf, 443));

            ClientConnectionManager ccm = new ThreadSafeClientConnManager(params, registry);

            return new DefaultHttpClient(ccm, params);
        } catch (Exception e) {
            return new DefaultHttpClient();
        }
    }
}
