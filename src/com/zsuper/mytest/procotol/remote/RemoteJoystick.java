package com.zsuper.mytest.procotol.remote;

import java.net.SocketException;
import java.net.UnknownHostException;

import android.util.Log;

import com.coship.easybus.transport.udp.EasybusUdp;
import com.coship.easybus.util.EasyConstants;
import com.coship.easycontrol.inputcontrol.JoystickCommand;
import com.zsuper.mytest.utils.String2Hex;

public class RemoteJoystick {
    private static final String TAG = "RemoteJoystick";

    /** udp客户端 */
    private EasybusUdp udpClient = null;
    private String mDeviceIP = null;

    public RemoteJoystick(String devIP) {
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

    public void sendJoystickEvent(char[] joystickAction) {
//        Log.d(TAG, "sendJoystickEvent ");
        try {
            Log.v(TAG, "sendJoystickEvent sent ---> " + String2Hex.toHexString(joystickAction));
            
            JoystickCommand jcommand = new JoystickCommand(getByte(joystickAction));
            
            if (null == udpClient) {
                udpClient = new EasybusUdp(mDeviceIP, EasyConstants.REMOTE_PORT);
                udpClient.connect();
            }
            
            udpClient.send(jcommand, mDeviceIP);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private byte[] getByte(char[] data) {
        byte[] bData = new byte[data.length];
        for (int i = 0; i < data.length; i++) {
            byte b = (byte) data[i];
            bData[i] = b;
        }
        return bData;
    }

}
