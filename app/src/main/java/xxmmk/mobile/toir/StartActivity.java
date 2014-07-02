package xxmmk.mobile.toir;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;


public class StartActivity extends Activity {

    @Override
    protected void onStart(){
        super.onStart();

        TextView loginInfo = (TextView) findViewById(R.id.LoginInfo);
        loginInfo.setText(statusConnect());

        TextView CatalogInfo = (TextView) findViewById(R.id.CatalogInfo);
        CatalogInfo.setText(statusCatalog());
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();

        StrictMode.setThreadPolicy(policy);
        MobileTOiRApp app = ((MobileTOiRApp) this.getApplication());
        //Log.d(app.getLOG_TAG(), "StartActivity.onCreate1");
        //Log.d(app.getLOG_TAG(), "StartActivity.onCreate2");
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_start);


        //Log.d(app.getLOG_TAG(), "StartActivity.onCreate3");

        Button loginButton = (Button) findViewById(R.id.buttonLogin);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(),LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                view.getContext().startActivity(intent);
            }
        });

        Button orgsButton = (Button) findViewById(R.id.buttonOrgs);
        orgsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(),OrgsActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_PREVIOUS_IS_TOP);
                view.getContext().startActivity(intent);
            }
        });



    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.start, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public String statusConnect() {
        MobileTOiRApp app = ((MobileTOiRApp) this.getApplication());
        Log.d(app.getLOG_TAG(), "StartActivity.statusConnect");
        StringBuilder builder = new StringBuilder();
        HttpClient client = new DefaultHttpClient();
        HttpGet httpGet = new HttpGet(app.getDataURL("400"));
        Log.d(app.getLOG_TAG(), "StartActivity.statusConnect "+app.getDataURL("400"));
        String vErrorToken="Login OK";
        String vErrorNetwork="Network OK";
        try {
            HttpResponse response = client.execute(httpGet);
            StatusLine statusLine = response.getStatusLine();
            int statusCode = statusLine.getStatusCode();
            if (statusCode == 200 || statusCode == 500)
            {
                HttpEntity entity = response.getEntity();
                InputStream content = entity.getContent();
                BufferedReader reader = new BufferedReader(new InputStreamReader(content));
                String line;
                while ((line = reader.readLine()) != null) {
                    builder.append(line);
                }
                try {
                    //Toast.makeText(this.getBaseContext(), builder.toString(), Toast.LENGTH_LONG).show();
                    JSONArray jsonArray = new JSONArray(builder.toString());
                    for (int i=0;i<jsonArray.length();i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);

                        vErrorToken = jsonObject.getString("ERROR");

                    }
                    //Toast.makeText(this.getBaseContext(),clientID, Toast.LENGTH_LONG).show();
                }
                catch (JSONException e) {
                    vErrorToken="Login OK";
                    app.getmDbHelper().refreshOrgs(builder.toString());
                    //e.printStackTrace();
                }
            }
            else {
                vErrorNetwork = "Network ERROR";
                vErrorToken="Login ERROR";
                //Log.e("Login fail", "Login fail");
            }
        }
        catch (ClientProtocolException e) {
            vErrorNetwork = "Network ERROR";
            vErrorToken="Login ERROR";
            e.printStackTrace();
        }
        catch (IOException e) {
            vErrorNetwork = "Network ERROR";
            vErrorToken="Login ERROR";
            e.printStackTrace();
        }

        return vErrorNetwork+" "+vErrorToken;
    }

    public String statusCatalog() {
        String vStatus;
        MobileTOiRApp app = ((MobileTOiRApp) this.getApplication());
        vStatus = "Кол-во организаций: " + app.getmDbHelper().getCountOrgs() + " Обновлено: "+app.getmDbHelper().getTimeOfOrgs();
        return vStatus;
    }
}
