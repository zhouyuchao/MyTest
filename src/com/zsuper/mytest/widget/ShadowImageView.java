package com.zsuper.mytest.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.widget.ImageView;

public class ShadowImageView extends ImageView
{
    public ShadowImageView(Context context)
    {
        super(context);
    }
    
    public ShadowImageView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }
    
    public ShadowImageView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
    }
    
    @Override
    protected void onDraw(Canvas canvas)
    {
        
        super.onDraw(canvas);
    }
}
