package com.zsuper.mytest.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper
{
    static final String TAG = DBHelper.class.getSimpleName();
    
    static final String DB_NAME = "tvhelper.db";
    static final int DB_VERSION = 1;
    
    public DBHelper(Context context)
    {
        super(context, DB_NAME, null, DB_VERSION);
    }
    
    @Override
    public void onCreate(SQLiteDatabase db)
    {
        db.execSQL(IDeviceHistoryList.DB_CREAT);
    }
    
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        db.execSQL("DROP TABLE IF EXISTS " + IDeviceHistoryList.TABLE_NAME + ";");
        onCreate(db);
    }
}
