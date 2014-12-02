package com.zsuper.mytest.device;

import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;

import javax.jmdns.JmDNS;
import javax.jmdns.NetworkTopologyDiscovery;
import javax.jmdns.ServiceEvent;
import javax.jmdns.ServiceInfo;
import javax.jmdns.ServiceListener;

import android.content.Context;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiManager.MulticastLock;

import com.zsuper.mytest.constant.Constant;
import com.zsuper.mytest.utils.ILog;
import com.zsuper.mytest.utils.Utils;

/**
 * MDNS服务管理
 * <功能描述>
 * @author  ZhouYuChao/907753
 * @version  [版本号, 2014-8-15]
 * @since  [产品/模块版本]
 */
public class MdnsSearchManager
{
    private static final String TAG = MdnsSearchManager.class.getSimpleName();
    
    private MulticastLock lock;
    private JmDNS mJmdns;
    private Context mContext;
    
    private boolean mIsSearching;
    private boolean mIsPausing;
    
    public MdnsSearchManager(Context context)
    {
        mIsSearching = false;
        mIsPausing = false;
        mContext = context;
        
        allowMulticast(context);
    }
    
    public void release()
    {
        releaseMulticast();
    }
    
    private void allowMulticast(Context context)
    {
        ILog.i(TAG, "allowMulticast ");
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        lock = wifiManager.createMulticastLock("androidxcontrol.udp");
        lock.acquire();
    } 
    
    private void releaseMulticast()
    {
        ILog.i(TAG, "releaseMulticast ");
        if(null != lock){
            lock.release();
        }
    }
    
    /**
     * 服务发现
     * @return void [返回类型说明]
     * @exception throws [违例类型] [违例说明]
     */
    public void startDiscovery()
    {
        ILog.i(TAG, "startDiscovery ");
        mIsSearching = true;
        mIsPausing = false;
        
        new Thread()
        {
            public void run()
            {
                startJmdns();
            };
        }.start();
    }
    
    private void startJmdns()
    {
        ILog.i(TAG, "startJmdns ");
        try
        {
            stopJmdns();
            
            mJmdns = JmDNS.create(getHostAddress());
            mJmdns.addServiceListener(Constant.TYPE, mServiceListener);
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }
    
    /**
     * 暂停搜索
     * @return void [返回类型说明]
     * @exception throws [违例类型] [违例说明]
     */
    public void pauseDiscovery()
    {
        mIsPausing = true;
        stopDiscovery();
    }
    
    public void stopDiscovery()
    {
        ILog.i(TAG, "stopDiscovery ");
//        stopJmdns();
        mIsSearching = false;
        
        new Thread()
        {
            public void run()
            {
                stopJmdns();
            };
        }.start();
    }
    
    private void stopJmdns()
    {
        ILog.i(TAG, "stopJmdns ");
        try
        {
            if (null != mJmdns)
            {
                if (null != mServiceListener)
                {
                    mJmdns.removeServiceListener(Constant.TYPE, mServiceListener);
                }
                mJmdns.close();
            }
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }
    
//    public void setSearching(boolean state)
//    {
//        mIsSearching = state;
//    }
    
    public boolean isSearching()
    {
        return mIsSearching;
    }
    
    public boolean isPausing()
    {
        return mIsPausing;
    }
    
    /**
     * 获取本机Host
     * <功能描述>
     * @return [参数说明]
     * @return InetAddress [返回类型说明]
     * @exception throws [违例类型] [违例说明]
     */
    private InetAddress getHostAddress()
    {
        InetAddress[] addresses = NetworkTopologyDiscovery.Factory.getInstance().getInetAddresses();
        InetAddress inetAddress = null;
        for (int i = 0; i < addresses.length; i++)
        {
            if (addresses[i] instanceof Inet4Address)
            {
                ILog.d(TAG, "startJmdns host : " + addresses[i].getHostAddress());
                inetAddress = addresses[i];
            }
        }
        return inetAddress;
    }
    
    /**
     * 服务发现监听
     */
    private ServiceListener mServiceListener = new ServiceListener()
    {
        @Override
        public void serviceAdded(ServiceEvent event)
        {
            ILog.v(TAG, " + serviceAdded : " + event.getName());
            ServiceInfo info = mJmdns.getServiceInfo(Constant.TYPE, event.getName());
            addMdnsDevice(info);
        }
        
        @Override
        public void serviceRemoved(ServiceEvent event)
        {
            ILog.w(TAG, " - serviceRemoved : " + event.getName());
            ServiceInfo info = mJmdns.getServiceInfo(Constant.TYPE, event.getName());
            removeMdnsDevice(info);
        }
        
        @Override
        public void serviceResolved(ServiceEvent event)
        {
            ILog.v(TAG, " * serviceResolved : " + event.getName());
            ServiceInfo info = mJmdns.getServiceInfo(Constant.TYPE, event.getName());
        }
    };
    
    /**
     * 添加Mdns设备 <功能描述>
     * @param serviceInfo [参数说明]
     * @return void [返回类型说明]
     * @exception throws [违例类型] [违例说明]
     */
    private void addMdnsDevice(ServiceInfo serviceInfo) 
    {
        if (null != serviceInfo) {
            MdnsDevice mdnsDevice = null;
            
            String name = serviceInfo.getName();
            String ip   = serviceInfo.getHostAddress();
            String mac  = serviceInfo.getPropertyString("deviceid").toLowerCase();

//            Log.d(TAG, "createMdnsDevice dvb = " + serviceInfo.getPropertyString("dvb"));
//            boolean dvb = false;
//            if (null == serviceInfo.getPropertyString("dvb")
//                    || serviceInfo.getPropertyString("dvb").equals("false")) {
//                dvb = false;
//            } else if (serviceInfo.getPropertyString("dvb").equals("true")) {
//                dvb = true;
//            }
//            
//            String hardware = serviceInfo.getPropertyString("hardware");
//            Log.d(TAG, "createMdnsDevice hardware = " + hardware);
//            if (null == hardware || TextUtils.isEmpty(hardware)) {
//                hardware = "unknow";
//            }
//            
//            String model = serviceInfo.getPropertyString("model");
//            Log.d(TAG, "createMdnsDevice model = " + model);
//            if (null == model || TextUtils.isEmpty(model)) {
//                model = "unknow";
//            } 
            
            mdnsDevice = new MdnsDevice(name, ip, mac);
            ILog.d(TAG, mdnsDevice.toString());
            
            DeviceManager.newInstance().addMdnsDevice(mdnsDevice);
        }
    }
    
    /**
     * 移除Mdns设备
     * @param serviceInfo [参数说明]
     * @return void [返回类型说明]
     * @exception throws [违例类型] [违例说明]
     */
    private void removeMdnsDevice(ServiceInfo serviceInfo)
    {
        if (null != serviceInfo) {
            MdnsDevice mdnsDevice = null;
            
            String name = serviceInfo.getName();
            String ip   = serviceInfo.getHostAddress();
            String mac  = serviceInfo.getPropertyString("deviceid").toLowerCase();
            
            mdnsDevice = new MdnsDevice(name, ip, mac);
            ILog.d(TAG, mdnsDevice.toString());
            
            DeviceManager.newInstance().removeMdnsDevice(mdnsDevice);
        }
    }
    
}
