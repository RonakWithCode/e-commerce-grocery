package com.crazyostudio.ecommercegrocery.Fragment;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.crazyostudio.ecommercegrocery.R;
import com.crazyostudio.ecommercegrocery.databinding.FragmentSelectLanguageBinding;

import java.util.Locale;

public class SelectLanguageFragment extends DialogFragment {
    private FragmentSelectLanguageBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentSelectLanguageBinding.inflate(inflater, container, false);

        getSavedLanguageCode();
        binding.next.setOnClickListener(view -> setLanguage());

        // Set listener to handle language selection
        binding.languageRadioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            switch (checkedId) {
                case R.id.englishRadioButton:
                    binding.textView.setText("choose your language");
                    binding.next.setText("Continue");
                    break;
                case R.id.hindiRadioButton:
                    binding.textView.setText("अपनी भाषा चुनें");
                    binding.next.setText("जारी रखना");
                    break;
                default:
                    binding.textView.setText("choose your language");
                    break;
            }
        });

        return binding.getRoot();
    }

    @SuppressLint("NonConstantResourceId")
    private void setLanguage() {
        // Determine the language code based on the selected RadioButton
        String languageCode = "en"; // Default to English
        switch (binding.languageRadioGroup.getCheckedRadioButtonId()) {
            case R.id.englishRadioButton:
                languageCode = "en";
                break;
            case R.id.hindiRadioButton:
                languageCode = "hi";
                break;
        }

        // Save the selected language code to SharedPreferences
        SharedPreferences preferences = requireActivity().getSharedPreferences("LanguagePrefs", Context.MODE_PRIVATE);
        preferences.edit().putString("languageCode", languageCode).apply();

        // Update locale configuration
        Locale locale = new Locale(languageCode);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.setLocale(locale);
        Resources resources = getResources();
        resources.updateConfiguration(config, resources.getDisplayMetrics());

        // Dismiss the dialog and recreate the activity to apply changes
        dismiss();
        requireActivity().recreate();
    }

    private void getSavedLanguageCode() {
        SharedPreferences preferences = requireActivity().getSharedPreferences("LanguagePrefs", Context.MODE_PRIVATE);
        // Retrieve the language code, default to "en" if not found
        String languageCode = preferences.getString("languageCode", "en");
        // Set the language radio button based on the saved language code
        setLanguageRadioGroup(languageCode);
    }

    private void setLanguageRadioGroup(String languageCode) {
        switch (languageCode) {
            case "en":
                binding.englishRadioButton.setChecked(true);
                break;
            case "hi":
                binding.hindiRadioButton.setChecked(true);
                break;
            default:
                binding.englishRadioButton.setChecked(true);
                break;
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        }
    }
}
