package com.crazyostudio.ecommercegrocery.Fragment;

import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.fragment.app.Fragment;

import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.SwitchPreference;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.crazyostudio.ecommercegrocery.R;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;


public class SettingsFragment extends PreferenceFragment {
    FirebaseAuth firebaseAuth;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        firebaseAuth = FirebaseAuth.getInstance();

        addPreferencesFromResource(R.xml.preferences_setting);
        // Find the preference with the key "name"

        Preference userNamePreference = findPreference("name");

        // Set the summary (user name) for the preference
        if (userNamePreference != null) {
            if (firebaseAuth.getCurrentUser() != null) {
                String userName = firebaseAuth.getCurrentUser().getDisplayName();
                userNamePreference.setSummary(userName);
            } else {
                userNamePreference.setSummary("WORK ON IT");
            }
        }

        SwitchPreference privacyModePreference = (SwitchPreference) findPreference("privacy_mode");
        SwitchPreference deal_notificationPreference = (SwitchPreference) findPreference("deal_notification");
        SwitchPreference account_shipping_notificationPreference = (SwitchPreference) findPreference("account_shipping_notification");
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        SharedPreferences.Editor editor = preferences.edit();

        if (privacyModePreference != null) {
            privacyModePreference.setOnPreferenceChangeListener((preference, newValue) -> {
                // Save the boolean value in SharedPreferences
                editor.putBoolean("privacy_mode", (Boolean) newValue);
                editor.apply();
                return true;
            });
            deal_notificationPreference.setOnPreferenceChangeListener((preference, newValue) -> {
                // Save the boolean value in SharedPreferences
                editor.putBoolean("deal_notification", (Boolean) newValue);
                editor.apply();
                return true;
            });
            account_shipping_notificationPreference.setOnPreferenceChangeListener((preference, newValue) -> {
                editor.putBoolean("account_shipping_notification", (Boolean) newValue);
                editor.apply();
                return true;
            });


        }
    }

}