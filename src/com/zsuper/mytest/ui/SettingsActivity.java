package com.zsuper.mytest.ui;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.zsuper.mytest.R;
import com.zsuper.mytest.constant.Constant;
import com.zsuper.mytest.utils.ILog;
import com.zsuper.mytest.utils.Utils;
import com.zsuper.mytest.widget.SlipButton;
import com.zsuper.mytest.widget.SlipButton.OnChangedListener;

/**
 * 功能设置界面
 * @author  ZhouYuChao/907753
 * @version  [版本号, 2014-11-24]
 * @since  [产品/模块版本]
 */
public class SettingsActivity extends BaseActivity
{
    private static final String TAG = SettingsActivity.class.getSimpleName();
    
    private SlipButton mVibrateBtn;
    private SlipButton mSoundBtn;
    
    private SharedPreferences sp;
    
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        ILog.i(TAG, "onCreate ");
        setContentView(R.layout.activity_settings);
        
        sp = getSharedPreferences(Constant.SETTINGS_PREF_FILE, MODE_PRIVATE);
        
        initTitle();
        initView();
    }
    
    @Override
    protected void onResume()
    {
        super.onResume();
        ILog.i(TAG, "onResume ");
        
    }
    
    @Override
    protected void onPause()
    {
        super.onPause();
        ILog.i(TAG, "onPause ");
    }
    
    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        ILog.i(TAG, "onDestroy ");
    }
    
    private void initTitle()
    {
        ILog.i(TAG, "initTitle ");
        RelativeLayout titleLayout = (RelativeLayout) findViewById(R.id.settings_title_layout);
        
        TextView title = (TextView) titleLayout.findViewById(R.id.title_content);
        title.setText(R.string.menu_settings);
        
        ImageButton backBtn = (ImageButton) titleLayout.findViewById(R.id.title_back_btn);
        backBtn.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                finishWithAnim();
                Utils.changeViewOutAnim(SettingsActivity.this);
            }
        });
    }
    
    private void initView() 
    {
        // ---------- 按键震动反馈  ---------- //
        RelativeLayout vibrateLayout = (RelativeLayout)findViewById(R.id.settings_vibrate_layout);
        
        TextView vibrateTips = (TextView)vibrateLayout.findViewById(R.id.settings_item_title);
        vibrateTips.setText(R.string.settings_vibrate_title);
        
        mVibrateBtn = (SlipButton)vibrateLayout.findViewById(R.id.settings_item_switch);
        mVibrateBtn.setOnChangedListener(new OnChangedListener()
        {
            @Override
            public void onCheckStateChanged(boolean check)
            {
//                ILog.v(TAG, "Vibrate state : " + check);
                savePerferences(Constant.SETTINGS_VIBRATE_PREF, check);
            }
        });
        
        if (sp != null) {
            boolean check = sp.getBoolean(Constant.SETTINGS_VIBRATE_PREF, false);
            mVibrateBtn.setCheck(check);
            ILog.v(TAG, "init vibrate check state : " + check);
        }
        
        // ---------- 按键声音反馈  ---------- //
        RelativeLayout soundLayout = (RelativeLayout)findViewById(R.id.settings_sound_layout);
        
        TextView soundTips = (TextView)soundLayout.findViewById(R.id.settings_item_title);
        soundTips.setText(R.string.settings_sound_title);
        
        mSoundBtn = (SlipButton)soundLayout.findViewById(R.id.settings_item_switch);
        mSoundBtn.setOnChangedListener(new OnChangedListener()
        {
            @Override
            public void onCheckStateChanged(boolean check)
            {
//                ILog.v(TAG, "Sound state : " + check);
                savePerferences(Constant.SETTINGS_SOUND_PREF, check);
            }
        });
        
        if (sp != null) {
            boolean check = sp.getBoolean(Constant.SETTINGS_SOUND_PREF, false);
            mSoundBtn.setCheck(check);
            ILog.v(TAG, "init sound check state : " + check);
        }
    }
    
    private void savePerferences(String key, boolean value)
    {
        Editor editor = sp.edit();
        editor.putBoolean(key, value);
        editor.commit();
    }
    
    @Override
    public void onBackPressed()
    {
        finishWithAnim();
    }
    
    private void finishWithAnim()
    {
        finish();
        Utils.changeViewOutAnim(SettingsActivity.this);
    }
}
