package com.zsuper.mytest.upgrade;

import java.lang.ref.WeakReference;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import com.zsuper.mytest.R;

public class UpgradeRunnable implements Runnable {

    private UpgradeHandler upgradeHandler;
    private Boolean isManual;
    private WeakReference<Context> contextReference;
    private ProgressDialog upgradeLoadAPKInfoDlg;

    class UpgradeHandler extends Handler {

        public UpgradeHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            Context mContext = contextReference.get();
            closeUpgradeLoadAPKInfoDlg(upgradeLoadAPKInfoDlg);
            if (mContext != null) {
                switch (msg.what) {
                    case UpgradeCheckStat.NEW_APK_DOWNLOAD_COMPLETE_MANUAL:
                        UpgradeManager.getInstance().createUpgradeChoiceDialog(mContext,
                                (String) msg.obj, true, isManual, null,
                                null, null);
                        break;
                    case UpgradeCheckStat.NEW_APK_DOWNLOAD_COMPLETE_FORCE:
                        UpgradeManager.getInstance().createForceUpgradeDialog(mContext, null);
                        break;
                    case UpgradeCheckStat.HAS_NEW_APK_MANUAL:
                        Bundle bundle = msg.getData();
                        UpgradeManager.getInstance().createUpgradeChoiceDialog(mContext,
                                bundle.getString("upgradeIntroduce"), false, isManual,
                                (UpgradeDownloadAPKInfo) msg.obj,
                                bundle.getString("downloadFileName"),
                                bundle.getString("downLoadUrl"));
                        break;
                    case UpgradeCheckStat.HAS_NEW_APK_FORCE:
                        UpgradeManager.getInstance().showCheckUpgradeTips(mContext,
                                R.string.notice_isForceUpgradedownload, isManual);
                        break;
                    case UpgradeCheckStat.ALREADY_NEW_VERSION:
                        UpgradeManager.getInstance().showCheckUpgradeTips(mContext,
                                R.string.notice_allready_new_ver, isManual);
                        break;
                    case UpgradeCheckStat.HTTP_REQUEST_SOCKET_TIMEOUT_EXCEPTION:
                        UpgradeManager.getInstance().showCheckUpgradeTips(mContext,
                                R.string.check_upgrade_fail, isManual);
                        break;
                    case UpgradeCheckStat.HTTP_REQUEST_NETWORK_UNAVAILABLE:
                        UpgradeManager.getInstance().showCheckUpgradeTips(mContext,
                                R.string.notice_upgrade_network_unavailable, isManual);
                        break;
                    case UpgradeCheckStat.HTTP_REQUEST_OTHER_EXCEPTION:
                        UpgradeManager.getInstance().showCheckUpgradeTips(mContext,
                                R.string.notice_upgrade_server_exception, isManual);
                        break;
                    case UpgradeCheckStat.NO_ENOUGH_CACHE_FOR_UPGRADE:
                        UpgradeManager.getInstance().showCheckUpgradeTips(mContext,
                                R.string.notice_upgrade_no_enough_cache,
                                isManual);
                        break;
                    case UpgradeCheckStat.NO_SDCARD_FOR_UPGRADE:
                        UpgradeManager.getInstance().showCheckUpgradeTips(mContext,
                                R.string.notice_no_sdcard, isManual);
                        break;

                    default:
                        break;
                }
            }

        }

    }

    public UpgradeRunnable(Looper looper, Boolean isManual, Context mContext) {
        this.upgradeHandler = new UpgradeHandler(looper);
        this.isManual = isManual;
        this.contextReference = new WeakReference<Context>(mContext);
        if (isManual) {
            this.upgradeLoadAPKInfoDlg = UpgradeManager.getInstance().showUpgradeLoadAPKInfoDlg(
                    mContext);
        }
    }

    @Override
    public void run() {
        Context mContext = contextReference.get();
        if (mContext != null) {
            UpgradeManager.getInstance().checkUpgrade(mContext, isManual, upgradeHandler);
        }
    }

    private void closeUpgradeLoadAPKInfoDlg(ProgressDialog upgradeLoadAPKInfoDlg) {
        if (isManual && upgradeLoadAPKInfoDlg != null) {
            upgradeLoadAPKInfoDlg.dismiss();
            upgradeLoadAPKInfoDlg = null;
        }
    }

}
