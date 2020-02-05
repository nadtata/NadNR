package com.example.nad.nadnr;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.util.Log;

public class BroadcastManager {
    private static final String SERVER_PACKAGENAME = "com.example.nad.noridemo";    // 고부장 어플 패키지명

    private static final String INT_KEY = "executable";
    private static final String GAME_CODE = "G100100101"; // 게임코드

    private static final String SERVER_RECEIVER_NAME = "com.nad.demo.server.RECEIVER";  // 고부장 어플 리시버명
    private static final String CLIENT_RECEIVER_NAME = "com.nad.demo.client.RECEIVER";  // 게임 어플 리시버명

    private static final int EXECUTE            =  0;
    private static final int NB_NO_RESPONSE     = -1;
    private static final int NB_NOT_INSTALLED   = -2;
    private static final int EXPIRED            = -3;

    // 리시버 서브 스레드 이용
    private static Looper broadcastReceiverLooper = null;
    private static Handler broadcastReceiverHandler = null;
    private static HandlerThread broadcastReceiverThread = null;
    private static final String HANDLERTHREAD_NAME = "broadcastReceiverThread";

    public static int result = NB_NO_RESPONSE;
    private static Context mContext;

    private static void registerClientReceiver(Context context) {
        broadcastReceiverThread = new HandlerThread(HANDLERTHREAD_NAME);
        broadcastReceiverThread.start();

        broadcastReceiverLooper = broadcastReceiverThread.getLooper();
        broadcastReceiverHandler = new Handler(broadcastReceiverLooper);

        IntentFilter filter = new IntentFilter();
        filter.addAction(CLIENT_RECEIVER_NAME);
        context.registerReceiver(mReceiver, filter,
                null, broadcastReceiverHandler);
    }

    private static void unregisterClientReceiver(Context context) {
        context.unregisterReceiver(mReceiver);
        broadcastReceiverLooper.quit();
    }

    private static void callServerReceiver(Context context) {
        Intent intent = new Intent(SERVER_RECEIVER_NAME);
        intent.putExtra("GAME_CODE", GAME_CODE);
        context.sendBroadcast(intent);
    }

    public static void resetResult() {
        result = NB_NO_RESPONSE;
        PackageManager pm = mContext.getPackageManager();
        Intent chkInstall = pm.getLaunchIntentForPackage(SERVER_PACKAGENAME);
        if (chkInstall == null) result = NB_NOT_INSTALLED;
    }

    public static int isExecutable(Context context) {
        mContext = context;

        resetResult();
        registerClientReceiver(mContext);
        callServerReceiver(mContext);

        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        unregisterClientReceiver(mContext);
        return result;
    }

    private static BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle bundle = intent.getExtras();
            if (bundle != null)
            {
                switch (bundle.getInt(INT_KEY))
                {
                    case 0:
                        result = EXECUTE;
                        break;
                    case -3:
                        result = EXPIRED;
                        break;
                    default:
                        break;
                }
            }
        }
    };

}
