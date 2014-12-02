package com.zsuper.mytest.device;


public interface DeviceListener
{
    public void onDeviceAdd(MdnsDevice device);
    
    public void onDeviceRemove(MdnsDevice device);
    
    public void onChioseDevice(MdnsDevice device);
}
