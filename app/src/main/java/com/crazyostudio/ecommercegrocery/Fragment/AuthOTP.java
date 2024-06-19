package com.crazyostudio.ecommercegrocery.Fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class AuthOTP extends Fragment {

    private static final String TAG = "AuthOTP";
    private static final String ARG_NUMBER = "number";

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

        firebaseAuth = FirebaseAuth.getInstance();
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
        PhoneAuthOptions options = PhoneAuthOptions.newBuilder(firebaseAuth)
                .setPhoneNumber("+91" + number)
                .setTimeout(60L, TimeUnit.SECONDS)
                .setActivity(requireActivity())
                .setCallbacks(verificationCallbacks)
                .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    private final PhoneAuthProvider.OnVerificationStateChangedCallbacks verificationCallbacks =
            new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                @Override
                public void onVerificationCompleted(@NonNull PhoneAuthCredential credential) {
                    signInWithPhoneAuthCredential(credential);
                    Toast.makeText(context, "Verification Completed", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onVerificationFailed(@NonNull FirebaseException e) {
                    Toast.makeText(context, "Verification Failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    loadingDialog.dismissDialog(); // Show loading dialog
                    requireActivity().onBackPressed();
                }

                @Override
                public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken token) {
                    super.onCodeSent(s, token);
                    verificationId = s;
                    Toast.makeText(context, "OTP Sent", Toast.LENGTH_SHORT).show();
                    loadingDialog.dismissDialog(); // Show loading dialog
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
                navController.navigate(R.id.action_authOTP_to_authUserDetailsFragment, bundle);
                loadingDialog.dismissDialog(); // Show loading dialog

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
}
