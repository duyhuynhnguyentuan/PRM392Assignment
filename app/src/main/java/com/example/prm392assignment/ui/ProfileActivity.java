package com.example.prm392assignment.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.prm392assignment.R;
import com.example.prm392assignment.model.GetDataResponse;
import com.example.prm392assignment.network.ApiService;
import com.example.prm392assignment.network.RetrofitClient;
import com.example.prm392assignment.utility.SharedPrefManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileActivity extends AppCompatActivity {
    private TextView profileFullName,email, phone;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_profile);
        profileFullName = findViewById(R.id.profileFullName);
        email = findViewById(R.id.email);
        phone = findViewById(R.id.phone);
        ApiService apiService = RetrofitClient.getInstance().getApiService();
        Call<GetDataResponse> call = apiService.getDataResponse(SharedPrefManager.getInstance(this).getAuthToken());
        call.enqueue(new Callback<GetDataResponse>() {
            @Override
            public void onResponse(Call<GetDataResponse> call, Response<GetDataResponse> response) {
                if (response.isSuccessful()) {
                    GetDataResponse getDataResponse = response.body();
                    if (getDataResponse != null) {
                        profileFullName.setText(getDataResponse.getFirstName() + " " + getDataResponse.getLastName());
                        email.setText("Email: " + getDataResponse.getEmail() );
                        phone.setText("Phone: " +getDataResponse.getPhone());
                    }
                } else {
                    Toast.makeText(ProfileActivity.this, "Can't get data", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<GetDataResponse> call, Throwable t) {
                Log.e("MainActivity", "Network error", t);
                Toast.makeText(ProfileActivity.this, "An error occurred", Toast.LENGTH_SHORT).show();
            }
        });

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigation);

        // Fix for extra bottom padding after rotation
        bottomNavigationView.setOnApplyWindowInsetsListener(null);
        bottomNavigationView.setPadding(0, 0, 0, 0);

        bottomNavigationView.setSelectedItemId(R.id.bottom_profile);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.bottom_home) {
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                finish();
                return true;
            } else if (itemId == R.id.bottom_map) {
                startActivity(new Intent(getApplicationContext(), MapActivity.class));
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                finish();
                return true;
            } else if (itemId == R.id.bottom_chat) {
                startActivity(new Intent(getApplicationContext(), ChatActivity.class));
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                finish();
                return true;
            } else if (itemId == R.id.bottom_profile) {
                return true;
            }
            return false;
        });
        Objects.requireNonNull(getSupportActionBar()).hide();
    }
}
