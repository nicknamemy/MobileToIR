package xxmmk.mobile.toir;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.nfc.tech.*;
import android.nfc.tech.NfcF;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.nfc.NfcAdapter;
import android.widget.Toast;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;


/**
 * Created by User on 07.07.2014.
 */
public class ScanActivity extends Activity {
    private MobileTOiRApp mMobileTOiRApp;

    private String mDescription;
    private String mCode;
    private String mObjectId;

    private Button btnSave;
    private Button btnCancel;

    private PendingIntent mPendingIntent;
    private IntentFilter[] mIntentFilters;
    private String[][] mNFCTechLists;
    private TextView ScanText4;
    private TextView ScanText2;

    private static final String TAG = ScanActivity.class.getName();

    protected NfcAdapter nfcAdapter;
    protected PendingIntent nfcPendingIntent;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        mMobileTOiRApp = ((MobileTOiRApp) this.getApplication());
        setContentView(R.layout.activity_scan);

        ScanText4 = (TextView) this.findViewById(R.id.ScanText4);
        ScanText2 = (TextView) this.findViewById(R.id.ScanText2);

        Bundle b = this.getIntent().getExtras();

        mDescription=b.getString("DESCRIPTION");
        mCode=b.getString("CODE");
        mObjectId=b.getString("OBJECT_ID");

        btnSave = (Button) this.findViewById(R.id.Scanbutton1);
        btnSave.setEnabled(false);
        btnCancel = (Button) this.findViewById(R.id.Scanbutton2);
        btnSave.setEnabled(true);

        if (mDescription == null) {
            finish();
            return;
        }

        ScanText2.setText(mDescription);
        ScanText4.setText(mCode);

        nfcAdapter = NfcAdapter.getDefaultAdapter(this);

        if (nfcAdapter == null) {
            // Stop here, we definitely need NFC
            Toast.makeText(this, "This device doesn't support NFC.", Toast.LENGTH_LONG).show();
            finish();
            return;

        }

        if (!nfcAdapter.isEnabled()) {
            Toast.makeText(this, "This device doesn't enabled NFC.", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        // initialize NFC

        nfcPendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, this.getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Log.v(mMobileTOiRApp.getLOG_TAG(), "btnSave= ");
                mMobileTOiRApp.getmDbHelper().setNewCode(mObjectId,mCode);
                finish();
                return;
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Log.v(mMobileTOiRApp.getLOG_TAG(), "btnCancel ");
                finish();
                return;
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
        //Log.d(TAG, "onNewIntent");

        if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(intent.getAction())) {

            Tag myTag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
            ScanText4.setText(bytesToHex(myTag.getId()));
            mCode = bytesToHex(myTag.getId());
            ScanText4.setBackgroundColor(0xfff00000);
            btnSave.setEnabled(true);
            vibrate();
        }
    }

    final protected static char[] hexArray = {'0','1','2','3','4','5','6','7','8','9','A','B','C','D','E','F'};
    public static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        int v;
        for ( int j = 0; j < bytes.length; j++ ) {
            v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }


}
