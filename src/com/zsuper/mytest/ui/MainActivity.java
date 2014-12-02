package com.zsuper.mytest.ui;

import android.os.Bundle;

import com.zsuper.mytest.R;
import com.zsuper.mytest.utils.ILog;

public class MainActivity extends BaseActivity
{
    private static final String TAG = MainActivity.class.getSimpleName();
    
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        ILog.i(TAG, "onCreate --->");
        setContentView(R.layout.activity_main);
        
    }
    
    @Override
    protected void onResume()
    {
        super.onResume();
        ILog.i(TAG, "onResume --->");
    }
    
    @Override
    protected void onPause()
    {
        super.onPause();
        ILog.i(TAG, "onPause --->");
    }
    
    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        ILog.i(TAG, "onDestroy --->");
    }
    
}
