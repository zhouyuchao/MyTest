package com.zsuper.mytest.procotol.remote;

import java.util.ArrayList;

import com.zsuper.mytest.device.MdnsDevice;
import com.zsuper.mytest.procotol.bean.KeyInfo;

public class OtORemoteKeyboardTask implements Runnable
{
    private static final String TAG = "OtORemoteKeyboardTask";
    
    private RemoteKeyboard mKeyboard = null;
    private MdnsDevice mRemote = null;
    private byte[] mLock = new byte[0];
    private boolean mFinished = false;
    private ArrayList<Integer> mData = new ArrayList<Integer>();
    private ArrayList<Integer> mAction = new ArrayList<Integer>();
    
    public OtORemoteKeyboardTask(MdnsDevice remote, int key)
    {
        mRemote = remote;
        mKeyboard = new RemoteKeyboard(remote == null ? null : remote.getIp());
        mData.add(key);
        mAction.add(KeyInfo.KEY_EVENT_CLICKED);
    }
    
    public OtORemoteKeyboardTask(MdnsDevice remote, int key, int action)
    {
        mRemote = remote;
        mKeyboard = new RemoteKeyboard(remote == null ? null : remote.getIp());
        mData.add(key);
        mAction.add(action);
    }
    
    public void stop()
    {
        mFinished = true;
        synchronized (mLock)
        {
            mLock.notifyAll();
        }
    }
    
    public void setRemote(MdnsDevice remote)
    {
        mRemote = remote;
    }
    
    public synchronized void sendKey(int key)
    {
        mData.add(key);
        mAction.add(KeyInfo.KEY_EVENT_CLICKED);
        // LogUtils.trace(Log.DEBUG, TAG, "sendKey: " + key);
        synchronized (mLock)
        {
            mLock.notifyAll();
        }
    }
    
    public synchronized void sendKeyAction(int key, int action)
    {
        mData.add(key);
        mAction.add(action);
//        LogUtils.trace(Log.DEBUG, TAG, "sendKey: " + key + "  action: " + action);
        synchronized (mLock)
        {
            mLock.notifyAll();
        }
    }
    
    private synchronized int getKey()
    {
        if (mData.size() > 0)
        {
            int key = mData.get(0);
//			LogUtils.trace(Log.DEBUG, TAG, "getKey: " + key);
            mData.remove(0);
            return key;
        }
        return -1;
    }
    
    private synchronized int getAction()
    {
        if (mAction.size() > 0)
        {
            int action = mAction.get(0);
//            LogUtils.trace(Log.DEBUG, TAG, "getAction: " + action);
            mAction.remove(0);
            return action;
        }
        return -1;
    }
    
    @Override
    public void run()
    {
        int key = -1;
        int action = -1;
        while (!mFinished)
        {
            if (mRemote != null)
            {
                while ((key = getKey()) != -1)
                {
                    mKeyboard.setRemote(mRemote.getIp());
                    action = getAction();
                    mKeyboard.remoteSendDownOrUpKeyCode(key, action);
                }
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
        if (mKeyboard != null)
        {
            mKeyboard.release();
            mKeyboard = null;
        }
        if (mData != null)
        {
            mData.clear();
        }
        if (mAction != null)
        {
            mAction.clear();
        }
        if (mRemote != null)
        {
            mRemote = null;
        }
        mFinished = true;
        synchronized (mLock)
        {
            mLock.notifyAll();
        }
    }
}
