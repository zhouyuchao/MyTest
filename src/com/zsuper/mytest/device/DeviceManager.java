package com.zsuper.mytest.device;

import java.util.ArrayList;

import android.content.Context;

import com.zsuper.mytest.db.DeviceHistoryListImpl;
import com.zsuper.mytest.utils.ILog;
import com.zsuper.mytest.utils.Utils;

public class DeviceManager
{
    private static final String TAG = DeviceManager.class.getSimpleName();
    
    /**
     * mdns list
     */
    private ArrayList<MdnsDevice> mMdnsList = new ArrayList<MdnsDevice>();
    private ArrayList<DeviceListener> devListenerList;
    private MdnsDevice choiceDevice = null;
    private Context mContext;
    
    private static DeviceManager mDeviceManager;
    public static DeviceManager newInstance()
    {
        if (null == mDeviceManager)
        {
            mDeviceManager = new DeviceManager();
        }
        return mDeviceManager;
    }
    
    private DeviceManager()
    {
    }
    
    public void setContext(Context context)
    {
        this.mContext = context;
    }
    
    public void addDeviceListener(DeviceListener deviceStateListener)
    {
        if (null == devListenerList)
        {
            devListenerList = new ArrayList<DeviceListener>();
        }
        if (!devListenerList.contains(deviceStateListener))
        {
            devListenerList.add(deviceStateListener);
        }
    }
    
    public void removeDeviceListener(DeviceListener deviceStateListener)
    {
        if (null != devListenerList && devListenerList.contains(deviceStateListener))
        {
            devListenerList.remove(deviceStateListener);
        }
    }
    
    /**
     * 添加一个mdns设备 mac 1:不为空怎以mac为标识 2:空则以ip和name为标识
     * @param mdnsDevice
     */
    public synchronized boolean addMdnsDevice(MdnsDevice mdnsDevice)
    {
        if (null == mdnsDevice)
        {
            ILog.e(TAG, "device is null");
            return false;
        }
        
        boolean isAdd = false;
        
        if (null == this.mMdnsList)
        {
            mMdnsList = new ArrayList<MdnsDevice>();
        }
        
        if (mMdnsList.isEmpty())
        {
            isAdd = mMdnsList.add(mdnsDevice);
        }
        else
        {
            ArrayList<MdnsDevice> removeMdnsList = new ArrayList<MdnsDevice>();
            for (MdnsDevice dev : mMdnsList)
            {
                if (null != dev && dev.equals(mdnsDevice))
                {
                    removeMdnsList.add(dev);
                }
            }
            mMdnsList.removeAll(removeMdnsList);
            isAdd = mMdnsList.add(mdnsDevice);
        }
        
        if (isAdd)
        {
            //通知更新设备更新
            if (null != devListenerList && devListenerList.size() > 0)
            {
                for (DeviceListener listener : devListenerList)
                {
                    listener.onDeviceAdd(mdnsDevice);
                }
            }
            
            // 连接历史记录设备
            connectHistoryDevice(mContext, mdnsDevice, Utils.getWifiSsid(mContext));
        }
        return isAdd;
    }
    
    /**
     * 移出一个mdns设备 mac 1:不为空怎以mac为标识 2:空则以ip和name为标识 TODO
     * @param mdnsDevice
     */
    public synchronized boolean removeMdnsDevice(MdnsDevice mdnsDevice)
    {
        boolean isRemove = false;
        if (null != mMdnsList && mMdnsList.size() > 0 && null != mdnsDevice)
        {
            ArrayList<MdnsDevice> removeList = new ArrayList<MdnsDevice>();
            for (MdnsDevice dev : mMdnsList)
            {
                if (null != dev && dev.equals(mdnsDevice))
                {
                    removeList.add(dev);
                }
            }
            isRemove = mMdnsList.removeAll(removeList);
        }
        
        if (isRemove)
        {
            //通知更新设备更新
            if (null != devListenerList && devListenerList.size() > 0)
            {
                for (DeviceListener listener : devListenerList)
                {
                    listener.onDeviceRemove(mdnsDevice);
                }
            }
        }
        return isRemove;
    }
    
    /**
     * 清空mdns列表数据
     */
    public void clearMdnsList()
    {
        if (null != mMdnsList && mMdnsList.size() > 0)
        {
            mMdnsList.clear();
            //通知更新设备更新
//			MyApplication.eventBus.post(DeviceConstants.EVENTBUS_DEVICE_UPDATE);
        }
    }
    
    public ArrayList<MdnsDevice> getMdnsList()
    {
        return mMdnsList;
    }
    
    public MdnsDevice getChoiceDevice()
    {
        return choiceDevice;
    }
    
    public synchronized void setChoiceDevice(MdnsDevice choiceDevice)
    {
        this.choiceDevice = choiceDevice;
        
        // 投投看时不选中设备
//		if (null != choiceDevice && TtkUtil.isTtkActive(null))
//		{
//		    ILog.w(TAG, "setChoiceDevice : ttk running, can not choice device...");
//		    return ;
//		}
        
        if (null != choiceDevice)
        {
//            ILog.d(TAG, "choiceDevice = " + choiceDevice.toString());
            saveDeviceConnectInfo(mContext, choiceDevice.getName(), Utils.getWifiSsid(mContext));
        }
        
        ILog.v(TAG, "setChoiceDevice --->");
        
        // 通知更新设备更新
        if (null != devListenerList && devListenerList.size() > 0)
        {
            for (DeviceListener listener : devListenerList)
            {
                listener.onChioseDevice(choiceDevice);
            }
        }
    }
    
    /**
     * 保存设备连接的记录
     * @param context
     * @param deviceName
     */
    public void saveDeviceConnectInfo(Context context, String deviceName, String wifiName)
    {
        DeviceHistoryListImpl deviceHistoryListImpl = new DeviceHistoryListImpl(context);
        int count = deviceHistoryListImpl.getDeviceConnectCount(deviceName, wifiName);
        if (count > 0)
        {
            boolean isUpdate = deviceHistoryListImpl.update(deviceName, wifiName, System.currentTimeMillis(), count + 1);
            ILog.d(TAG, "saveDeviceConnectInfo update [wifiName:" + wifiName + "], [deviceName:" + deviceName + "] ret : " + isUpdate);
        }
        else
        {
            long raw = deviceHistoryListImpl.insert(deviceName, wifiName, System.currentTimeMillis(), count + 1);
            ILog.d(TAG, "saveDeviceConnectInfo insert [wifiName:" + wifiName + "], [deviceName:" + deviceName + "] ret : " + raw);
        }
        deviceHistoryListImpl.close();
    }
    
    /**
     * 连接历史记录设备
     * @param context
     * @param device
     * @param wifiName [参数说明]
     * @return void [返回类型说明]
     * @exception throws [违例类型] [违例说明]
     */
    public synchronized void connectHistoryDevice(Context context, MdnsDevice device, String wifiName)
    {
        MdnsDevice dev = getChoiceDevice();
        if (null == dev)
        {
            DeviceHistoryListImpl deviceHistoryListImpl = new DeviceHistoryListImpl(context);
            if (deviceHistoryListImpl.hasHistoryInWifi(wifiName))
            {
                if (null != device && deviceHistoryListImpl.isLastConnectDevice(wifiName, device.getName()))
                {
                    ILog.d(TAG, "connectHistoryDevice device 1= " + device.getName());
                    setChoiceDevice(device);
                }
            }
            else
            {
                ILog.d(TAG, "connectHistoryDevice new device = " + device.getName());
                if (null != device)
                {
                    setChoiceDevice(device);
                }
            }
            deviceHistoryListImpl.close();
        }
    }
    
}
