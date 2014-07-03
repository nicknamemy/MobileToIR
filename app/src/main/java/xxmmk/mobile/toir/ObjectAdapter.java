package xxmmk.mobile.toir;

import android.content.Context;
import android.util.Log;
import android.widget.SimpleAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Alex on 02.07.2014.
 */
public class ObjectAdapter extends SimpleAdapter {
    private Context mContext;
    private MobileTOiRApp mMobileTOiRApp;


    public ObjectAdapter(Context context, List<? extends Map<String, ?>> data, int resource, String[] from, int[] to) {
        super(context, data, resource, from, to);

        mContext = context;
        mMobileTOiRApp = MobileTOiRApp.getInstance();
        Log.d(mMobileTOiRApp.getLOG_TAG(), "ObjectAdapter " + mMobileTOiRApp.getmHASH());

    }
}
