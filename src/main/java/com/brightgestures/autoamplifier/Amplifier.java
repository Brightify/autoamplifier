package com.brightgestures.autoamplifier;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaRecorder;

import com.brightgestures.autoamplifier.util.DataSender;
import com.brightgestures.autoamplifier.util.PreferenceProvider;

import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.RootContext;
import org.androidannotations.annotations.SystemService;

import java.io.IOException;

/**
 * Created by matous on 11/27/13.
 * Class that changes volume
 */
@EBean
public class Amplifier {
    private static final int MUSIC_STREAM = AudioManager.STREAM_MUSIC;
    private static final int DEFAULT_DELAY_INTERVAL = 100;
    private static final int MIC_ARRAY_LENGTH = 50;
    private static final double VOLUME_CHANGE = 1.2;
    public static final int MIC_DEFAULT_MIN = 2000;
    public static final int MIC_DEFAULT_MAX = 32767;
    public static final int VOLUME_DEFAULT_MIN = 2;
    public static final int VOLUME_DEFAULT_MAX = 15;

    @SystemService
    AudioManager audioManager;

    @RootContext
    Context context;

    @Bean
    DataSender dataSender;

    @Bean
    PreferenceProvider preferenceProvider;

    private MediaRecorder mediaRecorder;

    //amplifier settings variables
    private int volumeLow;
    private int volumeHigh;
    private int micLow;
    private int micHigh;
    private int currentVolume;
    private int lastVolume;
    private int delayInterval = DEFAULT_DELAY_INTERVAL; //100 ms
    private int[] micArray = new int[MIC_ARRAY_LENGTH];
    private boolean UIChangePerformed = false;
    private boolean mediaRecorderInitialised = false;

    public Amplifier() {

    }

    public void init() throws IOException {
        preferenceProvider.init();
        currentVolume = audioManager.getStreamVolume(MUSIC_STREAM);
        lastVolume = audioManager.getStreamVolume(MUSIC_STREAM);
        micLow = preferenceProvider.getMicLow();
        micHigh = preferenceProvider.getMicHigh();
        volumeLow = preferenceProvider.getVolumeLow();
        volumeHigh = preferenceProvider.getVolumeHigh();
        if (!mediaRecorderInitialised) {
            initialiseMediaRecorder();
            mediaRecorderInitialised = true;
        }
        initialiseMicArray();
    }

    public void amplify() {
        for (int i = 0; i < MIC_ARRAY_LENGTH; i++) {
            micArray[i] = getAmplitude();
            currentVolume = audioManager.getStreamVolume(MUSIC_STREAM);
            setDelayInterval(i);
            initialiseVolumes();
            setVolume(calculateVolume());
            lastVolume = calculateVolume();
            dataSender.sendToActivity(getAverageMic(), micLow, micHigh, volumeLow, volumeHigh);
            delay();
        }
    }

    private int calculateVolume() {
        int volume = lastVolume;
        double calculatedVolume = (float) volumeLow + ((float) getAverageMic() - (float) micLow) /
                ((float) micHigh - (float) micLow) * ((float) volumeHigh - (float) volumeLow);
        if (Math.abs(calculatedVolume) > VOLUME_CHANGE || UIChangePerformed) {
            volume = (int) Math.round(calculatedVolume);
        }
        UIChangePerformed = false;
        return volume;
    }

    private void initialiseMediaRecorder() throws IOException {
        mediaRecorder = new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        mediaRecorder.setOutputFile("/dev/null");
        mediaRecorder.prepare();
        mediaRecorder.start();
    }

    public void setValues(int volumeLow, int volumeHigh, int micLow, int micHigh) {
        this.volumeLow = volumeLow;
        this.volumeHigh = volumeHigh;
        this.micLow = micLow;
        this.micHigh = micHigh;
    }

    public void setVolumeLow(int volumeLow) {
        this.volumeLow = volumeLow;
    }

    public void setVolumeHigh(int volumeHigh) {
        this.volumeHigh = volumeHigh;
    }

    public void setMicLow(int micLow) {
        this.micLow = micLow;
    }

    public void setMicHigh(int micHigh) {
        this.micHigh = micHigh;
    }

    private int getAverageMic() {
        int average = 0;
        for (int i = 0; i < MIC_ARRAY_LENGTH; i++) {
            average += micArray[i];
        }
        average /= MIC_ARRAY_LENGTH;
        return average;
    }

    private void delay() {
        try {
            Thread.sleep(delayInterval);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void UIChangePerformed() {
        UIChangePerformed = true;
    }

    public void initialiseMicArray() {
        for (int i = 0; i < MIC_ARRAY_LENGTH; i++) {
            micArray[i] = mediaRecorder.getMaxAmplitude();
        }
    }

    private void initialiseVolumes() {
        if (volumeLow < 0) {
            volumeLow = 0;
        }
        if (volumeLow > audioManager.getStreamMaxVolume(MUSIC_STREAM)) {
            volumeLow = audioManager.getStreamMaxVolume(MUSIC_STREAM);
        }
        if (volumeHigh < 0) {
            volumeHigh = 0;
        }
    }

    public void setVolume(int volume) {
        if (volume < volumeLow) {
            volume = volumeLow;
        } else if (volume > volumeHigh) {
            volume = volumeLow;
        }
        lastVolume = volume;
        audioManager.setStreamVolume(MUSIC_STREAM, volume, 0);

    }

    private int getAmplitude() {
        return mediaRecorder.getMaxAmplitude();
    }

    private void setDelayInterval(int iteration) {
        if (iteration != 0 && (micArray[iteration] - micArray[iteration - 1]) < 0) {
            delayInterval = DEFAULT_DELAY_INTERVAL;
        } else {
            delayInterval = DEFAULT_DELAY_INTERVAL / 2;
        }
    }

    private int getMaximalVolume() {
        return audioManager.getStreamMaxVolume(MUSIC_STREAM);
    }

    public void resetValues() {
        micLow = MIC_DEFAULT_MIN;
        micHigh = MIC_DEFAULT_MAX;
        volumeLow = VOLUME_DEFAULT_MIN;
        volumeHigh = getMaximalVolume();
    }

    public void onStop() {
        mediaRecorder.stop();
        mediaRecorderInitialised = false;
    }

    public void increment() {
        volumeLow++;
        volumeHigh++;
        UIChangePerformed();
    }

    public void decrement() {
        if (volumeLow > 0) {
            volumeLow--;
        }
        if (volumeHigh > 0) {
            volumeHigh--;
        }
        UIChangePerformed();
    }

}
