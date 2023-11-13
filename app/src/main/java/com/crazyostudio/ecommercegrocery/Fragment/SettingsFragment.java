package com.crazyostudio.ecommercegrocery.Fragment;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.SwitchPreference;
import android.util.Log;
import android.widget.Toast;

import com.crazyostudio.ecommercegrocery.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;


public class SettingsFragment extends PreferenceFragment {
    FirebaseAuth firebaseAuth;
    FirebaseDatabase firebaseDatabase;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        firebaseAuth = FirebaseAuth.getInstance();

        addPreferencesFromResource(R.xml.preferences_setting);
        // Find the preference with the key "name"

        Preference userNamePreference = findPreference("name");
        firebaseDatabase = FirebaseDatabase.getInstance();
        // Set the summary (user name) for the preference
        if (userNamePreference != null) {
            if (firebaseAuth.getCurrentUser() != null) {
                String userName = firebaseAuth.getCurrentUser().getDisplayName();
                userNamePreference.setSummary(userName);
            } else {
                userNamePreference.setSummary("Sorry we are not found name  this is a error");
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
                firebaseDatabase.getReference().child("UserInfo").child(Objects.requireNonNull(firebaseAuth.getUid())).child("privacy_modeBool").setValue(newValue).addOnCompleteListener(task -> {
                    if (task.isSuccessful())
                    {
                        Toast.makeText(getContext(), "isSuccessful", Toast.LENGTH_SHORT).show();
                    }else {
                        Log.d("ERROR_IS", "onComplete: "+task.getException());
                    }
                });
                editor.putBoolean("privacy_modeBool", (Boolean) newValue);
                editor.apply();
                return true;
            });
            deal_notificationPreference.setOnPreferenceChangeListener((preference, newValue) -> {
                // Save the boolean value in SharedPreferences
//                deal_notificationBool
                firebaseDatabase.getReference().child("UserInfo").child(Objects.requireNonNull(firebaseAuth.getUid())).child("deal_notificationBool").setValue(newValue).addOnCompleteListener(task -> {
                    if (task.isSuccessful())
                    {
                        Toast.makeText(getContext(), "isSuccessful", Toast.LENGTH_SHORT).show();
                    }else {
                        Log.d("ERROR_IS", "onComplete: "+task.getException());
                    }
                });
                editor.putBoolean("deal_notificationBool", (Boolean) newValue);
                editor.apply();
                return true;
            });
            account_shipping_notificationPreference.setOnPreferenceChangeListener((preference, newValue) -> {
                firebaseDatabase.getReference().child("UserInfo").child(Objects.requireNonNull(firebaseAuth.getUid())).child("account_shipping_notificationBool").setValue(newValue).addOnCompleteListener(task -> {
                    if (task.isSuccessful())
                    {
                        Toast.makeText(getContext(), "isSuccessful", Toast.LENGTH_SHORT).show();
                    }else {
                        Log.d("ERROR_IS", "onComplete: "+task.getException());
                    }
                });
//                account_shipping_notificationBool
                editor.putBoolean("account_shipping_notificationBool", (Boolean) newValue);
                editor.apply();
                return true;
            });


        }
    }

}