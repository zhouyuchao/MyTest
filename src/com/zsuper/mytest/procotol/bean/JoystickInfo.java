package com.zsuper.mytest.procotol.bean;

import android.util.Log;

/**
 * 游戏手柄键值信息
 * <br>驱动键值：
 * <br>0x80x 0x80x 0x80x 0x80x 0xfx  0x0x  0x0x   普通
 * <br>0x80x 0x80x 0x80x 0x0x  0xfx  0x0x  0x0x   上键
 * <br>0x80x 0x80x 0x80x 0xffx 0xfx  0x0x  0x0x   下键
 * <br>0x80x 0x80x 0x0x  0x80x 0xfx  0x0x  0x0x   左键
 * <br>0x80x 0x80x 0xffx 0x80x 0xfx  0x0x  0x0x   右键
 * <br>0x80x 0x80x 0x80x 0x80x 0x1fx 0x0x  0x0x   面 1键
 * <br>0x80x 0x80x 0x80x 0x80x 0x2fx 0x0x  0x0x   面 2键
 * <br>0x80x 0x80x 0x80x 0x80x 0x4fx 0x0x  0x0x   面 3键
 * <br>0x80x 0x80x 0x80x 0x80x 0x8fx 0x0x  0x0x   面 4键
 * <br>0x80x 0x80x 0x80x 0x80x 0xfx  0x1x  0x0x   左 1键
 * <br>0x80x 0x80x 0x80x 0x80x 0xfx  0x4x  0x0x   左 2键
 * <br>0x80x 0x80x 0x80x 0x80x 0xfx  0x2x  0x0x   右 1键
 * <br>0x80x 0x80x 0x80x 0x80x 0xfx  0x8x  0x0x   右 2键
 * <br>0x80x 0x80x 0x80x 0x80x 0xfx  0x10x 0x0x   9
 * <br>0x80x 0x80x 0x80x 0x80x 0xfx  0x20x 0x0x   home
 * <br>0x80x 0x80x 0x80x 0x80x 0xfx  0x0x  0x1x   ANALOG变换
 * @author  ZhouYuChao/907753
 * @version  [版本号, 2014-9-18]
 * @since  [产品/模块版本]
 */
public class JoystickInfo {
    private static final String TAG = JoystickInfo.class.getSimpleName();

    public static final int JS_KEYDOWN = 0;
    public static final int JS_KEYUP   = 1;

    public static final int JS_KEYCODE_UNKNOW  = 0;
    public static final int JS_KEYCODE_UP      = 1;
    public static final int JS_KEYCODE_RIGHT   = 2;
    public static final int JS_KEYCODE_DOWN    = 3;
    public static final int JS_KEYCODE_LEFT    = 4;
    public static final int JS_KEYCODE_L_U     = 5;
    public static final int JS_KEYCODE_U_R     = 6;
    public static final int JS_KEYCODE_R_D     = 7;
    public static final int JS_KEYCODE_D_L     = 8;

    public static final int JS_KEYCODE_ONE     = 0;
    public static final int JS_KEYCODE_TWO     = 1;
    public static final int JS_KEYCODE_THREE   = 2;
    public static final int JS_KEYCODE_FOUR    = 3;

    public static final int JS_KEYCODE_L1   = 4;
    public static final int JS_KEYCODE_R1   = 5;
    public static final int JS_KEYCODE_L2 = 6;
    public static final int JS_KEYCODE_R2  = 7;

    public static final int JS_KEYCODE_START       = 8;
    public static final int JS_KEYCODE_HOME    = 9;
    public static final int JS_KEYCODE_ANALOG  = 10;

    private static char[] mJoystickKey = new char[] { 0x80, 0x80, 0x80, 0x80, 0xf, 0x0, 0x0 };

    public static char[] getJoystickArrowAction(int keyCode){
        switch (keyCode) {
        case JS_KEYCODE_UP:
            mJoystickKey[2] = 0x80;
            mJoystickKey[3] = 0x00;
            break;
        case JS_KEYCODE_DOWN:
            mJoystickKey[2] = 0x80;
            mJoystickKey[3] = 0xff;
            break;
        case JS_KEYCODE_LEFT:
            mJoystickKey[2] = 0x00;
            mJoystickKey[3] = 0x80;
            break;
        case JS_KEYCODE_RIGHT:
            mJoystickKey[2] = 0xff;
            mJoystickKey[3] = 0x80;
            break;
        case JS_KEYCODE_L_U:
            mJoystickKey[2] = 0x00;
            mJoystickKey[3] = 0x00;
            break;
        case JS_KEYCODE_U_R:
            mJoystickKey[2] = 0xff;
            mJoystickKey[3] = 0x00;
            break;
        case JS_KEYCODE_R_D:
            mJoystickKey[2] = 0xff;
            mJoystickKey[3] = 0xff;
            break;
        case JS_KEYCODE_D_L:
            mJoystickKey[2] = 0x00;
            mJoystickKey[3] = 0xff;
            break;
        case JS_KEYCODE_UNKNOW:
            mJoystickKey[2] = 0x80;
            mJoystickKey[3] = 0x80;
            break;
        default:
            Log.e(TAG, "getJoystickArrowAction unkown keycode : " + keyCode);
            break;
        }
        return mJoystickKey;
    }
    
    public static char[] getJoystickBtnAction(int keyType, int keyCode) {
//        Log.d(TAG, "getJoystickAction keyType : " + keyType + ", keyCode : " + keyCode);

        char[] joystickAction = null;

        switch (keyType) {
        case JS_KEYDOWN:
            joystickAction = handleOnKeyDown(keyCode);
            break;
        case JS_KEYUP:
            joystickAction = handleOnKeyUp(keyCode);
            break;
        default:
            break;
        }
        
//        Log.d(TAG, "getJoystickAction : " + joystickAction);

        return joystickAction;
    }

    private static char[] handleOnKeyDown(int keyCode) {
        switch (keyCode) {
        case JS_KEYCODE_ONE:
            mJoystickKey[4] += 0x10;
            break;
        case JS_KEYCODE_TWO:
            mJoystickKey[4] += 0x20;
            break;
        case JS_KEYCODE_THREE:
            mJoystickKey[4] += 0x40;
            break;
        case JS_KEYCODE_FOUR:
            mJoystickKey[4] += 0x80;
            break;
            
        case JS_KEYCODE_L1:
            mJoystickKey[5] += 0x1;
            break;
        case JS_KEYCODE_L2:
            mJoystickKey[5] += 0x4;
            break;
        case JS_KEYCODE_R1:
            mJoystickKey[5] += 0x2;
            break;
        case JS_KEYCODE_R2:
            mJoystickKey[5] += 0x8;
            break;
            
        case JS_KEYCODE_START:
            mJoystickKey[5] += 0x10;
            break;
        case JS_KEYCODE_HOME:
            mJoystickKey[5] += 0x20;
            break;
        case JS_KEYCODE_ANALOG:
            mJoystickKey[6] = 0x1;
            break;
        default:
            Log.e(TAG, "handleOnKeyDown unkown keycode : " + keyCode);
            break;
        }
        return mJoystickKey;
    }
    
    private static char[] handleOnKeyUp(int keyCode){
        switch (keyCode) {
        case JS_KEYCODE_ONE:
            mJoystickKey[4] -= 0x10;
            break;
        case JS_KEYCODE_TWO:
            mJoystickKey[4] -= 0x20;
            break;
        case JS_KEYCODE_THREE:
            mJoystickKey[4] -= 0x40;
            break;
        case JS_KEYCODE_FOUR:
            mJoystickKey[4] -= 0x80;
            break;
            
        case JS_KEYCODE_L1:
            mJoystickKey[5] -= 0x1;
            break;
        case JS_KEYCODE_L2:
            mJoystickKey[5] -= 0x4;
            break;
        case JS_KEYCODE_R1:
            mJoystickKey[5] -= 0x2;
            break;
        case JS_KEYCODE_R2:
            mJoystickKey[5] -= 0x8;
            break;
            
        case JS_KEYCODE_START:
            mJoystickKey[5] -= 0x10;
            break;
        case JS_KEYCODE_HOME:
            mJoystickKey[5] -= 0x20;
            break;
        case JS_KEYCODE_ANALOG:
            mJoystickKey[6] = 0x0;
            break;
        default:
            Log.e(TAG, "handleOnKeyUp unkown keycode : " + keyCode);
            break;
        }
        return mJoystickKey;
    }
}
