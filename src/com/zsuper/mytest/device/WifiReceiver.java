package com.zsuper.mytest.device;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo;
import android.net.NetworkInfo.DetailedState;
import android.net.wifi.WifiManager;

import com.zsuper.mytest.utils.ILog;

/**
 * 开机启动
 * <功能描述>
 * @author  ZhouYuChao/907753
 * @version  [版本号, 2014-11-25]
 * @since  [产品/模块版本]
 */
public class WifiReceiver extends BroadcastReceiver {
    private static final String ACTION_BOOT_COMPLETED = "android.intent.action.BOOT_COMPLETED";

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        ILog.i("BootReceiver", "onReceive : " + action);
        
        if (ACTION_BOOT_COMPLETED.equals(action)) {
            ILog.d("BootReceiver", "onReceive ACTION_BOOT_COMPLETED --->");
//            startService(context);
            
        } else if (WifiManager.NETWORK_STATE_CHANGED_ACTION.equals(action)) {
            DetailedState wifiState = ((NetworkInfo) intent
                    .getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO))
                    .getDetailedState();
            if (wifiState == DetailedState.CONNECTED) {
                ILog.d("BootReceiver", "onReceive ---> CONNECTED");
//                startService(context);
                
            } else if (wifiState == DetailedState.DISCONNECTED
                    || wifiState == DetailedState.FAILED) {
                ILog.d("BootReceiver", "onReceive ---> DISCONNECTED | FAILED");
//                stopService(context);
                
            } else {
                ILog.d("BootReceiver", "onReceive ---> unknow...");
            }
        }
    }

    private void startService(Context context) {
        Intent serviceIntent = new Intent();
//        serviceIntent.setAction("com.coship.mmkbox_service");
//        serviceIntent.setClass(context, OlaDlnaService.class);
        context.startService(serviceIntent);
    }

    private void stopService(Context context) {
        Intent serviceIntent = new Intent();
//        serviceIntent.setAction("com.coship.mmkbox_service");
//        serviceIntent.setClass(context, OlaDlnaService.class);
        context.stopService(serviceIntent);
    }
}
