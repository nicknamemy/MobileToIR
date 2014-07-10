package xxmmk.mobile.toir;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.HashMap;

public class OrgsActivity extends Activity {
    private MobileTOiRApp mMobileTOiRApp;
    private ListView listOrgs;
    protected NfcAdapter nfcAdapter;
    protected PendingIntent nfcPendingIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mMobileTOiRApp = ((MobileTOiRApp) this.getApplication());
        setContentView(R.layout.activity_orgs);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        nfcPendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, this.getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
    }

    @Override
    protected void onStart() {
        super.onStart();
        listOrgs = (ListView) this.findViewById(R.id.listOrgs);
        String[] from = {
                "ORG_CODE",
                "ORG_LOAD_TIME"
        };
        int[] to = {R.id.orgs_text1,R.id.orgs_text2};

        Orgs orgitems=Orgs.newInstance();
        final OrgsAdapter adapter = new OrgsAdapter(this.getBaseContext(), orgitems.OrgsList, R.layout.item_orgs, from, to);

        listOrgs.setAdapter(adapter);
        //listOrgs.setSelector(R.drawable.list_selector);
        listOrgs.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {


                Intent intent = new Intent(parent.getContext(), ObjectActivity.class);
                Bundle b = new Bundle();
                HashMap<String,String> obj = (HashMap<String,String>)adapter.getItem(position);


                b.putString("ORG_ID", obj.get("ORG_ID"));
                b.putString("ORG_CODE", obj.get("ORG_CODE"));
                b.putString("OBJECT_ID", "");
                //b.putSerializable("HashMap",obj);
                //Log.v(mMobileTOiRApp.getLOG_TAG(), "itemClick: position = " + position + ", id = " + id + " ORG_ID=" + obj.get("ORG_ID"));
                intent.putExtras(b);
                //intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
            }
        });

    }

    public void enableForegroundMode() {
        //Log.d(TAG, "enableForegroundMode");

        IntentFilter tagDetected = new IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED); // filter for all
        IntentFilter[] writeTagFilters = new IntentFilter[] {tagDetected};
        nfcAdapter.enableForegroundDispatch(this, nfcPendingIntent, writeTagFilters, null);
    }

    public void disableForegroundMode() {
        //Log.d(TAG, "disableForegroundMode");

        nfcAdapter.disableForegroundDispatch(this);
    }



    @Override
    protected void onResume() {
        //Log.d(TAG, "onResume");

        super.onResume();

        enableForegroundMode();
    }

    @Override
    protected void onPause() {
        //Log.d(TAG, "onPause");

        super.onPause();

        disableForegroundMode();
    }

    private void vibrate() {
        //Log.d(TAG, "vibrate");

        Vibrator vibe = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE) ;
        vibe.vibrate(500);
    }


    @Override
    protected void onNewIntent(Intent intent) {

        if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(intent.getAction())) {

            //vibrate();
        }
    }
}
