package com.brightgestures.autoamplifier;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by matous on 11/27/13.
 */
public class PreferenceProvider {
    private SharedPreferences sharedPreferences;
    public final static String VOLUME_LOW = "volumeLow";
    public final static String VOLUME_HIGH = "volumeHigh";
    public final static String MIC_LOW = "micLow";
    public final static String MIC_HIGH = "micHigh";
    public final static int NO_CHANGE = -1;
    private final static int NO_DATA = -1;

    public PreferenceProvider(Context context) {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public void saveValues(int volumeLow, int volumeHigh, int micLow, int micHigh) {
        saveInteger(volumeLow, VOLUME_LOW);
        saveInteger(volumeHigh, VOLUME_HIGH);
        saveInteger(micLow, MIC_LOW);
        saveInteger(micHigh, MIC_HIGH);
    }

    private void saveInteger(int value, String key) {
        if (value != -1) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putInt(key, value);
            editor.commit();
            editor.apply();
        }
    }

    private int getInteger(String key, int data) {
        return sharedPreferences.getInt(key, data);
    }

    public int getVolumeLow() {
        return getInteger(VOLUME_LOW, Amplifier.VOLUME_DEFAULT_MIN);
    }

    public int getVolumeHigh() {
        return getInteger(VOLUME_HIGH, Amplifier.VOLUME_DEFAULT_MAX);
    }

    public int getMicLow() {
        return getInteger(MIC_LOW, Amplifier.MIC_DEFAULT_MIN);
    }

    public int getMicHigh() {
        return getInteger(MIC_HIGH, Amplifier.MIC_DEFAULT_MAX);
    }

    public void setVolumeLow(int volumeLow) {
        saveInteger(volumeLow, VOLUME_LOW);
    }

    public void setVolumeHigh(int volumeHigh) {
        saveInteger(volumeHigh, VOLUME_HIGH);
    }

    public void setMicLow(int micLow) {
        saveInteger(micLow, MIC_LOW);
    }

    public void setMicHigh(int micHigh) {
        saveInteger(micHigh, MIC_HIGH);
    }
}
