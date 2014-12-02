package com.zsuper.mytest.widget;

import java.util.ArrayList;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

import com.zsuper.mytest.R;
import com.zsuper.mytest.device.DeviceManager;
import com.zsuper.mytest.device.MdnsDevice;
import com.zsuper.mytest.utils.ILog;

public class DevViewManager
{
    private static final String TAG = DevViewManager.class.getSimpleName();
    
    // 预置的设备图标显示位置坐标组
    private int[] devX = {345, 841, 595, 300, 886, 435, 751};
    private int[] devY = {146, 146, 32, 361, 361, 558, 558};
    
    /** 设备状态：已连接 */
    public static final int DEV_STATE_CONNECTED = 0;
    /** 设备状态：未连接（已断开） */
    public static final int DEV_STATE_DISCONNECT = 1;
    /** 设备状态：正在连接中 */
    public static final int DEV_STATE_CONNECTING = 2;
    
    private Context mContext;
    private ViewGroup mRootView;
    private ArrayList<MdnsDevice> mShownDevList = new ArrayList<MdnsDevice>();
    
    public DevViewManager(Context context, ViewGroup parent)
    {
        mContext = context;
        mRootView = parent;
    }
    
    /**
     * 添加设备
     * @param device [参数说明]
     * @return void [返回类型说明]
     * @exception throws [违例类型] [违例说明]
     */
    public void addDeviceView(final MdnsDevice device)
    {
        if (isAreadyExist(device)) {
            ILog.w(TAG, "addDeviceView : device has shown");
            return ;
        }
        
        int index = mShownDevList.size();
        ILog.d(TAG, "addDeviceView index : " + index);
        
        addDeviceView(device, index);
    }
    
    /**
     * 添加设备
     * @param device 设备
     * @param index 设备索引
     * @return void [返回类型说明]
     * @exception throws [违例类型] [违例说明]
     */
    private void addDeviceView(final MdnsDevice device, final int index)
    {
        final String devName = device.getName();
        
        mShownDevList.add(device);
        
        TextView devView = new TextView(mContext);
        devView.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.dev_discon_btn_selector, 0, 0);
        devView.setText(devName);
        devView.setTextColor(mContext.getResources().getColor(R.color.text_color_normal));
        devView.setTextSize(10);
        devView.setGravity(Gravity.CENTER_HORIZONTAL);
        devView.setClickable(true);
        devView.setId(index);
        devView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                ILog.d(TAG, "onClick device : " + devName);
                String devIp = device.getIp();
                
                MdnsDevice choiceDev = DeviceManager.newInstance().getChoiceDevice();
                if (null != choiceDev && devIp.equals(choiceDev.getIp()) && devName.equals(choiceDev.getName())) {
                    ILog.d(TAG, "onClick device has choice");
                    DeviceManager.newInstance().setChoiceDevice(null);
                    return ;
                }
                
                updateDevState(device, DEV_STATE_CONNECTING);
                DeviceManager.newInstance().setChoiceDevice(device);
            }
        });
        
        LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        params.leftMargin = devX[index];
        params.topMargin = devY[index];
        mRootView.addView(devView, params);
    }
    
    /**
     * 判断界面是否已经添加该设备
     * @param device
     * @return [参数说明]
     * @return boolean [返回类型说明]
     * @exception throws [违例类型] [违例说明]
     */
    private boolean isAreadyExist(MdnsDevice device)
    {
        for (MdnsDevice dev : mShownDevList) 
        {
            if (null != dev && dev.equals(device)) 
            {
                return true;
            }
        }
        return false;
    }
    
    /**
     * 删除设备
     * @param devName [参数说明]
     * @return void [返回类型说明]
     * @exception throws [违例类型] [违例说明]
     */
    public void removeDeviceView(MdnsDevice device)
    {
        int index = mShownDevList.indexOf(device);
        
        TextView devView = (TextView) mRootView.findViewById(index);
        if (null == devView) {
            return;
        }
        
        mRootView.removeView(mRootView.findViewById(index));
    }
    
    /**
     * 删除所有设备
     * @return void [返回类型说明]
     * @exception throws [违例类型] [违例说明]
     */
    public void removeAllDeviceView()
    {
        if (null == mShownDevList) {
            ILog.e(TAG, "removeAllDeviceView devList is empty...");
            return ;
        }
        
        int devCount = mShownDevList.size();
        for (int i = 0; i < devCount; i++) {
            mRootView.removeView(mRootView.findViewById(i));
        }
    }
    
    /**
     * 更新所有设备状态
     * @return void [返回类型说明]
     * @exception throws [违例类型] [违例说明]
     */
    public void updateAllDevState()
    {
        MdnsDevice choiceDev = DeviceManager.newInstance().getChoiceDevice();
        
        int count = mShownDevList.size();
        for (int index = 0; index < count; index++) {
            if (mShownDevList.contains(choiceDev)) {
                updateDevState(index, DEV_STATE_CONNECTED);
            } else {
                updateDevState(index, DEV_STATE_DISCONNECT);
            }
        }
    }
    
    /**
     * 更新设备状态
     * @param state 
     * @param devName 
     * @return void [返回类型说明]
     * @exception throws [违例类型] [违例说明]
     */
    public void updateDevState(MdnsDevice device, int state)
    {
        int index = mShownDevList.indexOf(device);
        updateDevState(index, state);
    }
    
    private void updateDevState(int index, int state)
    {
        TextView devView = (TextView)mRootView.findViewById(index);
        if (null == devView) {
            return ;
        }
        
        if (state == DEV_STATE_CONNECTED) {
            devView.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.device_connected, 0, 0);
        } else if (state == DEV_STATE_CONNECTING) {
            devView.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.device_connecting, 0, 0);
        } else if (state == DEV_STATE_DISCONNECT) {
            devView.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.dev_discon_btn_selector, 0, 0);
        }
    }
    
    
}
