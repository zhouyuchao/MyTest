package com.zsuper.mytest.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.text.TextUtils;

import com.zsuper.mytest.utils.ILog;

public class DeviceHistoryListImpl implements IDeviceHistoryList
{
    private static final String TAG = DeviceHistoryListImpl.class.getSimpleName();
    
    private Context mContext;
    private DatabaseAdapter mDatabaseAdapter;
    
    public DeviceHistoryListImpl(Context context)
    {
        this.mContext = context;
        this.mDatabaseAdapter = new DatabaseAdapter(mContext);
        this.mDatabaseAdapter.open();
    }
    
    @Override
    public long insert(String deviceName, String wifiName, long connectTime, int connectCount)
    {
        long row = -1;
        if (!TextUtils.isEmpty(deviceName) && !TextUtils.isEmpty(wifiName))
        {
            ContentValues cv = new ContentValues();
            cv.put(DEVICE_NAME, deviceName);
            cv.put(WIFI_NAME, wifiName);
            cv.put(CONNECTED_TIME, connectTime);
            cv.put(CONNECT_COUNT, connectCount);
            row = this.mDatabaseAdapter.insert(TABLE_NAME, cv);
        }
        return row;
    }
    
    @Override
    public boolean update(String deviceName, String wifiName, long connectTime, int connectCount)
    {
        boolean isUpdate = false;
        if (!TextUtils.isEmpty(deviceName) && !TextUtils.isEmpty(wifiName))
        {
            ContentValues cv = new ContentValues();
            cv.put(DEVICE_NAME, deviceName);
            cv.put(WIFI_NAME, wifiName);
            cv.put(CONNECTED_TIME, connectTime);
            cv.put(CONNECT_COUNT, connectCount);
            isUpdate = this.mDatabaseAdapter.update(TABLE_NAME, cv, DEVICE_NAME + "= ?", new String[] { deviceName });
        }
        return isUpdate;
    }
    
    @Override
    public Cursor queryWithWifi(String wifiName)
    {
        ILog.d(TAG, "queryWithWifi ---> wifiName:" + wifiName);
        if (null == wifiName)
        {
            ILog.e(TAG, "queryWithWifi ---> argument(wifiName) is null");
            return null;
        }
        
        Cursor mCursor = null;
        if (!TextUtils.isEmpty(wifiName))
        {
            mCursor = this.mDatabaseAdapter.query(TABLE_NAME, 
                    new String[] { KEY_ID, DEVICE_NAME, WIFI_NAME, DEVICE_TYPE, CONNECTED_TIME, CONNECT_COUNT }, 
                    WIFI_NAME + "= ?", 
                    new String[] { wifiName }, 
                    null, 
                    null, 
                    CONNECTED_TIME + " DESC");
        }
        return mCursor;
    }
    
    @Override
    public Cursor queryWithDeviceAndWifi(String deviceName, String wifiName)
    {
        ILog.d(TAG, "queryWithDeviceAndWifi devName:" + deviceName + ", wifiName:" + wifiName);
        if (null == deviceName || null == wifiName)
        {
            ILog.e(TAG, "queryWithWifi ---> arguments is null");
            return null;
        }
        
        Cursor mCursor = this.mDatabaseAdapter.query(TABLE_NAME, 
                new String[] { KEY_ID, DEVICE_NAME, WIFI_NAME, DEVICE_TYPE, CONNECTED_TIME, CONNECT_COUNT }, 
                DEVICE_NAME + "= ? and " + WIFI_NAME + "= ?", 
                new String[] { deviceName, wifiName }, 
                null, 
                null, 
                CONNECTED_TIME + " DESC");
        return mCursor;
    }
    
    @Override
    public Cursor query()
    {
        //按照时间降序查询
        Cursor mCursor = this.mDatabaseAdapter.query(TABLE_NAME, 
                new String[] { KEY_ID, DEVICE_NAME, WIFI_NAME, DEVICE_TYPE, CONNECTED_TIME, CONNECT_COUNT }, 
                null, null, null, null, CONNECTED_TIME + " DESC");
        return mCursor;
    }
    
    @Override
    public boolean delete(String deviceName)
    {
        return false;
    }
    
    /**
     * 关闭数据库
     */
    public void close()
    {
        this.mDatabaseAdapter.close();
    }
    
    public boolean isLastConnectDevice(String wifiName, String deviceName)
    {
        ILog.d(TAG, "isLastConnectDevice -> wifiName:" + wifiName + ", dev:" + deviceName);
        boolean flag = false;
        String saveDeviceName = null;
        int deviceNameIndex = -1;
        
        Cursor cursor = queryWithWifi(wifiName);
        if (null != cursor)
        {
            deviceNameIndex = cursor.getColumnIndex(DEVICE_NAME);
            if (cursor.moveToNext())
            {
                saveDeviceName = cursor.getString(deviceNameIndex);
                ILog.d(TAG, "isLastConnectDevice -> saveDeviceName:" + saveDeviceName);
                if (null != saveDeviceName && saveDeviceName.equals(deviceName))
                {
                    flag = true;
                }
            }
        }
        else
        {
            ILog.d(TAG, "cursor is null ");
        }
        
        ILog.d(TAG, "isLastConnectDevice -> " + flag);
        return flag;
    }
    
    public boolean hasHistoryInWifi(String wifiName)
    {
        boolean ret = false;
        
        Cursor cursor = queryWithWifi(wifiName);
        if (null != cursor && cursor.moveToNext())
        {
            ret = true;
        }
        
        ILog.d(TAG, "hasHistoryInWifi " + wifiName + " -> " + ret);
        return ret;
    }
    
    /**
     * 获取设备连接过的次数
     * @param deviceName
     * @return
     */
    public int getDeviceConnectCount(String deviceName, String wifiName)
    {
        int count = 0;
        
        Cursor cursor = queryWithDeviceAndWifi(deviceName, wifiName);
        if (null != cursor)
        {
            int connectCountIndex = cursor.getColumnIndex(CONNECT_COUNT);
            if (cursor.moveToNext())
            {
                count = cursor.getInt(connectCountIndex);
            }
            cursor.close();
        }
        
        return count;
    }
}
