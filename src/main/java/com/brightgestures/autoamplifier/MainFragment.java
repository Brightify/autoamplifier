package com.brightgestures.autoamplifier;

import android.app.ActivityManager;
import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.widget.CompoundButton;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.Switch;

import com.brightgestures.autoamplifier.settings.SettingsActivity;
import com.brightgestures.autoamplifier.util.DataSender;
import com.brightgestures.autoamplifier.util.PreferenceProvider;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.CheckedChange;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.OptionsItem;
import org.androidannotations.annotations.OptionsMenu;
import org.androidannotations.annotations.SeekBarProgressChange;
import org.androidannotations.annotations.SystemService;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;

/**
 * Created by matous on 25.1.14.
 * Main application fragment
 */

@EFragment(R.layout.fragment_main)
@OptionsMenu(R.menu.main)
public class MainFragment extends Fragment {

    private static final int VOLUME_THREAD_DELAY = 200;

    @ViewById(R.id.enable_switch)
    Switch enable;
    @ViewById(R.id.current_mic_progress)
    ProgressBar currentMic;
    @ViewById(R.id.current_volume_progress)
    ProgressBar currentVolume;
    @ViewById(R.id.quiet_mic_seekBar)
    SeekBar quietMic;
    @ViewById(R.id.quiet_volume_seekBar)
    SeekBar quietVolume;
    @ViewById(R.id.noisy_mic_seekBar)
    SeekBar noisyMic;
    @ViewById(R.id.noisy_volume_seekBar)
    SeekBar noisyVolume;
    @SystemService
    AudioManager audioManager;
    @SystemService
    ActivityManager activityManager;
    @Bean
    DataSender dataSender;
    @Bean
    PreferenceProvider preferenceProvider;
    private boolean volumeThreadRunning = true;
    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction() != null && intent.getAction().equals(AmplifierService.ACTION_DISABLE)) {
                enable.setChecked(false);
            } else {
                currentMic.setProgress(intent.getIntExtra(DataSender.MIC, 0));
            }
        }
    };

    @AfterViews
    void initialise() {
        preferenceProvider.init();
        currentVolume.setMax(audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC));
        quietVolume.setMax(audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC));
        quietVolume.setProgress(preferenceProvider.getVolumeLow());
        noisyVolume.setMax(audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC));
        noisyVolume.setProgress(preferenceProvider.getVolumeHigh());
        quietMic.setProgress(preferenceProvider.getMicLow());
        noisyMic.setProgress(preferenceProvider.getMicHigh());
        enable.setChecked(true);
    }

    @UiThread
    void updateCurrentVolume() {
        currentVolume.setProgress(audioManager.getStreamVolume(AudioManager.STREAM_MUSIC));
    }

    @OptionsItem(R.id.action_settings)
    void openSettings() {
        if (getActivity() != null && getActivity().getApplicationContext() != null) {
            startActivity(new Intent(getActivity().getApplicationContext(), SettingsActivity.class));
        }
    }

    @OptionsItem(R.id.action_reset)
    void reset() {
        dataSender.sendReset();
        preferenceProvider.resetPreferences();
        quietMic.setProgress(Amplifier.MIC_DEFAULT_MIN);
        noisyMic.setProgress(Amplifier.MIC_DEFAULT_MAX);
        quietVolume.setProgress(Amplifier.VOLUME_DEFAULT_MIN);
        noisyVolume.setProgress(Amplifier.VOLUME_DEFAULT_MAX);
    }

    @CheckedChange(R.id.enable_switch)
    void serviceEnabled(CompoundButton button) {
        if (button.isChecked()) {
            if (!isServiceRunning()) {
                AmplifierService_.intent(getActivity()).start();
            }
        } else {
            AmplifierService_.intent(getActivity()).stop();
        }

    }

    @SeekBarProgressChange(R.id.quiet_mic_seekBar)
    void quietMicChange(SeekBar seekBar, int progress, boolean fromUser) {
        if (fromUser) {
            dataSender.sendLowMic(progress);
            saveValues();
        }
    }

    @SeekBarProgressChange(R.id.quiet_volume_seekBar)
    void quietVolumeChange(SeekBar seekBar, int progress, boolean fromUser) {
        if (fromUser) {
            dataSender.sendLowVolume(progress);
            saveValues();
        }
    }

    @SeekBarProgressChange(R.id.noisy_mic_seekBar)
    void noisyMicChange(SeekBar seekBar, int progress, boolean fromUser) {
        if (fromUser) {
            dataSender.sendHighMic(progress);
            saveValues();
        }
    }

    @SeekBarProgressChange(R.id.noisy_volume_seekBar)
    void noisyVolumeChange(SeekBar seekBar, int progress, boolean fromUser) {
        if (fromUser) {
            dataSender.sendHighVolume(progress);
            saveValues();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        if (getActivity() != null && getActivity().getApplicationContext() != null) {
            IntentFilter intentFilter = new IntentFilter(AmplifierService.ACTION_DISABLE);
            intentFilter.addAction(DataSender.INTENT_TO_ACTIVITY);
            getActivity().getApplicationContext().registerReceiver(broadcastReceiver, intentFilter);
        }
        Thread volumeThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (volumeThreadRunning) {
                    updateCurrentVolume();
                    try {
                        Thread.sleep(VOLUME_THREAD_DELAY);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        volumeThread.start();
    }

    @Override
    public void onStop() {
        super.onStop();
        volumeThreadRunning = false;
        if (getActivity() != null && getActivity().getApplicationContext() != null) {
            getActivity().getApplicationContext().unregisterReceiver(broadcastReceiver);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if(!isServiceRunning()){
            enable.setChecked(false);
        }
    }

    private boolean isServiceRunning() {
        if (activityManager != null && activityManager.getRunningServices(Integer.MAX_VALUE) != null) {
            for (ActivityManager.RunningServiceInfo serviceInfo :
                    activityManager.getRunningServices(Integer.MAX_VALUE)) {
                if (AmplifierService_.class.getName().equals(serviceInfo.service.getClassName())) {
                    return true;
                }
            }
        }
        return false;
    }

    private void saveValues() {
        preferenceProvider.saveValues(quietVolume.getProgress(), noisyVolume.getProgress(),
                quietMic.getProgress(), noisyMic.getProgress());
    }
}
