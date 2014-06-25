package xxmmk.mobile.toir;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;


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
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}