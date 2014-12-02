package com.zsuper.mytest.procotol.remote;

import java.net.SocketException;
import java.net.UnknownHostException;

import android.util.Log;

import com.coship.easybus.transport.udp.EasybusUdp;
import com.coship.easybus.util.EasyConstants;
import com.coship.easycontrol.inputcontrol.KeyboardCommand;

public class RemoteKeyboard
{
    private static final String TAG = "RemoteKeyboard";
    int win_width = 0;
    int win_height = 0;
    String mIP = null;
    /**
     * udp客户端.
     */
    private EasybusUdp udpClient = null;
    private int KEYCODE_MUTE_DRIVER = 113;
    
    public RemoteKeyboard(String ip)
    {
        udpClient = new EasybusUdp(ip, EasyConstants.REMOTE_PORT);
        try
        {
            udpClient.connect();
        } catch (SocketException e)
        {
            e.printStackTrace();
        } catch (UnknownHostException e)
        {
            e.printStackTrace();
        }
        mIP = ip;
    }
    
    protected void destroy()
    {
        try
        {
            udpClient.disconnect();
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    
    public void setRemote(String IP)
    {
        mIP = IP;
    }
    
    public void remoteSendDownAndUpKeyCode(int keycode)
    {
        KeyboardCommand command = new KeyboardCommand();
        int key_value = -1;
        boolean shift_press = false;
        switch (keycode)
        {
        case 133:
            key_value = 29;
            shift_press = true;
            break;
        case 134:
            key_value = 30;
            shift_press = true;
            break;
        case 135:
            key_value = 31;
            shift_press = true;
            break;
        case 136:
            key_value = 32;
            shift_press = true;
            break;
        case 137:
            key_value = 33;
            shift_press = true;
            break;
        case 138:
            key_value = 34;
            shift_press = true;
            break;
        case 139:
            key_value = 35;
            shift_press = true;
            break;
        case 140:
            key_value = 36;
            shift_press = true;
            break;
        case 141:
            key_value = 37;
            shift_press = true;
            break;
        case 142:
            key_value = 38;
            shift_press = true;
            break;
        case 143:
            key_value = 39;
            shift_press = true;
            break;
        case 144:
            key_value = 40;
            shift_press = true;
            break;
        case 145:
            key_value = 41;
            shift_press = true;
            break;
        case 146:
            key_value = 42;
            shift_press = true;
            break;
        case 147:
            key_value = 43;
            shift_press = true;
            break;
        case 148:
            key_value = 44;
            shift_press = true;
            break;
        case 149:
            key_value = 45;
            shift_press = true;
            break;
        case 150:
            key_value = 46;
            shift_press = true;
            break;
        case 151:
            key_value = 47;
            shift_press = true;
            break;
        case 152:
            key_value = 48;
            shift_press = true;
            break;
        case 153:
            key_value = 49;
            shift_press = true;
            break;
        case 154:
            key_value = 50;
            shift_press = true;
            break;
        case 155:
            key_value = 51;
            shift_press = true;
            break;
        case 156:
            key_value = 52;
            shift_press = true;
            break;
        case 157:
            key_value = 53;
            shift_press = true;
            break;
        case 158:
            key_value = 54;
            shift_press = true;
            break;
        case 111:
            key_value = 10;
            shift_press = true;
            break;
        case 112:
            key_value = 12;
            shift_press = true;
            break;
        case 114:
            key_value = 14;
            shift_press = true;
            break;
        case 113:
            key_value = 13;
            shift_press = true;
            break;
        case 115:
            key_value = 15;
            shift_press = true;
            break;
        case 116:
            key_value = 16;
            shift_press = true;
            break;
        case 117:
            key_value = 7;
            shift_press = true;
            break;
        case 118:
            key_value = 70;
            shift_press = true;
            break;
        case 119:
            key_value = 68;
            shift_press = true;
            break;
        case 120:
            key_value = 76;
            shift_press = true;
            break;
        case 124:
            key_value = 71;
            shift_press = true;
            break;
        case 126:
            key_value = 74;
            shift_press = true;
            break;
        case 127:
            key_value = 75;
            shift_press = true;
            break;
        case 128:
            key_value = 73;
            shift_press = true;
            break;
        case 129:
            key_value = 11;
            shift_press = true;
            break;
        case 125:
            key_value = 72;
            shift_press = true;
            break;
        case 121:
        case 122:
        case 123:
        case 130:
        case 131:
        case 132:
        default:
            key_value = keycode;
        }
        // 设置键值
        command.setKeyValue(key_value);
        // 是否按下shift按键
        command.setFunctionKey(shift_press ? 1 : 0);
        try
        {
            if (null == udpClient)
            {
                udpClient = new EasybusUdp(mIP, EasyConstants.REMOTE_PORT);
                udpClient.connect();
            }
            udpClient.send(command, mIP);
        } catch (Exception e)
        {
            e.printStackTrace();
        }
        Log.e(TAG, "send keyboard click key_value:" + key_value + " shift:" + shift_press);
    }
    
    public void remoteSendDownOrUpKeyCode(int keycode, int event_type)
    {
        if (keycode == 91)
        {
            if (event_type == 0)
            {
                remoteSendToBoardVirtualDriver(KEYCODE_MUTE_DRIVER, (short) 1);
            }
            else
            {
                if (event_type != 2)
                    return;
                remoteSendToBoardVirtualDriver(KEYCODE_MUTE_DRIVER, (short) 0);
            }
        }
        else
            sendLongPressKeyRequest(keycode, (short) event_type);
    }
    
    private void remoteSendToBoardVirtualDriver(int keycode, short event_type)
    {
        KeyboardCommand command = new KeyboardCommand();
        command.setKeyValue(keycode);
        command.setFunctionKey(event_type);
        if (null == udpClient)
        {
            udpClient = new EasybusUdp(mIP, EasyConstants.REMOTE_PORT);
            try
            {
                udpClient.connect();
            } catch (SocketException e)
            {
                e.printStackTrace();
            } catch (UnknownHostException e)
            {
                e.printStackTrace();
            }
        }
        try
        {
            udpClient.send(command, mIP);
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    
    private void sendLongPressKeyRequest(int key_value, short event_type)
    {
        KeyboardCommand command = new KeyboardCommand();
        command.setKeyValue(key_value);
        // command.setFunctionKey(event_type);
        command.setFunctionKey(0);
        command.setAction(event_type);
        if (null == udpClient)
        {
            udpClient = new EasybusUdp(mIP, EasyConstants.REMOTE_PORT);
            try
            {
                udpClient.connect();
            } catch (SocketException e)
            {
                e.printStackTrace();
            } catch (UnknownHostException e)
            {
                e.printStackTrace();
            }
        }
        try
        {
            udpClient.send(command, mIP);
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    
    public void release()
    {
        if (udpClient != null)
        {
            try
            {
                udpClient.disconnect();
                udpClient = null;
            } catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }
}
