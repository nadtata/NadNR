package com.example.nad.nadnr;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;

public class BroadcastManager {
    private static final String SERVER_RECEIVER = "com.nad.demo.server.RECEIVER";
    private static final String CLIENT_RECEIVER = "com.nad.demo.client.RECEIVER";

    private static final int EXECUTE            =  0;
    private static final int NB_NO_RESPONSE     = -1;
    private static final int EXPIRED            = -3;

    public static int result = NB_NO_RESPONSE;

    // 리시버 서브 스레드 이용
    private static Looper broadcastReceiverLooper = null;
    private static Handler broadcastReceiverHandler = null;
    private static HandlerThread broadcastReceiverThread = null;
    private static final String HANDLERTHREAD_NAME = "broadcastReceiverThread";


    private static void registerClientReceiver(Context context) {
        broadcastReceiverThread = new HandlerThread(HANDLERTHREAD_NAME);
        broadcastReceiverThread.start();

        broadcastReceiverLooper = broadcastReceiverThread.getLooper();
        broadcastReceiverHandler = new Handler(broadcastReceiverLooper);

        IntentFilter filter = new IntentFilter();
        filter.addAction(CLIENT_RECEIVER);
        context.registerReceiver(mReceiver, filter,
                null, broadcastReceiverHandler);
    }

    private static void unregisterClientReceiver(Context context) {
        try {
            context.unregisterReceiver(mReceiver);
        }
        catch (IllegalArgumentException e) {}
        catch (Exception e) {}
        finally {}
        broadcastReceiverLooper.quit();
    }

    private static void callServerReceiver(Context context, String appCode) {
        Intent intent = new Intent(SERVER_RECEIVER);
        intent.putExtra("appCode", appCode);
        context.sendBroadcast(intent);
    }

    public static void resetResult() {
        result = NB_NO_RESPONSE;
    }

    public static int init(Context context, String appCode) {
        resetResult();
        registerClientReceiver(context);
        callServerReceiver(context, appCode);
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        unregisterClientReceiver(context);
        return result;
    }

    private static BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle bundle = intent.getExtras();
            if (bundle != null)
            {
                result = bundle.getInt("executable");
            }
        }
    };

}
