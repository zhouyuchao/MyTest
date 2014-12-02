package com.zsuper.mytest.upgrade;

import java.util.UUID;

import android.content.Context;
import android.provider.Settings.Secure;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;

public class PhoneInfoUtil {

    private TelephonyManager telephonyManager;
    static final Object sInstanceSync = new Object();
    private static PhoneInfoUtil sInstance;

    private PhoneInfoUtil(Context context) {
        telephonyManager = (TelephonyManager) context
                .getSystemService(Context.TELEPHONY_SERVICE);
    }

    public static PhoneInfoUtil getInstance(Context context) {
        synchronized (sInstanceSync) {
            if (sInstance == null) {
                sInstance = new PhoneInfoUtil(context);
            }
        }
        return sInstance;
    }

    public String getIMEI(Context mContext) {
        String deviceId = telephonyManager.getDeviceId();
        if (deviceId == null) {
            deviceId = Secure.getString(mContext.getContentResolver(), Secure.ANDROID_ID);
        }
        return deviceId != null ? deviceId : UUID.randomUUID().toString();
    }

    public String getPhoneNumber() {
        return telephonyManager.getLine1Number();
    }

    // 获取终端信息
    // 终端型号
    public static String getPhoneModel()
    {
        return android.os.Build.MODEL;
    }

    // 终端android版本
    public static String getAndroidVersion()
    {
        return "Android version:" + android.os.Build.VERSION.RELEASE;
    }

    // 终端分辨率信息
    public static String getPhoneScreen(Context context)
    {
        StringBuilder stringBuilder = new StringBuilder();
        DisplayMetrics dm = new DisplayMetrics();
        dm = context.getResources().getDisplayMetrics();
        int screenWidth = dm.widthPixels;
        int screenHeight = dm.heightPixels;
        stringBuilder.append("screenInfo：");
        stringBuilder.append(screenWidth);
        stringBuilder.append("*");
        stringBuilder.append(screenHeight);
        return stringBuilder.toString();
    }
}
