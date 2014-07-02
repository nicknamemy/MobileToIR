package xxmmk.mobile.toir;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;


public class MobileTOiRDB  extends SQLiteOpenHelper {
    Context mContext;

    public MobileTOiRDB(Context context) {
        // конструктор суперкласса
        super(context, "TOiRDB", null, 3);
        mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        Log.d(((MobileTOiRApp)mContext).getLOG_TAG(), "MobileTOiRDB.onCreate");
        // создаем таблицу с полями
        db.execSQL("create table settings ("
                + "id integer primary key autoincrement,"
                + "key text,"
                + "value text" + ");");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (newVersion == 2) {
            Log.d(((MobileTOiRApp)mContext).getLOG_TAG(), "MobileTOiRDB.onUpgrade newVersion="+newVersion);
            db.execSQL("insert into settings (key) values ('username');");
            db.execSQL("insert into settings (key) values ('password');");
            db.execSQL("insert into settings (key) values ('token');");
        }
        if (newVersion == 3) {
            Log.d(((MobileTOiRApp) mContext).getLOG_TAG(), "MobileTOiRDB.onUpgrade newVersion=" + newVersion);
            db.execSQL("create table orgs ("
                    + "id integer primary key autoincrement,"
                    + "org_id text,"
                    + "org_code text" + ");");
            db.execSQL("insert into settings (key) values ('orgs_date');");
        }
    }

    public String getSettingValue (String key) {
        String value = null;
        SQLiteDatabase db = this.getWritableDatabase();
        try
        {
            Cursor c = null;
            c = db.rawQuery("select value from settings where key = ?", new String[] { key });
            c.moveToFirst();
            value = c.getString(c.getColumnIndex("value"));
            c.close();
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        return value;
    }

    public void setSettingValue (String key, String newValue) {

        SQLiteDatabase db = this.getWritableDatabase();
        try
        {
            db.execSQL("update settings set value=? where key = ?", new String[]{newValue, key});
            db.close();
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    public void refreshOrgs (String jsonOrgs) {

        SQLiteDatabase db = this.getWritableDatabase();
        String vOrgId;
        String vOrgCode;

        try
        {
            db.execSQL("delete from orgs", new String[] {});

            try {
                //Toast.makeText(this.getBaseContext(), builder.toString(), Toast.LENGTH_LONG).show();
                JSONArray jsonArray = new JSONArray(jsonOrgs);
                for (int i=0;i<jsonArray.length();i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);

                    vOrgId = jsonObject.getString("ORG_ID");
                    vOrgCode = jsonObject.getString("ORG_CODE");

                    db.execSQL("insert into orgs (org_id,org_code) values (?,?);",new String[] {vOrgId,vOrgCode});

                }
                //Toast.makeText(this.getBaseContext(),clientID, Toast.LENGTH_LONG).show();
            }
            catch (JSONException e) {
                e.printStackTrace();

            }
            db.execSQL("update settings set value=? where key = ?", new String[]{new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(Calendar.getInstance().getTime()), "orgs_date"});
            db.close();

        }
        catch(Exception e)
        {
            e.printStackTrace();
        }


    }
    public String getCountOrgs () {
        String value = null;
        SQLiteDatabase db = this.getWritableDatabase();
        try
        {
            Cursor c = null;
            c = db.rawQuery("select count(*) as cc from orgs", new String[] {  });
            c.moveToFirst();
            value = c.getString(c.getColumnIndex("cc"));
            c.close();
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        return value;
    }

    public String getTimeOfOrgs () {
        String value = null;
        SQLiteDatabase db = this.getWritableDatabase();
        try
        {
            Cursor c = null;
            c = db.rawQuery("select value as cc from settings where key = ?", new String[] { "orgs_date" });
            c.moveToFirst();
            value = c.getString(c.getColumnIndex("cc"));
            c.close();
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        return value;
    }

    public ArrayList<HashMap<String,String>> getListOrgs() {
        ArrayList<HashMap<String,String>> returnList = new ArrayList<HashMap<String,String>>();
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.query("orgs Order BY org_code",
                new String[] { "*" }, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                HashMap<String, String> temp = new HashMap<String, String>();
                temp.put("ORG_ID", cursor.getString(1));
                temp.put("ORG_CODE", cursor.getString(2));
                returnList.add(temp);
            } while (cursor.moveToNext());
        }
        if (cursor != null && !cursor.isClosed()) {
            cursor.close();
        }
        return returnList;
    }

}