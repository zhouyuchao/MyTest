package com.zsuper.mytest.ui;

import java.util.ArrayList;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AlphaAnimation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.zsuper.mytest.R;
import com.zsuper.mytest.device.DeviceListener;
import com.zsuper.mytest.device.DeviceManager;
import com.zsuper.mytest.device.MdnsDevice;
import com.zsuper.mytest.device.MdnsSearchManager;
import com.zsuper.mytest.utils.ILog;
import com.zsuper.mytest.utils.Utils;
import com.zsuper.mytest.widget.DevViewManager;

/**
 * 设备发现界面
 * <功能描述>
 * @author  ZhouYuChao/907753
 * @version  [版本号, 2014-11-20]
 * @since  [产品/模块版本]
 */
public class DeviceActivity extends BaseActivity
{
    private static final String TAG = DeviceActivity.class.getSimpleName();
    
    /**
     * 每个动画的播放时间间隔
     */
    private static final int ANIMATION_EACH_OFFSET = 600;
    
    /** 显示动画2 */
    private static final int MSG_ANIM_TWO = 100;
    /** 显示动画3 */
    private static final int MSG_ANIM_THREE = 101;
    /** 显示动画4 */
    private static final int MSG_ANIM_FOUR = 102;
    /** 添加设备 */
    private static final int MSG_DEVICE_ADD = 103;
    /** 移除设备 */
    private static final int MSG_DEVICE_REMOVE = 104;
    /** 选中设备 */
    private static final int MSG_DEVICE_CHOICE = 105;
    
    // --------------- 搜索动画 --------------- //
    private AnimationSet mAnimSetOne;
    private AnimationSet mAnimSetTwo;
    private AnimationSet mAnimSetThree;
    private AnimationSet mAnimSetFour;
    private ImageView mWaveOne;
    private ImageView mWaveTwo;
    private ImageView mWaveThree;
    private ImageView mWaveFour;
    /** 是否已开始搜索动画 */
    private boolean hasAnimStarted;
    
    private RelativeLayout mRootView;
    private Button mSearchBtn;
    private TextView mCurrentWifi;
    
    /**
     * 设备发现
     */
    private MdnsSearchManager mMdnsManager;
    private DevViewManager mDevViewMgr;
    private ArrayList<MdnsDevice> mDevList;
    
    Handler mHandler = new Handler()
    {
        public void handleMessage(Message msg) {
            switch (msg.what) {
            case MSG_ANIM_TWO:
                if (null != mWaveTwo) {
                    mWaveTwo.startAnimation(mAnimSetTwo);
                }
                break;
               
            case MSG_ANIM_THREE:
                if (null != mWaveThree) {
                    mWaveThree.startAnimation(mAnimSetThree);
                }
                break;
            
            case MSG_ANIM_FOUR:
                if (null != mWaveFour) {
                    mWaveFour.startAnimation(mAnimSetFour);
                }
                break;
                
            case MSG_DEVICE_ADD:
                if (null != mDevViewMgr)
                {
                    MdnsDevice device = (MdnsDevice)msg.obj;
                    mDevViewMgr.addDeviceView(device);
                }
                break;
                
            case MSG_DEVICE_REMOVE:
                if (null != mDevViewMgr)
                {
                    MdnsDevice device = (MdnsDevice)msg.obj;
                    mDevViewMgr.removeDeviceView(device);
                }
                break;
                
            case MSG_DEVICE_CHOICE:
                if (null != mDevViewMgr)
                {
                    MdnsDevice device = (MdnsDevice)msg.obj;
                    if (null != device) {
                        // 选中设备（设备连接）
                        mDevViewMgr.updateDevState(device, DevViewManager.DEV_STATE_CONNECTED);
                    } else {
                        mDevViewMgr.updateAllDevState();
                    }
                }
                break;
                
            default:
                break;
            }
        };
    };
    
    private DeviceListener mDevListener = new DeviceListener()
    {
        @Override
        public void onDeviceRemove(MdnsDevice device)
        {
            ILog.w(TAG, "onDeviceRemove : " + device);
            Message msg = new Message();
            msg.what = MSG_DEVICE_REMOVE;
            msg.obj = device;
            mHandler.sendMessage(msg);
        }
        
        @Override
        public void onDeviceAdd(MdnsDevice device)
        {
            ILog.v(TAG, "onDeviceAdd : " + device);
            Message msg = new Message();
            msg.what = MSG_DEVICE_ADD;
            msg.obj = device;
            mHandler.sendMessage(msg);
        }
        
        @Override
        public void onChioseDevice(MdnsDevice device)
        {
            ILog.v(TAG, "onChioseDevice : " + device);
            Message msg = new Message();
            msg.what = MSG_DEVICE_CHOICE;
            msg.obj = device;
            mHandler.sendMessage(msg);
        }
    };
    
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        ILog.i(TAG, "onCreate");
        setContentView(R.layout.activity_device);
        
        initAnim();
        initView();
        initDevView();
        
        mMdnsManager = new MdnsSearchManager(this);
        DeviceManager.newInstance().addDeviceListener(mDevListener);
    }
    
    @Override
    protected void onResume()
    {
        super.onResume();
        ILog.i(TAG, "onResume");
        
        if (null != mMdnsManager && mMdnsManager.isPausing()) {
            startSearch();
        }
    }
    
    @Override
    protected void onPause()
    {
        super.onPause();
        ILog.i(TAG, "onPause");
        
        if (null != mMdnsManager && mMdnsManager.isSearching()) {
            pauseSearch();
        }
    }
    
    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        ILog.i(TAG, "onDestroy");
        
        if (null != mMdnsManager) {
            mMdnsManager.stopDiscovery();
            mMdnsManager.release();
        }
    }
    
    private void initAnim()
    {
        hasAnimStarted = false;
        
        mAnimSetOne = getAnimationSet();
        mAnimSetTwo = getAnimationSet();
        mAnimSetThree = getAnimationSet();
        mAnimSetFour = getAnimationSet();
    }
    
    private void initView()
    {
        ILog.i(TAG, "initView");
        mRootView = (RelativeLayout)findViewById(R.id.device_root_view);
        
        ImageButton backBtn = (ImageButton)findViewById(R.id.device_back_btn);
        backBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                finishWithAnim();
            }
        });
        
        mSearchBtn = (Button)findViewById(R.id.device_search);
        mSearchBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isWifiAvailable()) {
                    ILog.e(TAG, "SearchClick wifi is inavilable");
                    return ;
                }
                
                if (hasAnimStarted) {
                    stopSearch();
                } else {
                    startSearch();
                }
            }
        });
        
        mCurrentWifi = (TextView)findViewById(R.id.device_current_wifi);
        mCurrentWifi.setText(getString(R.string.device_current_wifi) + getCurrentWifiName());
        
        mWaveOne = (ImageView)findViewById(R.id.device_wave1);
        mWaveTwo = (ImageView)findViewById(R.id.device_wave2);
        mWaveThree = (ImageView)findViewById(R.id.device_wave3);
        mWaveFour = (ImageView)findViewById(R.id.device_wave4);
    }
    
    private void initDevView()
    {
        mDevViewMgr = new DevViewManager(this, mRootView);
        
        // 如果已经有设备，则添加显示设备
        mDevList = DeviceManager.newInstance().getMdnsList();
        ILog.d(TAG, "initDevView " + mDevList);
        if (null != mDevList && !mDevList.isEmpty())
        {
            for (MdnsDevice device : mDevList)
            {
                mDevViewMgr.addDeviceView(device);
            }
            mDevViewMgr.updateAllDevState();
        }
    }
    
    @Override
    public void onBackPressed()
    {
        finishWithAnim();
    }
    
    private void finishWithAnim()
    {
        ILog.i(TAG, "finishWithAnim");
        stopSearch();
        
        finish();
        Utils.changeViewOutAnim(DeviceActivity.this);
    }
    
    /**
     * 扫描动画
     * <功能描述>
     * @return [参数说明]
     * @return AnimationSet [返回类型说明]
     * @exception throws [违例类型] [违例说明]
     */
    private AnimationSet getAnimationSet()
    {
        ILog.i(TAG, "getAnimationSet");
        AnimationSet animSet = new AnimationSet(true);
        
        ScaleAnimation scale = new ScaleAnimation(1f, 6.0f, 1f, 6.0f, ScaleAnimation.RELATIVE_TO_SELF, 0.5f, ScaleAnimation.RELATIVE_TO_SELF, 0.5f);
        scale.setDuration(ANIMATION_EACH_OFFSET * 6);
        scale.setRepeatCount(-1);
        animSet.addAnimation(scale);
        
        AlphaAnimation alpha = new AlphaAnimation(1, 0.0f);
        alpha.setRepeatCount(-1);
        alpha.setDuration(ANIMATION_EACH_OFFSET * 6);
        animSet.addAnimation(alpha);
        return animSet;
    }
    
    /**
     * 获取wifi ssid
     * @return [参数说明]
     * @return String [返回类型说明]
     * @exception throws [违例类型] [违例说明]
     */
    private String getCurrentWifiName()
    {
        String name = "";
        
        if (isWifiAvailable()) {
            WifiManager wifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);
            if (!wifi.isWifiEnabled())
            {
                Toast.makeText(this, R.string.device_no_wifi_tips, Toast.LENGTH_LONG).show();
            }
            
            WifiInfo info = wifi.getConnectionInfo();
            name = info.getSSID();
            ILog.v(TAG, "getCurrentWifiName : " + name);
            
        } else {
            name = getString(R.string.menu_device_no_connect);
        }
        
        return name;
    }
    
    /**
     * 判断wifi是否可用和wifi是否已连接
     * @return [参数说明]
     * @return boolean [返回类型说明]
     * @exception throws [违例类型] [违例说明]
     */
    private boolean isWifiAvailable()
    {
        ConnectivityManager connMgr = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifi =connMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (!wifi.isAvailable()) {
            Toast.makeText(this, R.string.device_no_wifi_tips, Toast.LENGTH_LONG).show();
            return false;
        }
        
        if (!wifi.isConnected()) {
            Toast.makeText(this, R.string.device_discon_wifi_tips, Toast.LENGTH_LONG).show();
            return false;
        }
        
        return true;
    }
    
    /**
     * 开始搜索设备
     * @return void [返回类型说明]
     * @exception throws [违例类型] [违例说明]
     */
    private void startSearch() 
    {
        ILog.i(TAG, "startSearch");
        startWaveAnim();
        
        if (null != mMdnsManager)
        {
            mMdnsManager.startDiscovery();
        }
    }
    
    /**
     * 暂停搜索
     * @return void [返回类型说明]
     * @exception throws [违例类型] [违例说明]
     */
    private void pauseSearch()
    {
        ILog.i(TAG, "stopSearch");
        if (null != mMdnsManager)
        {
            mMdnsManager.pauseDiscovery();
        }
        
        stopWaveAnim();
    }
    
    /**
     * 停止搜索设备
     * @return void [返回类型说明]
     * @exception throws [违例类型] [违例说明]
     */
    private void stopSearch()
    {
        ILog.i(TAG, "stopSearch");
        if (null != mMdnsManager)
        {
            mMdnsManager.stopDiscovery();
        }
        
        stopWaveAnim();
    }
    
    /**
     * 显示动画
     * @return void [返回类型说明]
     * @exception throws [违例类型] [违例说明]
     */
    private void startWaveAnim()
    {
        ILog.i(TAG, "startWaveAnim");
        
        if (null != mSearchBtn) 
        {
            mSearchBtn.setText(R.string.device_searching);
        }
        
        hasAnimStarted = true;
        if (null != mWaveOne){
            mWaveOne.startAnimation(mAnimSetOne);
        }
        
        mHandler.sendEmptyMessageDelayed(MSG_ANIM_TWO, (int)(ANIMATION_EACH_OFFSET * 1.5));
        mHandler.sendEmptyMessageDelayed(MSG_ANIM_THREE, ANIMATION_EACH_OFFSET * 3);
        mHandler.sendEmptyMessageDelayed(MSG_ANIM_FOUR, (int)(ANIMATION_EACH_OFFSET * 4.5));
    }
    
    /**
     * 取消动画
     * @return void [返回类型说明]
     * @exception throws [违例类型] [违例说明]
     */
    private void stopWaveAnim()
    {
        ILog.i(TAG, "stopWaveAnim");
        hasAnimStarted = false;
        
        if (null != mSearchBtn) 
        {
            mSearchBtn.setText(R.string.device_search);
        }
        
        if (mHandler.hasMessages(MSG_ANIM_TWO)) {
            mHandler.removeMessages(MSG_ANIM_TWO);
        }
        
        if (mHandler.hasMessages(MSG_ANIM_THREE)) {
            mHandler.removeMessages(MSG_ANIM_THREE);
        }
        
        if (mHandler.hasMessages(MSG_ANIM_FOUR)) {
            mHandler.removeMessages(MSG_ANIM_FOUR);
        }
        
        if (null != mWaveOne) {
            mWaveOne.clearAnimation();
        }
        
        if (null != mWaveTwo) {
            mWaveTwo.clearAnimation();
        }
        
        if (null != mWaveThree) {
            mWaveThree.clearAnimation();
        }
        
        if (null != mWaveFour) {
            mWaveFour.clearAnimation();
        }
    }
}
