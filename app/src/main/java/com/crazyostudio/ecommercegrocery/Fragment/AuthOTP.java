package com.crazyostudio.ecommercegrocery.Fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.crazyostudio.ecommercegrocery.HelperClass.ValuesHelper;
import com.crazyostudio.ecommercegrocery.Model.UserinfoModels;
import com.crazyostudio.ecommercegrocery.R;
import com.crazyostudio.ecommercegrocery.Services.DatabaseService;
import com.crazyostudio.ecommercegrocery.databinding.FragmentAuthOTPBinding;
import com.crazyostudio.ecommercegrocery.javaClasses.LoadingDialog;
import com.crazyostudio.ecommercegrocery.javaClasses.TokenManager;
import com.crazyostudio.ecommercegrocery.javaClasses.basicFun;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import com.google.android.material.snackbar.Snackbar;

public class AuthOTP extends Fragment {

    private static final String TAG = "AuthOTP";
    private static final String ARG_NUMBER = "number";
    private static final int MAX_RETRIES = 3;
    private int retryCount = 0;
    private static final long RETRY_DELAY_MS = 2000; // 2 seconds

    private FragmentAuthOTPBinding binding;
    private FirebaseAuth firebaseAuth;
    private NavController navController;

    private EditText mEt1, mEt2, mEt3, mEt4, mEt5, mEt6;
    private String verificationId;
    private String number;
    private Context context;
    private LoadingDialog loadingDialog;
    DatabaseService databaseService;
    public AuthOTP() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            number = getArguments().getString(ARG_NUMBER);
        }
        // Initialize Firebase Auth at the start
        firebaseAuth = FirebaseAuth.getInstance();
        
        // Add Firebase connection state listener
        DatabaseReference connectedRef = FirebaseDatabase.getInstance().getReference(".info/connected");
        connectedRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                boolean connected = Boolean.TRUE.equals(snapshot.getValue(Boolean.class));
                Log.d(TAG, "Firebase connection state: " + connected);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "Firebase connection listener cancelled", error.toException());
            }
        });
    }

    @SuppressLint("SetTextI18n")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentAuthOTPBinding.inflate(inflater, container, false);
        context = getContext();
        navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment);
        if (number == null) {
            navController.popBackStack();
            return binding.getRoot();
        }

        loadingDialog = new LoadingDialog(requireActivity()); // Initialize LoadingDialog
        loadingDialog.startLoadingDialog(); // Show loading dialog
        databaseService = new DatabaseService();

        binding.fullNumber.setText("+91" + number + " ");
        binding.tvPhoneNo.setOnClickListener(view -> navController.popBackStack());
        binding.tvResend.setOnClickListener(view -> sendOTP());

        initializeEditTexts();
        addTextWatchers();
        sendOTP();
        binding.btnVerify.setOnClickListener(view -> verifyOTP());

        return binding.getRoot();
    }

    private void initializeEditTexts() {
        mEt1 = binding.otpEditText1;
        mEt2 = binding.otpEditText2;
        mEt3 = binding.otpEditText3;
        mEt4 = binding.otpEditText4;
        mEt5 = binding.otpEditText5;
        mEt6 = binding.otpEditText6;
    }

    private void addTextWatchers() {
        addTextWatcher(mEt1, mEt2);
        addTextWatcher(mEt2, mEt3, mEt1);
        addTextWatcher(mEt3, mEt4, mEt2);
        addTextWatcher(mEt4, mEt5, mEt3);
        addTextWatcher(mEt5, mEt6, mEt4);
        addTextWatcher(mEt6, null, mEt5);
    }

    private void addTextWatcher(@NonNull final EditText current, final EditText next) {
        current.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { }

            @Override
            public void afterTextChanged(Editable s) {
                if (current.length() == 1 && next != null) {
                    next.requestFocus();
                } else if (current.length() == 0 && next != null) {
                    current.requestFocus();
                } else if (current.length() == 1) {
                    hideKeyboard();
                }
            }
        });
    }

    private void addTextWatcher(@NonNull final EditText current, final EditText next, final EditText previous) {
        current.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { }

            @Override
            public void afterTextChanged(Editable s) {
                if (current.length() == 1 && next != null) {
                    next.requestFocus();
                } else if (current.length() == 0 && previous != null) {
                    previous.requestFocus();
                }
            }
        });
    }

    private void hideKeyboard() {
        InputMethodManager inputManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (inputManager != null && requireActivity().getCurrentFocus() != null) {
            inputManager.hideSoftInputFromWindow(requireActivity().getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    private void sendOTP() {
        Log.d(TAG, "Starting sendOTP process...");
        
        if (!isNetworkAvailable()) {
            Log.e(TAG, "Network not available");
            showNetworkError();
            return;
        }

        try {
            Log.i(TAG, "Attempting to send OTP to: " + number);
            loadingDialog.startLoadingDialog();
            
            // Add connection timeout
            PhoneAuthOptions options = PhoneAuthOptions.newBuilder(firebaseAuth)
                    .setPhoneNumber("+91" + number)
                    .setTimeout(60L, TimeUnit.SECONDS)  // Reduced timeout
                    .setActivity(requireActivity())
                    .setCallbacks(verificationCallbacks)
                    .setForceResendingToken(null)  // Clear any existing token
                    .build();
            
            Log.d(TAG, "PhoneAuthOptions built successfully");
            
            // Clear any existing verification in progress
            if (firebaseAuth.getCurrentUser() != null) {
                firebaseAuth.signOut();
            }
            
            PhoneAuthProvider.verifyPhoneNumber(options);
            Log.d(TAG, "verifyPhoneNumber called");
        } catch (Exception e) {
            Log.e(TAG, "Exception in sendOTP: ", e);
            loadingDialog.dismissDialog();
            handleSendOTPError(e);
        }
    }

    private boolean hasActiveInternetConnection() {
        try {
            Log.d(TAG, "Checking active internet connection with ping...");
            Runtime runtime = Runtime.getRuntime();
            Process ipProcess = runtime.exec("/system/bin/ping -c 1 8.8.8.8");
            int exitValue = ipProcess.waitFor();
            Log.d(TAG, "Ping exit value: " + exitValue);
            return (exitValue == 0);
        } catch (IOException | InterruptedException e) {
            Log.e(TAG, "Error checking internet connection", e);
            return false;
        }
    }

    private void handleSendOTPError(Exception e) {
        Log.e(TAG, "Handling send OTP error. Current retry count: " + retryCount);
        if (retryCount < MAX_RETRIES) {
            retryCount++;
            Log.i(TAG, "Scheduling retry attempt " + retryCount + " in " + RETRY_DELAY_MS + "ms");
            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                Log.i(TAG, "Executing retry attempt " + retryCount);
                sendOTP();
            }, RETRY_DELAY_MS);
        } else {
            Log.e(TAG, "Max retries reached. Giving up.");
            retryCount = 0;
            showError("Failed to send OTP after multiple attempts. Please try again later.");
            Log.e(TAG, "Final error: ", e);
        }
    }

    private final PhoneAuthProvider.OnVerificationStateChangedCallbacks verificationCallbacks =
            new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                @Override
                public void onVerificationCompleted(@NonNull PhoneAuthCredential credential) {
                    Log.d(TAG, "onVerificationCompleted: Credential received");
                    retryCount = 0;
                    signInWithPhoneAuthCredential(credential);
                }

                @Override
                public void onVerificationFailed(@NonNull FirebaseException e) {
                    Log.e(TAG, "onVerificationFailed with exception: ", e);
                    loadingDialog.dismissDialog();
                    retryCount = 0;

                    if (e instanceof FirebaseNetworkException) {
                        Log.e(TAG, "FirebaseNetworkException detected");
                        if (!isNetworkAvailable()) {
                            Log.e(TAG, "Network not available during verification");
                            showNetworkError();
                        } else {
                            Log.w(TAG, "Network available but still got FirebaseNetworkException");
                            handleSendOTPError(e);
                        }
                    } else if (e instanceof FirebaseAuthInvalidCredentialsException) {
                        Log.e(TAG, "Invalid credentials: " + e.getMessage());
                        showError("Invalid phone number format");
                    } else if (e instanceof FirebaseTooManyRequestsException) {
                        Log.e(TAG, "Too many requests: " + e.getMessage());
                        showError("Too many requests. Please try again later");
                    } else {
                        Log.e(TAG, "Unknown verification error: " + e.getMessage());
                        showError("Verification failed. Please try again");
                    }
                }

                @Override
                public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken token) {
                    super.onCodeSent(s, token);
                    Log.d(TAG, "OTP code sent successfully. VerificationId received");
                    verificationId = s;
                    retryCount = 0;
                    loadingDialog.dismissDialog();
                    Snackbar.make(binding.getRoot(), "OTP Sent Successfully", Snackbar.LENGTH_SHORT).show();
                }
            };

    private void verifyOTP() {
        String userOtp = mEt1.getText().toString() + mEt2.getText().toString() + mEt3.getText().toString() +
                mEt4.getText().toString() + mEt5.getText().toString() + mEt6.getText().toString();

        if (userOtp.trim().length() == 6) {
            PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, userOtp);
            signInWithPhoneAuthCredential(credential);
        } else {
            vibrateDevice();
        }
    }

    private void vibrateDevice() {
        Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        if (vibrator != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator.vibrate(VibrationEffect.createOneShot(150, VibrationEffect.DEFAULT_AMPLITUDE));
            } else {
                vibrator.vibrate(150);
            }
        }
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        loadingDialog.startLoadingDialog(); // Show loading dialog
        firebaseAuth.signInWithCredential(credential).addOnCompleteListener(requireActivity(), task -> {
            if (task.isSuccessful()) {
                navigateToNextScreen();
            } else {
                Toast.makeText(context, "Sign-in failed, please try again", Toast.LENGTH_SHORT).show();
                loadingDialog.dismissDialog(); // Show loading dialog
            }
        });
    }

    private void navigateToNextScreen() {
        if (firebaseAuth.getCurrentUser() != null && firebaseAuth.getCurrentUser().getDisplayName() == null) {

            databaseService.CheckNotificationToken(new DatabaseService.UpdateTokenCallback() {
                @Override
                public void onSuccess(String token) {
                    TokenManager.getInstance(requireContext()).clearToken();
                    TokenManager.getInstance(requireContext()).saveToken(token);
                    setupUser(token);
                }

                @Override
                public void onError(String errorMessage) {
                    Log.i("onError", "onError: "+errorMessage);
                }
            });

        } else {
            updateToken();
        }
    }

    void setupUser(String token){
        String defaultUserName = ValuesHelper.DEFAULT_USER_NAME;
        UserinfoModels UserinfoModels = new UserinfoModels(token,FirebaseAuth.getInstance().getUid(), defaultUserName,number,true);
        databaseService.setUserInfo(UserinfoModels, new DatabaseService.SetUserInfoCallback() {
            @Override
            public void onSuccess(Task<Void> task) {
                Bundle bundle = new Bundle();
                bundle.putString("number", number);
                loadingDialog.dismissDialog(); // Show loading dialog
                navController.navigate(R.id.action_authOTP_to_authUserDetailsFragment, bundle);

            }
            @Override
            public void onError(String errorMessage) {
                basicFun.AlertDialog(requireContext(),errorMessage);
            }
        });
    }

    private void updateToken() {
        databaseService.CheckNotificationToken(new DatabaseService.UpdateTokenCallback() {
            @Override
            public void onSuccess(String token) {
                TokenManager.getInstance(requireContext()).clearToken();
                TokenManager.getInstance(requireContext()).saveToken(token);
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                DocumentReference userRef = db.collection("UserInfo").document(Objects.requireNonNull(firebaseAuth.getUid()));
                userRef.update("token", token)
                        .addOnSuccessListener(aVoid -> {
                            loadingDialog.dismissDialog(); // Show loading dialog
                            requireActivity().finish();
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(context, "Failed to update token", Toast.LENGTH_SHORT).show();
                        });
            }

            @Override
            public void onError(String errorMessage) {
                Log.e(TAG, "Token update error: " + errorMessage);
            }
        });
    }

    private boolean isNetworkAvailable() {
        Log.d(TAG, "Checking network availability...");
        ConnectivityManager connectivityManager = (ConnectivityManager) requireContext()
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager == null) {
            Log.e(TAG, "ConnectivityManager is null");
            return false;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Network activeNetwork = connectivityManager.getActiveNetwork();
            if (activeNetwork == null) {
                Log.e(TAG, "Active network is null");
                return false;
            }

            NetworkCapabilities capabilities = connectivityManager.getNetworkCapabilities(activeNetwork);
            boolean hasInternet = capabilities != null && (
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ||
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET));
            
            Log.d(TAG, "Network capabilities check result: " + hasInternet);
            if (capabilities != null) {
                Log.d(TAG, "WIFI: " + capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI));
                Log.d(TAG, "CELLULAR: " + capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR));
                Log.d(TAG, "ETHERNET: " + capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET));
            }
            return hasInternet;
        } else {
            NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
            boolean isConnected = activeNetworkInfo != null && activeNetworkInfo.isConnected();
            Log.d(TAG, "Legacy network check result: " + isConnected);
            return isConnected;
        }
    }

    private void showNetworkError() {
        loadingDialog.dismissDialog();
        Snackbar.make(binding.getRoot(), 
            "No internet connection. Please check your network and try again", 
            Snackbar.LENGTH_LONG)
            .setAction("Retry", v -> sendOTP())
            .show();
    }

    private void showError(String message) {
        loadingDialog.dismissDialog();
        Snackbar.make(binding.getRoot(), message, Snackbar.LENGTH_SHORT).show();
    }

    // Add method to check Firebase configuration
    private void checkFirebaseConfig() {
        try {
            FirebaseOptions options = FirebaseApp.getInstance().getOptions();
            Log.d(TAG, "Firebase Project ID: " + options.getProjectId());
            Log.d(TAG, "Firebase App ID: " + options.getApplicationId());
            
            // Verify Google Play Services
            GoogleApiAvailability googleApi = GoogleApiAvailability.getInstance();
            int result = googleApi.isGooglePlayServicesAvailable(requireContext());
            if (result != ConnectionResult.SUCCESS) {
                Log.e(TAG, "Google Play Services not available: " + result);
                showError("Please update Google Play Services");
                return;
            }
            
            Log.d(TAG, "Firebase configuration verified successfully");
        } catch (Exception e) {
            Log.e(TAG, "Firebase configuration error: ", e);
        }
    }
}
