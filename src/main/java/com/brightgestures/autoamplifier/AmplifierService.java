package com.brightgestures.autoamplifier;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.widget.Toast;

import com.brightgestures.autoamplifier.util.DataSender;

import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EService;

import java.io.IOException;

@EService
public class AmplifierService extends Service {

    private static final int NOTIFICATION_ID = 55;

    private BroadcastReceiver br = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String mode = intent.getStringExtra(DataSender.VALUE);
            if (mode != null) {
                if (mode.equals(DataSender.MIC_LOW)) {
                    amplifier.setMicLow(intent.getIntExtra(DataSender.MIC_LOW, 1));
                } else if (mode.equals(DataSender.MIC_HIGH)) {
                    amplifier.setMicHigh(intent.getIntExtra(DataSender.MIC_HIGH, 1));
                } else if (mode.equals(DataSender.VOLUME_LOW)) {
                    amplifier.setVolumeLow(intent.getIntExtra(DataSender.VOLUME_LOW, 1));
                } else if (mode.equals(DataSender.VOLUME_HIGH)) {
                    amplifier.setVolumeHigh(intent.getIntExtra(DataSender.VOLUME_HIGH, 1));
                } else if (mode.equals(DataSender.RESET)) {
                    if (intent.getBooleanExtra(DataSender.RESET, false)) {
                        amplifier.resetValues();
                    }
                }
                amplifier.UIChangePerformed();
            }
        }

    };

    @Bean
    Amplifier amplifier;

    private boolean amplifyingThreadRunning = true;

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        if (getApplicationContext() != null) {
            try {
                amplifier.init();
                amplifier.initialiseMicArray();
            } catch (IOException e) {
                Toast.makeText(getApplicationContext(), "Couldn't start service", Toast.LENGTH_LONG)
                        .show();
            }
        }
    }

    @SuppressWarnings("deprecation")
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        registerReceiver(br, new IntentFilter("toService"));
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
        Notification notification = new Notification(R.drawable.ic_stat_loudspeaker,
                getString(R.string.notification_title), System.currentTimeMillis());
        notification.flags |= Notification.FLAG_ONGOING_EVENT;
        notification.setLatestEventInfo(getApplicationContext(),
                getString(R.string.app_name), getString(R.string.notification_title),
                PendingIntent.getActivity(this, 0, new Intent(this,
                        MainActivity.class)
                        .setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0));
        startForeground(NOTIFICATION_ID, notification);
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(br);
        amplifyingThreadRunning = false;
        if (amplifier != null) {
            amplifier.onStop();
        }
    }
}
