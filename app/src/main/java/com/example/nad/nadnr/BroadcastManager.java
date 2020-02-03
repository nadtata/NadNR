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
    private static final String TAG = BroadcastManager.class.getSimpleName();
    private static final String SERVER_PACKAGENAME = "com.example.nad.noridemo";

    private static final String INT_KEY = "executable";
    private static final String PACKAGENAME = "packagename";

    private static final String SERVER_RECEIVER_NAME = "com.nad.demo.server.RECEIVER";
    private static final String CLIENT_RECEIVER_NAME = "com.nad.demo.client.RECEIVER";

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

    /**
     * 클라이언트 리시버 등록
     */
    private static void registerClientReceiver(Context context) {
        Log.d(TAG, "registerClientReceiver");

        broadcastReceiverThread = new HandlerThread(HANDLERTHREAD_NAME);
        broadcastReceiverThread.start();

        broadcastReceiverLooper = broadcastReceiverThread.getLooper();
        broadcastReceiverHandler = new Handler(broadcastReceiverLooper);

        IntentFilter filter = new IntentFilter();
        filter.addAction(CLIENT_RECEIVER_NAME);
        context.registerReceiver(mReceiver, filter,
                null, broadcastReceiverHandler);
    }

    /**
     * 클라이언트 리시버 해제
     */
    private static void unregisterClientReceiver(Context context) {
        Log.d(TAG, "unregisterClientReceiver");
        context.unregisterReceiver(mReceiver);
        broadcastReceiverLooper.quit();
    }

    /**
     * 서버 리시버 송신
     */
    private static void callServerReceiver(Context context) {
        Log.d(TAG, "callServerReceiver");
        Intent intent = new Intent(SERVER_RECEIVER_NAME);
        intent.putExtra(PACKAGENAME,context.getPackageName());
        Log.d(TAG, PACKAGENAME+":"+context.getPackageName());
        context.sendBroadcast(intent);
    }


    public static void resetResult() {
        result = NB_NO_RESPONSE;
        PackageManager pm = mContext.getPackageManager();
        Intent chkInstall = pm.getLaunchIntentForPackage(SERVER_PACKAGENAME);
        if (chkInstall == null) result = NB_NOT_INSTALLED;
    }

    /**
     * 클라이언트에서 호출
     */
    public static int isExecutable(Context context) {
        Log.d(TAG, "isExecutable");
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

    /**
     * 클라이언트 브로드캐스트 리시버
     */
    private static BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG, "onReceive");
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
                Log.e(TAG,"onReceive - result :"+ result);
            }
        }
    };

}
