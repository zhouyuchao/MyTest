package com.zsuper.mytest.procotol.bean;

import java.util.ArrayList;

public class MultiTouchInfo
{
    private static final String TAG = "MultiTouchInfo";
    public static final int MAX_FINGER_NUM = 5;
    private int fingerNum = 0;
    private ArrayList<FingerInfo> fingers = new ArrayList<FingerInfo>();
    
    public MultiTouchInfo()
    {
        for (int i = 0; i < 5; ++i)
        {
            this.fingers.add(new FingerInfo());
        }
    }
    
    public void setFingerNum(int num)
    {
        this.fingerNum = num;
    }
    
    public int getFingerNum()
    {
        return this.fingerNum;
    }
    
    /**
     * 设置手指信息
     * 
     * @param index 手指下标
     * @param x 手指X坐标
     * @param y 手指y坐标
     * @param press 手指是按下还是抬起 1：按下，2：抬起
     */
    public void setFingerInfo(int index, int x, int y, int press)
    {
        FingerInfo info = (FingerInfo) this.fingers.get(index);
        info.setX(x);
        info.setY(y);
        info.setPress(press);
    }
    
    public FingerInfo getFingerInfo(int index)
    {
        return (FingerInfo) this.fingers.get(index);
    }
    
    public void print()
    {
        // Log.d(TAG, "finger Num: " + this.fingerNum);
        for (int i = 0; i < 5; ++i)
        {
            FingerInfo info = (FingerInfo) this.fingers.get(i);
            // Log.d(TAG, "finger " + i + " x: " + info.getX() + " y: " + info.getY() + " press: " + info.getPress());
        }
    }
}
