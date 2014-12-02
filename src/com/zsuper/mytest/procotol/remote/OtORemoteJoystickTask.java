package com.zsuper.mytest.procotol.remote;

import java.util.ArrayList;

import com.zsuper.mytest.device.MdnsDevice;

public class OtORemoteJoystickTask implements Runnable {
    private final static String TAG = "OtORemoteJoystick";
    
    private ArrayList<char[]> mData = new ArrayList<char[]>();
    private RemoteJoystick mJoystick = null;
    private MdnsDevice mRemote = null;
    private byte[] mLock = new byte[0];
    private boolean mFinished = false;

    public OtORemoteJoystickTask(MdnsDevice remote) {
        mJoystick = new RemoteJoystick(remote == null ? null : remote.getIp());
    }

    public void stop() {
        mFinished = true;
        
        synchronized (mLock) {
            mLock.notify();
        }
    }

    public void setRemote(MdnsDevice remote) {
        mRemote = remote;
    }

    public synchronized void sendJoystickAction(char[] joystickAction) {
        mData.add(joystickAction);
//        Log.d(TAG, "sendJoystickAction");
        
        synchronized (mLock) {
            mLock.notify();
        }
    }

    private synchronized char[] getjoystickAction() {
        if (mData.size() > 0) {
            
            char[] joystickAction = mData.get(0);
            mData.remove(0);
            
//            Log.d(TAG, "getjoystickAction");
            return joystickAction;
        }
        return null;
    }

    @Override
    public void run() {
        char[] joystickAction = null;
        while (!mFinished) {
            while ((joystickAction = getjoystickAction()) != null) {
                mJoystick.setRemote(mRemote.getIp());
                mJoystick.sendJoystickEvent(joystickAction);
            }

            if (mFinished == true)
                return;

            synchronized (mLock) {
                try {
                    mLock.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void release() {
        if (mData != null) {
            mData.clear();
        }
        
        mFinished = true;
        
        if (mJoystick != null) {
            mJoystick.release();
            mJoystick = null;
        }
        
        if (mRemote != null) {
            mRemote = null;
        }
        
        synchronized (mLock) {
            mLock.notifyAll();
        }
    }
}