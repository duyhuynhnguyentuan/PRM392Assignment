package com.example.prm392assignment.ui;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.prm392assignment.R;
import com.example.prm392assignment.model.LoginRequest;
import com.example.prm392assignment.model.LoginResponse;
import com.example.prm392assignment.network.ApiService;
import com.example.prm392assignment.network.RetrofitClient;
import com.example.prm392assignment.utility.SharedPrefManager;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Login extends AppCompatActivity {
    TextInputEditText editTextEmail, editTextPassword;
    Button buttonLog, showHideBtn;
    ProgressBar progressBar;
    TextView registerNow;

    private static final String TAG = "LoginActivity";

    @Override
    public void onStart() {
        super.onStart();
        if (SharedPrefManager.getInstance(getApplicationContext()).getAuthToken() != null && SharedPrefManager.getInstance(getApplicationContext()).getUserId() != null) {
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
            finish();
        } else {
            System.out.println(SharedPrefManager.getInstance(getApplicationContext()).getAuthToken());
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        editTextEmail = findViewById(R.id.email);
        editTextPassword = findViewById(R.id.password);
        buttonLog = findViewById(R.id.btn_login);
        progressBar = findViewById(R.id.progressBar);
        registerNow = findViewById(R.id.registerNow);
        showHideBtn = findViewById(R.id.showHideBtn);
        editTextPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
        showHideBtn.setOnClickListener(view -> {
            if (showHideBtn.getText().toString().equals("Show")) {
                editTextPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                showHideBtn.setText("Hide");
            } else {
                editTextPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                showHideBtn.setText("Show");
            }
        });
        registerNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Register.class);
                startActivity(intent);
                finish();
            }
        });

        buttonLog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                String email, password;
                email = String.valueOf(editTextEmail.getText());
                password = String.valueOf(editTextPassword.getText());

                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(Login.this, "Enter email!", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                    return;
                }

                if (TextUtils.isEmpty(password)) {
                    Toast.makeText(Login.this, "Enter password!", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                    return;
                }
                LoginRequest loginRequest = new LoginRequest(email, password);
                ApiService apiService = RetrofitClient.getInstance().getApiService();
                Call<LoginResponse> call = apiService.loginUser(loginRequest);
                call.enqueue(new Callback<LoginResponse>() {
                    @Override
                    public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                        progressBar.setVisibility(View.GONE);
                        if (response.isSuccessful()) {
                            LoginResponse loginResponse = response.body();
                            if (loginResponse != null) {
                                // Save auth token and user ID
                                boolean isAuthTokenSaved = SharedPrefManager.getInstance(getApplicationContext()).saveAuthToken(loginResponse.getAuthToken());
                                boolean isUserIdSaved = SharedPrefManager.getInstance(getApplicationContext()).saveUserId(loginResponse.getUserId());

                                if (isAuthTokenSaved && isUserIdSaved) {
                                    Toast toast = Toast.makeText(getApplicationContext(), "Login successful", Toast.LENGTH_SHORT);
                                    toast.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, 0);
                                    toast.show();
                                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                    startActivity(intent);
                                    finish();
                                } else {
                                    Toast.makeText(Login.this, "Failed to save login data", Toast.LENGTH_SHORT).show();
                                }
                            }
                        } else {
                            // Handle request errors depending on status code
                            Log.e(TAG, "Login failed: " + response.code() + " - " + response.message());
                            if (response.errorBody() != null) {
                                try {
                                    String errorBody = response.errorBody().string();
                                    Log.e(TAG, "Error body: " + errorBody);

                                    // Parse the error body to extract the message
                                    JSONObject jsonObject = new JSONObject(errorBody);
                                    String errorMessage = jsonObject.optString("message", "An error occurred");

                                    Toast.makeText(Login.this, errorMessage, Toast.LENGTH_SHORT).show();
                                } catch (IOException | JSONException e) {
                                    Log.e(TAG, "Error parsing error body", e);
                                    Toast.makeText(Login.this, "An error occurred", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<LoginResponse> call, Throwable t) {
                        // Handle failure
                        progressBar.setVisibility(View.GONE);
                        Log.e(TAG, "Network error", t);
                        Toast.makeText(Login.this, "An error occurred", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        editTextEmail.setText("");
        editTextPassword.setText("");
    }
}
