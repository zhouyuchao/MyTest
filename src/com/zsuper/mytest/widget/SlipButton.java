package com.zsuper.mytest.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

import com.zsuper.mytest.R;

/**
 * <b>可滑动开关</b>
 * </br></br>
 * 实现类似Switch的效果，可滑动切换按键状态，可单击切换状态
 * @author ZhouYuChao/907753
 * @version [版本号, 2014-11-24]
 * @since [产品/模块版本]
 */
public class SlipButton extends View implements OnTouchListener
{
    private static final String TAG = SlipButton.class.getSimpleName();
    
    private OnChangedListener listener;
    private Bitmap mBtnOpen;
    private Bitmap mBtnClose;
    private Bitmap mSlipBtn;
    
    private float mCurrentX; // 当前的x
    private float mTouchX;
    
    private boolean mIsChecked;   // 记录当前按钮是否打开,true为打开,flase为关闭
    private boolean mIsSliping = false;// 记录用户是否在滑动的变量
    
    private boolean mIsToChanged;
    
    public SlipButton(Context context)
    {
        super(context);
        init();
    }
    
    public SlipButton(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        init();
    }
    
    private void init()
    { 
        mIsChecked = false;
        mIsToChanged = false;
        
        mBtnOpen = BitmapFactory.decodeResource(getResources(), R.drawable.settings_switch_open);
        mBtnClose = BitmapFactory.decodeResource(getResources(), R.drawable.settings_switch_close);
        mSlipBtn = BitmapFactory.decodeResource(getResources(), R.drawable.settings_switch_btn);
        
        setOnTouchListener(this); // 设置监听器,也可以直接复写OnTouchEvent
    }
    
    @Override
    protected void onDraw(Canvas canvas)
    {
//        ILog.i(TAG, "onDraw");
        
        Bitmap bitmap = mBtnClose;
        float width = mBtnOpen.getWidth() - mSlipBtn.getWidth();
        float x = mCurrentX - mSlipBtn.getWidth() / 2;
        
        if (x < width / 2) {
            bitmap = mBtnClose;
        } else {
            bitmap = mBtnOpen;
        }
        
        if (x < 0) {
            x = 0;
        }
        
        if (x > width) {
            x =  width;
        }
        
        Paint paint = new Paint();
        canvas.drawBitmap(bitmap, 0, 0, paint);
        canvas.drawBitmap(mSlipBtn, x, 0, paint);
        super.onDraw(canvas);
    }
    
    public boolean onTouch(View v, MotionEvent event)
    {
        float width = mBtnOpen.getWidth();
        
        switch (event.getAction())
        {// 根据动作来执行代码
        case MotionEvent.ACTION_DOWN:// 按下
//            ILog.i(TAG, "onTouch DOWN");
            if (0 <= event.getX() && event.getX() <= mBtnOpen.getWidth() 
                    && 0 <= event.getY() && event.getY() <= mBtnOpen.getHeight())
            {
                mCurrentX = event.getX();
                mIsSliping = true;
                
                // 实现类似单击切换状态的效果
                mTouchX = mCurrentX;
                mIsToChanged = true;
            }
            else
            {
                mIsSliping = false;
            }
            break;
            
        case MotionEvent.ACTION_MOVE:// 滑动
//            ILog.i(TAG, "onTouch MOVE");
            
            // 实现类似单击切换状态的效果
            if (mTouchX < width / 2) {
                if (event.getX() > width / 2) {
                    mIsToChanged = false;
                }
            } else {
                if (event.getX() < width / 2) {
                    mIsToChanged = false;
                }
            }
            
            if (mIsSliping) 
            {
                mCurrentX = event.getX();
            }
            break;
            
        case MotionEvent.ACTION_UP:// 松开
//            ILog.i(TAG, "onTouch UP");
            mIsSliping = false;
            
            // 实现类似单击切换状态的效果
            if (mIsToChanged) {
                if (mIsChecked == false && listener != null) {
                    mCurrentX = width;
                    listener.onCheckStateChanged(true);
                    mIsChecked = true;
                } else if (mIsChecked == true && listener != null) {
                    mCurrentX = 0;
                    listener.onCheckStateChanged(false);
                    mIsChecked = false;
                }
                break;
            }
            
            if (mCurrentX > width / 2) {
                mCurrentX = width;
                if (mIsChecked == false && listener != null) {
                    listener.onCheckStateChanged(true);
                    mIsChecked = true;
                }
            } else {
                mCurrentX = 0;
                if (mIsChecked == true && listener != null) {
                    listener.onCheckStateChanged(false);
                    mIsChecked = false;
                }
            }
            break;
            
        default:
            break;
        }
        invalidate();
        return true; // super.onTouchEvent(event);
    }
    
    public void setOnChangedListener(OnChangedListener l)
    {
        // 设置监听器,当状态修改的时候
        listener = l;
    }
    
    public interface OnChangedListener
    {
        abstract void onCheckStateChanged(boolean check);
    }
    
    public void setCheck(boolean isChecked)
    {
        this.mIsChecked = isChecked;
        
        if (mIsChecked == false && listener != null) {
            mCurrentX = 0;
            listener.onCheckStateChanged(false);
        } else if (mIsChecked == true && listener != null) {
            mCurrentX = mBtnOpen.getWidth();
            listener.onCheckStateChanged(true);
        }
        
        invalidate();
    }
    
    public boolean isCheck()
    {
        return mIsChecked;
    }
    
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
    {
        int measuredHeight = measureHeight(heightMeasureSpec);
        int measuredWidth = measureWidth(widthMeasureSpec);
        setMeasuredDimension(measuredWidth, measuredHeight);
    }
    
    private int measureHeight(int measureSpec)
    {
        return mBtnOpen.getHeight();
    }
    
    private int measureWidth(int measureSpec)
    {
        return mBtnOpen.getWidth();
    }
}
