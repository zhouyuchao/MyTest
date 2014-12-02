package com.zsuper.mytest.procotol.remote;

import java.util.ArrayList;

import android.util.Log;

import com.zsuper.mytest.device.MdnsDevice;
import com.zsuper.mytest.procotol.bean.MultiTouchInfo;

public class OtORemoteTouchTask implements Runnable
{
    private final static String TAG = "OtORemoteTouchTask";
    private RemoteTouch mTouch = null;
    private MdnsDevice mRemote = null;
    private ArrayList<MultiTouchInfo> mData = new ArrayList<MultiTouchInfo>();
    private byte[] mLock = new byte[0];
    private boolean mFinished = false;
    
    public OtORemoteTouchTask(MdnsDevice remote)
    {
        mTouch = new RemoteTouch(remote == null ? null : remote.getIp());
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
    
    public synchronized void sendTouch(MultiTouchInfo touch)
    {
        mData.add(touch);
        Log.d(TAG, "sendTouch");
        synchronized (mLock)
        {
            mLock.notify();
        }
    }
    
    private synchronized MultiTouchInfo getTouch()
    {
        if (mData.size() > 0)
        {
            MultiTouchInfo touch = mData.get(0);
            mData.remove(0);
            Log.d(TAG, "getTouch");
            return touch;
        }
        return null;
    }
    
    @Override
    public void run()
    {
        MultiTouchInfo touch = null;
        while (!mFinished)
        {
            while ((touch = getTouch()) != null)
            {
                mTouch.setRemote(mRemote.getIp());
                mTouch.sendMultiTouchEvent(touch);
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
        if (mTouch != null)
        {
            mTouch.release();
            mTouch = null;
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
