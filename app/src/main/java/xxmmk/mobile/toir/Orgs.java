package xxmmk.mobile.toir;

import android.content.Context;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class Orgs {
    private Context mContext;
    private MobileTOiRApp mMobileTOiRApp;
    public static List<HashMap<String,String>> OrgsList = new ArrayList<HashMap<String,String>>();

    public static Orgs newInstance() {
        Orgs mOrgs = new Orgs();

        return mOrgs;
    }

    public Orgs() {
        mMobileTOiRApp = MobileTOiRApp.getInstance();
        Log.d(mMobileTOiRApp.getLOG_TAG(), "Orgs " + mMobileTOiRApp.getmHASH());
        OrgsList = mMobileTOiRApp.getmDbHelper().getListOrgs();
    }
}
