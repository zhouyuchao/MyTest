package com.zsuper.mytest.utils;

import java.math.BigDecimal;

import android.app.Activity;
import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

import com.zsuper.mytest.R;

public class Utils
{
    /**
     * 切换界面进入动画
     * @param context
     * @return
     */
    public static void changeViewInAnim(Activity context)
    {
        context.overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
    }
    
    /**
     * 切换界面退出动画
     * @param context
     * @return
     */
    public static void changeViewOutAnim(Activity context)
    {
        context.overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
    }
    
    public static String getWifiSsid(Context context)
    {
        String ssid = "";
        try
        {
            WifiManager wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
            if (null != wifi && wifi.isWifiEnabled())
            {
                WifiInfo info = wifi.getConnectionInfo();
                ssid = info.getSSID();
            }
        } catch (Exception e) {
        }
        
        return ssid;
    }
    
    /**
     * 将byte转换为KB或MB
     * @param bytes
     * @return [参数说明]
     * @return String [返回类型说明]
     * @exception throws [违例类型] [违例说明]
     */
    public static String bytes2kb(long bytes)
    {
        BigDecimal filesize = new BigDecimal(bytes);
        BigDecimal megabyte = new BigDecimal(1024 * 1024);
        
        float returnValue = filesize.divide(megabyte, 2, BigDecimal.ROUND_UP).floatValue();
        if (returnValue > 1)
        {
            return (returnValue + "MB");
        }
        
        BigDecimal kilobyte = new BigDecimal(1024);
        returnValue = filesize.divide(kilobyte, 2, BigDecimal.ROUND_UP).floatValue();
        return (returnValue + "KB");
    }
}
