package com.crazyostudio.ecommercegrocery.Fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
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
import androidx.core.content.ContextCompat;
import android.graphics.Color;

public class AuthOTP extends Fragment {

    private static final String TAG = "AuthOTP";
    private static final String ARG_NUMBER = "number";
    private static final int MAX_RETRIES = 3;
    private static final long RETRY_DELAY_MS = 2000; // 2 seconds
    private static final long RATE_LIMIT_COOLDOWN_MS = 60000; // 1 minute cooldown
    private static final long RESEND_DELAY_MS = 30000; // 30 seconds
    private static final int OTP_LENGTH = 6;
    
    private long lastOTPRequestTime = 0;
    private int retryCount = 0;
    
    private CountDownTimer resendTimer;
    private boolean isVerificationInProgress = false;
    private String lastVerificationId;
    private PhoneAuthProvider.ForceResendingToken resendToken;
    
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
//                showError("Firebase connection state: " + connected);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "Firebase connection listener cancelled", error.toException());
//                showError("connection listener cancelled");
            }
        });
    }

    @SuppressLint("SetTextI18n")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentAuthOTPBinding.inflate(inflater, container, false);
        initializeViews();
        setupListeners();
        startOtpProcess();
        return binding.getRoot();
    }

    private void initializeViews() {
        context = requireContext();
        navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment);
        loadingDialog = new LoadingDialog(requireActivity());
        databaseService = new DatabaseService();
        
        if (number == null) {
            navController.popBackStack();
            return;
        }

        binding.fullNumber.setText(String.format("+91 %s", number));
        initializeEditTexts();
        addTextWatchers();
    }

    private void setupListeners() {
        binding.tvPhoneNo.setOnClickListener(v -> navController.popBackStack());
        binding.tvResend.setOnClickListener(v -> resendOTP());
        binding.btnVerify.setOnClickListener(v -> verifyOTP());
    }

    private void startOtpProcess() {
        if (!isNetworkAvailable()) {
            showNetworkError();
            return;
        }
        sendOTP();
        startResendTimer();
    }

    private void startResendTimer() {
        binding.tvResend.setEnabled(false);
        if (resendTimer != null) {
            resendTimer.cancel();
        }

        resendTimer = new CountDownTimer(RESEND_DELAY_MS, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                if (isAdded()) {
                    binding.tvResend.setText(String.format(
                        getString(R.string.resend_countdown),
                        millisUntilFinished / 1000
                    ));
                }
            }

            @Override
            public void onFinish() {
                if (isAdded()) {
                    binding.tvResend.setEnabled(true);
                    binding.tvResend.setText(getString(R.string.resend_otp));
                }
            }
        }.start();
    }

    private void sendOTP() {
        if (isVerificationInProgress) {
            return;
        }

        isVerificationInProgress = true;
        loadingDialog.startLoadingDialog("Sending OTP...");

        try {
            PhoneAuthOptions options = PhoneAuthOptions.newBuilder(firebaseAuth)
                    .setPhoneNumber("+91" + number)
                    .setTimeout(60L, TimeUnit.SECONDS)
                    .setActivity(requireActivity())
                    .setCallbacks(verificationCallbacks)
                    .setForceResendingToken(resendToken)
                    .build();

            PhoneAuthProvider.verifyPhoneNumber(options);
        } catch (Exception e) {
            isVerificationInProgress = false;
            loadingDialog.dismissDialog();
            handleSendOTPError(e);
        }
    }

    private void resendOTP() {
        if (resendToken == null) {
            sendOTP();
            return;
        }

        loadingDialog.startLoadingDialog("Resending OTP...");
        PhoneAuthOptions options = PhoneAuthOptions.newBuilder(firebaseAuth)
                .setPhoneNumber("+91" + number)
                .setTimeout(60L, TimeUnit.SECONDS)
                .setActivity(requireActivity())
                .setCallbacks(verificationCallbacks)
                .setForceResendingToken(resendToken)
                .build();

        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    private void verifyOTP() {
        String userOtp = getEnteredOTP();
        if (userOtp.length() != OTP_LENGTH) {
            showError("Please enter complete OTP");
            vibrateDevice();
            return;
        }

        if (lastVerificationId == null) {
            showError("Please wait for OTP");
            return;
        }

        loadingDialog.startLoadingDialog("Verifying OTP...");
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(lastVerificationId, userOtp);
        signInWithPhoneAuthCredential(credential);
    }

    private String getEnteredOTP() {
        return mEt1.getText().toString() +
               mEt2.getText().toString() +
               mEt3.getText().toString() +
               mEt4.getText().toString() +
               mEt5.getText().toString() +
               mEt6.getText().toString();
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
        
        // If we hit the rate limit, enforce a longer cooldown
        if (e instanceof FirebaseTooManyRequestsException) {
            retryCount = 0;
            lastOTPRequestTime = System.currentTimeMillis();
            showError("Too many requests. Please try again after a few minutes.");
            return;
        }
        
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
                    isVerificationInProgress = false;
                    loadingDialog.dismissDialog();
                    signInWithPhoneAuthCredential(credential);
                }

                @Override
                public void onVerificationFailed(@NonNull FirebaseException e) {
                    isVerificationInProgress = false;
                    loadingDialog.dismissDialog();
                    handleVerificationError(e);
                }

                @Override
                public void onCodeSent(@NonNull String verificationId,
                                     @NonNull PhoneAuthProvider.ForceResendingToken token) {
                    super.onCodeSent(verificationId, token);
                    isVerificationInProgress = false;
                    lastVerificationId = verificationId;
                    resendToken = token;
                    loadingDialog.dismissDialog();
                    
                    showSuccess("OTP sent successfully");
                    startResendTimer();
                }
            };

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(requireActivity(), task -> {
                    if (task.isSuccessful()) {
                        handleSuccessfulSignIn();
                    } else {
                        handleSignInError(task.getException());
                    }
                });
    }

    private void handleSuccessfulSignIn() {
        if (firebaseAuth.getCurrentUser() != null) {
            if (firebaseAuth.getCurrentUser().getDisplayName() == null) {
                setupNewUser();
            } else {
                updateExistingUser();
            }
        }
    }

    private void setupNewUser() {
        databaseService.CheckNotificationToken(new DatabaseService.UpdateTokenCallback() {
            @Override
            public void onSuccess(String token) {
                TokenManager.getInstance(requireContext()).saveToken(token);
                UserinfoModels userInfo = new UserinfoModels(
                    token,
                    firebaseAuth.getUid(),
                    ValuesHelper.DEFAULT_USER_NAME,
                    number,
                    true
                );
                saveUserInfo(userInfo);
            }

            @Override
            public void onError(String errorMessage) {
                loadingDialog.dismissDialog();
                showError("Failed to setup user: " + errorMessage);
            }
        });
    }

    private void saveUserInfo(UserinfoModels userInfo) {
        databaseService.setUserInfo(userInfo, new DatabaseService.SetUserInfoCallback() {
            @Override
            public void onSuccess(Task<Void> task) {
                loadingDialog.dismissDialog();
                Bundle bundle = new Bundle();
                bundle.putString("number", number);
                navController.navigate(R.id.action_authOTP_to_authUserDetailsFragment, bundle);
            }

            @Override
            public void onError(String errorMessage) {
                loadingDialog.dismissDialog();
                showError(errorMessage);
            }
        });
    }

    private void updateExistingUser() {
        databaseService.CheckNotificationToken(new DatabaseService.UpdateTokenCallback() {
            @Override
            public void onSuccess(String token) {
                TokenManager tokenManager = TokenManager.getInstance(requireContext());
                tokenManager.clearToken();
                tokenManager.saveToken(token);
                
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                DocumentReference userRef = db.collection("UserInfo")
                    .document(Objects.requireNonNull(firebaseAuth.getUid()));
                
                userRef.update("token", token)
                    .addOnSuccessListener(aVoid -> {
                        loadingDialog.dismissDialog();
                        requireActivity().finish();
                    })
                    .addOnFailureListener(e -> {
                        loadingDialog.dismissDialog();
                        showError("Failed to update token");
                        Log.e(TAG, "Token update failed", e);
                    });
            }

            @Override
            public void onError(String errorMessage) {
                loadingDialog.dismissDialog();
                showError("Failed to update token: " + errorMessage);
                Log.e(TAG, "Token check failed: " + errorMessage);
            }
        });
    }

    private void handleSignInError(Exception exception) {
        loadingDialog.dismissDialog();
        if (exception instanceof FirebaseAuthInvalidCredentialsException) {
            showError(getString(R.string.invalid_otp));
            clearOTPFields();
        } else {
            showError(getString(R.string.verification_failed));
            Log.e(TAG, "Sign-in failed", exception);
        }
    }

    private void handleVerificationError(FirebaseException e) {
        String errorMessage;
        if (e instanceof FirebaseAuthInvalidCredentialsException) {
            errorMessage = "Invalid phone number format";
        } else if (e instanceof FirebaseTooManyRequestsException) {
            errorMessage = getString(R.string.too_many_requests);
        } else if (e instanceof FirebaseNetworkException) {
            errorMessage = getString(R.string.network_error);
        } else {
            errorMessage = "Verification failed: " + e.getMessage();
        }
        showError(errorMessage);
        Log.e(TAG, "Verification failed", e);
    }

    private void clearOTPFields() {
        mEt1.setText("");
        mEt2.setText("");
        mEt3.setText("");
        mEt4.setText("");
        mEt5.setText("");
        mEt6.setText("");
        mEt1.requestFocus();
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
        if (isAdded()) {
            Snackbar.make(binding.getRoot(), message, Snackbar.LENGTH_LONG)
                    .setAction("OK", v -> {})
                    .setActionTextColor(ContextCompat.getColor(requireContext(), R.color.white))
                    .setBackgroundTint(ContextCompat.getColor(requireContext(), R.color.error_color))
                    .setTextColor(Color.WHITE)
                    .show();
        }
    }

    private void showSuccess(String message) {
        if (isAdded()) {
            Snackbar.make(binding.getRoot(), message, Snackbar.LENGTH_SHORT)
                    .setBackgroundTint(ContextCompat.getColor(requireContext(), R.color.success_color))
                    .setTextColor(Color.WHITE)
                    .show();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (resendTimer != null) {
            resendTimer.cancel();
        }
        loadingDialog.dismissDialog();
    }
}


