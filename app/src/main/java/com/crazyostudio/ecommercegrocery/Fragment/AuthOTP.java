package com.crazyostudio.ecommercegrocery.Fragment;

import static android.content.Context.VIBRATOR_SERVICE;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

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

import com.crazyostudio.ecommercegrocery.MainActivity;
import com.crazyostudio.ecommercegrocery.R;
import com.crazyostudio.ecommercegrocery.databinding.FragmentAuthOTPBinding;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AuthOTP#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AuthOTP extends Fragment {
    FragmentAuthOTPBinding binding;
    ProgressDialog dialog;
    String verificationId;
    FirebaseAuth firebaseAuth;
    private EditText mEt1, mEt2, mEt3, mEt4, mEt5, mEt6;
    private Context context;
    @SuppressLint("SetTextI18n")
    NavController navController;
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_NUMBER = "number";

    // TODO: Rename and change types of parameters
    private String number;

    public AuthOTP() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param Number Parameter 2.
     * @return A new instance of fragment AuthOTP.
     */
    // TODO: Rename and change types and number of parameters
    public static AuthOTP newInstance(String Number) {
        AuthOTP fragment = new AuthOTP();
        Bundle args = new Bundle();
        args.putString("Number", Number);
        fragment.setArguments(args);
        return fragment;
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
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentAuthOTPBinding.inflate(inflater,container,false);
        context = getContext();
        navController = Navigation.findNavController(requireActivity(),R.id.nav_host_fragment );
        if (getArguments().getString(ARG_NUMBER)==null){
            navController.popBackStack();
        }
        firebaseAuth =  FirebaseAuth.getInstance();
        dialog = new ProgressDialog(context);
        binding.fullNumber.setText("+91"+number+" ");
        binding.tvPhoneNo.setOnClickListener(view-> navController.popBackStack());
        binding.tvResend.setOnClickListener(view-> sentOTP());

        initialize();
        addTextWatcher(mEt1);
        addTextWatcher(mEt2);
        addTextWatcher(mEt3);
        addTextWatcher(mEt4);
        addTextWatcher(mEt5);
        addTextWatcher(mEt6);
        sentOTP();
        binding.btnVerify.setOnClickListener(view-> SignUp());

        return binding.getRoot();
    }
    private void initialize() {
        mEt1 = binding.otpEditText1;
        mEt2 = binding.otpEditText2;
        mEt3 = binding.otpEditText3;
        mEt4 = binding.otpEditText4;
        mEt5 = binding.otpEditText5;
        mEt6 = binding.otpEditText6;
    }
    private void addTextWatcher(@NonNull final EditText one) {
        one.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @SuppressLint("NonConstantResourceId")
            @Override
            public void afterTextChanged(Editable s) {
                switch (one.getId()) {
                    case R.id.otp_edit_text1:
                        if (one.length() == 1) {
                            mEt2.requestFocus();
                        }
                        break;
                    case R.id.otp_edit_text2:
                        if (one.length() == 1) {
                            mEt3.requestFocus();
                        } else if (one.length() == 0) {
                            mEt1.requestFocus();
                        }
                        break;
                    case R.id.otp_edit_text3:
                        if (one.length() == 1) {
                            mEt4.requestFocus();
                        } else if (one.length() == 0) {
                            mEt2.requestFocus();
                        }
                        break;
                    case R.id.otp_edit_text4:
                        if (one.length() == 1) {
                            mEt5.requestFocus();
                        } else if (one.length() == 0) {
                            mEt3.requestFocus();
                        }
                        break;
                    case R.id.otp_edit_text5:
                        if (one.length() == 1) {
                            mEt6.requestFocus();
                        } else if (one.length() == 0) {
                            mEt4.requestFocus();
                        }
                        break;
                    case R.id.otp_edit_text6:
                        if (one.length() == 1) {
                            InputMethodManager inputManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                            inputManager.hideSoftInputFromWindow(requireActivity().getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                        } else if (one.length() == 0) {
                            mEt5.requestFocus();
                        }
                        break;
                }
            }
        });
    }
    private void sentOTP(){
        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(firebaseAuth)
                        .setPhoneNumber("+91"+number)
                        .setTimeout(60L, TimeUnit.SECONDS)
                        .setActivity(requireActivity())
                        .setCallbacks(mCallback)
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }
    PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallback = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        @Override
        public void onVerificationCompleted(@NonNull PhoneAuthCredential credential) {
            signInWithPhoneAuthCredential(credential);
//            final String code = credential.getSmsCode();
//            if (code!=null)
//            {
////                verifycode(code);
//            }
            Toast.makeText(context, "on Verification Completed", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onVerificationFailed(@NonNull FirebaseException e) {
//            Toast.makeText(getContext(), e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            Log.e("TAG", "onVerificationFailed: ", e);
            Toast.makeText(context, "Error"+e, Toast.LENGTH_SHORT).show();

        }

        @Override
        public void onCodeSent(@NonNull String s,
                               @NonNull PhoneAuthProvider.ForceResendingToken token) {
            super.onCodeSent(s,token);
            verificationId = s;
            Toast.makeText(context, "Sent OTP", Toast.LENGTH_SHORT).show();
//            tokens = token;
        }
    };
    private void SignUp(){
        String UserOtp = mEt1.getText().toString() + mEt2.getText().toString() + mEt3.getText().toString() + mEt4.getText().toString() + mEt5.getText().toString() + mEt6.getText().toString();
        if (!(UserOtp.trim().length() == 6)) {
            if (Build.VERSION.SDK_INT >= 26) {
                ((Vibrator) context.getSystemService(VIBRATOR_SERVICE)).vibrate(VibrationEffect.createOneShot(150, VibrationEffect.DEFAULT_AMPLITUDE));
            } else {
                ((Vibrator) context.getSystemService(VIBRATOR_SERVICE)).vibrate(150);
            }
        }else {
//            if (UserOtp.equals(verificationId)) {
            PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId,UserOtp);
            signInWithPhoneAuthCredential(credential);
//            }

        }
    }
    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        dialog.setTitle("Waiting We are Try to Login ");
        dialog.show();
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(requireActivity(), task -> {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        if(firebaseAuth.getCurrentUser() !=null) {
                            dialog.setTitle("User is Successful Sign In we are check some information");
                            if (dialog.isShowing()) {dialog.dismiss();}
                            if (firebaseAuth.getCurrentUser().getDisplayName() == null) {
                                Bundle bundle = new Bundle();
                                bundle.putString("number", number);
                                navController.navigate(R.id.action_authOTP_to_authUserDetailsFragment,bundle);

                            }
                            else {
                                if (dialog.isShowing()) {
                                    dialog.dismiss();
                                    getActivity().finish();
//                                navController.navigate(R.id.action_authOTP_to_homeMainActivity2);

                            }

                            }
                        }
                    }

                    else {
                        if (dialog.isShowing()) {
                            dialog.dismiss();
                        }
                        Toast.makeText(context, "Retry after some time ", Toast.LENGTH_SHORT).show();
                        // Sign in failed, display a message and update the UI
                    }

                });


    }

}