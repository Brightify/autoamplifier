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
package org.brightify.autoamplifier.settings;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;

import org.brightify.autoamplifier.R;
import org.brightify.autoamplifier.util.PreferenceProvider;

import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EFragment;

/**
 * Created by matous on 26.1.14.
 * Fragment that represents preference screen
 */

@EFragment
public class SettingsFragment extends PreferenceFragment {

    private final static String PREFERENCE_AUTHOR = "author_info";
    private final static String AUTHOR_G_PLUS_URI = "https://plus.google.com/114245631152542174178/posts";

    @Bean
    PreferenceProvider preferenceProvider;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
        Preference author = findPreference(PREFERENCE_AUTHOR);
        if (author != null) {
            author.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    startActivity(new Intent().setAction(Intent.ACTION_VIEW)
                            .setData(Uri.parse(AUTHOR_G_PLUS_URI)));
                    return false;
                }
            });
        }
    }
}
