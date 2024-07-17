package com.example.prm392assignment.utility;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.example.prm392assignment.model.Product;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class SharedPrefManager {
    private static final String SHARED_PREF_NAME = "my_shared_pref";
    private static final String KEY_AUTH_TOKEN = "auth_token";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_PRODUCT_QUANTITY_PREFIX = "product_quantity_";
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
    public boolean saveEmail(String email){
        Log.d("SharedPrefManager", "Saving user email: " + email);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_EMAIL, email);
        return editor.commit();
    }
    public String getEmail(){
        String email = sharedPreferences.getString(KEY_EMAIL, null);
        Log.d("SharedPrefManager", "Retrieved user email: " + email);
        return email;
    }
    public boolean clearData() {
        Log.d("SharedPrefManager", "Clearing auth token and user ID");
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(KEY_AUTH_TOKEN);
        editor.remove(KEY_USER_ID);
        editor.remove(KEY_EMAIL);
        return editor.commit(); // Use commit() for debugging
    }

    public void saveProductQuantity(String productName, int quantity) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(KEY_PRODUCT_QUANTITY_PREFIX + productName, quantity);
        editor.apply();
    }

    public int getProductQuantity(String productName) {
        return sharedPreferences.getInt(KEY_PRODUCT_QUANTITY_PREFIX + productName, 0);
    }

    public void removeProductQuantity(String productName) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(KEY_PRODUCT_QUANTITY_PREFIX + productName);
        editor.apply();
    }

    public void saveProductList(String key, List<Product> productList) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(productList);
        editor.putString(key, json);
        editor.apply();
    }

    public ArrayList<Product> getProductList(String key) {
        Gson gson = new Gson();
        String json = sharedPreferences.getString(key, null);
        Type type = new TypeToken<ArrayList<Product>>() {}.getType();
        return gson.fromJson(json, type);
    }
}
