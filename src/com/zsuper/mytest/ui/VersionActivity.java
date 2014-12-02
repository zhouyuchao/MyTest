package com.zsuper.mytest.ui;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.zsuper.mytest.R;
import com.zsuper.mytest.upgrade.UpgradeDownloadAPKInfo;
import com.zsuper.mytest.upgrade.UpgradeManager;
import com.zsuper.mytest.utils.ILog;
import com.zsuper.mytest.utils.Utils;

public class VersionActivity extends BaseActivity
{
    private static final String TAG = VersionActivity.class.getSimpleName();
    
    private UpgradeDownloadAPKInfo mRemoteVersionInfo;
    private TextView mNewVersion;
    private TextView mVersionSize;
    private TextView mVersionChanged;
    
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        ILog.i(TAG, "onCreate");
        setContentView(R.layout.activity_version);
        
        mRemoteVersionInfo = UpgradeManager.getInstance().getNewVersionInfo();
        
        initTitle();
        initView();
    }
    
    @Override
    protected void onResume()
    {
        super.onResume();
        ILog.i(TAG, "onResume");
        
        updateVersionContent();
    }
    
    @Override
    protected void onPause()
    {
        super.onPause();
        ILog.i(TAG, "onPause");
    }
    
    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        ILog.i(TAG, "onDestroy");
    }
    
    private void initTitle()
    {
        RelativeLayout titleLayout = (RelativeLayout) findViewById(R.id.version_title_layout);
        
        TextView title = (TextView) titleLayout.findViewById(R.id.title_content);
        title.setText(R.string.menu_version_update);
        
        ImageButton backBtn = (ImageButton) titleLayout.findViewById(R.id.title_back_btn);
        backBtn.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                finishWithAnim();
                Utils.changeViewOutAnim(VersionActivity.this);
            }
        });
    }
    
    private void initView()
    {
        ILog.i(TAG, "initView");
        mNewVersion = (TextView)findViewById(R.id.version_new);
        mVersionSize = (TextView)findViewById(R.id.version_size);
        mVersionChanged = (TextView)findViewById(R.id.version_changed_content);
        
        Button updateBtn = (Button)findViewById(R.id.version_update);
        updateBtn.setOnClickListener(mUpdateBtnListener);
    }
    
    @Override
    public void onBackPressed()
    {
        finishWithAnim();
    }
    
    private void finishWithAnim()
    {
        finish();
        Utils.changeViewOutAnim(VersionActivity.this);
    }
    
    /**
     * 更新新版本信息
     * @return void [返回类型说明]
     * @exception throws [违例类型] [违例说明]
     */
    private void updateVersionContent()
    {
        if (null == mRemoteVersionInfo) 
        {
            ILog.e(TAG, "updateVersionContent is null");
            return ;
        }
        
        String versionCode = mRemoteVersionInfo.getVersionName() + "."+ mRemoteVersionInfo.getVersionCode();
        long size = mRemoteVersionInfo.getFileSize();
        String changed = mRemoteVersionInfo.getUpgradeIntroduce();
        
        mNewVersion.setText(getString(R.string.version_new) + versionCode);
        mVersionSize.setText(getString(R.string.version_size) + Utils.bytes2kb(size));
        mVersionChanged.setText(changed);
    }
    
    OnClickListener mUpdateBtnListener = new OnClickListener()
    {
        @Override
        public void onClick(View v)
        {
            ILog.d(TAG, "onClick UPDATE ");
            UpgradeManager.getInstance().upgrade(VersionActivity.this, true);
        }
    };
}
