package xxmmk.mobile.toir;

import android.app.Application;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;


public class MobileTOiRApp extends Application {
    private static MobileTOiRApp instance;
    public MobileTOiRDB getmDbHelper() {
        return mDbHelper;
    }

    private MobileTOiRDB mDbHelper;
    private String mDataBasicURL = "http://161.8.223.166:8020/getdata.aspx";
    private String mLoginURL = "http://161.8.223.166:8020/login_kis.aspx";

    public String getDataURL(String mCode) {
        return this.mDataBasicURL+"?s="+mCode+"&token="+this.getmHASH();
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

}
