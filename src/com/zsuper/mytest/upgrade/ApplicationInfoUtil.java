package com.zsuper.mytest.upgrade;

import android.content.Context;
import android.content.pm.PackageManager.NameNotFoundException;

import com.zsuper.mytest.utils.ILog;

public class ApplicationInfoUtil {
    private static final String TAG = ApplicationInfoUtil.class.getSimpleName();

    public static boolean isDevVersion(int VersionCode) {
        String versionCode = Integer.toString(VersionCode);
        Character flag = versionCode.charAt(versionCode.length() - 2);
        return Integer.valueOf(flag) % 2 != 0;
    }

    public static String getChannelNum(Context context) {
        return PropertiesUtil.getProperties(context, "channel.properties", "channelNum", "0000");
    }

    /**
     * @param context
     * @return
     */
    public static int getVerCode(Context context) {
        int verCode = -1;
        try {
            verCode = context.getPackageManager().getPackageInfo(
                    context.getPackageName(), 0).versionCode;
        } catch (NameNotFoundException e) {
            ILog.e(TAG, e.getMessage());
        }
        ILog.i(TAG, "getVerCode : " + verCode);
        return verCode;
    }

    /**
     * @param context
     * @return
     */
    public static String getVerName(Context context) {
        String verName = "";
        try {
            verName = context.getPackageManager().getPackageInfo(
                    context.getPackageName(), 0).versionName;
        } catch (NameNotFoundException e) {
            ILog.e(TAG, e.getMessage());
        }
        ILog.i(TAG, "getVerName : " + verName);
        return verName;
    }
}
