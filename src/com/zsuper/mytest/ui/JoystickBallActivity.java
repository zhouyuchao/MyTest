package com.zsuper.mytest.ui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.zsuper.mytest.R;
import com.zsuper.mytest.constant.Constant;
import com.zsuper.mytest.device.DeviceListener;
import com.zsuper.mytest.device.DeviceManager;
import com.zsuper.mytest.device.MdnsDevice;
import com.zsuper.mytest.procotol.bean.JoystickInfo;
import com.zsuper.mytest.upgrade.PreferencesUtils;
import com.zsuper.mytest.upgrade.UpgradeManager;
import com.zsuper.mytest.utils.DateFormatUtil;
import com.zsuper.mytest.utils.ILog;
import com.zsuper.mytest.utils.Utils;
import com.zsuper.mytest.widget.slidingmenu.SlidingMenu;

/**
 * 游戏手柄
 * <功能描述>
 * @author  ZhouYuChao/907753
 * @version  [版本号, 2014-11-18]
 * @since  [产品/模块版本]
 */
public class JoystickBallActivity extends BaseActivity implements OnTouchListener, OnClickListener {
    private static final String TAG = JoystickBallActivity.class.getSimpleName();
    // SlidingFragmentActivity
    
    /** 选中设备 */
    private static final int MSG_DEVICE_CHOICE = 105;
    
    /**
     *  方向盘限制调节参数
     */
    private static final int RADIUS_OFFSET = 6;
    
    private Context mContext;
    private SharedPreferences mSharedPreferences;
    private ArrayList<String> mData;
    private MenuListAdapter mAdapter;
    // SlidingMenu
    private SlidingMenu mSlidinMenu;
    // 抽屉效果
    private DrawerLayout mDrawerLayout;
    
    private RelativeLayout mArrPad;
    private ImageView mArrView;
    private ImageButton mBtnSetting;
    private ImageButton mDevBtn;
    private ImageButton mBtnA;
    private ImageButton mBtnB;
    private ImageButton mBtnX;
    private ImageButton mBtnY;
    private Button mBtnStart;
    private Button mBtnSelect;
    
    private MdnsDevice mCurrDev;
    private Vibrator mVibrator;
    private SoundPool mSoundPool;
    private static int SOUND_ID;
    /** 震动开关 */
    private boolean mVibrateSwitch;
    /** 按键音开关 */
    private boolean mSoundSwitch;
    
    private int mArrHeight;   // 事件触发区域
    private int mArrWidht;    // 事件触发区域
    private int mArrPadEdge;  // 方向球绘制区域
    private int mArrBallEdge; // 方向球绘制区域
    
    private int mOldKeyCode;
    
    Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
            case MSG_DEVICE_CHOICE:
                if (null != mAdapter) {
                    ILog.d(TAG, "Device choice, update menu list");
                    mAdapter.notifyDataSetChanged();
                }
                break;
                
            default:
                break;
            }
        };
    };
    
    private DeviceListener mDevListener = new DeviceListener() {
        @Override
        public void onDeviceRemove(MdnsDevice device) {
        }
        
        @Override
        public void onDeviceAdd(MdnsDevice device) {
        }
        
        @Override
        public void onChioseDevice(MdnsDevice device)
        {
//            ILog.v(TAG, "onChioseDevice : " + device);
            Message msg = new Message();
            msg.what = MSG_DEVICE_CHOICE;
            msg.obj = device;
            mHandler.sendMessage(msg);
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "onCreate ...");
        setContentView(R.layout.activity_main); // activity_joystick_ball

        mContext = this;
        initView();
        initListView();
        initDrawerLayout();
        checkUpgrade();
        
        DeviceManager.newInstance().setContext(this);
        DeviceManager.newInstance().addDeviceListener(mDevListener);
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        Log.i(TAG, "onResume ...");
        initFeedback();
        
        mCurrDev = DeviceManager.newInstance().getChoiceDevice();
        if (null != mCurrDev) {
            mDevBtn.setBackgroundResource(R.drawable.joystick_bt_dev_con_states);
        } else {
            mDevBtn.setBackgroundResource(R.drawable.joystick_bt_dev_discon_states);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i(TAG, "onPause ...");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "onDestroy ...");
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        int keyCode = KeyEvent.KEYCODE_UNKNOWN;
        switch (v.getId()) {
        case R.id.arr_layout:
//             Log.d(TAG, "onTouch [Arrow]");
            
//            handleJoystickArrowTouch(event);
            handleTVArrowTouch(event);
            return true;

        case R.id.btn_up:  // Y键  --> 菜单
//            Log.d(TAG, "onClick [Y]");
            
//            handleJoystickTouch(event, JoystickInfo.JS_KEYCODE_ONE);
            keyCode = KeyEvent.KEYCODE_BUTTON_Y;
//            keyCode = KeyEvent.KEYCODE_MENU;
            break;
            
        case R.id.btn_left:  // X键  --> 主页
//            Log.d(TAG, "onTouch [X]");
            
//            handleJoystickTouch(event, JoystickInfo.JS_KEYCODE_THREE);
            keyCode = KeyEvent.KEYCODE_BUTTON_X;
//            keyCode = KeyEvent.KEYCODE_HOME;
            break;
            
        case R.id.btn_down:  // A键 --> 确定
//            Log.d(TAG, "onTouch [A]");
            
//            handleJoystickTouch(event, JoystickInfo.JS_KEYCODE_TWO);
            keyCode = KeyEvent.KEYCODE_BUTTON_A;
//            keyCode = KeyEvent.KEYCODE_DPAD_CENTER;
            break;

        case R.id.btn_right:  // B键 --> 返回
//          Log.d(TAG, "onTouch [B]");
            
//            handleJoystickTouch(event, JoystickInfo.JS_KEYCODE_FOUR);
            keyCode = KeyEvent.KEYCODE_BUTTON_B;
//            keyCode = KeyEvent.KEYCODE_BACK;
            break;

        default:
            return false;
        }
        handleTouch(keyCode, event.getAction());
        return false;
    }
    
    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
        case R.id.btn_settings:
            ILog.d(TAG, "onClick btn_settings");
            mDrawerLayout.openDrawer(Gravity.RIGHT);
            break;
        case R.id.btn_device:
            ILog.d(TAG, "onClick btn_device");
            startActivityWithAnim(DeviceActivity.class);
            break;
        default:
            break;
        }
    }
    
    private void initView() {
        Log.i(TAG, "initView ...");
        mArrPad = (RelativeLayout) findViewById(R.id.arr_layout);
        mArrView = (ImageView) findViewById(R.id.arrBackground);
        
        mBtnSetting = (ImageButton) findViewById(R.id.btn_settings);
//        mBtnSetting.setVisibility(View.GONE);
        
        mDevBtn = (ImageButton) findViewById(R.id.btn_device);

        RelativeLayout keyGroup = (RelativeLayout) findViewById(R.id.key_group);
        mBtnA = (ImageButton) keyGroup.findViewById(R.id.btn_down);
        mBtnB = (ImageButton) keyGroup.findViewById(R.id.btn_right);
        mBtnX = (ImageButton) keyGroup.findViewById(R.id.btn_left);
        mBtnY = (ImageButton) keyGroup.findViewById(R.id.btn_up);
        
        RelativeLayout startBtnGroup = (RelativeLayout) findViewById(R.id.start_btn_group);
        mBtnStart = (Button) startBtnGroup.findViewById(R.id.btn_start);
        mBtnSelect = (Button) startBtnGroup.findViewById(R.id.btn_select);
        
        mArrPad.setOnTouchListener(this);
        mBtnSetting.setOnClickListener(this);
        mDevBtn.setOnClickListener(this);
        mBtnA.setOnTouchListener(this);
        mBtnB.setOnTouchListener(this);
        mBtnX.setOnTouchListener(this);
        mBtnY.setOnTouchListener(this);
        
        mBtnStart.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
//                Log.d(TAG, "onTouch -> mBtnStart");
                
//                handleJoystickTouch(event, JoystickInfo.JS_KEYCODE_START);
                handleTouch(KeyEvent.KEYCODE_BUTTON_START, event.getAction());
                return false;
            }
        });
        
        mBtnSelect.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
//                Log.d(TAG, "onTouch -> mBtnStart2");
                
//                handleJoystickTouch(event, JoystickInfo.JS_KEYCODE_HOME);
                handleTouch(KeyEvent.KEYCODE_BUTTON_SELECT, event.getAction());
                return false;
            }
        });
    }
    
    private void setBehindView() 
    {
        // set the Behind View
        LayoutInflater inflater = LayoutInflater.from(this);
        View view = inflater.inflate(R.layout.menulist, null, false);
        
        ListView list = (ListView) view.findViewById(R.id.menu_list);
        mData = getMenuData();
        mAdapter = new MenuListAdapter(this, mData);
        list.setAdapter(mAdapter);
        list.setOnItemClickListener(mMenuItemClickListener);
        
//        setBehindContentView(view);
    }
    
    private void initSlidingMenu()
    {
//        mSlidinMenu = getSlidingMenu();
        mSlidinMenu.setBehindWidth(500);
        mSlidinMenu.setFadeDegree(0.35f);
        mSlidinMenu.setBehindScrollScale(0.85f);
        mSlidinMenu.setMode(SlidingMenu.RIGHT);
        mSlidinMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_MARGIN);
    }
    
    /**
     * 获取菜单列表数据
     * <功能描述>
     * @return [参数说明]
     * @return ArrayList<String> [返回类型说明]
     * @exception throws [违例类型] [违例说明]
     */
    private ArrayList<String> getMenuData()
    {
        String[] menulist = getResources().getStringArray(R.array.menulist);
        ArrayList<String> menuArr = new ArrayList<String>(Arrays.asList(menulist));
        return menuArr;
    }
    
    private void initDrawerLayout()
    {
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
    }
    
    private void initListView()
    {
        ListView list = (ListView)findViewById(R.id.menu_list);
        mData = getMenuData();
        mAdapter = new MenuListAdapter(this, mData);
        list.setAdapter(mAdapter);
        list.setOnItemClickListener(mMenuItemClickListener);
    }
    
    /**
     * 按键反馈（震动、按键音）
     * @return void [返回类型说明]
     * @exception throws [违例类型] [违例说明]
     */
    private void initFeedback()
    {
        SharedPreferences sp = getSharedPreferences(Constant.SETTINGS_PREF_FILE, MODE_PRIVATE);
        mVibrateSwitch = sp.getBoolean(Constant.SETTINGS_VIBRATE_PREF, false);
        mSoundSwitch = sp.getBoolean(Constant.SETTINGS_SOUND_PREF, false);
        
        ILog.d(TAG, "initFeedback vibrate : " + mVibrateSwitch);
        ILog.d(TAG, "initFeedback sound : " + mSoundSwitch);
        
        if (mVibrateSwitch) 
        {
            mVibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        }
        
        if (mSoundSwitch) 
        {
            //第一个参数为同时播放数据流的最大个数，第二数据流类型，第三为声音质量
            mSoundPool = new SoundPool(10, AudioManager.STREAM_SYSTEM, 0);
            SOUND_ID = mSoundPool.load(this, R.raw.effect_tick, 1);
        }
    }
    
    /**
     * 检测升级
     * @return void [返回类型说明]
     * @exception throws [违例类型] [违例说明]
     */
    private void checkUpgrade()
    {
        ILog.i(TAG, "checkUpgrade");
        autoCheckUpgrade();
        UpgradeManager.getInstance().startUpgradeDownloadService(this);
    }
    
    /**
     * （电视键值发送）功能键触摸处理，发送按下和抬起消息
     * @param event
     * @param keyCode [参数说明]
     * @return void [返回类型说明]
     * @exception throws [违例类型] [违例说明]
     */
    private void handleTouch(int keyCode, int action) {
        if (action == MotionEvent.ACTION_DOWN) {
            playFeedback();
        }
        
        if (null == mCurrDev)
        {
            ILog.e(TAG, "Device is null, no need to send key");
            return;
        }
        
        if (action == MotionEvent.ACTION_DOWN)
                //|| action == MotionEvent.ACTION_UP)
        {
            // 发送电视键值
            sendTVKeyEvent(keyCode, action);
        }
    }
    
    /**
     * （电视键值发送）方向键处理
     * @param event [参数说明]
     * @return void [返回类型说明]
     * @exception throws [违例类型] [违例说明]
     */
    private void handleTVArrowTouch(MotionEvent event){
        int keyCode = JoystickInfo.JS_KEYCODE_UNKNOW;
        int action = event.getAction();
        
        switch (action) {
        case MotionEvent.ACTION_DOWN:
//            Log.d(TAG, "handleArrowTouch -> ACTION_DOWN");
            getArrAttribute();
            drawArrBall(MotionEvent.ACTION_DOWN, event.getX(), event.getY());
            
            keyCode = getJoystickArrowAction(event);
            mOldKeyCode = keyCode;
            break;
        case MotionEvent.ACTION_MOVE:
//            Log.d(TAG, "handleArrowTouch -> ACTION_MOVE");
            drawArrBall(MotionEvent.ACTION_MOVE, event.getX(), event.getY());
            
            keyCode = getJoystickArrowAction(event);
            if (keyCode == mOldKeyCode) {
                mOldKeyCode = keyCode;
                return ;
            }
            mOldKeyCode = keyCode;
            break;
        case MotionEvent.ACTION_UP:
//            Log.d(TAG, "handleArrowTouch -> ACTION_UP");
            drawArrBall(MotionEvent.ACTION_UP, mArrPadEdge / 2, mArrPadEdge / 2);
            
            mOldKeyCode = -1;
            keyCode = JoystickInfo.JS_KEYCODE_UNKNOW;
            break;
        default:
            return ;
        }
        
        if (action == MotionEvent.ACTION_DOWN) 
        {
            playFeedback();
        }
        
        sendDirectionKeyEvent(event, keyCode);
    }
    
    private void sendDirectionKeyEvent(MotionEvent event, int keyCode)
    {
        switch(keyCode){
        case JoystickInfo.JS_KEYCODE_UP:
            sendTVKeyEvent(KeyEvent.KEYCODE_DPAD_UP, MotionEvent.ACTION_DOWN);
//            sendTVKeyEvent(KeyEvent.KEYCODE_DPAD_RIGHT, MotionEvent.ACTION_UP);
//            sendTVKeyEvent(KeyEvent.KEYCODE_DPAD_LEFT, MotionEvent.ACTION_UP);
            break;
        case JoystickInfo.JS_KEYCODE_DOWN:
            sendTVKeyEvent(KeyEvent.KEYCODE_DPAD_DOWN, MotionEvent.ACTION_DOWN);
//            sendTVKeyEvent(KeyEvent.KEYCODE_DPAD_RIGHT, MotionEvent.ACTION_UP);
//            sendTVKeyEvent(KeyEvent.KEYCODE_DPAD_LEFT, MotionEvent.ACTION_UP);
            break;
        case JoystickInfo.JS_KEYCODE_LEFT:
            sendTVKeyEvent(KeyEvent.KEYCODE_DPAD_LEFT, MotionEvent.ACTION_DOWN);
//            sendTVKeyEvent(KeyEvent.KEYCODE_DPAD_UP, MotionEvent.ACTION_UP);
//            sendTVKeyEvent(KeyEvent.KEYCODE_DPAD_DOWN, MotionEvent.ACTION_UP);
            break;
        case JoystickInfo.JS_KEYCODE_RIGHT:
            sendTVKeyEvent(KeyEvent.KEYCODE_DPAD_RIGHT, MotionEvent.ACTION_DOWN);
//            sendTVKeyEvent(KeyEvent.KEYCODE_DPAD_UP, MotionEvent.ACTION_UP);
//            sendTVKeyEvent(KeyEvent.KEYCODE_DPAD_DOWN, MotionEvent.ACTION_UP);
            break;
            
//        case JoystickInfo.JS_KEYCODE_L_U:
//            sendTVKeyEvent(KeyEvent.KEYCODE_DPAD_UP, MotionEvent.ACTION_DOWN);
//            sendTVKeyEvent(KeyEvent.KEYCODE_DPAD_LEFT, MotionEvent.ACTION_DOWN);
//            break;
//        case JoystickInfo.JS_KEYCODE_U_R:
//            sendTVKeyEvent(KeyEvent.KEYCODE_DPAD_UP, MotionEvent.ACTION_DOWN);
//            sendTVKeyEvent(KeyEvent.KEYCODE_DPAD_RIGHT, MotionEvent.ACTION_DOWN);
//            break;
//        case JoystickInfo.JS_KEYCODE_D_L:
//            sendTVKeyEvent(KeyEvent.KEYCODE_DPAD_DOWN, MotionEvent.ACTION_DOWN);
//            sendTVKeyEvent(KeyEvent.KEYCODE_DPAD_LEFT, MotionEvent.ACTION_DOWN);
//            break;
//        case JoystickInfo.JS_KEYCODE_R_D:
//            sendTVKeyEvent(KeyEvent.KEYCODE_DPAD_RIGHT, MotionEvent.ACTION_DOWN);
//            sendTVKeyEvent(KeyEvent.KEYCODE_DPAD_DOWN, MotionEvent.ACTION_DOWN);
//            break;
//            
//        case JoystickInfo.JS_KEYCODE_UNKNOW:
//            sendTVKeyEvent(KeyEvent.KEYCODE_DPAD_UP, MotionEvent.ACTION_UP);
//            sendTVKeyEvent(KeyEvent.KEYCODE_DPAD_DOWN, MotionEvent.ACTION_UP);
//            sendTVKeyEvent(KeyEvent.KEYCODE_DPAD_RIGHT, MotionEvent.ACTION_UP);
//            sendTVKeyEvent(KeyEvent.KEYCODE_DPAD_LEFT, MotionEvent.ACTION_UP);
//            break;
        
        default:
            break;
        }
    }
    
    /**
     * （游戏手柄键值发送）功能键触摸处理，发送按下和抬起消息
     * @param event
     * @param keyCode 键值对应游戏手柄键值
     * @return void [返回类型说明]
     * @exception throws [违例类型] [违例说明]
     */
    private void handleJoystickTouch(MotionEvent event, int keyCode) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            playFeedback();
        }
        
        if (null == mCurrDev)
        {
            ILog.e(TAG, "Device is null, no need to send key");
            return ;
        }
        
        // YAHA 发送游戏手柄键值
        char[] action = JoystickInfo.getJoystickBtnAction(event.getAction(), keyCode);
//        Log.d(TAG, "handleJoystickTouch [" + keyCode + "]" + action.toString());
        if (event.getAction() == MotionEvent.ACTION_DOWN 
                || event.getAction() == MotionEvent.ACTION_UP) {
            sendJoystickEvent(action);
        }
    }
    
    /**
     * （游戏手柄键值发送）方向键处理
     * @param event [参数说明]
     * @return void [返回类型说明]
     * @exception throws [违例类型] [违例说明]
     */
    private void handleJoystickArrowTouch(MotionEvent event){
        int keyCode = JoystickInfo.JS_KEYCODE_UNKNOW;
        
        switch (event.getAction()) {
        case MotionEvent.ACTION_DOWN:
//            Log.d(TAG, "handleArrowTouch -> ACTION_DOWN");
            getArrAttribute();
            drawArrBall(MotionEvent.ACTION_DOWN, event.getX(), event.getY());
            
            keyCode = getJoystickArrowAction(event);
            mOldKeyCode = keyCode;
            break;
        case MotionEvent.ACTION_MOVE:
//            Log.d(TAG, "handleArrowTouch -> ACTION_MOVE");
            drawArrBall(MotionEvent.ACTION_MOVE, event.getX(), event.getY());
            
            keyCode = getJoystickArrowAction(event);
            if (keyCode == mOldKeyCode) {
                mOldKeyCode = keyCode;
                return ;
            }
            mOldKeyCode = keyCode;
            break;
        case MotionEvent.ACTION_UP:
//            Log.d(TAG, "handleArrowTouch -> ACTION_UP");
            drawArrBall(MotionEvent.ACTION_UP, mArrPadEdge / 2, mArrPadEdge / 2);
            
            mOldKeyCode = -1;
            keyCode = JoystickInfo.JS_KEYCODE_UNKNOW;
            break;
        default:
            return ;
        }
        
        if (event.getAction() == MotionEvent.ACTION_DOWN) 
        {
            playFeedback();
        }
        
        char[] action = JoystickInfo.getJoystickArrowAction(keyCode);
        if (null != mCurrDev) 
        {
            sendJoystickEvent(action);
        }
    }
    
    private void getArrAttribute(){
        mArrHeight = mArrPad.getHeight();
        mArrWidht = mArrPad.getWidth();
        mArrPadEdge = mArrHeight > mArrWidht ? mArrWidht : mArrHeight;
        mArrBallEdge = mArrView.getWidth() ;
//        Log.d(TAG, "mArrHeight : " + mArrHeight + ", mArrWidht : " + mArrWidht);
    }
    
    /**
     * 获取方向键KeyCode
     * @param event
     * @return [参数说明]
     * @return int [返回类型说明]
     * @exception throws [违例类型] [违例说明]
     */
    private int getJoystickArrowAction(MotionEvent event){
        int keyCode = JoystickInfo.JS_KEYCODE_UNKNOW;
        
        float x = event.getX();
        float y = event.getY();
//        Log.d(TAG, "getArrowAction " + x + ", " + y);
        
        float xCell = mArrWidht / 3;
        float yCell = mArrHeight / 3;
        
        if (x < xCell) {                        // x >= 0 && 
            if (y < yCell) {                    // y >= 0 && 
                keyCode = JoystickInfo.JS_KEYCODE_L_U;
            } else if (y >= yCell && y < yCell * 2) {
                keyCode = JoystickInfo.JS_KEYCODE_LEFT;
            } else if (y >= yCell * 2) {        //  && y <= mArrHeight
                keyCode = JoystickInfo.JS_KEYCODE_D_L;
            }
        } else if (x >= xCell && x < xCell * 2) {
            if (y < yCell) {                    // y >= 0 && 
                keyCode = JoystickInfo.JS_KEYCODE_UP;
            } else if (y >= yCell && y < yCell * 2) {
                keyCode = JoystickInfo.JS_KEYCODE_UNKNOW;
            } else if (y >= yCell * 2) {        //  && y <= mArrHeight
                keyCode = JoystickInfo.JS_KEYCODE_DOWN;
            }
        } else if (x >= xCell * 2) {            //  && x <= mArrWidht
            if (y < yCell) {                    // y >= 0 && 
                keyCode = JoystickInfo.JS_KEYCODE_U_R;
            } else if (y >= yCell && y < yCell * 2) {
                keyCode = JoystickInfo.JS_KEYCODE_RIGHT;
            } else if (y >= yCell * 2) {        //  && y <= mArrHeight
                keyCode = JoystickInfo.JS_KEYCODE_R_D;
            }
        }
        
//        Log.d(TAG, "getArrowAction keyCode:" + keyCode);
        return keyCode;
    }
    
    /**
     * 按键反馈
     * @return void [返回类型说明]
     * @exception throws [违例类型] [违例说明]
     */
    private void playFeedback()
    {
        vibrate();
        playVoice();
    }
    
    /**
     * 按键震动
     * @return void [返回类型说明]
     * @exception throws [违例类型] [违例说明]
     */
    private void vibrate(){
        if (mVibrateSwitch) 
        {
            mVibrator.vibrate(50);
        }
    }
    
    /**
     * 按键声音
     * @return void [返回类型说明]
     * @exception throws [违例类型] [违例说明]
     */
    private void playVoice()
    {
        if (mSoundSwitch) 
        {
            /**
             * 参数1：播放的音乐ID; 
             * 参数2：左声道音量 ; 
             * 参数3：右声道音量  ;
             * 参数4：优先级，0为最低  
             * 参数5：循环次数，0为不循环，-1为永远循环  ;
             * 参数6：回放速度，该值在0.5-2.0之间，1为正常速度。  
             */
            mSoundPool.play(SOUND_ID, 1, 1, 0, 0, 1);
        }
    }

    /**
     * 绘制方向滑动球
     * @return void [返回类型说明]
     * @exception throws [违例类型] [违例说明]
     */
    private void drawArrBall(int action, float x, float y){
        if (mArrView == null) {
            Log.e(TAG, "drawArrBall mArrView is null");
            return ;
        }
        
        // 转换为点击的点
        float touchX = x - mArrBallEdge / 2;
        float touchY = y - mArrBallEdge / 2;
        
        if (MotionEvent.ACTION_UP == action) {
            mArrView.setX(touchX);
            mArrView.setY(touchY);
            return ;
        }
        
        // 大圆盘半径
        float R = mArrPadEdge / 2;
        
        // 转换原点为圆心
        float convertX = x - R;
        float convertY = y - R;
        
        float limitR = R - mArrBallEdge / 2 + RADIUS_OFFSET; // 小球圆心到大球圆心的长度（限制半径长）
        double touchR = Math.sqrt(convertX * convertX + convertY * convertY); // 点击点的半径
        
        if (touchR > limitR) {
            double a = Math.atan((y - R) / (x - R));
//            Log.v(TAG, "drawArrBall a = " + a);
            if (x < R) {
                a += Math.PI;
            }
//            Log.e(TAG, "drawArrBall a = " + a);
            double h = limitR * Math.sin(a);
            double w = limitR * Math.cos(a);
            
            float limitY = (float) (R + h);
            float limitX = (float) (R + w);
            
            touchX = limitX - mArrBallEdge / 2;
            touchY = limitY - mArrBallEdge / 2;
        } 
        
        mArrView.setX(touchX);
        mArrView.setY(touchY);
    }
    
    /**
     * 发送游戏手柄键值消息
     * @param action [参数说明]
     * @return void [返回类型说明]
     * @exception throws [违例类型] [违例说明]
     */
    private void sendJoystickEvent(char[] action)
    {
        if (null == mCurrDev)
        {
            ILog.e(TAG, "sendJoyStickEvent Device is null, no need to send key");
            return ;
        }
        
        mCurrDev.adapter().joystick(mCurrDev, action);
    }
    
    /**
     * 发送电视按键键值
     * @param key
     * @param action [参数说明]
     * @return void [返回类型说明]
     * @exception throws [违例类型] [违例说明]
     */
    private void sendTVKeyEvent(int key, int action)
    {
        if (null == mCurrDev)
        {
            ILog.e(TAG, "sendTVKeyEvent Device is null, no need to send key");
            return ;
        }
        
        mCurrDev.adapter().key(mCurrDev, key);
    }
    
    /**
     * 菜单列表项点击事件
     */
    OnItemClickListener mMenuItemClickListener = new OnItemClickListener()
    {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id)
        {
            String menuItem = mData.get(position);
            ILog.v(TAG, "onItemClick -> " + menuItem);
            
            if (null == menuItem && TextUtils.isEmpty(menuItem)) {
                return ;
            }
            
            if (null != mDrawerLayout) {
                mDrawerLayout.closeDrawers();
            }
            
            if (menuItem.equals(mContext.getString(R.string.menu_device_title))) {
                startActivityWithAnim(DeviceActivity.class);
                
            } else if (menuItem.equals(mContext.getString(R.string.menu_settings))) {
                startActivityWithAnim(SettingsActivity.class);
                
            } else if (menuItem.equals(mContext.getString(R.string.menu_version_update))) {
                startActivityWithAnim(VersionActivity.class);
                
            } else if (menuItem.equals(mContext.getString(R.string.menu_about))) {
                startActivityWithAnim(AboutActivity.class);
            }
        }
    };
    
    /**
     * 启动新界面
     * @param cls [参数说明]
     * @return void [返回类型说明]
     * @exception throws [违例类型] [违例说明]
     */
    private void startActivityWithAnim(Class<?> cls)
    {
        Intent intent = new Intent();
        intent.setClass(JoystickBallActivity.this, cls);
        startActivity(intent);
        
        Utils.changeViewInAnim(JoystickBallActivity.this);
    }
    
    /**
     * 自动检测升级，首次安装后3天内不检测升级
     * @return void [返回类型说明]
     * @exception throws [违例类型] [违例说明]
     */
    private void autoCheckUpgrade()
    {
        ILog.i(TAG, "autoCheckUpgrade ");
        
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        if (!mSharedPreferences.getBoolean("isFirstLaunch", false))
        {
            Editor sharedata = mSharedPreferences.edit();
            sharedata.putBoolean("isFirstLaunch", true);
            sharedata.commit();
            
            String currentTime = DateFormatUtil.dateTimeToString(new Date(System.currentTimeMillis()), UpgradeManager.DATE_FORMAT);
            PreferencesUtils.putStringPreferences(this, PreferencesUtils.KEY_FIRST_LAUNCH_TIME, currentTime);
        }
        else
        {
            String userIgnoreUpgradeTime = PreferencesUtils.getStringPreferences(this, PreferencesUtils.KEY_FIRST_LAUNCH_TIME);
            if (userIgnoreUpgradeTime != null)
            {
                Date ignoreTime = DateFormatUtil.stringToDate(userIgnoreUpgradeTime, UpgradeManager.DATE_FORMAT);
                if (DateFormatUtil.diffDate(new Date(System.currentTimeMillis()), ignoreTime) < 3)
                {
                    return;
                }
            }
            UpgradeManager.getInstance().upgrade(this, false);
        }
    }
}
