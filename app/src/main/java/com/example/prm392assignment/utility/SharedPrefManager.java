package com.example.prm392assignment.utility;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

public class SharedPrefManager {
    private static final String SHARED_PREF_NAME = "my_shared_pref";
    private static final String KEY_AUTH_TOKEN = "auth_token";
    private static final String KEY_USER_ID = "user_id";
    private static SharedPrefManager instance;
    private SharedPreferences sharedPreferences;

    private SharedPrefManager(Context context) {
        sharedPreferences = context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
    }

    public static synchronized SharedPrefManager getInstance(Context context) {
        if (instance == null) {
            instance = new SharedPrefManager(context);
        }
        return instance;
    }

    public boolean saveAuthToken(String authToken) {
        Log.d("SharedPrefManager", "Saving auth token: " + authToken);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_AUTH_TOKEN, authToken);
        return editor.commit(); // Use commit() for debugging
    }

    public String getAuthToken() {
        String authToken = sharedPreferences.getString(KEY_AUTH_TOKEN, null);
        Log.d("SharedPrefManager", "Retrieved auth token: " + authToken);
        return authToken;
    }

    public boolean saveUserId(String userId) {
        Log.d("SharedPrefManager", "Saving user ID: " + userId);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_USER_ID, userId);
        return editor.commit(); // Use commit() for debugging
    }

    public String getUserId() {
        String userId = sharedPreferences.getString(KEY_USER_ID, null);
        Log.d("SharedPrefManager", "Retrieved user ID: " + userId);
        return userId;
    }

    public boolean clearData() {
        Log.d("SharedPrefManager", "Clearing auth token and user ID");
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(KEY_AUTH_TOKEN);
        editor.remove(KEY_USER_ID);
        return editor.commit(); // Use commit() for debugging
    }
}
