package com.zsuper.mytest.utils;

import android.util.Log;

/**
 * 打印日志类 <功能描述>
 * @author ZhouYuChao/907753
 * @version [版本号, 2014-7-18]
 * @since [产品/模块版本]
 */
public class ILog
{
    private static final String APP_TAG = "TVHelper";
    private static final boolean FLAG = true;
    
    public static void i(String TAG, String msg)
    {
        if (FLAG)
            Log.i(TAG, msg);
    }
    
    public static void d(String TAG, String msg)
    {
        if (FLAG)
            Log.d(TAG, msg);
    }
    
    public static void e(String TAG, String msg)
    {
        if (FLAG)
            Log.e(TAG, msg);
    }
    
    public static void v(String TAG, String msg)
    {
        if (FLAG)
            Log.v(TAG, msg);
    }
    
    public static void w(String TAG, String msg)
    {
        if (FLAG)
            Log.w(TAG, msg);
    }
    
    /**
     * 获取异常所在行 <功能描述>
     * 
     * @param exception
     * @return [参数说明]
     * @return int [返回类型说明]
     * @exception throws [违例类型] [违例说明]
     */
    public static int getExceptionLine(final Exception exception)
    {
        int lineNumber = 0;
        final StackTraceElement[] stackTraceElement = exception.getStackTrace();
        if (stackTraceElement != null && stackTraceElement.length > 0)
        {
            lineNumber = stackTraceElement[0].getLineNumber();
        }
        return lineNumber;
    }
}
