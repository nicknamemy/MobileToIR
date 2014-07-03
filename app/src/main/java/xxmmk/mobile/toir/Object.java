package xxmmk.mobile.toir;

import android.content.Context;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Alex on 02.07.2014.
 */
public class Object {
    private Context mContext;
    private MobileTOiRApp mMobileTOiRApp;
    public static List<HashMap<String,String>> ObjectsList = new ArrayList<HashMap<String,String>>();

    public static Object newInstance(String parentId, String orgId) {
        Object mObject = new Object(parentId,orgId);

        return mObject;
    }

    public Object(String parentId, String orgId) {
        mMobileTOiRApp = MobileTOiRApp.getInstance();
        Log.d(mMobileTOiRApp.getLOG_TAG(), "Object parentId=" + parentId);
        ObjectsList = mMobileTOiRApp.getmDbHelper().getListObjects(parentId,orgId);
    }
}
