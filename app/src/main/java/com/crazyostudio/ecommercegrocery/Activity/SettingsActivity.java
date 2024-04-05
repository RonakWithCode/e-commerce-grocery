package com.crazyostudio.ecommercegrocery.Activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SwitchPreference;

import com.crazyostudio.ecommercegrocery.MainActivity;
import com.crazyostudio.ecommercegrocery.Model.QueryModel;
import com.crazyostudio.ecommercegrocery.Model.RecentLoginsModels;
import com.crazyostudio.ecommercegrocery.R;
import com.crazyostudio.ecommercegrocery.databinding.AboutFragmentBinding;
import com.crazyostudio.ecommercegrocery.databinding.HelpSupportBinding;
import com.crazyostudio.ecommercegrocery.databinding.OpenSourceLicensesBinding;
import com.crazyostudio.ecommercegrocery.databinding.PrivacyPolicyWebBinding;
import com.crazyostudio.ecommercegrocery.databinding.ReviewRecentLoginsBinding;
import com.crazyostudio.ecommercegrocery.databinding.TermsConditionsBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class SettingsActivity extends AppCompatActivity implements
        PreferenceFragmentCompat.OnPreferenceStartFragmentCallback {

    private static final String TITLE_TAG = "settingsActivityTitle";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);
        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.settings, new SettingsFragment())
                    .commit();
        } else {
            setTitle(savedInstanceState.getCharSequence(TITLE_TAG));
        }
        getSupportFragmentManager().addOnBackStackChangedListener(
                () -> {
                    if (getSupportFragmentManager().getBackStackEntryCount() == 0) {
                        setTitle(R.string.title_activity_settings);
                    }
                });
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        // Save current activity title so we can set it again after a configuration change
        outState.putCharSequence(TITLE_TAG, getTitle());
    }

    @Override
    public boolean onSupportNavigateUp() {
        if (getSupportFragmentManager().popBackStackImmediate()) {
            return true;
        }
        return super.onSupportNavigateUp();
    }

    @Override
    public boolean onPreferenceStartFragment(@NonNull PreferenceFragmentCompat caller, Preference pref) {
        // Instantiate the new Fragment
        final Bundle args = pref.getExtras();
        final Fragment fragment = getSupportFragmentManager().getFragmentFactory().instantiate(
                getClassLoader(),
                Objects.requireNonNull(pref.getFragment()));
        fragment.setArguments(args);
        fragment.setTargetFragment(caller, 0);
        // Replace the existing Fragment with the new Fragment
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.settings, fragment)
                .addToBackStack(null)
                .commit();
        setTitle(pref.getTitle());
        return true;
    }

    public static class SettingsFragment extends PreferenceFragmentCompat {

        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.preferences_setting, rootKey);
            FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
            FirebaseFirestore db = FirebaseFirestore.getInstance();

            Preference userNamePreference = findPreference("name");
            if (userNamePreference != null) {
                if (firebaseAuth.getCurrentUser() != null) {
                    String userName = firebaseAuth.getCurrentUser().getDisplayName();
                    userNamePreference.setSummary(userName);
                } else {
                    userNamePreference.setSummary("Sorry, we could not find your name. This is an error.");
                }
            }

            SwitchPreference privacyModePreference = findPreference("privacy_mode");
            SwitchPreference dealNotificationPreference = findPreference("deal_notification");
            SwitchPreference accountShippingNotificationPreference = findPreference("account_shipping_notification");

            if (privacyModePreference != null) {
                privacyModePreference.setOnPreferenceChangeListener((preference, newValue) -> {
                    // Save the boolean value in Firestore
                    saveBooleanToFirestore(db, "privacy_modeBool", newValue);
                    return true;
                });
            }

            if (dealNotificationPreference != null) {
                dealNotificationPreference.setOnPreferenceChangeListener((preference, newValue) -> {
                    // Save the boolean value in Firestore
                    saveBooleanToFirestore(db, "deal_notificationBool", newValue);
                    return true;
                });
            }

            if (accountShippingNotificationPreference != null) {
                accountShippingNotificationPreference.setOnPreferenceChangeListener((preference, newValue) -> {
                    // Save the boolean value in Firestore
                    saveBooleanToFirestore(db, "account_shipping_notificationBool", newValue);
                    return true;
                });
            }
        }

        private void saveBooleanToFirestore(FirebaseFirestore db, String fieldName, Object value) {
            FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
            if (firebaseAuth.getCurrentUser() != null) {
                Map<String, Object> data = new HashMap<>();
                data.put(fieldName, value);

                // Reference to the user's document
                DocumentReference userDocRef = db.collection("UserInfo").document(Objects.requireNonNull(firebaseAuth.getUid()));

                // Update the field in Firestore
                userDocRef.set(data, SetOptions.merge())
                        .addOnSuccessListener(aVoid -> Toast.makeText(getContext(), "Success", Toast.LENGTH_SHORT).show())
                        .addOnFailureListener(e -> Toast.makeText(getContext(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show());
            }
        }
    }





    public static class ManagePermissionsFragment extends PreferenceFragmentCompat {
        private PreferenceCategory permissionCategory;
        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            // Load the preferences from the XML resource
            setPreferencesFromResource(R.xml.manage_permissions, rootKey);
            permissionCategory = findPreference("permissions_category");

            // Add permissions dynamically
            addAppPermission("LOCATION","android.permission.ACCESS_COARSE_LOCATION", R.drawable.ic_location);
            addAppPermission("NOTIFICATIONS","android.permission.POST_NOTIFICATIONS", R.drawable.ic_notifications);
            addAppPermission("STORAGE","android.permission.READ_EXTERNAL_STORAGE", R.drawable.ic_storage);
            addAppPermission("CAMERA","android.permission.CAMERA", R.drawable.ic_baseline_camera_enhance_24);
        }

        private void addAppPermission(String permissionName,String permission, int iconResId) {
            if (permissionCategory != null) {
                Preference checkBoxPreference = new Preference(requireContext());
                checkBoxPreference.setTitle(permissionName);
                checkBoxPreference.setIcon(ContextCompat.getDrawable(requireContext(), iconResId));
                checkBoxPreference.setSummary(checkPermissionGranted(permission) ? "There is permission" : "There is no permission");
                checkBoxPreference.setOnPreferenceClickListener(preference -> {
                    Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    Uri uri = Uri.fromParts("package", requireActivity().getPackageName(), null);
                    intent.setData(uri);
                    startActivity(intent);
                    return true;
                });
                permissionCategory.addPreference(checkBoxPreference);
            }
        }

        private boolean checkPermissionGranted(String permission) {
            return ContextCompat.checkSelfPermission(requireContext(), permission) == PackageManager.PERMISSION_GRANTED;
        }
    }
    public static class ReviewRecentLoginsFragment extends Fragment{
        @Override
        public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            ReviewRecentLoginsBinding binding = ReviewRecentLoginsBinding.inflate(inflater,container,false);
// Retrieve recent logins details
            final ArrayList<String> time = new ArrayList<>();
            final ArrayList<String> device = new ArrayList<>();
            final ArrayList<String> IP_ARRAY = new ArrayList<>();
            final ArrayList<String> approximateLocation = new ArrayList<>();

            FirebaseDatabase.getInstance().getReference().child("RecentLogins")
                    .child(Objects.requireNonNull(FirebaseAuth.getInstance().getUid())).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            RecentLoginsModels models5 = snapshot.getValue(RecentLoginsModels.class);
                            if (models5 != null) {
                                time.addAll(models5.getTime());
                                approximateLocation.addAll(models5.getApproximateLocation());
                                IP_ARRAY.addAll(models5.getIp());
                                device.addAll(models5.getDevice());
                            }

                            // Now, you have the data in the time, approximateLocation, IP_ARRAY, and device lists
                            // You can create a RecentLoginsModels instance with these values


                            // Do something with the recentLoginsModels instance, like updating UI or passing it to another method
                            StringBuilder details = new StringBuilder();

                            for (int i = 0; i < time.size(); i++) {
                                details.append("Time: ").append(time.get(i)).append("\n");
                                details.append("Device: ").append(device.get(i)).append("\n");
                                details.append("IP: ").append(IP_ARRAY.get(i)).append("\n");
                                details.append("Location: ").append(approximateLocation.get(i)).append("\n\n\n");
                            }


                            TextView detailsTextView = binding.detailsTextView;
                            detailsTextView.setText(details);
                            detailsTextView.setOnClickListener(view -> Toast.makeText(requireContext(), "THIS FUNCTION I WILL MAKE SOON", Toast.LENGTH_SHORT).show());
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            // Handle errors that occur while reading data
                            // For example, log the error or show an error message
                        }
                    });
            // Display details in a TextView (replace with the appropriate view)




//            binding.getRoot()


            return binding.getRoot();
        }


    }
    public static class PrivacyPolicyFragment extends PreferenceFragmentCompat {
        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.privacypolicy, rootKey);
            // Find preferences by key
            Preference privacyPolicyPref = findPreference("privacy_policy");
//            Preference reviewRecentLoginsPref = findPreference("review_recent_logins");
            Preference logoutAllDevicesPref = findPreference("logout_all_devices");
//            Preference logoutDevicesPref = findPreference("logout_devices");
            Preference deleteAccountPref = findPreference("delete");

            if (privacyPolicyPref != null) {
                privacyPolicyPref.setOnPreferenceClickListener(preference -> {
                    // Handle click for Privacy Policy
                    // Example: Open a new activity or perform some action
                    return true;
                });
            }



            if (logoutAllDevicesPref != null) {
                logoutAllDevicesPref.setOnPreferenceClickListener(preference -> {
                    AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
                    builder.setMessage("do you want to log out from all devices?")
                            .setPositiveButton("yes", (dialog, id) -> {
                                // User clicked the Delete button
                                ProgressDialog progressDialog = new ProgressDialog(requireContext());
                                progressDialog.setTitle("sign Out");
                                progressDialog.show();
                                progressDialog.setCancelable(false);
                                FirebaseAuth.getInstance().signOut();
                                progressDialog.dismiss();
                                Intent intent = new Intent(requireContext(), MainActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                                dialog.dismiss();
                                requireActivity().finish();
                            })
                            .setNegativeButton("no", (dialog, id) -> {
                                // User clicked the Cancel button
                                dialog.dismiss();
                            });
                    // Create the AlertDialog object and show it
                    builder.create().show();
                    return true;
                });
            }
//
//            if (logoutDevicesPref != null) {
//                logoutDevicesPref.setOnPreferenceClickListener(preference -> {
//                    // Handle click for Logout from This Device
//                    // Example: Log the user out from this device
//
////                    NOW WORK ON IT
//                    return true;
//                });
//            }

            if (deleteAccountPref != null) {
                deleteAccountPref.setOnPreferenceClickListener(preference -> {
                    // Handle click for Delete Account
                    // Example: Show a confirmation dialog and delete the account if confirmed
                    AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
                    builder.setMessage("Are you sure you want to delete your account?")
                            .setPositiveButton("Delete", (dialog, id) -> {
                                // User clicked the Delete button
//                                    deleteAccount();
                                Toast.makeText(requireContext(), "contact this number XXXXXXXXXX", Toast.LENGTH_SHORT).show();
                                dialog.dismiss();

                            })
                            .setNegativeButton("Cancel", (dialog, id) -> {
                                // User clicked the Cancel button
                                dialog.dismiss();
                            });
                    // Create the AlertDialog object and show it
                    builder.create().show();
                    return true;
                });
            }
        }
        }

    public static class HelpSupportFragment extends Fragment{
//        onCreateView
        HelpSupportBinding binding;
        @Nullable
        @Override
        public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            binding = HelpSupportBinding.inflate(inflater,container,false);
            binding.btnSubmit.setOnClickListener(submit->submitForm());
            return binding.getRoot();
        }

        private void submitForm() {
            String name = Objects.requireNonNull(binding.editTextName.getText()).toString();
            String email = Objects.requireNonNull(binding.editTextEmail.getText()).toString();
            String subject = Objects.requireNonNull(binding.editTextSubject.getText()).toString();
            String message = Objects.requireNonNull(binding.editTextMessage.getText()).toString();
            if (TextUtils.isEmpty(name) || TextUtils.isEmpty(email) || TextUtils.isEmpty(subject) || TextUtils.isEmpty(message)) {
                // Display an error message or highlight the empty fields
                Toast.makeText(requireContext(), "All fields must be filled", Toast.LENGTH_SHORT).show();
                return;
            }
            // TODO: Validate input fields
            String deviceInfo = "null";
            if (binding.checkbox.isChecked()) {
                deviceInfo = getDeviceInfo();
            }
            writeToFirebase(name, email, subject, message, deviceInfo);
        }
        private String getDeviceInfo() {
            // Get device information, e.g., device model, OS version, etc.
            // You can use Build class to get this information
            return "Device Model: " + Build.MODEL + ", OS Version: " + Build.VERSION.RELEASE;
        }

        private void writeToFirebase(String name, String email, String subject, String message, String deviceInfo) {
            // Write data to Firebase Realtime Database
            FirebaseDatabase databaseReference = FirebaseDatabase.getInstance();

            QueryModel queryModel = new QueryModel(FirebaseAuth.getInstance().getUid(), name, email, subject, message, deviceInfo);
            databaseReference.getReference().child("Query").child(Objects.requireNonNull(FirebaseAuth.getInstance().getUid())).push().setValue(queryModel).addOnCompleteListener(task -> {
                Toast.makeText(requireContext(), "Query submitted successfully", Toast.LENGTH_SHORT).show();
                requireActivity().finish(); // Close the activity or dialog after submission
            });
        }
    }
    public static class TermsConditionsFragment extends Fragment{
        TermsConditionsBinding binding;

        @Nullable
        @Override
        public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            binding = TermsConditionsBinding.inflate(inflater,container,false);

            // Load the web page
            String url = "LOAD MY PAGE ";
            WebSettings webSettings = binding.webView.getSettings();
            webSettings.setJavaScriptEnabled(true); // Enable JavaScript if needed
            binding.webView.loadUrl(url);
            return binding.getRoot();
        }
    }


    public static class AboutFragment extends Fragment{
        AboutFragmentBinding binding;
        @Nullable
        @Override
        public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            binding = AboutFragmentBinding.inflate(inflater,container,false);
//            LOAD a WEB PAGE
            return binding.getRoot();
        }
    }

    public static class OpenSourceLicenses extends Fragment{
        @Nullable
        @Override
        public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            OpenSourceLicensesBinding binding;
            binding = OpenSourceLicensesBinding.inflate(inflater,container,false);
//            LOAD a WEB PAGE

            return binding.getRoot();
        }
    }



    public static class PrivacyPolicyWebFragment extends Fragment{
        @Nullable
        @Override
        public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//            privacy_policy_web
            PrivacyPolicyWebBinding binding;
            binding = PrivacyPolicyWebBinding.inflate(inflater,container,false);
//            LOAD a WEB PAGE

            return binding.getRoot();
        }
    }





    public static class SettingAboutFragment extends PreferenceFragmentCompat{
        @Override
        public void onCreatePreferences(@Nullable Bundle savedInstanceState, @Nullable String rootKey) {
            addPreferencesFromResource(R.xml.setting_about);

            Preference app_version = findPreference("app_version");
            if (app_version != null) {
                app_version.setOnPreferenceClickListener(preference -> {
                    try {
                        PackageInfo pInfo = requireContext().getPackageManager().getPackageInfo(requireContext().getPackageName(), 0);
                        String versionName = pInfo.versionName;

                        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
                        builder.setTitle("App Version")
                                .setMessage("Current version: " + versionName)
                                .setPositiveButton("OK", (dialog, which) -> dialog.dismiss());

                        AlertDialog dialog = builder.create();
                        dialog.show();
                    } catch (PackageManager.NameNotFoundException e) {
                        e.printStackTrace();
                    }
                    return false;
                });
            }
//

        }
    }

}