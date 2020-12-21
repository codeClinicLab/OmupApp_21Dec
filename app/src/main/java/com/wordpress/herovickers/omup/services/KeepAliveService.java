package com.wordpress.herovickers.omup.services;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import androidx.annotation.Nullable;
import android.util.Log;

import com.plivo.endpoint.Incoming;
import com.plivo.endpoint.Outgoing;
import com.wordpress.herovickers.omup.utility.Phone;
import com.wordpress.herovickers.omup.utility.Listeners.EndPointListner;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Siva on 19/03/18.
 */

public class KeepAliveService extends Service implements EndPointListner {

    final Handler handler = new Handler();
    Timer timer = new Timer();

    @Override
    public void onCreate() {
        super.onCreate();

        TimerTask doAsynchronousTask = new TimerTask() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    @SuppressWarnings("unchecked")
                    public void run() {
                        try {
                            Log.d("SivaCherukuri", "KeepAlive()");

                            Phone.getInstance(KeepAliveService.this).login("js1180202081542","12345");

                            Phone.getInstance(KeepAliveService.this).keepAlive();

                        }
                        catch (Exception e) {
                            // TODO Auto-generated catch block
                        }
                    }
                });
            }
        };
        timer.schedule(doAsynchronousTask, 3000,300000);

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // service On start
        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        // service finished
        super.onDestroy();
    }

    public void onLogin() {

        Log.d("KeepAlive()", "Logging in");

    }

    public void onLogout() {

        Log.d("PlivoInbound", "Logged out");

    }

    public void onLoginFailed() {

        Log.d("PlivoInbound", "Login failed");

    }

    public void onIncomingCall(Incoming incoming) {

        Log.d("KeepAlive", "OnIncomingCalled()");

        incoming.answer();
    }

    public void onIncomingCallHangup(Incoming incoming) {

    }

    public void onIncomingCallRejected(Incoming incoming) {

    }

    public void onOutgoingCall(Outgoing outgoing) {

    }

    public void onOutgoingCallAnswered(Outgoing outgoing) {

    }

    public void onOutgoingCallRejected(Outgoing outgoing) {

    }

    public void onOutgoingCallHangup(Outgoing outgoing) {

    }

    public void onOutgoingCallInvalid(Outgoing outgoing){

    }

    public void onIncomingDigitNotification(String digits) {

    }
}

