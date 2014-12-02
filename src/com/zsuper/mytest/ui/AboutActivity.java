package com.zsuper.mytest.ui;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.zsuper.mytest.R;
import com.zsuper.mytest.utils.Utils;

public class AboutActivity extends BaseActivity
{
    private static final String TAG = AboutActivity.class.getSimpleName();
    
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        
        initTitle();
    }
    
    @Override
    protected void onResume()
    {
        super.onResume();
    }
    
    @Override
    protected void onPause()
    {
        super.onPause();
    }
    
    @Override
    protected void onDestroy()
    {
        super.onDestroy();
    }
    
    private void initTitle()
    {
        RelativeLayout titleLayout = (RelativeLayout) findViewById(R.id.about_title_layout);
        
        TextView title = (TextView) titleLayout.findViewById(R.id.title_content);
        title.setText(R.string.menu_about);
        
        ImageButton backBtn = (ImageButton) titleLayout.findViewById(R.id.title_back_btn);
        backBtn.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                finishWithAnim();
            }
        });
    }
    
    @Override
    public void onBackPressed()
    {
        finishWithAnim();
    }
    
    private void finishWithAnim()
    {
        finish();
        Utils.changeViewOutAnim(AboutActivity.this);
    }
}
