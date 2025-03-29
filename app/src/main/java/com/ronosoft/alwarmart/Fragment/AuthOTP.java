package com.ronosoft.alwarmart.Fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
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

import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.ronosoft.alwarmart.HelperClass.ValuesHelper;
import com.ronosoft.alwarmart.Manager.ProductManager;
import com.ronosoft.alwarmart.Model.UserinfoModels;
import com.ronosoft.alwarmart.R;
import com.ronosoft.alwarmart.Services.AuthService;
import com.ronosoft.alwarmart.Services.DatabaseService;
import com.ronosoft.alwarmart.databinding.FragmentAuthOTPBinding;
import com.ronosoft.alwarmart.javaClasses.LoadingDialog;
import com.otpless.main.OtplessManager;
import com.otpless.main.OtplessView;
import com.otpless.dto.HeadlessRequest;
import com.otpless.dto.HeadlessResponse;
import com.ronosoft.alwarmart.javaClasses.TokenManager;
import com.ronosoft.alwarmart.javaClasses.basicFun;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONArray;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class AuthOTP extends Fragment {

    private static final String TAG = "AuthOTP";
    private static final String ARG_NUMBER = "number";
    private static final String API_BASE_URL = "https://alwar-mart-admin-on-web-for-mobile-app.vercel.app/api/generate-token";

    private FragmentAuthOTPBinding binding;
    private EditText mEt1, mEt2, mEt3, mEt4, mEt5, mEt6;
    private String number;
    private LoadingDialog loadingDialog;
    private OtplessView otplessView;
    private OkHttpClient client;
    private FirebaseAuth mAuth;
    private DatabaseService databaseService;
    private NavController navController;
    private boolean isLoading = false;

    public AuthOTP() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .build();
        if (getArguments() != null) {
            number = getArguments().getString(ARG_NUMBER);
        }
    }

    @SuppressLint("SetTextI18n")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentAuthOTPBinding.inflate(inflater, container, false);
        loadingDialog = new LoadingDialog(requireActivity());
        navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment);

        initializeOTPless();
        databaseService = new DatabaseService();
        setupViews();

        if (number != null) {
            sendOTP();
        }

        binding.btnVerify.setOnClickListener(view -> {
            if (!isLoading) {
                verifyOTP();
            }
        });

        return binding.getRoot();
    }

    private void initializeOTPless() {
        try {
            showLoading();
            otplessView = OtplessManager.getInstance().getOtplessView(requireActivity());
            otplessView.initHeadless("JU7POLNCN4P9VT1GY3LD");
            otplessView.setHeadlessCallback(this::onHeadlessCallback);
            Log.d(TAG, "OTPless initialized successfully");
        } catch (Exception e) {
            Log.e(TAG, "Error initializing OTPless: " + e.getMessage());
            hideLoading();
            showError("Failed to initialize authentication");
        }
    }

    private void setupViews() {
        initializeEditTexts();
    }

    private void sendOTP() {
        showLoading();
        HeadlessRequest request = new HeadlessRequest();
        request.setPhoneNumber("+91", number);
        otplessView.startHeadless(request, this::onHeadlessCallback);
    }

    private void verifyOTP() {
        String userOTP = getOtpFromInputs();
        if (userOTP.length() < 6) {
            Snackbar.make(binding.getRoot(), "Please enter a valid OTP", Snackbar.LENGTH_SHORT).show();
            return;
        }
        showLoading();
        HeadlessRequest request = new HeadlessRequest();
        request.setPhoneNumber("+91", number);
        request.setOtp(userOTP);
        otplessView.startHeadless(request, this::onHeadlessCallback);
    }

    private void onHeadlessCallback(@NonNull HeadlessResponse response) {
        Log.d(TAG, "OTPless callback received: " + response.getResponseType());
        Log.d(TAG, "Response status code: " + response.getStatusCode());
        if (response.getStatusCode() == 200) {
            try {
                JSONObject responseData = response.getResponse();
                Log.d(TAG, "OTPless Response: " + responseData.toString());
                switch (response.getResponseType()) {
                    case "ONETAP":
                        handleOTPlessAuthentication(responseData);
                        break;
                    case "VERIFY":
                        Log.d(TAG, "Verification completed");
                        hideLoading();
                        break;
                    default:
                        hideLoading();
                        Log.d(TAG, "Unhandled response type: " + response.getResponseType());
                        break;
                }
            } catch (Exception e) {
                hideLoading();
                Log.e(TAG, "Error processing response: " + e.getMessage(), e);
                showError("Authentication failed: " + e.getMessage());
            }
        } else {
            hideLoading();
            Log.e(TAG, "Error response: " + response.getResponse().toString());
            showError("Authentication failed with status: " + response.getStatusCode());
        }
    }

    private void handleOTPlessAuthentication(JSONObject responseData) {
        try {
            String userId = responseData.getString("userId");
            Log.d(TAG, "Got userId from OTPless: " + userId);
            JSONArray identities = responseData.getJSONArray("identities");
            JSONObject identity = identities.getJSONObject(0);
            String otplessToken = responseData.getString("token");
            String idToken = responseData.getString("idToken");

            saveTokens(otplessToken, idToken);
            getCustomToken(userId);
        } catch (Exception e) {
            hideLoading();
            Log.e(TAG, "Error handling authentication: " + e.getMessage(), e);
            showError("Failed to process authentication data");
        }
    }

    private void saveTokens(String otplessToken, String idToken) {
        SharedPreferences prefs = requireActivity()
                .getSharedPreferences("AuthTokens", Context.MODE_PRIVATE);
        prefs.edit()
                .putString("otpless_token", otplessToken)
                .putString("otpless_id_token", idToken)
                .putLong("token_timestamp", System.currentTimeMillis())
                .apply();
    }

    private void getCustomToken(String userId) {
        showLoading();
        Log.d(TAG, "Requesting custom token for userId: " + userId);
        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("uid", userId);
            jsonBody.put("phone", number);
            jsonBody.put("premium", false);
        } catch (JSONException e) {
            hideLoading();
            Log.e(TAG, "Error creating request body", e);
            showError("Failed to create authentication request");
            return;
        }
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        RequestBody body = RequestBody.create(jsonBody.toString(), JSON);
        Request request = new Request.Builder()
                .url(API_BASE_URL)
                .post(body)
                .addHeader("Content-Type", "application/json")
                .build();

        Log.d(TAG, "Sending request to: " + API_BASE_URL);
        Log.d(TAG, "Request body: " + jsonBody.toString());

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.e(TAG, "Network request failed", e);
                requireActivity().runOnUiThread(() -> {
                    hideLoading();
                    showError("Network error: " + e.getMessage());
                });
            }
            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                String responseBody = response.body() != null ? response.body().string() : "";
                Log.d(TAG, "Response code: " + response.code());
                Log.d(TAG, "Response body: " + responseBody);
                if (response.isSuccessful()) {
                    try {
                        JSONObject jsonResponse = new JSONObject(responseBody);
                        if (jsonResponse.getBoolean("success")) {
                            String customToken = jsonResponse.getString("token");
                            if (!customToken.contains(".")) {
                                Log.e(TAG, "Invalid custom token format: " + customToken);
                                requireActivity().runOnUiThread(() -> {
                                    hideLoading();
                                    showError("Received invalid token format");
                                });
                                return;
                            }
                            Log.i(TAG, "Got custom token successfully: " + customToken);
                            signInWithCustomToken(customToken);
                        } else {
                            String error = jsonResponse.optString("error", "Unknown error");
                            Log.e(TAG, "Error in response: " + error);
                            requireActivity().runOnUiThread(() -> {
                                hideLoading();
                                showError("Authentication failed: " + error);
                            });
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Error parsing response", e);
                        requireActivity().runOnUiThread(() -> {
                            hideLoading();
                            showError("Failed to process server response");
                        });
                    }
                } else {
                    Log.e(TAG, "Server error: " + response.code());
                    requireActivity().runOnUiThread(() -> {
                        hideLoading();
                        showError("Server error: " + response.code());
                    });
                }
            }
        });
    }

    private void signInWithCustomToken(String customToken) {
        showLoading();
        Log.d(TAG, "Signing in with custom token");

        mAuth.signInWithCustomToken(customToken)
                .addOnCompleteListener(requireActivity(), task -> {
                    DatabaseReference authRef = FirebaseDatabase.getInstance().getReference().child("AuthLogs");
                    String logId = authRef.push().getKey();

                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();

                        if (user != null) {
                            Map<String, Object> authData = new HashMap<>();
                            authData.put("status", "success");
                            authData.put("uid", user.getUid());
                            authData.put("email", user.getEmail());
                            authData.put("displayName", user.getDisplayName());
                            authData.put("phoneNumber", user.getPhoneNumber());
                            authData.put("provider", user.getProviderId());
                            authData.put("tenantId", mAuth.getTenantId());
                            authData.put("timestamp", System.currentTimeMillis());

                            authRef.child("firebase").child(logId).setValue(authData);
                        }

                        Log.i(TAG, "Firebase user: " + user);
                        Log.i(TAG, "User UID: " + mAuth.getUid());
                        Log.i(TAG, "Tenant ID: " + mAuth.getTenantId());
                        navigateToNextScreen();
                    } else {
                        Exception exception = task.getException();
                        String errorMessage = (exception != null) ? exception.getMessage() : "Unknown error";

                        Map<String, Object> errorData = new HashMap<>();
                        errorData.put("status", "failure");
                        errorData.put("error", errorMessage);
                        errorData.put("timestamp", System.currentTimeMillis());

                        authRef.child("firebase").child(logId).setValue(errorData);

                        Log.e(TAG, "signInWithCustomToken:failure", exception);
                        hideLoading();
                        showError("Firebase authentication failed: " + errorMessage);
                    }
                });
    }

    private void navigateToNextScreen() {
        showLoading();
        new AuthService().ClearData(requireContext());
        if (mAuth.getCurrentUser() != null && mAuth.getCurrentUser().getDisplayName() == null) {
            databaseService.CheckNotificationToken(new DatabaseService.UpdateTokenCallback() {
                @Override
                public void onSuccess(String token) {
                    TokenManager.getInstance(requireContext()).clearToken();
                    TokenManager.getInstance(requireContext()).saveToken(token);
                    setupUser(token);
                }
                @Override
                public void onError(String errorMessage) {
                    hideLoading();
                    Log.i("onError", "onError: " + errorMessage);
                }
            });
        } else {
            updateToken();
        }
    }

    void setupUser(String token) {
        showLoading();
        String defaultUserName = ValuesHelper.DEFAULT_USER_NAME;
        UserinfoModels userInfo = new UserinfoModels(token, mAuth.getUid(), defaultUserName, number, true);
        databaseService.setUserInfo(userInfo, new DatabaseService.SetUserInfoCallback() {
            @Override
            public void onSuccess(Task<Void> task) {
                hideLoading();
                Bundle bundle = new Bundle();
                bundle.putString("number", number);
                navController.navigate(R.id.action_authOTP_to_authUserDetailsFragment, bundle);
            }
            @Override
            public void onError(String errorMessage) {
                hideLoading();
                basicFun.AlertDialog(requireContext(), errorMessage);
            }
        });
    }

    private void updateToken() {
        showLoading();
        databaseService.CheckNotificationToken(new DatabaseService.UpdateTokenCallback() {
            @Override
            public void onSuccess(String token) {
                TokenManager.getInstance(requireContext()).clearToken();
                TokenManager.getInstance(requireContext()).saveToken(token);
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                DocumentReference userRef = db.collection("UserInfo").document(Objects.requireNonNull(mAuth.getUid()));
                userRef.update("token", token)
                        .addOnSuccessListener(aVoid -> {
                            CheckUserCart();
                        })
                        .addOnFailureListener(e -> {
                            hideLoading();
                            Toast.makeText(requireContext(), "Failed to update token", Toast.LENGTH_SHORT).show();
                        });
            }
            @Override
            public void onError(String errorMessage) {
                hideLoading();
                Log.e(TAG, "Token update error: " + errorMessage);
            }
        });
    }

    private void CheckUserCart() {
        showLoading();
        ProductManager productManager = new ProductManager(requireContext());
        productManager.syncCartFromFirebase(new ProductManager.SyncCartCallback() {
            @Override
            public void onSyncSuccess() {
                hideLoading();
                requireActivity().finish();
            }

            @Override
            public void onSyncFailure(String error) {
                hideLoading();
                Toast.makeText(requireContext(), "Sync failed: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showError(String message) {
        requireActivity().runOnUiThread(() -> {
            hideLoading();
            Snackbar.make(binding.getRoot(), message, Snackbar.LENGTH_LONG).show();
        });
    }

    private void showLoading() {
        if (!isLoading) {
            isLoading = true;
            requireActivity().runOnUiThread(() -> {
                loadingDialog.startLoadingDialog();
                binding.btnVerify.setEnabled(false);
            });
        }
    }

    private void hideLoading() {
        if (isLoading) {
            isLoading = false;
            requireActivity().runOnUiThread(() -> {
                loadingDialog.dismissDialog();
                binding.btnVerify.setEnabled(true);
            });
        }
    }

    private void initializeEditTexts() {
        mEt1 = binding.otpEditText1;
        mEt2 = binding.otpEditText2;
        mEt3 = binding.otpEditText3;
        mEt4 = binding.otpEditText4;
        mEt5 = binding.otpEditText5;
        mEt6 = binding.otpEditText6;
        addTextWatchers();
    }

    private String getOtpFromInputs() {
        return mEt1.getText().toString() +
                mEt2.getText().toString() +
                mEt3.getText().toString() +
                mEt4.getText().toString() +
                mEt5.getText().toString() +
                mEt6.getText().toString();
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
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) { }
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
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) { }
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
        InputMethodManager inputManager = (InputMethodManager) requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (inputManager != null && requireActivity().getCurrentFocus() != null) {
            inputManager.hideSoftInputFromWindow(requireActivity().getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}