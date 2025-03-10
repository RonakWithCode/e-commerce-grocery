package com.ronosoft.alwarmart.javaClasses;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

/**
 * A singleton class for managing user tokens with SharedPreferences.
 */
public class TokenManager {

    private static final String TAG = "TokenManager";
    private static final String SHARED_PREF_NAME = "User";
    private static final String KEY_TOKEN = "token";

    private static TokenManager instance;
    private final SharedPreferences sharedPreferences;

    /**
     * Private constructor to enforce singleton pattern.
     * Uses the application context to avoid memory leaks.
     *
     * @param context the application context.
     */
    private TokenManager(Context context) {
        sharedPreferences = context.getApplicationContext()
                .getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
    }

    /**
     * Returns the singleton instance of TokenManager.
     *
     * @param context the context used to get the application context.
     * @return the singleton instance.
     */
    public static synchronized TokenManager getInstance(Context context) {
        if (instance == null) {
            instance = new TokenManager(context);
        }
        return instance;
    }

    /**
     * Saves the token synchronously using commit().
     * The commit() method returns a boolean indicating success or failure.
     *
     * @param token the token string to be saved.
     * @return true if the token was saved successfully, false otherwise.
     */
    public boolean saveToken(String token) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_TOKEN, token);
        boolean isSaved = editor.commit(); // Synchronous saving
        if (!isSaved) {
            Log.e(TAG, "Failed to save token");
        }
        return isSaved;
    }

    /**
     * Retrieves the token from SharedPreferences.
     *
     * @return the token string, or null if not found.
     */
    public String getToken() {
        return sharedPreferences.getString(KEY_TOKEN, null);
    }

    /**
     * Clears the token from SharedPreferences.
     *
     * @return true if the token was cleared successfully, false otherwise.
     */
    public boolean clearToken() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(KEY_TOKEN);
        boolean isCleared = editor.commit(); // Synchronous removal
        if (!isCleared) {
            Log.e(TAG, "Failed to clear token");
        }

        return isCleared;
    }

    /**
     * Checks whether a token exists in SharedPreferences.
     *
     * @return true if a token exists, false otherwise.
     */
    public boolean hasToken() {
        return sharedPreferences.contains(KEY_TOKEN);
    }
}
