package xxmmk.mobile.toir;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
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
import java.util.ArrayList;
import java.util.HashMap;

import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnClickListener;

import xxmmk.mobile.toir.R;

public class ObjectActivity extends Activity {
    private MobileTOiRApp mMobileTOiRApp;
    private ListView listObjects;
    private View mObjectForm;
    private View mProgressView;
    private View mBottomBar;
    private LoadObjects mLoadTask = null;
    private SaveObjects mSaveTask = null;
    private String mObjectId;
    private String mOrgId;
    private String mOrgCode;
    private Button btnLoad;
    private Button btnUpLoad;
    private ObjectAdapter adapter;
    private AlertDialog.Builder ad;
    Context context;
    ProgressDialog ringProgressDialog;
    ProgressDialog barProgressDialog;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_objects);
        listObjects = (ListView) this.findViewById(R.id.listObjects);
        mObjectForm = this.findViewById(R.id.objectForm);
        mProgressView = findViewById(R.id.login_progress);
        mBottomBar =findViewById(R.id.bottom_bar);
        mMobileTOiRApp = ((MobileTOiRApp) this.getApplication());
        context = ObjectActivity.this;
    }


    /*@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.objects, menu);
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
    */

    @Override
    protected void onStart() {
        super.onStart();

        Bundle b = this.getIntent().getExtras();
        mObjectId = b.getString("OBJECT_ID");
        mOrgId = b.getString("ORG_ID");
        mOrgCode = b.getString("ORG_CODE");
        //HashMap<String,String> params = (HashMap<String,String>)b.getSerializable("HashMap");



        btnLoad = (Button) this.findViewById(R.id.btnLoad);
        btnUpLoad = (Button) this.findViewById(R.id.btnUpload);

        btnUpLoad.setOnClickListener(new View.OnClickListener() {
                                         @Override
                                         public void onClick(View view) {
                                             saveHierarchy(mOrgId);
                                         }
                                     });

        btnLoad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Log.v(mMobileTOiRApp.getLOG_TAG(), "mObjectId = " + mObjectId + " mOrgId= "+mOrgId );
                String countNewCode = mMobileTOiRApp.getmDbHelper().getCountNewCodeByOrg(mOrgId);
                if (countNewCode.equals("0")) {
                    loadHierarchy(mOrgId);
                } else {
                    ad = new AlertDialog.Builder(ObjectActivity.this);
                    ad.setTitle("Внимание");  // заголовок
                    ad.setMessage("Существуют невыгруженные метки, которые могут быть утеряны. Продолжить?"); // сообщение
                    String button1String = "Да";
                    String button2String = "Отмена";
                    ad.setPositiveButton(button1String, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int arg1) {
                            //String countNewCode = mMobileTOiRApp.getmDbHelper().getCountNewCodeByOrg(mOrgId);
                            //Toast.makeText(context, "Да", Toast.LENGTH_LONG).show();
                            loadHierarchy(mOrgId);
                        }
                    });
                    ad.setNegativeButton(button2String, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int arg1) {
                            Toast.makeText(context, "Отмена", Toast.LENGTH_LONG).show();
                            //saveHierarchy(mOrgId);
                        }
                    });
                    ad.setCancelable(false);
                /*ad.setOnCancelListener(new OnCancelListener() {
                    public void onCancel(DialogInterface dialog) {
                        Toast.makeText(context, "Вы ничего не выбрали",
                                Toast.LENGTH_LONG).show();
                    }
                });*/
                    ad.show();
                    //loadHierarchy(mOrgId);
                }
            }
            });

        if (mObjectId != null && !mObjectId.equals("")) {
            mBottomBar.setVisibility(View.INVISIBLE);
            RelativeLayout.LayoutParams p = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            p.addRule(RelativeLayout.ABOVE);
            p.addRule(RelativeLayout.BELOW,R.id.linearLayout);
            listObjects.setLayoutParams(p);
            //btnLoad.setVisibility(View.INVISIBLE);
            //btnUpLoad.setVisibility(View.INVISIBLE);
        }

        TextView textPath = (TextView) this.findViewById(R.id.textPath);
        textPath.setText(mOrgCode);

        /*Object objectitems=Object.newInstance(mObjectId);
        if (objectitems.ObjectsList.isEmpty()) {
            TextView textPath = (TextView) this.findViewById(R.id.textPath);
            textPath.setText("Данные не загружены");
            btnUpLoad.setEnabled(false);

        }
        final OrgsAdapter adapter = new OrgsAdapter(this.getBaseContext(), objectitems.ObjectsList, R.layout.item_objects, from, to);

        listObjects.setAdapter(adapter);
        */


        listObjects.setItemsCanFocus(false);
        listObjects.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {


                Intent intent = new Intent(parent.getContext(), ObjectActivity.class);
                Bundle b = new Bundle();
                HashMap<String,String> obj = (HashMap<String,String>)adapter.getItem(position);


                b.putString("ORG_ID", mOrgId);
                b.putString("OBJECT_ID", obj.get("OBJECT_ID"));
                b.putString("ORG_CODE", mOrgCode + "/" +obj.get("SN"));
                //b.putSerializable("HashMap",obj);
                Log.v(mMobileTOiRApp.getLOG_TAG(), "itemClick: position = " + position + ", id = " + id + " OBJECT_ID=" + obj.get("OBJECT_ID"));
                intent.putExtras(b);
                //intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
            }
        });

        listObjects.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View v,
                                    final int position, long id) {


                HashMap<String,String> obj = (HashMap<String,String>)adapter.getItem(position);

                String countChild = obj.get("CHILD_CNT");

                Intent intent;
                if (countChild.equals("0")) {
                    intent = new Intent(parent.getContext(), ScanActivity.class);
                } else
                {
                    intent = new Intent(parent.getContext(), ObjectActivity.class);
                }
                //intent = new Intent(parent.getContext(), ObjectActivity.class);
                Bundle b = new Bundle();
                b.putString("ORG_ID", mOrgId);
                b.putString("OBJECT_ID", obj.get("OBJECT_ID"));
                b.putString("ORG_CODE", mOrgCode + "/" +obj.get("SN"));
                //b.putSerializable("HashMap",obj);
                Log.v(mMobileTOiRApp.getLOG_TAG(), "itemClick: position = " + position + ", id = " + id + " OBJECT_ID=" + obj.get("OBJECT_ID"));
                intent.putExtras(b);
                //intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
            }
        });

        loadList();
    }

    public void loadList() {
        String[] from = {
                "DESCRIPTION",
                "CODE"
        };
        int[] to = {R.id.objects_text1,R.id.objects_text2};

        Object objectitems;
        Log.v(mMobileTOiRApp.getLOG_TAG(), "loadList() mObjectId = " + mObjectId );
        if (mObjectId != null && !mObjectId.equals("")) {
            objectitems = Object.newInstance(mObjectId,mOrgId);
        } else {
            String xObjectId=mMobileTOiRApp.getmDbHelper().getObjectId(mOrgId);
            Log.v(mMobileTOiRApp.getLOG_TAG(), "xObjectId = " + xObjectId );
            objectitems = Object.newInstance(xObjectId,mOrgId);
        }

        //Object.newInstance(mObjectId,mOrgId);
        if (objectitems.ObjectsList.isEmpty()) {
            TextView textPath = (TextView) this.findViewById(R.id.textPath);
            textPath.setText("Данные не загружены");
            btnUpLoad.setEnabled(false);

        }
        adapter = new ObjectAdapter(this.getBaseContext(), objectitems.ObjectsList, R.layout.item_objects, from, to);

        listObjects.setAdapter(adapter);
    }

    public void loadHierarchy(String orgId){
        ringProgressDialog =ProgressDialog.show(ObjectActivity.this, "Подождите ...", "Загружается иерархия оборудования ...", true);
        ringProgressDialog.setCancelable(false);

        //showProgress(true);
        mLoadTask = new LoadObjects(orgId);
        mLoadTask.execute((Void) null);

    }

    public void saveHierarchy(String orgId){
        //showProgress(true);
        ArrayList<HashMap<String,String>> newCodes =mMobileTOiRApp.getmDbHelper().getNewCodeByOrg(mOrgId) ;
        if (!newCodes.isEmpty()) {
            barProgressDialog = new ProgressDialog(ObjectActivity.this);
            barProgressDialog.setTitle("Сохранение ...");
            barProgressDialog.setMessage("Передача данных ...");
            barProgressDialog.setProgressStyle(barProgressDialog.STYLE_HORIZONTAL);
            barProgressDialog.setProgress(0);
            barProgressDialog.setMax(newCodes.size());
            barProgressDialog.show();
            mSaveTask = new SaveObjects(orgId);
            mSaveTask.execute((Void) null);
        }

    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    public void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mObjectForm.setVisibility(show ? View.GONE : View.VISIBLE);
            mObjectForm.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mObjectForm.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mObjectForm.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    /**
     * Загрузка иерархии оборудования по цеху в ассинхронном потоке
     */
    public class LoadObjects extends AsyncTask<Void, Void, Boolean> {

        private final String mOrgId;
        private String mToken = "null";

        LoadObjects(String orgId) {
            mOrgId = orgId;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.
            Boolean vStatus = false;
            try {
                // Simulate network access.
                StringBuilder builder = new StringBuilder();
                HttpClient client = mMobileTOiRApp.getNewHttpClient(); new DefaultHttpClient();
                //MobileTOiRApp app = MobileTOiRApp.getInstance();
                Log.d(mMobileTOiRApp.getLOG_TAG(), "LoadObjects mOrgId=" + mOrgId);
                HttpGet httpGet = new HttpGet(mMobileTOiRApp.getObjectDataURL("361", mOrgId));
                //Log.d(mMobileTOiRApp.getLOG_TAG(), "LoadObjects mOrgId=" + mOrgId);

                try {
                    HttpResponse response = client.execute(httpGet);
                    StatusLine statusLine = response.getStatusLine();
                    int statusCode = statusLine.getStatusCode();
                    if (statusCode == 200 )
                    {
                        HttpEntity entity = response.getEntity();
                        InputStream content = entity.getContent();
                        BufferedReader reader = new BufferedReader(new InputStreamReader(content));
                        String line;
                        while ((line = reader.readLine()) != null) {
                            builder.append(line);
                        }
                        mMobileTOiRApp.getmDbHelper().loadObjects(builder.toString(),mOrgId);

                    }
                    else {
                        Log.d(mMobileTOiRApp.getLOG_TAG(), "LoadObjects Error = " + statusCode);
                        //Toast.makeText(this, "Example action.", Toast.LENGTH_SHORT).show();
                    }
                }
                catch (ClientProtocolException e) {
                    e.printStackTrace();
                }
                catch (IOException e) {
                    e.printStackTrace();
                }

                Thread.sleep(10);

            } catch (InterruptedException e) {
                return false;
            }

            /*for (String credential : DUMMY_CREDENTIALS) {
                String[] pieces = credential.split(":");
                if (pieces[0].equals(mEmail)) {
                    // Account exists, return true if the password matches.
                    return pieces[1].equals(mPassword);
                }
            }

            // TODO: register the new account here.
            return true;*/
            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mLoadTask = null;
            ringProgressDialog.dismiss();
            //showProgress(false);

            if (success) {
                finish();
            } else {
                Toast.makeText(getParent(), "Error.", Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        protected void onCancelled() {
            mLoadTask = null;
            showProgress(false);
        }


    }

    /**
     * Загрузка иерархии оборудования по цеху в ассинхронном потоке
     */
    public class SaveObjects extends AsyncTask<Void, Void, Boolean> {

        private final String mOrgId;
        private String mToken = "null";

        SaveObjects(String orgId) {
            mOrgId = orgId;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            Boolean vStatus = false;
            ArrayList<HashMap<String,String>> newCodes =mMobileTOiRApp.getmDbHelper().getNewCodeByOrg(mOrgId) ;
            if (!newCodes.isEmpty()) {
                for (HashMap<String,String> temp : newCodes )
                try {
                    StringBuilder builder = new StringBuilder();
                    HttpClient client = mMobileTOiRApp.getNewHttpClient();
                    new DefaultHttpClient();
                    //MobileTOiRApp app = MobileTOiRApp.getInstance();
                    HttpGet httpGet = new HttpGet(mMobileTOiRApp.putDataURL(temp.get("OBJECT_ID"), temp.get("CODE")));
                    Log.d(mMobileTOiRApp.getLOG_TAG(), "SaveObjects httpGet=" + mMobileTOiRApp.putDataURL(temp.get("OBJECT_ID"), temp.get("CODE")));

                    try {
                        HttpResponse response = client.execute(httpGet);
                        StatusLine statusLine = response.getStatusLine();
                        int statusCode = statusLine.getStatusCode();
                        if (statusCode == 200) {
                            HttpEntity entity = response.getEntity();
                            InputStream content = entity.getContent();
                            BufferedReader reader = new BufferedReader(new InputStreamReader(content));
                            String line;
                            while ((line = reader.readLine()) != null) {
                                builder.append(line);
                            }
                            Log.d(mMobileTOiRApp.getLOG_TAG(), "SaveObjects OBJECT_ID=" + temp.get("CODE"));
                            mMobileTOiRApp.getmDbHelper().DeleteNewCode(temp.get("OBJECT_ID"));
                            mMobileTOiRApp.getmDbHelper().UpdateHierarchy(temp.get("OBJECT_ID"),temp.get("CODE"));

                        } else {
                            Log.d(mMobileTOiRApp.getLOG_TAG(), "SaveObjects Error = " + statusCode);
                            //Toast.makeText(this, "Example action.", Toast.LENGTH_SHORT).show();
                        }
                        barProgressDialog.incrementProgressBy(1);

                    } catch (ClientProtocolException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    Thread.sleep(1);

                } catch (InterruptedException e) {
                    return false;
                }
                return true;
            }
            else {
                //Toast.makeText(context, "Нет меток", Toast.LENGTH_SHORT).show();
                return true;
            }
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mSaveTask = null;
            barProgressDialog.dismiss();
            //showProgress(false);

            if (success) {
                finish();
            } else {
                Toast.makeText(getParent(), "Error.", Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        protected void onCancelled() {
            mSaveTask = null;
            barProgressDialog.dismiss();
        }


    }


}
