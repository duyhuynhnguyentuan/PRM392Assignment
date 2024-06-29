package com.example.prm392assignment.ui;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.prm392assignment.R;
import com.example.prm392assignment.model.User;
import com.example.prm392assignment.model.UserResponse;
import com.example.prm392assignment.network.ApiService;
import com.example.prm392assignment.network.RetrofitClient;
import com.example.prm392assignment.utility.SharedPrefManager;
import com.google.android.material.textfield.TextInputEditText;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Register extends AppCompatActivity {
    TextInputEditText editTextEmail, editTextPassword, editTextFirstName, editTextLastName, editTextPhone;
    Button buttonReg;
    ProgressBar progressBar;
    TextView loginNow;

    private static final String TAG = "RegisterActivity";

    @Override
    public void onStart() {
        super.onStart();
        if (SharedPrefManager.getInstance(getApplicationContext()).getAuthToken() != null && SharedPrefManager.getInstance(getApplicationContext()).getUserId() != null) {
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Find views by ID
        editTextFirstName = findViewById(R.id.firstName);
        editTextLastName = findViewById(R.id.lastName);
        editTextEmail = findViewById(R.id.email);
        editTextPassword = findViewById(R.id.password);
        editTextPhone = findViewById(R.id.phone);
        buttonReg = findViewById(R.id.btn_register);
        progressBar = findViewById(R.id.progressBar);
        loginNow = findViewById(R.id.loginNow);

        loginNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Login.class);
                startActivity(intent);
                finish();
            }
        });

        buttonReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                String firstName, lastName, email, password, phone;
                firstName = String.valueOf(editTextFirstName.getText());
                lastName = String.valueOf(editTextLastName.getText());
                email = String.valueOf(editTextEmail.getText());
                password = String.valueOf(editTextPassword.getText());
                phone = String.valueOf(editTextPhone.getText());

                if (TextUtils.isEmpty(firstName)) {
                    Toast.makeText(Register.this, "Enter first name!", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                    return;
                }

                if (TextUtils.isEmpty(lastName)) {
                    Toast.makeText(Register.this, "Enter last name!", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                    return;
                }

                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(Register.this, "Enter email!", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                    return;
                }

                if (TextUtils.isEmpty(password)) {
                    Toast.makeText(Register.this, "Enter password!", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                    return;
                }

                if (TextUtils.isEmpty(phone)) {
                    Toast.makeText(Register.this, "Enter phone number!", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                    return;
                }

                User user = new User();
                user.setFirstName(firstName);
                user.setLastName(lastName);
                user.setEmail(email);
                user.setPassword(password);
                user.setPhone(phone);

                ApiService apiService = RetrofitClient.getInstance().getApiService();
                Call<UserResponse> call = apiService.createUser(user);

                call.enqueue(new Callback<UserResponse>() {
                    @Override
                    public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
                        progressBar.setVisibility(View.GONE);
                        if (response.isSuccessful() && response.body() != null) {
                            UserResponse userResponse = response.body();
                            Toast.makeText(Register.this, userResponse.getMessage(), Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(getApplicationContext(), Login.class);
                            startActivity(intent);
                            finish();
                        } else {
                            Log.e(TAG, "Registration failed: " + response.code() + " - " + response.message());
                            if (response.errorBody() != null) {
                                try {
                                    String errorBody = response.errorBody().string();
                                    Log.e(TAG, "Error body: " + errorBody);
                                    Toast.makeText(Register.this, errorBody, Toast.LENGTH_SHORT).show();
                                } catch (IOException e) {
                                    Log.e(TAG, "Error parsing error body", e);
                                }
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<UserResponse> call, Throwable t) {
                        progressBar.setVisibility(View.GONE);
                        Log.e(TAG, "Network error", t);
                        Toast.makeText(Register.this, "An error occurred. Please try again.", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        editTextFirstName.setText("");
        editTextLastName.setText("");
        editTextEmail.setText("");
        editTextPassword.setText("");
        editTextPhone.setText("");
    }
}
