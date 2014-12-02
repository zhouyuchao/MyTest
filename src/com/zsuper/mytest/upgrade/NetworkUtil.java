package com.zsuper.mytest.upgrade;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class NetworkUtil
{
    private final static String TAG = NetworkUtil.class.getSimpleName();

    /**
     * 判断网络是否正常
     * 
     * @return
     */
    public static boolean isNetworkAvailable(Context context)
    {
        ConnectivityManager con = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkinfo = con.getActiveNetworkInfo();

        return networkinfo != null && networkinfo.isAvailable();

    }

    // 联网方式
    public static String getNetConnectType(Context context)
    {
        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = cm.getActiveNetworkInfo();
        return null != info ? info.getTypeName() : "no network";

    }
}
