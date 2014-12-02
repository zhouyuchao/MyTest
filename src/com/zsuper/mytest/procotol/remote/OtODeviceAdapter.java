package com.zsuper.mytest.procotol.remote;

import android.util.Log;

import com.zsuper.mytest.device.MdnsDevice;
import com.zsuper.mytest.procotol.bean.GSensor;
import com.zsuper.mytest.procotol.bean.KeyInfo;
import com.zsuper.mytest.procotol.bean.Mouse;
import com.zsuper.mytest.procotol.bean.MultiTouchInfo;

public class OtODeviceAdapter
{
    private static final String TAG = "OtODeviceAdapter";
    
    private static OtODeviceAdapter gAdapter = null;
    
    private OtORemoteKeyboardTask mKeyboardTask = null;
    private OtORemoteSensorTask mSensorTask = null;
    private OtORemoteTouchTask mTouchTask = null;
    private OtORemoteMouseTask mMouseTask = null;
    private OtORemoteJoystickTask mJoystickTask = null;
    
    private OtODeviceAdapter()
    {
    }
    
    public static OtODeviceAdapter create()
    {
        if (gAdapter == null)
            gAdapter = new OtODeviceAdapter();
        return gAdapter;
    }
    
    public void key(MdnsDevice remote, int key)
    {
        if (mKeyboardTask == null)
        {
            mKeyboardTask = new OtORemoteKeyboardTask(remote, key);
            Thread thread = new Thread(mKeyboardTask);
            thread.start();
        }
        else
        {
            mKeyboardTask.setRemote(remote);
            mKeyboardTask.sendKey(key);
        }
    }
    
    public void key(MdnsDevice remote, int key, int action)
    {
        if (mKeyboardTask == null)
        {
            mKeyboardTask = new OtORemoteKeyboardTask(remote, key);
            Thread thread = new Thread(mKeyboardTask);
            thread.start();
        }
        else
        {
            mKeyboardTask.setRemote(remote);
            mKeyboardTask.sendKeyAction(key, action);
        }
    }
    
    public void keyDown(MdnsDevice remote, int key)
    {
        if (mKeyboardTask == null)
        {
            mKeyboardTask = new OtORemoteKeyboardTask(remote, key, KeyInfo.KEY_EVENT_DOWN);
            Thread thread = new Thread(mKeyboardTask);
            thread.start();
        }
        else
        {
            mKeyboardTask.setRemote(remote);
            mKeyboardTask.sendKeyAction(key, KeyInfo.KEY_EVENT_DOWN);
        }
    }
    
    public void keyUp(MdnsDevice remote, int key)
    {
        if (mKeyboardTask == null)
        {
            mKeyboardTask = new OtORemoteKeyboardTask(remote, key, KeyInfo.KEY_EVENT_UP);
            Thread thread = new Thread(mKeyboardTask);
            thread.start();
        }
        else
        {
            mKeyboardTask.setRemote(remote);
            mKeyboardTask.sendKeyAction(key, KeyInfo.KEY_EVENT_UP);
        }
    }
    
    public void mouse(MdnsDevice remote, Mouse mouse)
    {
        if (mMouseTask == null)
        {
            mMouseTask = new OtORemoteMouseTask(remote);
            Thread thread = new Thread(mMouseTask);
            thread.start();
        }
        else
        {
            mMouseTask.setRemote(remote);
            mMouseTask.sendMouse(mouse);
        }
    }
    
    public void sensor(MdnsDevice remote, GSensor sensor)
    {
        if (mSensorTask == null)
        {
            mSensorTask = new OtORemoteSensorTask(remote, sensor);
            Thread sensorThread = new Thread(mSensorTask);
            sensorThread.start();
        }
        else
        {
            mSensorTask.setRemote(remote);
            mSensorTask.sendSensor(sensor);
        }
    }
    
    public void touch(MdnsDevice remote, MultiTouchInfo touch)
    {
        Log.d(TAG, "[touch] mTouchTask : " + mTouchTask);
        if (mTouchTask == null)
        {
            mTouchTask = new OtORemoteTouchTask(remote);
            Thread touchTask = new Thread(mTouchTask);
            touchTask.start();
        }
        else
        {
            mTouchTask.setRemote(remote);
            mTouchTask.sendTouch(touch);
        }
    }
    
    public void joystick(MdnsDevice remote, char[] joystickAction)
    {
//      Log.d(TAG, "[joystick] mJoystickTask : " + mJoystickTask);
        if (null == mJoystickTask)
        {
            mJoystickTask = new OtORemoteJoystickTask(remote);
            Thread joystickTask = new Thread(mJoystickTask);
            joystickTask.start();
        }
        mJoystickTask.setRemote(remote);
        mJoystickTask.sendJoystickAction(joystickAction);
    }
    
    public void releaseRes()
    {
        if (mKeyboardTask != null)
        {
            mKeyboardTask.release();
            mKeyboardTask = null;
        }
        if (mMouseTask != null)
        {
            mMouseTask.release();
            mMouseTask = null;
        }
        if (mSensorTask != null)
        {
            mSensorTask.release();
            mSensorTask = null;
        }
        if (mTouchTask != null)
        {
            mTouchTask.release();
            mTouchTask = null;
        }
        gAdapter = null;
    }
}
