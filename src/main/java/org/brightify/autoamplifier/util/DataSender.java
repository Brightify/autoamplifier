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
import android.content.Intent;

import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.RootContext;

/**
 * Created by matous on 11/30/13.
 * Class that makes communication between components easier
 */
@EBean
public class DataSender {
    public static final String INTENT_TO_SERVICE = "toService";
    public static final String INTENT_TO_ACTIVITY = "toActivity";
    public final static String VOLUME_LOW = "volumeLow";
    public final static String VOLUME_HIGH = "volumeHigh";
    public final static String MIC_LOW = "micLow";
    public final static String MIC_HIGH = "micHigh";
    public final static String MIC = "mic";
    public final static String RESET = "reset";
    public final static String VALUE = "value";

    @RootContext
    Context context;
    private Intent toService;
    private Intent toActivity;

    public DataSender() {
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
