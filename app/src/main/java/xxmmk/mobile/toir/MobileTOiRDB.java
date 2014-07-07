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
        super(context, "TOiRDB", null, 1);
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
        db.execSQL("insert into settings (key) values ('username');");
        db.execSQL("insert into settings (key) values ('password');");
        db.execSQL("insert into settings (key) values ('token');");

        db.execSQL("create table orgs ("
                + "id integer primary key autoincrement,"
                + "org_id text,"
                + "org_code text" + ");");
        db.execSQL("insert into settings (key) values ('orgs_date');");

        db.execSQL("create table hierarchy ("
                + "id integer primary key autoincrement,"
                + "object_id text,"
                + "sn text,"
                + "description text,"
                + "parent_object_id text,"
                + "up_flag text,"
                + "org_id text,"
                + "code text,"
                + "child_cnt text);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        /*
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
        if (newVersion == 4) {
            Log.d(((MobileTOiRApp) mContext).getLOG_TAG(), "MobileTOiRDB.onUpgrade newVersion=" + newVersion);
            db.execSQL("create table hierarchy ("
                    + "id integer primary key autoincrement,"
                    + "object_id text,"
                    + "sn text,"
                    + "description text,"
                    + "parent_object_id text,"
                    + "up_flag text" + ");");
        }
        if (newVersion == 5) {
            Log.d(((MobileTOiRApp) mContext).getLOG_TAG(), "MobileTOiRDB.onUpgrade newVersion=" + newVersion);
            db.execSQL("drop table hierarchy;");
            db.execSQL("create table hierarchy ("
                    + "id integer primary key autoincrement,"
                    + "object_id text,"
                    + "sn text,"
                    + "description text,"
                    + "parent_object_id text,"
                    + "up_flag text,"
                    + "org_id text"+ ");");
        }
        if (newVersion == 6) {
            Log.d(((MobileTOiRApp) mContext).getLOG_TAG(), "MobileTOiRDB.onUpgrade newVersion=" + newVersion);
            db.execSQL("drop table hierarchy;");
            db.execSQL("create table hierarchy ("
                    + "id integer primary key autoincrement,"
                    + "object_id text,"
                    + "sn text,"
                    + "description text,"
                    + "parent_object_id text,"
                    + "up_flag text,"
                    + "code text,"
                    + "child_cnt text,"
                    + "org_id text"+ ");");
        }
        if (newVersion == 7) {
            Log.d(((MobileTOiRApp) mContext).getLOG_TAG(), "MobileTOiRDB.onUpgrade newVersion=" + newVersion);
            db.execSQL("drop table hierarchy;");
            db.execSQL("create table hierarchy ("
                    + "id integer primary key autoincrement,"
                    + "object_id text,"
                    + "sn text,"
                    + "description text,"
                    + "parent_object_id text,"
                    + "up_flag text,"
                    + "org_id text,"
                    + "code text,"
                    + "child_cnt text);");
        }*/
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

    public ArrayList<HashMap<String,String>> getListObjects(String parentId, String orgId) {
        ArrayList<HashMap<String,String>> returnList = new ArrayList<HashMap<String,String>>();
        String selection = null;
        String[] selectionArgs = null;

        SQLiteDatabase db = this.getWritableDatabase();

        selection = "up_flag = ? and org_id = ?";
        selectionArgs = new String[] { parentId, orgId};

        Cursor cursor = db.query("hierarchy",
                new String[] { "OBJECT_ID","SN","DESCRIPTION","PARENT_OBJECT_ID","UP_FLAG","ORG_ID","CODE","CHILD_CNT" }, selection, selectionArgs, null, null, " sn");
        if (cursor.moveToFirst()) {
            do {
                HashMap<String, String> temp = new HashMap<String, String>();
                temp.put("OBJECT_ID", cursor.getString(0));
                temp.put("SN", cursor.getString(1));
                temp.put("DESCRIPTION", cursor.getString(2));
                temp.put("PARENT_OBJECT_ID", cursor.getString(3));
                temp.put("UP_FLAG", cursor.getString(4));
                temp.put("ORG_ID", cursor.getString(5));
                temp.put("CODE", cursor.getString(6));
                temp.put("CHILD_CNT", cursor.getString(7));
                returnList.add(temp);
            } while (cursor.moveToNext());
        }
        if (cursor != null && !cursor.isClosed()) {
            cursor.close();
        }
        return returnList;
    }

    public void loadObjects (String jsonObjects, String orgId) {

        SQLiteDatabase db = this.getWritableDatabase();


        try
        {
            db.execSQL("delete from hierarchy where org_id=?", new String[] {orgId});

            try {
                //Toast.makeText(this.getBaseContext(), builder.toString(), Toast.LENGTH_LONG).show();
                JSONArray jsonArray = new JSONArray(jsonObjects);
                for (int i=0;i<jsonArray.length();i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);

                    db.execSQL("insert into hierarchy (OBJECT_ID,SN,DESCRIPTION,PARENT_OBJECT_ID,UP_FLAG,ORG_ID,CODE,CHILD_CNT) values (?,?,?,?,?,?,?,?);",
                            new String[] {jsonObject.getString("OBJECT_ID")
                                        ,jsonObject.getString("SN")
                                        ,jsonObject.getString("DESCRIPTION")
                                        ,jsonObject.getString("PARENT_OBJECT_ID")
                                        ,jsonObject.getString("UP_FLAG")
                                        ,jsonObject.getString("ORG_ID")
                                        ,jsonObject.getString("CODE")
                                        ,jsonObject.getString("CHILD_CNT")
                            });

                }

            }
            catch (JSONException e) {
                e.printStackTrace();

            }
            //db.execSQL("update settings set value=? where key = ?", new String[]{new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(Calendar.getInstance().getTime()), "orgs_date"});
            db.close();

        }
        catch(Exception e)
        {
            e.printStackTrace();
        }


    }

    public String getObjectId (String orgId){
        String mReturn ="";
        SQLiteDatabase db = this.getWritableDatabase();

        try
        {
            Cursor c = null;
            c = db.rawQuery("select OBJECT_ID as OBJECT_ID from hierarchy where UP_FLAG ='' and ORG_ID = ?", new String[] { orgId });
            c.moveToFirst();
            mReturn = c.getString(c.getColumnIndex("OBJECT_ID"));
            c.close();
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }

        return mReturn;
    }


}