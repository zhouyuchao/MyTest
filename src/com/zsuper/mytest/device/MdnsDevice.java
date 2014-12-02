package com.zsuper.mytest.device;

import android.text.TextUtils;

import com.zsuper.mytest.procotol.remote.OtODeviceAdapter;

/**
 * Mdns服务实例对象 <功能描述>
 * @author ZhouYuChao/907753
 * @version [版本号, 2014-11-17]
 * @since [产品/模块版本]
 */
public class MdnsDevice
{
    private OtODeviceAdapter mAdapter;
    
    private String name;
    private String ip;
    private String mac;
    
    public MdnsDevice()
    {
    }
    
    public MdnsDevice(String name, String ip, String mac)
    {
        this.name = name;
        this.ip = ip;
        this.mac = mac;
    }
    
    public OtODeviceAdapter adapter(){
        if(mAdapter == null){
            mAdapter = OtODeviceAdapter.create();
        }
        return mAdapter;
    }
    
    public String getName()
    {
        return name;
    }
    
    public void setName(String name)
    {
        this.name = name;
    }
    
    public String getIp()
    {
        return ip;
    }
    
    public void setIp(String ip)
    {
        this.ip = ip;
    }
    
    public String getMac()
    {
        return mac;
    }
    
    public void setMac(String mac)
    {
        this.mac = mac;
    }
    
    @Override
    public boolean equals(Object o)
    {
        MdnsDevice mdnsDevice = (MdnsDevice)o;
        if (null == mdnsDevice) {
            return false;
        }
        
        String mac = mdnsDevice.getMac();
        String ip = mdnsDevice.getIp();
        String name = mdnsDevice.getName();
        
        if (TextUtils.isEmpty(mac))
        {
            if (null != name && name.equals(this.name) 
                    && null != ip && ip.equals(this.ip))
            {
                return true;
            }
        }
        else
        {
            if (null != mac && mac.equals(this.mac) 
                    && null != ip && ip.equals(this.ip))
            {
                return true;
            }
        }
        return false;
    }
    
    @Override
    public String toString() {
        String s = "name = "+ name 
                + " && ip = "+ ip 
                + " && mac = " + mac;
        return s;
    }
}
