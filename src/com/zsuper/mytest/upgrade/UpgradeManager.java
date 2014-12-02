package com.zsuper.mytest.upgrade;

import java.io.File;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.Date;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.ContextThemeWrapper;
import android.view.WindowManager;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.zsuper.mytest.R;
import com.zsuper.mytest.utils.DateFormatUtil;
import com.zsuper.mytest.utils.ILog;

public class UpgradeManager
{
    private static final String TAG = UpgradeManager.class.getSimpleName();
    
    private static final int UPGRADE_MIN_CACHE_SIZE = 20 * 1024 * 1024; // 20MB
    
    /** 下载存放路径 */
    public static final String EXTERNAL_DIR = "coship/tvhelper/upgrade";
    /** 升级服务器主页 */
    public static final String FORMAL_UPGRADE_SERVER_ADDR = "/upgrade/index_olaservice.php";
    /** 服务器域名 */
    public static final String DOMAIN = "http://update.oladata.com";
    
    public static final String DATE_FORMAT = "yyyy-MM-dd";
    
    private AlertDialog mAlertDialog;
    
    private UpgradeDownloadAPKInfo mUpgradeDownloadAPKInfo;
    
    private static class UpgradeManagerHolder
    {
        static final UpgradeManager INSTANCE = new UpgradeManager();
    }
    
    public static UpgradeManager getInstance()
    {
        return UpgradeManagerHolder.INSTANCE;
    }
    
    private UpgradeManager()
    {
    }
    
    public void checkUpgrade(Context mContext, Boolean isManual, Handler mHandler)
    {
        ILog.d(TAG, "checkUpgrade ");
        
        String upgradeURL = getCurServerAddr(mContext, DOMAIN);
        ILog.d(TAG, "checkUpgrade upgradeURL : " + upgradeURL);
        try
        {
            // 下载升级json文件
            mUpgradeDownloadAPKInfo = getUpgradeDownloadAPKInfo(mContext, upgradeURL, isManual, mHandler);
            
            // 判断是否只能手动升级
            if (!isManual && UpgradeStrategy.ONLY_MANUAL_UPGRADE.equals(mUpgradeDownloadAPKInfo.getIsForceUpgrade()))
            {
                return;
            }
            
            // 应用报名相同且应用版本编号高于当前版本，VersionCode倒数第二位如果是偶数就是正式版，否则是开发版
            boolean isDevVersion = ApplicationInfoUtil.isDevVersion(ApplicationInfoUtil.getVerCode(mContext));
            
            String localPkgName = mContext.getPackageName();
            String remotePkgName = mUpgradeDownloadAPKInfo.getPackageName();
            ILog.i(TAG, "checkUpgrade localPkgName:" + localPkgName + ", remotePkgName:" + remotePkgName);
            
            // 判断是否需要升级
            if (isUpgrade(mContext, mUpgradeDownloadAPKInfo, isDevVersion) 
                    && mContext.getPackageName().equals(mUpgradeDownloadAPKInfo.getPackageName()))
            {
                // 是否之前已经下载好安装包，但没安装
                if (isDownloadComplete(mContext, mUpgradeDownloadAPKInfo))
                {
                    // 是否是强制升级
                    if (UpgradeStrategy.ONLY_FORCE_UPGRADE.equals(mUpgradeDownloadAPKInfo.getIsForceUpgrade()))
                    {
                        mHandler.obtainMessage(UpgradeCheckStat.NEW_APK_DOWNLOAD_COMPLETE_FORCE).sendToTarget();
                    }
                    else
                    {
                        Message msg = mHandler.obtainMessage(UpgradeCheckStat.NEW_APK_DOWNLOAD_COMPLETE_MANUAL);
                        msg.obj = generateUpgradeIntroduce(mContext, mUpgradeDownloadAPKInfo, R.string.notice_has_download);
                        msg.sendToTarget();
                    }
                    return;
                }
                else
                {
                    // 删除之前下载的安装包
                    deleteUpradeFiles(mContext);
                }
                
                // 判断有没有SD卡
                if (!StorageUtil.hasSDCard())
                {
                    ILog.e(TAG, "checkUpgrade NO_SDCARD_FOR_UPGRADE");
                    mHandler.obtainMessage(UpgradeCheckStat.NO_SDCARD_FOR_UPGRADE).sendToTarget();
                    return;
                }
                
                // 判断SD卡的容量是否低于20M
                if (!StorageUtil.isEnoughCacheForUpgrade(UPGRADE_MIN_CACHE_SIZE))
                {
                    ILog.e(TAG, "checkUpgrade UPGRADE_MIN_CACHE_SIZE");
                    mHandler.obtainMessage(UpgradeCheckStat.NO_ENOUGH_CACHE_FOR_UPGRADE).sendToTarget();
                    return;
                }
                
                String downloadFileName = generateAPKName(mUpgradeDownloadAPKInfo.getVersionName(), mUpgradeDownloadAPKInfo.getVersionCode());
                String downLoadUrl = mUpgradeDownloadAPKInfo.getDownloadUrl();
                ILog.v(TAG, "checkUpgrade downLoadUrl : " + downLoadUrl);
                
                // 判断是否需要增量升级，判断当前版本号是否大于或等于SmartUpgradeVersion
//                if (isNeedPatchUpgrade(mContext, upgradeDownloadAPKInfo.getSmartUpgradeVersion()))
//                {
//                    // 组装增量包的地址
//                    StringBuilder serverBuilder = new StringBuilder(DOMAIN);
//                    serverBuilder.append(FORMAL_UPGRADE_SERVER_ADDR);
//                    downloadFileName = generatePatchFileName(mContext, 
//                            upgradeDownloadAPKInfo.getVersionName(), 
//                            upgradeDownloadAPKInfo.getVersionCode());
//                    serverBuilder.append(downloadFileName);
//                    downLoadUrl = serverBuilder.toString();
//                }
                
                // 判断是否强制升级
                if (!UpgradeStrategy.ONLY_FORCE_UPGRADE.equals(mUpgradeDownloadAPKInfo.getIsForceUpgrade()))
                {
                    String upgradeIntroduce = generateUpgradeIntroduce(mContext, mUpgradeDownloadAPKInfo, R.string.notice_find_new_ver);
                    Message msg = mHandler.obtainMessage(UpgradeCheckStat.HAS_NEW_APK_MANUAL);
                    msg.obj = mUpgradeDownloadAPKInfo;
                    Bundle bundle = new Bundle();
                    bundle.putString("upgradeIntroduce", upgradeIntroduce);
                    bundle.putString("downloadFileName", downloadFileName);
                    bundle.putString("downLoadUrl", downLoadUrl);
                    msg.setData(bundle);
                    msg.sendToTarget();
                }
                else
                {
                    downLoadfile(mContext, mUpgradeDownloadAPKInfo, downloadFileName, downLoadUrl);
                    mHandler.obtainMessage(UpgradeCheckStat.HAS_NEW_APK_FORCE).sendToTarget();
                }
            }
            else
            {
                ILog.e(TAG, "allready new version");
                mHandler.obtainMessage(UpgradeCheckStat.ALREADY_NEW_VERSION).sendToTarget();
            }
        } catch (SocketTimeoutException e)
        {
            e.printStackTrace();
            mHandler.obtainMessage(UpgradeCheckStat.HTTP_REQUEST_SOCKET_TIMEOUT_EXCEPTION).sendToTarget();
        } catch (UnknownHostException e)
        {
            e.printStackTrace();
            mHandler.obtainMessage(UpgradeCheckStat.HTTP_REQUEST_NETWORK_UNAVAILABLE).sendToTarget();
        } catch (Exception e)
        {
            e.printStackTrace();
            mHandler.obtainMessage(UpgradeCheckStat.HTTP_REQUEST_OTHER_EXCEPTION).sendToTarget();
        }
    }
    
    private String generateUpgradeIntroduce(Context mContext,
            UpgradeDownloadAPKInfo upgradeDownloadAPKInfo, int introduceId)
    {
        StringBuilder sb = new StringBuilder();
        sb.append(mContext.getResources().getString(introduceId, upgradeDownloadAPKInfo.getVersionName() 
                + "." + upgradeDownloadAPKInfo.getVersionCode()));
        sb.append("\n");
        sb.append(upgradeDownloadAPKInfo.getUpgradeIntroduce());
        return sb.toString();
    }
    
    public static String getCurServerAddr(Context context, String updateHostURL)
    {
        StringBuilder serverStringBuilder = new StringBuilder();
        serverStringBuilder.append(updateHostURL);
        serverStringBuilder.append(FORMAL_UPGRADE_SERVER_ADDR);
        serverStringBuilder.append("?iMei=");
        serverStringBuilder.append(PhoneInfoUtil.getInstance(context).getIMEI(context));
        serverStringBuilder.append("&isDevVersion=");
        
        boolean isDevVersion = ApplicationInfoUtil.isDevVersion(ApplicationInfoUtil.getVerCode(context));
        serverStringBuilder.append(isDevVersion);
        serverStringBuilder.append("&channel=");
        serverStringBuilder.append(ApplicationInfoUtil.getChannelNum(context));
        return serverStringBuilder.toString();
    }
    
    public UpgradeDownloadAPKInfo getUpgradeDownloadAPKInfo(Context mContext, String upgradeURL,
            boolean isManual, Handler mHandler) throws Exception
    {
        // 得到打开的链接对象
        HttpURLConnection upgradeConn = null;
        try
        {
            URL url = new URL(upgradeURL);
            upgradeConn = (HttpURLConnection) url.openConnection();
            if (Build.VERSION.SDK != null && Build.VERSION.SDK_INT > 13)
            {
                upgradeConn.setRequestProperty("Connection", "close");
            }
            
            // 设置请求超时与请求方式
            upgradeConn.setReadTimeout(3 * 1000);
            upgradeConn.setConnectTimeout(3 * 1000);
            upgradeConn.setRequestMethod("GET");
            HttpURLConnection.setFollowRedirects(true);
            upgradeConn.setInstanceFollowRedirects(true);
            if (upgradeConn.getResponseCode() == 200)
            {
                // 从链接中获取一个输入流对象
                InputStream inStream = upgradeConn.getInputStream();
                // 调用数据流处理方法
                byte[] data = IOStreamUtil.readInputStream(inStream);
                if (data != null)
                {
                    Gson gson = new Gson();
                    return (UpgradeDownloadAPKInfo) gson.fromJson(new String(data), UpgradeDownloadAPKInfo.class);
                }
                else
                {
                    mHandler.obtainMessage(UpgradeCheckStat.HTTP_REQUEST_OTHER_EXCEPTION).sendToTarget();
                }
            }
            else if (upgradeConn.getResponseCode() == 256)
            {
                mHandler.obtainMessage(UpgradeCheckStat.ALREADY_NEW_VERSION).sendToTarget();
            }
            else
            {
                mHandler.obtainMessage(UpgradeCheckStat.HTTP_REQUEST_OTHER_EXCEPTION).sendToTarget();
            }
        } finally
        {
            if (upgradeConn != null)
            {
                upgradeConn.disconnect();
                upgradeConn = null;
            }
        }
        return null;
    }
    
    public void showCheckUpgradeTips(Context mContext, int tipId, boolean isNeedShowTips)
    {
        if (isNeedShowTips)
        {
            String checkUpgradeFail = mContext.getResources().getString(tipId);
            Toast.makeText(mContext, checkUpgradeFail, Toast.LENGTH_SHORT).show();
        }
    }
    
    private boolean isDownloadComplete(Context mContext,
            UpgradeDownloadAPKInfo upgradeDownloadAPKInfo)
    {
        boolean isFileExist = isUpgradeDownloadFileExist(mContext);
        return isFileExist 
                && upgradeDownloadAPKInfo.getVersionName().equals(PreferencesUtils.getStringPreferences(mContext, PreferencesUtils.KEY_NEW_VERSION_NAME)) 
                && (upgradeDownloadAPKInfo.getVersionCode() == PreferencesUtils.getIntPreferences(mContext, PreferencesUtils.KEY_VERSION_CODE));
    }
    
    public static Boolean isUpgrade(Context context, UpgradeDownloadAPKInfo upgradeDownloadAPKInfo, Boolean isDevVersion)
    {
        String versionName = ApplicationInfoUtil.getVerName(context);
        String remoteVerName = upgradeDownloadAPKInfo.getVersionName();
        ILog.d(TAG, "isUpgrade localVer:" + versionName + ", remoteVer:" + remoteVerName);
        
        Boolean isUpgrade = isNeedUpgrade(remoteVerName, versionName);
        if (isDevVersion)
        {
            if (isUpgrade)
            {
                return true;
            }
            else
            {
                if (upgradeDownloadAPKInfo.getVersionName().equals(versionName))
                {
                    return upgradeDownloadAPKInfo.getVersionCode() > ApplicationInfoUtil.getVerCode(context);
                }
            }
        }
        ILog.d(TAG, "isUpgrade : " + isUpgrade);
        return isUpgrade;
    }
    
    public static Boolean isNeedUpgrade(String newVersion, String currentVersion)
    {
        if (newVersion == null || currentVersion == null)
        {
            return false;
        }
        
        String[] newVersionStrings = newVersion.split("\\.");
        String[] currentVersionStrings = currentVersion.split("\\.");
        
        for (int i = 0; i < currentVersionStrings.length; i++)
        {
            if (Integer.valueOf(newVersionStrings[i]) > Integer.valueOf(currentVersionStrings[i]))
            {
                return true;
            }
            else if (Integer.valueOf(newVersionStrings[i]) < Integer.valueOf(currentVersionStrings[i]))
            {
                return false;
            }
        }
        return false;
    }
    
    public static Boolean isUpgradeDownloadFileExist(final Context mContext)
    {
        File file = getDownloadUpgradeFile(mContext);
        Boolean isFileExist = file != null && file.exists();
        return isFileExist;
    }
    
    public static File getDownloadUpgradeFile(final Context mContext)
    {
        long downloadId = PreferencesUtils.getLongPreferences(mContext, PreferencesUtils.KEY_NAME_DOWNLOAD_ID);
        String fileName = DownloadManagerUtil.getInstance(mContext).getFileName(downloadId);
        File file = null;
        
        
        if (fileName != null)
        {
            file = new File(fileName);
        }
        return file;
    }
    
    /**
     * 启动下载
     * <功能描述>
     * @param mContext
     * @param upgradeDownloadAPKInfo
     * @param downloadFileName
     * @param downloadURL [参数说明]
     * @return void [返回类型说明]
     * @exception throws [违例类型] [违例说明]
     */
    private void downLoadfile(Context mContext, UpgradeDownloadAPKInfo upgradeDownloadAPKInfo,
            String downloadFileName, String downloadURL)
    {
        int state = mContext.getPackageManager().getApplicationEnabledSetting("com.android.providers.downloads");
        
        // 判断系统自带的下载器是否被用户关闭
        if (state != PackageManager.COMPONENT_ENABLED_STATE_DISABLED_USER && state != PackageManager.COMPONENT_ENABLED_STATE_DISABLED)
        {
            // 将下载信息保存在本地
            saveDownloadAPKInfo(mContext, upgradeDownloadAPKInfo);
            // 添加下载任务
            long downloadId = DownloadManagerUtil.getInstance(mContext).addDownloadTask(mContext, downloadURL, EXTERNAL_DIR, downloadFileName);
            // 启动监测服务
            mContext.startService(new Intent(mContext, UpgradeDownloadService.class));
            PreferencesUtils.putLongPreferences(mContext, PreferencesUtils.KEY_NAME_DOWNLOAD_ID, downloadId);
        }
        else
        {
            Toast.makeText(mContext, "下载管理器已经被关闭，请去应用管理器启动。", Toast.LENGTH_LONG).show();
        }
    }
    
    private void saveDownloadAPKInfo(Context mContext, UpgradeDownloadAPKInfo upgradeDownloadAPKInfo)
    {
        PreferencesUtils.putStringPreferences(mContext, PreferencesUtils.KEY_NEW_VERSION_NAME, upgradeDownloadAPKInfo.getVersionName());
        PreferencesUtils.putIntPreferences(mContext, PreferencesUtils.KEY_VERSION_CODE, upgradeDownloadAPKInfo.getVersionCode());
        PreferencesUtils.putLongPreferences(mContext, PreferencesUtils.KEY_NAME_FILE_SIZE, upgradeDownloadAPKInfo.getFileSize());
        PreferencesUtils.putStringPreferences(mContext, PreferencesUtils.KEY_FILE_MD5, upgradeDownloadAPKInfo.getMd5());
        PreferencesUtils.putStringPreferences(mContext, PreferencesUtils.KEY_IS_FORCE_UPGRADE, upgradeDownloadAPKInfo.getIsForceUpgrade());
    }
    
    private void deleteUpradeFiles(Context mContext)
    {
        long oldDownloadID = PreferencesUtils.getLongPreferences(mContext, PreferencesUtils.KEY_NAME_DOWNLOAD_ID);
        DownloadManagerUtil.getInstance(mContext).deleteDownloadFile(mContext, oldDownloadID);
        if (FileUtil.isFolderExist(EXTERNAL_DIR))
        {
            FileUtil.deleteAllFiles(Environment.getExternalStoragePublicDirectory(EXTERNAL_DIR));
        }
    }
    
    public ProgressDialog showUpgradeLoadAPKInfoDlg(Context context)
    {
        ProgressDialog upgradeLoadAPKInfoDlg = new ProgressDialog(context);
        upgradeLoadAPKInfoDlg.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        upgradeLoadAPKInfoDlg.setMessage(context.getString(R.string.notice_update));
        upgradeLoadAPKInfoDlg.show();
        upgradeLoadAPKInfoDlg.setCanceledOnTouchOutside(false);
        return upgradeLoadAPKInfoDlg;
    }
    
    public void createUpgradeChoiceDialog(final Context mContext, String messageText,
            final Boolean isDownloadComplete, final Boolean isManual,
            final UpgradeDownloadAPKInfo upgradeDownloadAPKInfo, final String downloadFileName,
            final String downloadURL)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(mContext, R.style.Theme_upgrade_dialog));
        builder.setIcon(android.R.drawable.ic_dialog_info);
        builder.setTitle(R.string.notice_title);
        
        ScrollView scrollView = new ScrollView(mContext);
        scrollView.setVerticalScrollBarEnabled(true);
        
        TextView textView = new TextView(mContext);
        scrollView.addView(textView);
        textView.setTextSize(14);
        textView.setText(messageText);
        builder.setView(scrollView);
        
        int confirmId = R.string.choice_comfirm;
        
        if (isDownloadComplete)
        {
            confirmId = R.string.choice_confirm_install;
        }
        
        builder.setPositiveButton(confirmId, new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface arg0, int arg1)
            {
                if (isDownloadComplete)
                {
                    new Thread(new IntallAPKRunnable(mContext)).start();
                }
                else
                {
                    downLoadfile(mContext, upgradeDownloadAPKInfo, downloadFileName, downloadURL);
                }
            }
        });
        builder.setNegativeButton(R.string.choice_cancle, new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface dialog, int which)
            {
                if (!isManual)
                {
                    String currentTime = DateFormatUtil.dateTimeToString(new Date(System.currentTimeMillis()), DATE_FORMAT);
                    PreferencesUtils.putStringPreferences(mContext, PreferencesUtils.KEY_USER_IGNORE_UPGRADE_TIME, currentTime);
                }
            }
        });
        builder.setCancelable(false);
        builder.show();
    }
    
    // 安装apk
    public void installAPK(Context mContext, Handler mHandler)
    {
        File file = getDownloadUpgradeFile(mContext);
        if (mContext == null || file == null || !file.exists())
        {
            mHandler.obtainMessage(UpgradeInstallStat.DOWNLOAD_FILE_NOT_EXIST).sendToTarget();
            return;
        }
        
        // 判断是否是增量升级
        if (!file.getName().contains("patch"))
        {
            install(mContext, file, mHandler);
        }
        else
        {
            String newVersionName = PreferencesUtils.getStringPreferences(mContext, PreferencesUtils.KEY_NEW_VERSION_NAME);
            int newVersionCode = PreferencesUtils.getIntPreferences(mContext, PreferencesUtils.KEY_VERSION_CODE);
            
            String dir = file.getParent() + File.separator;
            String oldAPKPath = dir + generateAPKName(ApplicationInfoUtil.getVerName(mContext), ApplicationInfoUtil.getVerCode(mContext));
            String newAPKPath = dir + generateAPKName(newVersionName, newVersionCode);
            
            // 拷贝旧的apk data/app
            PatchUtils.backupApplication(mContext.getPackageName(), oldAPKPath);
            
            // 合成apk
            int ret = PatchUtils.patch(oldAPKPath, newAPKPath, file.getPath());
            if (ret == 0)
            {
                install(mContext, new File(newAPKPath), mHandler);
            }
            else
            {
                mHandler.obtainMessage(UpgradeInstallStat.PATCH_APK_FAIL).sendToTarget();
            }
        }
    }
    
    public void install(Context context, File file, Handler mHandler)
    {
        long fileSize = PreferencesUtils.getLongPreferences(context, PreferencesUtils.KEY_NAME_FILE_SIZE);
        String md5 = PreferencesUtils.getStringPreferences(context, PreferencesUtils.KEY_FILE_MD5);
        
        // md5
        boolean res = FileUtil.checkFileMd5(file, md5);
        
        // 判断安装包是否被篡改
        if (res && fileSize == file.length())
        {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        }
        else
        {
            mHandler.obtainMessage(UpgradeInstallStat.DOWNLOAD_FILE_CHANGED).sendToTarget();
        }
    }
    
    public static String generatePatchFileName(final Context mContext, String newVersionName,
            int newVersionCode)
    {
        return "olaService_" + generateVersionName(ApplicationInfoUtil.getVerName(mContext), 
                ApplicationInfoUtil.getVerCode(mContext)) + "_" + generateVersionName(newVersionName, newVersionCode) + ".patch";
    }
    
    public static String generateAPKName(String newVersionName, int newVersionCode)
    {
        return "olaService_" + generateVersionName(newVersionName, newVersionCode) + ".apk";
    }
    
    public static String generateVersionName(String versionName, int versionCode)
    {
        StringBuilder versionBuilder = new StringBuilder();
        versionBuilder.append(versionName);
        versionBuilder.append(".");
        versionBuilder.append(versionCode);
        return versionBuilder.toString();
    }
    
    public void createForceUpgradeDialog(final Context mContext, final Activity activity)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setIcon(android.R.drawable.ic_dialog_info);
        builder.setTitle(R.string.notice_title);
        
        ScrollView scrollView = new ScrollView(mContext);
        scrollView.setVerticalScrollBarEnabled(true);
        
        TextView textView = new TextView(mContext);
        scrollView.addView(textView);
        textView.setTextSize(14);
        textView.setText(R.string.notice_isForceUpgrade);
        builder.setView(scrollView);
        
        builder.setPositiveButton(R.string.choice_confirm_install, new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface arg0, int arg1)
            {
                new Thread(new IntallAPKRunnable(mContext)).start();
                if (activity != null)
                {
                    activity.finish();
                }
            }
        });
        builder.setCancelable(false);
        builder.show();
    }
    
    public void createForceUpgradeAlertDialog(final Context mContext, final Activity activity)
    {
        if (null != mAlertDialog && mAlertDialog.isShowing())
        {
            return;
        }
        
        AlertDialog.Builder builder = new Builder(mContext);
        builder.setTitle(R.string.notice_title);
        builder.setMessage(R.string.notice_isForceUpgrade);
        builder.setPositiveButton(R.string.button_ok, new OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                dialog.dismiss();
                new Thread(new IntallAPKRunnable(mContext)).start();
            }
        });
        mAlertDialog = builder.create();
        mAlertDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        mAlertDialog.setCancelable(false);
        mAlertDialog.show();
    }
    
    public static boolean isNeedPatchUpgrade(Context context, String smartUpgradeVersion)
    {
        return StorageUtil.hasSDCard() && !isNeedUpgrade(smartUpgradeVersion, ApplicationInfoUtil.getVerName(context));
    }
    
    public Boolean isUpgradeDownloading(Context context)
    {
        long downloadId = PreferencesUtils.getLongPreferences(context, PreferencesUtils.KEY_NAME_DOWNLOAD_ID);
        boolean flag = DownloadManagerUtil.getInstance(context).getStatusById(downloadId) == DownloadManager.STATUS_RUNNING;
        ILog.d(TAG, "isUpgradeDownloading  flag: " + flag);
        return flag;
    }
    
    public void upgrade(Context mContext, Boolean isManual)
    {
        ILog.d(TAG, "upgrade isManual:" + isManual);
        
        // 判断是否在下载
        if (UpgradeManager.getInstance().isUpgradeDownloading(mContext))
        {
            // 手动下载给予提示
            if (isManual)
            {
                String isForceUpgrade = PreferencesUtils.getStringPreferences(mContext, PreferencesUtils.KEY_IS_FORCE_UPGRADE);
                int tipSourceId;
                if (UpgradeStrategy.ONLY_FORCE_UPGRADE.equals(isForceUpgrade))
                {
                    tipSourceId = R.string.notice_isForceUpgradedownload;
                }
                else
                {
                    tipSourceId = R.string.notice_isdownload;
                }
                Toast.makeText(mContext, tipSourceId, Toast.LENGTH_SHORT).show();
            }
        }
        else
        {
            // 自动下载点击取消之后，3天之后才会自动检测
            if (!isManual)
            {
                String userIgnoreUpgradeTime = PreferencesUtils.getStringPreferences(mContext, PreferencesUtils.KEY_USER_IGNORE_UPGRADE_TIME);
                if (userIgnoreUpgradeTime != null)
                {
                    Date ignoreTime = DateFormatUtil.stringToDate(userIgnoreUpgradeTime, UpgradeManager.DATE_FORMAT);
                    if (DateFormatUtil.diffDate(new Date(System.currentTimeMillis()), ignoreTime) < 3)
                    {
                        ILog.e(TAG, "Upgrade after 3 days");
                        return;
                    }
                }
            }
            
            // 判断网络是否连接
            if (NetworkUtil.isNetworkAvailable(mContext))
            {
                ILog.d(TAG, "Upgrade Network Available");
                new Thread(new UpgradeRunnable(Looper.getMainLooper(), isManual, mContext)).start();
            }
            else
            {
                showCheckUpgradeTips(mContext, R.string.notice_upgrade_no_network, isManual);
            }
        }
    }
    
    public void startUpgradeDownloadService(Context mContext)
    {
        long downloadId = PreferencesUtils.getLongPreferences(mContext, PreferencesUtils.KEY_NAME_DOWNLOAD_ID);
        
        int status = DownloadManagerUtil.getInstance(mContext).getStatusById(downloadId);
        ILog.d(TAG, "startUpgradeDownloadService  status : " + status);
        
        switch (status)
        {
        case DownloadManager.STATUS_RUNNING:
        case DownloadManager.STATUS_PAUSED:
        case DownloadManager.STATUS_PENDING:
            mContext.startService(new Intent(mContext, UpgradeDownloadService.class));
            break;
        default:
            break;
        }
    }
    
    public UpgradeDownloadAPKInfo getNewVersionInfo()
    {
        return mUpgradeDownloadAPKInfo;
    }
}
