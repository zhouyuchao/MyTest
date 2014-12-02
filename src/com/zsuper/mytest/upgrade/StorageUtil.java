package com.zsuper.mytest.upgrade;

import java.io.File;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;

public class StorageUtil {
    public static boolean hasSDCard() {
        return android.os.Environment.getExternalStorageState().equals(
                android.os.Environment.MEDIA_MOUNTED);
    }

    public static boolean isEnoughCacheForUpgrade(long minCacheSize) {
        File file = Environment.getExternalStorageDirectory();
        return getUsableSpace(file) >= minCacheSize;
    }

    /**
     * 检查提供的路径有多少可用的空间
     * 
     * @param path
     * @return
     */
    @SuppressLint("NewApi")
    public static long getUsableSpace(File path) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
            return path.getUsableSpace();
        }
        final StatFs stats = new StatFs(path.getPath());
        return (long) stats.getBlockSize() * (long) stats.getAvailableBlocks();
    }

    public static String getDiskCacheDir(Context context, String uniqueName) {
        String cachePath = null;

        // Check if media is mounted or storage is built-in, if so, try and use
        // external cache dir
        // otherwise use internal cache dir
        if (Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED
                || !isExternalStorageRemovable()) {
            File mFile = getExternalCacheDir(context);

            if (mFile != null) {
                cachePath = mFile.getPath();
            } else {
                return null;
            }

        } else {
            cachePath = context.getCacheDir().getPath();
        }

        String cacheDirPath = cachePath + File.separator + uniqueName;
        File cacheDir = new File(cacheDirPath);

        if (cacheDir != null && !cacheDir.exists()) {
            cacheDir.mkdir();
        }

        return cacheDirPath;
    }

    public static File getExternalCacheDir(Context context) {
        if (hasExternalCacheDir()) {
            return context.getExternalCacheDir();
        }

        // Before Froyo we need to construct the external cache dir ourselves
        final String cacheDir = "/Android/data/" + context.getPackageName() + "/cache/";
        return new File(Environment.getExternalStorageDirectory().getPath() + cacheDir);
    }

    public static boolean hasExternalCacheDir() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO;
    }

    public static boolean isExternalStorageRemovable() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
            return Environment.isExternalStorageRemovable();
        }
        return true;
    }

}
