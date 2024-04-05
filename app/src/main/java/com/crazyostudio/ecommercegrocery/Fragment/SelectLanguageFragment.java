package com.crazyostudio.ecommercegrocery.Fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;

import com.crazyostudio.ecommercegrocery.R;
import com.crazyostudio.ecommercegrocery.databinding.FragmentSelectLanguageBinding;

import java.util.Locale;


public class SelectLanguageFragment extends Fragment {
    FragmentSelectLanguageBinding binding;
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentSelectLanguageBinding.inflate(inflater,container,false);

        getSavedLanguageCode();
        binding.next.setOnClickListener(view->{
            setLanguage();
        });

        // Set listener to handle language selection
        binding.languageRadioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            switch (checkedId) {
                case R.id.englishRadioButton:
                    binding.textView.setText("choose your language");
                    binding.next.setText("Continue");
//                   ( (AppCompatActivity) .setTitle());
                    ((AppCompatActivity) requireActivity()).getSupportActionBar().setTitle("ASHOK E-Commerce Grocery");

                    break;
                case R.id.hindiRadioButton:
                    binding.textView.setText("अपनी भाषा चुनें");
                    binding.next.setText("जारी रखना");
                    ((AppCompatActivity) requireActivity()).getSupportActionBar().setTitle("अशोक ई-कॉमर्स किराना");
                    break;
                default:
                    // Default case
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
        Configuration config = new Configuration(getResources().getConfiguration());
        config.setLocale(locale);
        Resources resources = requireActivity().getResources();
        resources.updateConfiguration(config, resources.getDisplayMetrics());

        // Refresh UI
        requireActivity().recreate(); // Recreate activity to apply the new locale
        NavController navController = Navigation.findNavController(requireActivity(),R.id.nav_host_fragment );
        navController.popBackStack();
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
                // Default to English if the saved language code is not recognized
                binding.englishRadioButton.setChecked(true);
                break;
        }
    }

}