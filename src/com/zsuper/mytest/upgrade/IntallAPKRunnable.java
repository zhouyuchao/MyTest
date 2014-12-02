package com.zsuper.mytest.upgrade;

import java.lang.ref.WeakReference;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import com.zsuper.mytest.R;

public class IntallAPKRunnable implements Runnable {
    private InstallAPKHandler installAPKHandler;
    private WeakReference<Context> contextReference;

    class InstallAPKHandler extends Handler {

        public InstallAPKHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            Context mContext = contextReference.get();
            if (mContext != null) {
                switch (msg.what) {
                    case UpgradeInstallStat.DOWNLOAD_FILE_CHANGED:
                        UpgradeManager.getInstance().showCheckUpgradeTips(mContext,
                                R.string.upgrade_apk_invalidate, true);
                        break;
                    case UpgradeInstallStat.DOWNLOAD_FILE_NOT_EXIST:
                        UpgradeManager.getInstance().showCheckUpgradeTips(mContext,
                                R.string.upgrade_error, true);
                        break;
                    case UpgradeInstallStat.PATCH_APK_FAIL:
                        UpgradeManager.getInstance().showCheckUpgradeTips(mContext,
                                R.string.upgrade_error, true);
                        break;

                    default:
                        break;
                }
            }

        }

    }

    public IntallAPKRunnable(Context context) {
        this.contextReference = new WeakReference<Context>(context);
        installAPKHandler = new InstallAPKHandler(Looper.getMainLooper());
    }

    @Override
    public void run() {
        Context mContext = contextReference.get();
        if (mContext != null) {
            UpgradeManager.getInstance().installAPK(mContext, installAPKHandler);
        }
    }

}
