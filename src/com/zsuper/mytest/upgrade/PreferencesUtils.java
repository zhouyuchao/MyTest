package com.zsuper.mytest.upgrade;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * 
 * @author  ZhouYuChao/907753
 * @version  [版本号, 2014-11-25]
 * @since  [产品/模块版本]
 */
public class PreferencesUtils {

    public static final String PREFERENCES_NAME = "tvhelper_upgrade";
    public static final String KEY_NAME_DOWNLOAD_ID = "downloadId";
    public static final String KEY_NAME_FILE_SIZE = "fileSize";
    public static final String KEY_FILE_MD5 = "md5";
    public final static String KEY_NEW_VERSION_NAME = "newVersionName";
    public final static String KEY_VERSION_CODE = "versionCode";
    public final static String KEY_IS_FORCE_UPGRADE = "isForceUpgrade";
    public static final String KEY_USER_IGNORE_UPGRADE_TIME = "user_ignore_upgrade_time";
    public static final String KEY_FIRST_LAUNCH_TIME = "first_launch_time";
    /**
     * put long preferences
     * 
     * @param context
     * @param key
     * @param value
     */
    public static void putLongPreferences(Context context, String key, long value) {
        SharedPreferences settings = context.getSharedPreferences(PREFERENCES_NAME,
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putLong(key, value);
        editor.commit();
    }

    /**
     * get long preferences
     * 
     * @param context
     * @param key
     * @return
     */
    public static long getLongPreferences(Context context, String key) {
        SharedPreferences settings = context.getSharedPreferences(PREFERENCES_NAME,
                Context.MODE_PRIVATE);
        return settings.getLong(key, -1);
    }

    public static void removeData(Context context, String key) {
        SharedPreferences settings = context.getSharedPreferences(PREFERENCES_NAME,
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.remove(key);
        editor.commit();
    }

    public static int getIntPreferences(Context context, String key) {
        SharedPreferences settings = context.getSharedPreferences(PREFERENCES_NAME,
                Context.MODE_PRIVATE);
        return settings.getInt(key, -1);
    }

    public static void putIntPreferences(Context context, String key, int value) {
        SharedPreferences settings = context.getSharedPreferences(PREFERENCES_NAME,
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putInt(key, value);
        editor.commit();
    }

    public static String getStringPreferences(Context context, String key) {
        SharedPreferences settings = context.getSharedPreferences(PREFERENCES_NAME,
                Context.MODE_PRIVATE);
        return settings.getString(key, null);
    }

    public static void putStringPreferences(Context context, String key, String value) {
        SharedPreferences settings = context.getSharedPreferences(PREFERENCES_NAME,
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(key, value);
        editor.commit();
    }

}
