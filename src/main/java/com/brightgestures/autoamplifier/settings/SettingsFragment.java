package com.brightgestures.autoamplifier.settings;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;

import com.brightgestures.autoamplifier.R;

/**
 * Created by matous on 26.1.14.
 */
public class SettingsFragment extends PreferenceFragment {

    private final static String PREFERENCE_AUTHOR = "author_info";
    private final static String AUTHOR_G_PLUS_URI = "https://plus.google.com/114245631152542174178/posts";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
        Preference author = findPreference(PREFERENCE_AUTHOR);
        if (author != null) {
            author.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    startActivity(new Intent().setAction(Intent.ACTION_VIEW).setData(Uri.parse(AUTHOR_G_PLUS_URI)));
                    return false;
                }
            });
        }
    }
}
