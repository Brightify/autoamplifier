package com.brightify.autoamplifier;

import android.content.Context;
import android.media.AudioManager;

import org.brightify.autoamplifier.Amplifier_;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.*;

import static org.junit.Assert.assertEquals;

@RunWith(RobolectricTestRunner.class)
public class AmplifierTests {

    Amplifier_ amplifier;
    AudioManager audioManager;
    Context context;

    @Before
    public void setup() {
        context = Robolectric.getShadowApplication()
                .getApplicationContext();
        amplifier = Amplifier_.getInstance_(context);
        audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
    }

    @Test
    public void testSettingVolume() {
        amplifier.setVolume(10);
        assertEquals(10, audioManager.getStreamVolume(AudioManager.STREAM_MUSIC));
    }
}