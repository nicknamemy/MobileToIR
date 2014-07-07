package xxmmk.mobile.toir;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.HashMap;

public class OrgsActivity extends Activity {
    private MobileTOiRApp mMobileTOiRApp;
    private ListView listOrgs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mMobileTOiRApp = ((MobileTOiRApp) this.getApplication());
        setContentView(R.layout.activity_orgs);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    @Override
    protected void onStart() {
        super.onStart();
        listOrgs = (ListView) this.findViewById(R.id.listOrgs);
        String[] from = {
                "ORG_ID",
                "ORG_CODE"
        };
        int[] to = {R.id.orgs_text2,R.id.orgs_text1};

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
                Log.v(mMobileTOiRApp.getLOG_TAG(), "itemClick: position = " + position + ", id = " + id + " ORG_ID=" + obj.get("ORG_ID"));
                intent.putExtras(b);
                //intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
            }
        });

    }
}
