package com.zsuper.mytest.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class DatabaseAdapter
{
    private static final String TAG = DatabaseAdapter.class.getSimpleName();
    
    private Context mContext;
    private DBHelper mDatabaseHelper;
    private SQLiteDatabase mSQLiteDatabase;
    
    public DatabaseAdapter(Context context)
    {
        mContext = context;
        mDatabaseHelper = new DBHelper(mContext);
    }
    
    // 开启
    public void open()
    {
        mSQLiteDatabase = mDatabaseHelper.getWritableDatabase();
    }
    
    // 关闭
    public void close()
    {
        if (null != mSQLiteDatabase)
        {
            mSQLiteDatabase.close();
        }
        if (null != mDatabaseHelper)
        {
            mDatabaseHelper.close();
        }
    }
    
    /**
     * 插入数据
     * @param tableName 表名
     * @param initialValues 要插入的列对应值
     * @return long [返回类型说明]
     */
    public long insert(String tableName, ContentValues initialValues)
    {
        if (mSQLiteDatabase == null)
        {
            return -1;
        }
        return mSQLiteDatabase.insert(tableName, null, initialValues);
    }
    
    /**
     * 删除数据 <功能描述>
     * @param tableName 表名
     * @param deleteCondition 删除的条件
     * @param deleteArgs 如果deleteCondition中有“？”号，将用此数组中的值替换
     * @return boolean
     */
    public boolean delete(String tableName, String deleteCondition, String[] deleteArgs)
    {
        if (mSQLiteDatabase == null)
        {
            return false;
        }
        return mSQLiteDatabase.delete(tableName, deleteCondition, deleteArgs) > 0;
    }
    
    /**
     * 更新
     * @param tableName 表名
     * @param initialValues 要更新的列
     * @param selection 更新的条件
     * @param selectArgs 如果selection中有“？”号，将用此数组中的值替换
     * @return boolean [返回类型说明]
     */
    public boolean update(String tableName, ContentValues initialValues, String selection, String[] selectArgs)
    {
        if (mSQLiteDatabase == null)
        {
            return false;
        }
        int returnValue = mSQLiteDatabase.update(tableName, initialValues, selection, selectArgs);
        return returnValue > 0;
    }
    
    public Cursor query(String tableName, String[] columns, String selection,
            String[] selectionArgs, String groupBy, String having, String orderBy)
    {
        return mSQLiteDatabase.query(tableName, columns, selection, selectionArgs, groupBy, having, orderBy);
    }
    
    public Cursor query(String tableName, String[] columns, String selection,
            String[] selectionArgs, String groupBy, String having, String orderBy, String limit)
    {
        return mSQLiteDatabase.query(tableName, columns, selection, selectionArgs, groupBy, having, orderBy, limit);
    }
    
    public Cursor rawQuery(String string, String[] selectionArgs)
    {
        return mSQLiteDatabase.rawQuery(string, selectionArgs);
    }
    
    public void openReadableDB()
    {
        mDatabaseHelper = new DBHelper(mContext);
        mSQLiteDatabase = mDatabaseHelper.getReadableDatabase();
    }
    
    public void ExecSQL(String sqlString)
    {
        mSQLiteDatabase.execSQL(sqlString);
    }
    
    public void beginTransaction()
    {
        mSQLiteDatabase.beginTransaction();
    }
    
    public void setTransactionSuccessful()
    {
        mSQLiteDatabase.setTransactionSuccessful();
    }
    
    public void endTransaction()
    {
        mSQLiteDatabase.endTransaction();
    }
    
    public void release()
    {
        mContext = null;
    }
}
