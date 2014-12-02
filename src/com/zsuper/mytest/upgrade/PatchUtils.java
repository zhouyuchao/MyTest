package com.zsuper.mytest.upgrade;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class PatchUtils {

    static {
        System.loadLibrary("apkpatch");
    }

    /**
     * native方法 使用路径为oldApkPath的apk与路径为patchPath的补丁包，合成新的apk，并存储于newApkPath
     * 
     * @param oldApkPath
     * @param newApkPath
     * @param patchPath
     * @return
     */
    public static native int patch(String oldApkPath, String newApkPath,
            String patchPath);

    /**
     * @Description 将app由data/app目录拷贝到sd卡下的指定目录中
     * @param packageName 应用的包名
     * @param dest 需要将应用程序拷贝的目标位置
     */
    public static String backupApplication(String packageName, String dest) {

        if (packageName == null || packageName.length() == 0

                || dest == null || dest.length() == 0) {
            return "illegal parameters";
        }

        // check file /data/app/appId-1.apk exists
        String apkPath = "/data/app/" + packageName + "-1.apk";

        File apkFile = new File(apkPath);

        if (apkFile.exists() == false) {
            return apkPath + " doesn't exist!";
        }

        FileInputStream in = null;

        try {
            in = new FileInputStream(apkFile);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return e.getMessage();
        }

        // create dest folder if necessary
        int i = dest.lastIndexOf('/');

        if (i != -1) {
            File dirs = new File(dest.substring(0, i));
            dirs.mkdirs();
            dirs = null;
        }

        // do file copy operation
        byte[] c = new byte[1024];

        int slen;

        FileOutputStream out = null;

        try {
            out = new FileOutputStream(dest);

            while ((slen = in.read(c, 0, c.length)) != -1)
                out.write(c, 0, slen);
        } catch (IOException e) {
            e.printStackTrace();
            return e.getMessage();
        } finally {
            if (out != null)
                try {
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    return e.getMessage();
                }
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    return e.getMessage();
                }
            }
        }

        return "success";

    }
}
