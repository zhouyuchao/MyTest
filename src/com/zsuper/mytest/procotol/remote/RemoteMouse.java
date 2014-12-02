package com.zsuper.mytest.procotol.remote;

import java.net.SocketException;
import java.net.UnknownHostException;

import android.util.Log;

import com.coship.easybus.transport.udp.EasybusUdp;
import com.coship.easybus.util.EasyConstants;
import com.coship.easycontrol.inputcontrol.MouseCommand;

public class RemoteMouse {
    private static final String TAG = "RemoteMouse";
    
    /** udp客户端 */
    private EasybusUdp udpClient = null;
    private String mDeviceIP = null;

    public static final int MOUSE_WHEEL_DOWN = 0;
    public static final int MOUSE_WHEEL_UP = 2;
    
    /**
     * 1表示鼠标移动事件
     */
    public static final short MOUSE_ACTION_MOVE = 1;
    /**
     * 2 表示鼠标右键单击事件
     */

    public static final short MOUSE_RIGHT_SINGLE_CLICK = 2;
    /**
     * 3 表示鼠标右键双击事件
     */
    public static final short MOUSE_RIGHT_DOUBLE_CLICK = 3;
    /**
     * 4 表示鼠标右键按下事件
     */
    public static final short MOUSE_RIGHT_DOWN = 4;
    /**
     * 5 表示鼠标右键按下后弹起事件
     */
    public static final short MOUSE_RIGHT_UP = 5;
    /**
     * 6 表示鼠标右键按下后移动事件
     */
    public static final short MOUSE_RGIHT_DOWN_MOVE = 6;
    /**
     * 7 表示鼠标左键单击事件
     */
    public static final short MOUSE_LEFT_SINGLE_CLICK = 7;
    /**
     * 8 表示鼠标左键双击事件
     */
    public static final short MOUSE_LEFT_DOUBLE_CLICK = 8;

    /**
     * 9 表示鼠标左键按下事件
     */
    public static final short MOUSE_LEFT_DOWN = 9;
    /**
     * 10 表示鼠标左键按下后弹起事件
     */
    public static final short MOUSE_LEFT_UP = 10;
    /**
     * 11 表示鼠标左键按下后移动事件
     */
    public static final short MOUSE_LEFT_DOWN_MOVE = 11;

    public RemoteMouse(String devIP) {
        this.mDeviceIP = devIP;

        udpClient = new EasybusUdp(devIP, EasyConstants.REMOTE_PORT);
        try {
            udpClient.connect();
        } catch (SocketException e) {
            e.printStackTrace();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

    }

    public void setRemote(String ip) {
        mDeviceIP = ip;
    }

    protected void destory() {
        try {
            udpClient.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendMouseWheelEvent(int wheelEvent) {
        MouseCommand command = new MouseCommand();
        command.setClickType(wheelEvent);
        if (MOUSE_WHEEL_DOWN == wheelEvent) {
            command.setDy(-10);
        } else if (MOUSE_WHEEL_UP == wheelEvent) {
            command.setDy(10);
        }

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

    public void sendMouseMoveEvent(int event_type, int sendX, int sendY) {
        Log.e(TAG, "send mouse move: sendX:" + sendX + " sendY:" + sendY);
        MouseCommand command = new MouseCommand(event_type, 0, sendX, sendY);
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

    public void sendMouseClickEvent(int event_type) {
        if (event_type == MOUSE_RIGHT_SINGLE_CLICK) {
            RemoteKeyboard key = new RemoteKeyboard(this.mDeviceIP);
            key.remoteSendDownAndUpKeyCode(4);
        } else {
            MouseCommand command = new MouseCommand();
            command.setClickType(event_type);

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
