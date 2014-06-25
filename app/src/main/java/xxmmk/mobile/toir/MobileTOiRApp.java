package xxmmk.mobile.toir;

import android.app.Application;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;


public class MobileTOiRApp extends Application {
    private MobileTOiRDB mDbHelper;

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

}
