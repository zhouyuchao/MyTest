package com.zsuper.mytest.procotol.remote;

import java.util.ArrayList;

import android.util.Log;

import com.zsuper.mytest.device.MdnsDevice;
import com.zsuper.mytest.procotol.bean.Mouse;

public class OtORemoteMouseTask implements Runnable
{
    private final static String TAG = "OtORemoteMouseTask";
    private RemoteMouse mMouse = null;
    private MdnsDevice mRemote = null;
    private ArrayList<Mouse> mData = new ArrayList<Mouse>();
    private byte[] mLock = new byte[0];
    private boolean mFinished = false;
    
    public OtORemoteMouseTask(MdnsDevice remote)
    {
        mMouse = new RemoteMouse(remote == null ? null : remote.getIp());
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
    
    public synchronized void sendMouse(Mouse mouse)
    {
        mData.add(mouse);
        Log.d(TAG, "sendMouse");
        synchronized (mLock)
        {
            mLock.notify();
        }
    }
    
    private synchronized Mouse getMouse()
    {
        if (mData.size() > 0)
        {
            Mouse mouse = mData.get(0);
            mData.remove(0);
            Log.d(TAG, "getMouse");
            return mouse;
        }
        return null;
    }
    
    @Override
    public void run()
    {
        Mouse mouse = null;
        while (!mFinished)
        {
            while ((mouse = getMouse()) != null)
            {
                if (mMouse == null)
                {
                    mMouse = new RemoteMouse(mRemote.getIp());
                }
                else
                {
                    mMouse.setRemote(mRemote.getIp());
                }
                if (mMouse != null && mouse != null)
                {
                    switch (mouse.action())
                    {
                    case Mouse.ACTION_DOUBLE_CLICK:
                        if (mouse.type() == Mouse.MOUSE_LEFT)
                        {
                            mMouse.sendMouseClickEvent(RemoteMouse.MOUSE_LEFT_DOUBLE_CLICK);
                        }
                        else if (mouse.type() == Mouse.MOUSE_RIGHT)
                        {
                            mMouse.sendMouseClickEvent(RemoteMouse.MOUSE_RIGHT_DOUBLE_CLICK);
                        }
                        else if (mouse.type() == Mouse.MOUSE_MID)
                        {
                            mMouse.sendMouseClickEvent(RemoteMouse.MOUSE_WHEEL_DOWN);
                        }
                        break;
                    case Mouse.ACTION_SINGLE_CLICK:
                        if (mouse.type() == Mouse.MOUSE_LEFT)
                        {
                            mMouse.sendMouseClickEvent(RemoteMouse.MOUSE_LEFT_SINGLE_CLICK);
                        }
                        else if (mouse.type() == Mouse.MOUSE_RIGHT)
                        {
                            mMouse.sendMouseClickEvent(RemoteMouse.MOUSE_RIGHT_SINGLE_CLICK);
                        }
                        else if (mouse.type() == Mouse.MOUSE_MID)
                        {
                            mMouse.sendMouseClickEvent(RemoteMouse.MOUSE_WHEEL_DOWN);
                        }
                        break;
                    case Mouse.ACTION_DOWN:
                        if (mouse.type() == Mouse.MOUSE_LEFT)
                        {
                            Log.d(TAG, "mMouse.sendMouseClickEvent(RemoteMouse.MOUSE_LEFT_DOWN)");
                            mMouse.sendMouseClickEvent(RemoteMouse.MOUSE_LEFT_DOWN);
                        }
                        else if (mouse.type() == Mouse.MOUSE_RIGHT)
                        {
                            mMouse.sendMouseClickEvent(RemoteMouse.MOUSE_RIGHT_DOWN);
                        }
                        else if (mouse.type() == Mouse.MOUSE_MID)
                        {
                            mMouse.sendMouseClickEvent(RemoteMouse.MOUSE_WHEEL_DOWN);
                        }
                        break;
                    case Mouse.ACTION_UP:
                        if (mouse.type() == Mouse.MOUSE_LEFT)
                        {
                            Log.d(TAG, "mMouse.sendMouseClickEvent(RemoteMouse.MOUSE_LEFT_UP)");
                            mMouse.sendMouseClickEvent(RemoteMouse.MOUSE_LEFT_UP);
                        }
                        else if (mouse.type() == Mouse.MOUSE_RIGHT)
                        {
                            mMouse.sendMouseClickEvent(RemoteMouse.MOUSE_RIGHT_UP);
                        }
                        else if (mouse.type() == Mouse.MOUSE_MID)
                        {
                            mMouse.sendMouseClickEvent(RemoteMouse.MOUSE_WHEEL_UP);
                        }
                        break;
                    case Mouse.ACTION_MOVE:
                        Log.e(TAG, "mMouse.sendMouseMoveEvent(x,y)");
                        mMouse.sendMouseMoveEvent(RemoteMouse.MOUSE_ACTION_MOVE, mouse.x(), mouse.y());
                        break;
                    case Mouse.ACTION_ROLL:
                        if (mouse.type() == Mouse.MOUSE_ROLL_UP)
                        {
                            mMouse.sendMouseWheelEvent(0);
                        }
                        else if (mouse.type() == Mouse.MOUSE_ROLL_DOWN)
                        {
                            mMouse.sendMouseWheelEvent(2);
                        }
                        break;
                    //add by yw start 2013-10-16
                    case Mouse.ACTION_DOWN_MOVE:
                        if (mouse.type() == Mouse.MOUSE_LEFT)
                        {// 左键按下移动事件
                            mMouse.sendMouseMoveEvent(RemoteMouse.MOUSE_LEFT_DOWN_MOVE, mouse.x(), mouse.y());
                        }
                        break;
                    default:
                        break;
                    }
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
        if (mMouse != null)
        {
            mMouse.release();
            mMouse = null;
        }
        if (mData != null)
        {
            mData.clear();
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
