/*
    AutoAmplifier - Android application that changes media volume according to noise in the surroundings.
    Copyright (C) 2014  Brightify s.r.o.

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.brightify.autoamplifier.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import org.brightify.autoamplifier.Amplifier;

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
        if (value != -1 && isSavingEnabled()) {
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

    public boolean isSavingEnabled() {
        return sharedPreferences.getBoolean(SAVE_VALUES, SAVE_DEFAULT);
    }
}
