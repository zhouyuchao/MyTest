package com.zsuper.mytest.upgrade;

import android.app.DownloadManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;

import com.zsuper.mytest.R;

public class DownloadManagerUtil {
    public static final Uri CONTENT_URI = Uri.parse("content://downloads/my_downloads");
    private DownloadManager downloadManager;
    static final Object sInstanceSync = new Object();
    private static DownloadManagerUtil sInstance;

    private DownloadManagerUtil(Context context) {
        downloadManager = (DownloadManager) context
                .getSystemService(Context.DOWNLOAD_SERVICE);
    }

    public static DownloadManagerUtil getInstance(Context context) {
        synchronized (sInstanceSync) {
            if (sInstance == null) {
                sInstance = new DownloadManagerUtil(context);
            }
        }
        return sInstance;
    }

    // 增加下载任务到下载管理器
    public long addDownloadTask(Context mContext, String downloadURL, String folder,
            String downloadFileName) {
        DownloadManager.Request request = new DownloadManager.Request(
                Uri.parse(downloadURL));

        // 判断是否有SD卡
        if (StorageUtil.hasSDCard()) {
            if (FileUtil.isFolderExist(folder)) {
                request.setDestinationInExternalPublicDir(
                        folder, downloadFileName);
            }

        } else {
            // 支持没有SD卡的下载
            request.setDestinationUri(null);
        }

        request.setTitle(mContext.getString(R.string.app_name));
        request.setDescription(mContext.getString(R.string.upgrade_description));
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE);
        request.setMimeType("application/vnd.android.package-archive");// 设置下载文件的类型
        request.setVisibleInDownloadsUi(false); // 不要在下载管理页面显示
        return downloadManager.enqueue(request);
    }

    public Uri getUpgradeDownloadUri(Context mContext) {
        long downloadId = PreferencesUtils.getLongPreferences(mContext,
                PreferencesUtils.KEY_NAME_DOWNLOAD_ID);
        Uri uri = null;
        if (downloadId != -1) {
            uri = downloadManager.getUriForDownloadedFile(downloadId);
        }
        return uri;
    }

    public void deleteDownloadFile(Context mContext, long downloadID) {
        if (downloadID != -1) {
            int state = mContext.getPackageManager().getApplicationEnabledSetting(
                    "com.android.providers.downloads");
            if (state != PackageManager.COMPONENT_ENABLED_STATE_DISABLED_USER
                    && state != PackageManager.COMPONENT_ENABLED_STATE_DISABLED) {
                downloadManager.remove(downloadID);
            }

        }
    }

    /**
     * get download status
     * 
     * @param downloadId
     * @return
     */
    public int getStatusById(long downloadId) {
        return getInt(downloadId, DownloadManager.COLUMN_STATUS);
    }

    /**
     * get downloaded byte, total byte
     * 
     * @param downloadId
     * @return a int array with two elements
     *         <ul>
     *         <li>result[0] represents downloaded bytes, This will initially be
     *         -1.</li>
     *         <li>result[1] represents total bytes, This will initially be -1.</li>
     *         </ul>
     */
    public int[] getDownloadBytes(long downloadId) {
        int[] bytesAndStatus = getBytesAndStatus(downloadId);
        return new int[] {
                bytesAndStatus[0], bytesAndStatus[1]
        };
    }

    /**
     * get downloaded byte, total byte and download status
     * 
     * @param downloadId
     * @return a int array with three elements
     *         <ul>
     *         <li>result[0] represents downloaded bytes, This will initially be
     *         -1.</li>
     *         <li>result[1] represents total bytes, This will initially be -1.</li>
     *         <li>result[2] represents download status, This will initially be
     *         0.</li>
     *         </ul>
     */
    public int[] getBytesAndStatus(long downloadId) {
        int[] bytesAndStatus = new int[] {
                -1, -1, 0
        };
        DownloadManager.Query query = new DownloadManager.Query().setFilterById(downloadId);
        Cursor c = null;
        try {
            c = downloadManager.query(query);
            if (c != null && c.moveToFirst()) {
                bytesAndStatus[0] = c.getInt(c
                        .getColumnIndexOrThrow(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR));
                bytesAndStatus[1] = c.getInt(c
                        .getColumnIndexOrThrow(DownloadManager.COLUMN_TOTAL_SIZE_BYTES));
                bytesAndStatus[2] = c.getInt(c.getColumnIndex(DownloadManager.COLUMN_STATUS));
            }
        } finally {
            if (c != null) {
                c.close();
            }
        }
        return bytesAndStatus;
    }

    /**
     * get download file name
     * 
     * @param downloadId
     * @return
     */
    public String getFileName(long downloadId) {
        return getString(downloadId, DownloadManager.COLUMN_LOCAL_FILENAME);
    }

    /**
     * get download uri
     * 
     * @param downloadId
     * @return
     */
    public String getUri(long downloadId) {
        return getString(downloadId, DownloadManager.COLUMN_URI);
    }

    /**
     * get failed code or paused reason
     * 
     * @param downloadId
     * @return <ul>
     *         <li>if status of downloadId is
     *         {@link DownloadManager#STATUS_PAUSED}, return
     *         {@link #getPausedReason(long)}</li>
     *         <li>if status of downloadId is
     *         {@link DownloadManager#STATUS_FAILED}, return
     *         {@link #getErrorCode(long)}</li>
     *         <li>if status of downloadId is neither
     *         {@link DownloadManager#STATUS_PAUSED} nor
     *         {@link DownloadManager#STATUS_FAILED}, return 0</li>
     *         </ul>
     */
    public int getReason(long downloadId) {
        return getInt(downloadId, DownloadManager.COLUMN_REASON);
    }

    /**
     * get paused reason
     * 
     * @param downloadId
     * @return <ul>
     *         <li>if status of downloadId is
     *         {@link DownloadManager#STATUS_PAUSED}, return one of
     *         {@link DownloadManager#PAUSED_WAITING_TO_RETRY}<br/>
     *         {@link DownloadManager#PAUSED_WAITING_FOR_NETWORK}<br/>
     *         {@link DownloadManager#PAUSED_QUEUED_FOR_WIFI}<br/>
     *         {@link DownloadManager#PAUSED_UNKNOWN}</li>
     *         <li>else return {@link DownloadManager#PAUSED_UNKNOWN}</li>
     *         </ul>
     */
    public int getPausedReason(long downloadId) {
        return getInt(downloadId, DownloadManager.COLUMN_REASON);
    }

    /**
     * get failed error code
     * 
     * @param downloadId
     * @return one of {@link DownloadManager#ERROR_*}
     */
    public int getErrorCode(long downloadId) {
        return getInt(downloadId, DownloadManager.COLUMN_REASON);
    }

    /**
     * get string column
     * 
     * @param downloadId
     * @param columnName
     * @return
     */
    private String getString(long downloadId, String columnName) {
        DownloadManager.Query query = new DownloadManager.Query().setFilterById(downloadId);
        String result = null;
        Cursor c = null;
        try {
            c = downloadManager.query(query);
            if (c != null && c.moveToFirst()) {
                result = c.getString(c.getColumnIndex(columnName));
            }
        } finally {
            if (c != null) {
                c.close();
            }
        }
        return result;
    }

    /**
     * get int column
     * 
     * @param downloadId
     * @param columnName
     * @return
     */
    private int getInt(long downloadId, String columnName) {
        DownloadManager.Query query = new DownloadManager.Query().setFilterById(downloadId);
        int result = -1;
        Cursor c = null;
        try {
            c = downloadManager.query(query);
            if (c != null && c.moveToFirst()) {
                result = c.getInt(c.getColumnIndex(columnName));
            }
        } finally {
            if (c != null) {
                c.close();
            }
        }
        return result;
    }

    /**
     * get download local uri
     * 
     * @param downloadId
     * @return
     */
    public String getLocalUri(long downloadId) {
        return getString(downloadId, DownloadManager.COLUMN_LOCAL_URI);
    }
}
