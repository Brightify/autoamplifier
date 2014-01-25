package com.brightgestures.autoamplifier;

import android.content.Context;
import android.content.Intent;

/**
 * Created by matous on 11/30/13.
 */
public class DataSender {
    private static final String INTENT_TO_SERVICE = "toService";
    private static final String INTENT_TO_ACTIVITY = "toActivity";
    public final static String VOLUME_LOW = "volumeLow";
    public final static String VOLUME_HIGH = "volumeHigh";
    public final static String MIC_LOW = "micLow";
    public final static String MIC_HIGH = "micHigh";
    public final static String MIC = "mic";
    public final static String RESET = "reset";
    public final static String VALUE = "value";

    private Context context;
    private Intent toService;
    private Intent toActivity;

    public DataSender(Context context) {
        this.context = context;
        toService = new Intent(INTENT_TO_SERVICE);
        toActivity = new Intent(INTENT_TO_ACTIVITY);
    }

    public void sendLowVolume(int lowVolume) {
        toService.putExtra(VALUE, VOLUME_LOW);
        toService.putExtra(VOLUME_LOW, lowVolume);
        context.sendBroadcast(toService);
        toService = new Intent(INTENT_TO_SERVICE);
    }

    public void sendHighVolume(int highVolume) {
        toService.putExtra(VALUE, VOLUME_HIGH);
        toService.putExtra(VOLUME_HIGH, highVolume);
        context.sendBroadcast(toService);
        toService = new Intent(INTENT_TO_SERVICE);
    }

    public void sendLowMic(int lowMic) {
        toService.putExtra(VALUE, MIC_LOW);
        toService.putExtra(MIC_LOW, lowMic);
        context.sendBroadcast(toService);
        toService = new Intent(INTENT_TO_SERVICE);
    }

    public void sendHighMic(int highMic) {
        toService.putExtra(VALUE, MIC_HIGH);
        toService.putExtra(MIC_HIGH, highMic);
        context.sendBroadcast(toService);
        toService = new Intent(INTENT_TO_SERVICE);
    }

    public void sendToActivity(int mic, int micLow, int micHigh, int volumeLow, int volumeHigh) {
        toActivity.putExtra(MIC, mic);
        toActivity.putExtra(MIC_LOW, micLow);
        toActivity.putExtra(MIC_HIGH, micHigh);
        toActivity.putExtra(VOLUME_LOW, volumeLow);
        toActivity.putExtra(VOLUME_HIGH, volumeHigh);
        context.sendBroadcast(toActivity);
        toActivity = new Intent(INTENT_TO_ACTIVITY);
    }

    public void sendReset() {
        toService.putExtra(VALUE, RESET);
        toService.putExtra(RESET, true);
        context.sendBroadcast(toService);
        toService = new Intent(INTENT_TO_SERVICE);
    }

}
