package com.zsuper.mytest.procotol.remote;

import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;

import android.util.Log;

import com.coship.easybus.transport.udp.EasybusUdp;
import com.coship.easybus.util.EasyConstants;
import com.coship.easycontrol.inputcontrol.TouchCommand;
import com.coship.easycontrol.inputcontrol.entity.TouchEntity;
import com.zsuper.mytest.procotol.bean.FingerInfo;
import com.zsuper.mytest.procotol.bean.MultiTouchInfo;


public class RemoteTouch {
    private static final String TAG = "RemoteTouch";

    /** udp客户端 */
    private EasybusUdp udpClient = null;
    private String mDeviceIP = null;
    public static final int TOUCH_EVENT_DOWN = 0;
    public static final int TOUCH_EVENT_UP = 1;
    public static final int TOUCH_EVENT_MOVE = 2;

    public RemoteTouch(String devIP) {
        udpClient = new EasybusUdp(devIP, EasyConstants.REMOTE_PORT);
        try {
            udpClient.connect();
        } catch (SocketException e) {
            e.printStackTrace();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        mDeviceIP = devIP;
    }

    protected void destory() {
        try {
            udpClient.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setRemote(String address) {
        mDeviceIP = address;
    }

    public void sendTouchEvent(int touchEvent, float sendX, float sendY) {
        Log.d(TAG, "send touch event type:" + touchEvent + " sendX:" + sendX + " sendY:" + sendY);

        TouchEntity touchEntity = new TouchEntity(Integer.parseInt(Float
                .toString(sendX)), Integer.parseInt(Float.toString(sendY)),
                touchEvent);

        TouchCommand command = new TouchCommand(new TouchEntity[] { touchEntity });
        try {
            if (null == udpClient) {
                udpClient = new EasybusUdp(mDeviceIP, EasyConstants.REMOTE_PORT);
                udpClient.connect();
            }
            udpClient.send(command, mDeviceIP);
        } catch (Exception e) {
            e.printStackTrace();
        }
	}

	public void sendMultiTouchEvent(MultiTouchInfo info) {
        //Log.d(TAG, "sendMultiTouchEvent begin");
        try {
            int maxTouch = info.getFingerNum();

            TouchEntity[] touchEntitys = new TouchEntity[maxTouch];

            for (int i = 0; i < maxTouch; ++i) {
                FingerInfo fin = info.getFingerInfo(i);
                touchEntitys[i] = new TouchEntity(fin.getX(), fin.getY(), fin.getPress());
                
                Log.d(TAG, "[SEND] finger: "+i+", x: "+fin.getX()+", y: "+fin.getY()+", press: "+fin.getPress());
            }

            TouchCommand command = new TouchCommand(touchEntitys);
            
            //command.toString();
            if (null == udpClient) {
                udpClient = new EasybusUdp(mDeviceIP, EasyConstants.REMOTE_PORT);
                udpClient.connect();
            }
            udpClient.send(command,mDeviceIP);
        } catch (Exception e) {
            e.getStackTrace();
        }
        // Log.d(TAG, "sendMultiTouchEvent end");
    }

    private byte[] getMultiTouchByteData(MultiTouchInfo info) {
        ByteBuffer msgbuf = null;
        byte[] msg = null;

        msg = new byte[64];
        msgbuf = ByteBuffer.allocate(70);
        msgbuf.putInt(Integer.reverseBytes(info.getFingerNum()));
        for (int i = 0; i < 5; ++i) {
            if (i < info.getFingerNum()) {
                FingerInfo fin = info.getFingerInfo(i);
                msgbuf.putInt(Integer.reverseBytes(fin.getX()));
                msgbuf.putInt(Integer.reverseBytes(fin.getY()));
                msgbuf.putInt(Integer.reverseBytes(fin.getPress()));
                Log.e(TAG, "finger " + i + " x:" + fin.getX() + "y: " + fin.getY() + "press: " + fin.getPress());
            } else {
                msgbuf.putInt(0);
                msgbuf.putInt(0);
                msgbuf.putInt(0);
            }

        }

        if ((msgbuf != null) && (msg != null)) {
            Log.e(TAG, "UDP msgLen=" + msg.length + ",msgbuf.capacity()=" + msgbuf.capacity());

            msgbuf.rewind();
            msgbuf.get(msg, 0, msg.length);
            
            Log.e(TAG, "after get UDP msgLen=" + msg.length);
            msgbuf.clear();
        }
        return msg;
    }
    
    public void release(){
        if(udpClient != null){
            try {
                udpClient.disconnect();
                udpClient = null;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
