package com.brightgestures.autoamplifier.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.brightgestures.autoamplifier.Amplifier;

import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.RootContext;

/**
 * Created by matous on 11/27/13.
 * Class that makes accessing preferences easier
 */

@EBean
public class PreferenceProvider {

    public final static String VOLUME_LOW = "volumeLow";
    public final static String VOLUME_HIGH = "volumeHigh";
    public final static String MIC_LOW = "micLow";
    public final static String MIC_HIGH = "micHigh";
    public final static String SAVE_VALUES = "saveValues";
    private final boolean SAVE_DEFAULT = true;
    public final static int NO_CHANGE = -1;
    private final static int NO_DATA = -1;

    @RootContext
    Context context;

    private SharedPreferences sharedPreferences;

    public PreferenceProvider() {

    }

    @AfterInject
    void init() {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public void saveValues(int volumeLow, int volumeHigh, int micLow, int micHigh) {
        saveInteger(volumeLow, VOLUME_LOW);
        saveInteger(volumeHigh, VOLUME_HIGH);
        saveInteger(micLow, MIC_LOW);
        saveInteger(micHigh, MIC_HIGH);
    }

    private void saveInteger(int value, String key) {
        if (value != -1 && getSaveValues()) {
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

    public void setVolumeLow(int volumeLow) {
        saveInteger(volumeLow, VOLUME_LOW);
    }

    public int getVolumeHigh() {
        return getInteger(VOLUME_HIGH, Amplifier.VOLUME_DEFAULT_MAX);
    }

    public void setVolumeHigh(int volumeHigh) {
        saveInteger(volumeHigh, VOLUME_HIGH);
    }

    public int getMicLow() {
        return getInteger(MIC_LOW, Amplifier.MIC_DEFAULT_MIN);
    }

    public void setMicLow(int micLow) {
        saveInteger(micLow, MIC_LOW);
    }

    public int getMicHigh() {
        return getInteger(MIC_HIGH, Amplifier.MIC_DEFAULT_MAX);
    }

    public void setMicHigh(int micHigh) {
        saveInteger(micHigh, MIC_HIGH);
    }

    public void resetPreferences() {
        saveValues(Amplifier.VOLUME_DEFAULT_MIN, Amplifier.VOLUME_DEFAULT_MAX,
                Amplifier.MIC_DEFAULT_MIN, Amplifier.MIC_DEFAULT_MAX);
    }

    public void setSaveValues(boolean save) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(SAVE_VALUES, save);
        editor.commit();
    }

    public boolean getSaveValues() {
        return sharedPreferences.getBoolean(SAVE_VALUES, SAVE_DEFAULT);
    }
}
