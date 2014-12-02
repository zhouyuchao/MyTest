package com.zsuper.mytest.utils;

public class String2Hex
{
    public static String toHexString(char[] b)
    {
        StringBuffer buffer = new StringBuffer();
        for (int i = 0; i < b.length; ++i)
        {
            buffer.append(toHexString(b[i]));
            buffer.append(" ");
        }
        return buffer.toString();
    }
    
    public static String toHexString(char b)
    {
        String s = Integer.toHexString(b & 0xFF);
        if (s.length() == 1)
        {
            return "0x0" + s;
        }
        else
        {
            return "0x" + s;
        }
    }
    
}
