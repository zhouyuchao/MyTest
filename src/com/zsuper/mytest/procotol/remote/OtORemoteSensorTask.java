package com.zsuper.mytest.procotol.remote;

import java.util.ArrayList;

import com.zsuper.mytest.device.MdnsDevice;
import com.zsuper.mytest.procotol.bean.GSensor;

public class OtORemoteSensorTask implements Runnable
{
    private static final String TAG = "OtORemoteSensorTask";
    private RemoteSensor mSensor = null;
    private MdnsDevice mRemote = null;
    private ArrayList<GSensor> mData = new ArrayList<GSensor>();
    private byte[] mLock = new byte[0];
    private boolean mFinished = false;
    
    public OtORemoteSensorTask(MdnsDevice remote, GSensor sensor)
    {
        mSensor = new RemoteSensor(remote == null ? null : remote.getIp());
        mRemote = remote;
        mData.add(sensor);
    }
    
    public void stop()
    {
        mFinished = true;
        synchronized (mLock)
        {
            mLock.notify();
        }
    }
    
    public void setRemote(MdnsDevice remote)
    {
        mRemote = remote;
    }
    
    public synchronized void sendSensor(GSensor sensor)
    {
        mData.add(sensor);
        //Log.d(TAG, "sendSensor");
        synchronized (mLock)
        {
            mLock.notify();
        }
    }
    
    private synchronized GSensor getGSensor()
    {
        if (mData.size() > 0)
        {
            GSensor sensor = mData.get(0);
            mData.remove(0);
            //Log.d(TAG, "getGSensor");
            return sensor;
        }
        return null;
    }
    
    @Override
    public void run()
    {
        GSensor sensor = null;
        while (!mFinished)
        {
            while ((sensor = getGSensor()) != null)
            {
                mSensor.setRemote(mRemote.getIp());
                mSensor.sendSensorEvent(sensor.type(), sensor.x(), sensor.y(), sensor.z());
            }
            if (mFinished == true)
                return;
            synchronized (mLock)
            {
                try
                {
                    mLock.wait();
                } catch (InterruptedException e)
                {
                    e.printStackTrace();
                }
            }
        }
    }
    
    public void release()
    {
        if (mData != null)
        {
            mData.clear();
        }
        mFinished = true;
        if (mSensor != null)
        {
            mSensor.release();
            mSensor = null;
        }
        if (mRemote != null)
        {
            mRemote = null;
        }
        synchronized (mLock)
        {
            mLock.notifyAll();
        }
    }
}
