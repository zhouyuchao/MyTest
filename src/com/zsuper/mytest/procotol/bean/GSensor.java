package com.zsuper.mytest.procotol.bean;

import java.nio.ByteBuffer;

public class GSensor
{
    protected int mType;
    protected float mX, mY, mZ;
    
    public GSensor()
    {
        mType = 0;
        mX = mY = mZ = 0;
    }
    
    public GSensor(int type, float x, float y, float z)
    {
        mType = type;
        mX = x;
        mY = y;
        mZ = z;
    }
    
    public void setType(int type)
    {
        mType = type;
    }
    
    public void setX(float x)
    {
        mX = x;
    }
    
    public void setY(float y)
    {
        mY = y;
    }
    
    public void setZ(float z)
    {
        mZ = z;
    }
    
    public int type()
    {
        return mType;
    }
    
    public float x()
    {
        return mX;
    }
    
    public float y()
    {
        return mY;
    }
    
    public float z()
    {
        return mZ;
    }
    
    public byte[] toByte()
    {
        byte[] msg = new byte[16];
        ByteBuffer msgbuf = ByteBuffer.allocate(16);
        
        if (msgbuf == null)
            return null;
        
        msgbuf.putInt(mType);
        msgbuf.putFloat(mX);
        msgbuf.putFloat(mY);
        msgbuf.putFloat(mZ);
        msgbuf.rewind();
        msgbuf.get(msg, 0, 16);
        return msg;
    }
    
    public boolean fromByteBuffer(ByteBuffer msg)
    {
        if (msg.capacity() - msg.position() < 12)
            return false;
        
        mType = msg.getInt();
        mX = msg.getFloat();
        mY = msg.getFloat();
        mZ = msg.getFloat();
        return true;
    }
}
