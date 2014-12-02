package com.zsuper.mytest.db;

import android.database.Cursor;

/**
 * 设备连接历史记录
 * @author  ZhouYuChao/907753
 * @version  [版本号, 2014-11-25]
 * @since  [产品/模块版本]
 */
public interface IDeviceHistoryList
{
    public static final String TABLE_NAME = "device_history";     //表名  
    public static final String KEY_ID = "_id";                    //id  
    public static final String CONNECTED_TIME = "connected_time"; //连接时间  
    public static final String DEVICE_NAME = "device_name";       //连接设备名称
    public static final String WIFI_NAME = "wifi_name";           //连接wifi名称  
    public static final String DEVICE_TYPE = "device_type";       //连接设备类型 
    public static final String CONNECT_COUNT = "connect_count";   //连接次数
    
    //新建一个表  保存历史连接的设备记录
    public static final String DB_CREAT = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " (" + KEY_ID + " INTEGER PRIMARY KEY," + DEVICE_NAME + " VARCHAR," + WIFI_NAME + " VARCHAR," + DEVICE_TYPE + " VARCHAR," + CONNECTED_TIME + " LONG," + CONNECT_COUNT + " INTEGER)";
    
    /**
     * 插入
     * @param deviceName
     * @param wifiName
     * @param connectTime
     * @param connectCount
     * @return
     */
    public long insert(String deviceName, String wifiName, long connectTime, int connectCount);
    
    /**
     * 更新
     * @param deviceName
     * @param wifiName
     * @param connectTime
     * @param connectCount
     * @return
     */
    public boolean update(String deviceName, String wifiName, long connectTime, int connectCount);
    
    /**
     * 按照wifi名进行查询
     * @param deviceName
     * @return
     */
    public Cursor queryWithWifi(String wifiName);
    
    /**
     * 按照设备名进行查询
     * @param deviceName
     * @return
     */
    public Cursor queryWithDeviceAndWifi(String deviceName, String wifiName);
    
    /**
     * 查询所有记录
     * @return
     */
    public Cursor query();
    
    /**
     * 按照设备名删除记录
     * @param deviceName
     * @return
     */
    public boolean delete(String deviceName);
}
