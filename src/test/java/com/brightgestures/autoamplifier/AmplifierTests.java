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