package com.zsuper.mytest.upgrade;

import android.os.Environment;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class FileUtil
{
    private static final String TAG = FileUtil.class.getSimpleName();

    public static boolean isFolderExist(String dir) {
        File folder = Environment.getExternalStoragePublicDirectory(dir);
        return (folder != null && folder.exists() && folder.isDirectory()) ? true : folder.mkdirs();
    }

    public static void deleteAllFiles(File root) {
        File files[] = root.listFiles();
        if (files != null)
            for (File f : files) {
                if (f.isDirectory()) { // 判断是否为文件夹
                    deleteAllFiles(f);
                    try {
                        f.delete();
                    } catch (Exception e) {
                    }
                } else {
                    if (f.exists()) { // 判断是否存在
                        try {
                            f.delete();
                        } catch (Exception e) {
                        }
                    }
                }
            }
    }

    /**
     * @param file
     * @return
     */
    public static String getMD5(File file)
    {
        FileInputStream fis = null;
        try
        {
            MessageDigest md = MessageDigest.getInstance("MD5");
            fis = new FileInputStream(file);
            byte[] buffer = new byte[8192];
            int length = -1;
            while ((length = fis.read(buffer)) != -1)
            {
                md.update(buffer, 0, length);
            }
            return bytesToString(md.digest());
        } catch (IOException ex)
        {
            Logger.getLogger(TAG).log(Level.SEVERE,
                    null, ex);
            return null;
        } catch (NoSuchAlgorithmException ex)
        {
            Logger.getLogger(TAG).log(Level.SEVERE,
                    null, ex);
            return null;
        } finally
        {
            try
            {
                fis.close();
            } catch (IOException ex)
            {
                Logger.getLogger(TAG).log(
                        Level.SEVERE, null, ex);
            }
        }
    }

    /**
     * @param file
     * @return
     */
    public static String getSHA(File file)
    {
        FileInputStream fis = null;
        try
        {
            MessageDigest md = MessageDigest.getInstance("SHA");
            fis = new FileInputStream(file);
            byte[] buffer = new byte[8192];
            int length = -1;
            while ((length = fis.read(buffer)) != -1)
            {
                md.update(buffer, 0, length);
            }
            return bytesToString(md.digest());
        } catch (IOException ex)
        {
            Logger.getLogger(TAG).log(Level.SEVERE,
                    null, ex);
            return null;
        } catch (NoSuchAlgorithmException ex)
        {
            Logger.getLogger(TAG).log(Level.SEVERE,
                    null, ex);
            return null;
        } finally
        {
            try
            {
                fis.close();
            } catch (IOException ex)
            {
                Logger.getLogger(TAG).log(
                        Level.SEVERE, null, ex);
            }
        }
    }

    public static String bytesToString(byte[] data)
    {
        char hexDigits[] =
        {
                '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd',
                'e', 'f'
        };
        char[] temp = new char[data.length * 2];
        for (int i = 0; i < data.length; i++)
        {
            byte b = data[i];
            temp[i * 2] = hexDigits[b >>> 4 & 0x0f];
            temp[i * 2 + 1] = hexDigits[b & 0x0f];
        }
        return new String(temp);

    }

    public static boolean checkFileMd5(File file, String md5)
    {
        String fileMd5 = getMD5(file);
        if (fileMd5 != null && md5 != null && md5.equals(fileMd5))
        {
            return true;
        }
        return false;

    }

    public static boolean checkFilSHA(File file, String sha)
    {
        String fileSHA = getSHA(file);
        if (fileSHA != null && sha != null && sha.equals(fileSHA))
        {
            return true;
        }
        return false;

    }

}
