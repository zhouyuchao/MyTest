package com.zsuper.mytest.upgrade;

import java.io.File;

import android.app.DownloadManager;
import android.app.Service;
import android.content.Intent;
import android.database.ContentObserver;
import android.os.Handler;
import android.os.IBinder;
import android.widget.Toast;

import com.zsuper.mytest.R;
import com.zsuper.mytest.utils.ILog;

public class UpgradeDownloadService extends Service {
    private static final String TAG = UpgradeDownloadService.class.getSimpleName();
    
    private DownloadChangeObserver downloadChangeObserver;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        ILog.i(TAG, "onCreate");
        // 监听下载进度
        downloadChangeObserver = new DownloadChangeObserver();
        getContentResolver().registerContentObserver(DownloadManagerUtil.CONTENT_URI, true,
                downloadChangeObserver);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        ILog.i(TAG, "onStartCommand");
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        ILog.i(TAG, "onDestroy");
        getContentResolver().unregisterContentObserver(downloadChangeObserver);
        super.onDestroy();
    }

    class DownloadChangeObserver extends ContentObserver {

        public DownloadChangeObserver() {
            super(new Handler());
        }

        @Override
        public void onChange(boolean selfChange) {
            ILog.i(TAG, "onchange");
            update();
        }

    }

    public void update() {
        long downloadId = PreferencesUtils.getLongPreferences(this,
                PreferencesUtils.KEY_NAME_DOWNLOAD_ID);
        String fileName = DownloadManagerUtil.getInstance(this).getFileName(downloadId);
        if (fileName != null) {
            File file = new File(fileName);
            int[] status = DownloadManagerUtil.getInstance(this).getBytesAndStatus(downloadId);
            switch (status[2]) {
                case DownloadManager.STATUS_RUNNING:
                    ILog.i(TAG, "running");
                    // 如果下载过程中，下载文件被删除，默认是取消下载
                    if (file != null && !file.exists() && status[0] > 0) {
                        ILog.i(TAG, "remove downloadId");
                        DownloadManagerUtil.getInstance(this).deleteDownloadFile(this, downloadId);
                        stopSelf();
                    }
                    break;
                case DownloadManager.STATUS_SUCCESSFUL:
                    ILog.i(TAG, "download success");
                    String isForceUpgrade = PreferencesUtils.getStringPreferences(this,
                            PreferencesUtils.KEY_IS_FORCE_UPGRADE);
                    // 如果强制升级，给出提示
                    if (UpgradeStrategy.ONLY_FORCE_UPGRADE.equals(isForceUpgrade)) {
                        UpgradeManager.getInstance().createForceUpgradeAlertDialog(this, null);
//                        UpgradeManager.getInstance().createForceUpgradeDialog(this.getApplicationContext(), null);
                    } else {
                        // 安装apk或者合成apk并安装
                        new Thread(new IntallAPKRunnable(this)).start();
                    }
                    stopSelf();
                    break;
                case DownloadManager.STATUS_FAILED:
                    ILog.i(TAG, "download fail");
                    Toast.makeText(this, R.string.upgrade_error, Toast.LENGTH_LONG).show();
                    stopSelf();
                    break;
            }
        }
    }
}
