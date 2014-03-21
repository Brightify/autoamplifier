package com.brightgestures.autoamplifier;

import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;

import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EService;

@EService
public class AmplifierService extends Service {

    public static final String ACTION_DISABLE = "com.brightgestures.autoamplifier.disable";
    private static final int NOTIFICATION_ID = 55;
    private static final String ACTION_INCREASE = "com.brightgestures.autoamplifier.increase";
    private static final String ACTION_DECREASE = "com.brightgestures.autoamplifier.decrease";
    private BroadcastReceiver notificationReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction() != null) {
                if (intent.getAction().equals(ACTION_DISABLE)) {
                    stopSelf();
                } else if (intent.getAction().equals(ACTION_INCREASE)) {
                    amplifier.increment();
                } else if (intent.getAction().equals(ACTION_DECREASE)) {
                    amplifier.decrement();
                }
            }
        }
    };
    @Bean
    Amplifier amplifier;
    private BroadcastReceiver activityReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            amplifier.setIntent(intent);
        }

    };
    private boolean amplifyingThreadRunning = true;

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        //initialiseAmplifier();
        registerReceiver(activityReceiver, new IntentFilter("toService"));
        IntentFilter filter = new IntentFilter(ACTION_DISABLE);
        filter.addAction(ACTION_DECREASE);
        filter.addAction(ACTION_INCREASE);
        registerReceiver(notificationReceiver, filter);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        initialiseAmplifier();
        amplifyingThreadRunning = true;
        Thread amplifyingThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (amplifyingThreadRunning) {
                    amplifier.amplify();
                }
            }
        });
        amplifyingThread.start();

        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext());
        builder.setSmallIcon(R.drawable.ic_notification);
        builder.setContentTitle(getString(R.string.app_name));
        builder.setContentText(getString(R.string.notification_title));
        builder.setOngoing(true);
        builder.setContentIntent(PendingIntent.getActivity(this, 0, new Intent(this,
                MainActivity_.class)
                .setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0));
        builder.addAction(R.drawable.ic_stat_disable, "",
                PendingIntent.getBroadcast(this, 0, new Intent(ACTION_DISABLE), 0));
        builder.addAction(R.drawable.ic_stat_increase, "",
                PendingIntent.getBroadcast(this, 0, new Intent(ACTION_INCREASE), 0));
        builder.addAction(R.drawable.ic_stat_decrease, "",
                PendingIntent.getBroadcast(this, 0, new Intent(ACTION_DECREASE), 0));
        builder.setPriority(50);

        startForeground(NOTIFICATION_ID, builder.build());
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(activityReceiver);
        unregisterReceiver(notificationReceiver);
        amplifyingThreadRunning = false;
        if (amplifier != null) {
            amplifier.onStop();
        }
    }

    private void initialiseAmplifier() {
        amplifier.initialiseMicArray();
    }
}
