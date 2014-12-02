package com.zsuper.mytest.procotol.bean;

import java.nio.ByteBuffer;

public class Mouse
{
    //action
    public static final int ACTION_DOWN = 0x0000;
    public static final int ACTION_UP = 0x0001;
    public static final int ACTION_SINGLE_CLICK = 0x0002;
    public static final int ACTION_DOUBLE_CLICK = 0x0003;
    public static final int ACTION_MOVE = 0x0004;
    public static final int ACTION_ROLL = 0x0005;
    
    //add by yw start 2013-10-16
    public static final int ACTION_DOWN_MOVE = 0x0006;
    //end
    
    //type
    public static final int MOUSE_LEFT = 1;
    public static final int MOUSE_RIGHT = 2;
    public static final int MOUSE_MID = 4;
    public static final int MOUSE_ROLL_UP = 8;
    public static final int MOUSE_ROLL_DOWN = 16;
    public static final int MOUSE_NONE = 32;
    private int mX;
    private int mY;
    private int mAction;
    private int mType;
    
    public Mouse()
    {
        mX = mY = mAction = mType = -1;
    }
    
    public Mouse(int action, int type, int x, int y)
    {
        mAction = action;
        mType = type;
        mX = x;
        mY = y;
    }
    
    public int action()
    {
        return mAction;
    }
    
    public int type()
    {
        return mType;
    }
    
    public int x()
    {
        return mX;
    }
    
    public int y()
    {
        return mY;
    }
    
    public void setAction(int action)
    {
        mAction = action;
    }
    
    public void setType(int type)
    {
        mType = type;
    }
    
    public void move(int x, int y)
    {
        mX = x;
        mY = y;
    }
    
    public byte[] toByte()
    {
        byte[] msg = new byte[16];
        ByteBuffer msgbuf = ByteBuffer.allocate(16);
        
        if (msgbuf == null)
            return null;
        
        msgbuf.putInt(mX);
        msgbuf.putInt(mY);
        msgbuf.putInt(mAction);
        msgbuf.putInt(mType);
        msgbuf.rewind();
        msgbuf.get(msg, 0, 16);
        return msg;
    }
    
    public boolean fromByteBuffer(ByteBuffer buf)
    {
        if (buf.capacity() - buf.position() < 16)
        {
            return false;
        }
        
        mX = buf.getInt();
        mY = buf.getInt();
        mAction = buf.getInt();
        mType = buf.getInt();
        return true;
    }
}
