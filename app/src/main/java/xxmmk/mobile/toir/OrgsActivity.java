package xxmmk.mobile.toir;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ListView;

public class OrgsActivity extends Activity {
    private MobileTOiRApp mMobileTOiRApp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mMobileTOiRApp = ((MobileTOiRApp) this.getApplication());
        setContentView(R.layout.activity_orgs);
    }

    @Override
    protected void onStart() {
        super.onStart();
        ListView listOrgs = (ListView) this.findViewById(R.id.listOrgs);
        String[] from = {
                "ORG_ID",
                "ORG_CODE"
        };
        int[] to = {R.id.orgs_text2,R.id.orgs_text1};

        Orgs orgitems=Orgs.newInstance();
        OrgsAdapter adapter = new OrgsAdapter(this.getBaseContext(), orgitems.OrgsList, R.layout.item_orgs, from, to);

        listOrgs.setAdapter(adapter);
        //listOrgs.setSelector(R.drawable.list_selector);

    }
}
