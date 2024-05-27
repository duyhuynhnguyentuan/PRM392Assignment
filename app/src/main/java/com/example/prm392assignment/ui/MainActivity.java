package com.example.prm392assignment.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.example.prm392assignment.R;
import com.example.prm392assignment.model.Product;
import com.example.prm392assignment.network.RetrofitClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
    FirebaseAuth auth;
//    Button logoutButton, closeAppButton;
    TextView textView;
    FirebaseUser user;
    private RecyclerView recyclerView;
    private ProductAdapter productAdapter;
    private LottieAnimationView loading_animation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Objects.requireNonNull(getSupportActionBar()).hide();
        auth = FirebaseAuth.getInstance();
//        logoutButton = (Button) findViewById(R.id.logout_btn);
//        closeAppButton = (Button) findViewById(R.id.shut_down);
        textView = findViewById(R.id.user_details);
        loading_animation = findViewById(R.id.loading_animation);
        user = auth.getCurrentUser();
        if(user == null) {
            Intent intent = new Intent(getApplicationContext(), Login.class);
            startActivity(intent);
            finish();
        }
        // lấy string được gửi về từ intent của login
        if(getIntent() != null) {
            String emailName = getIntent().getStringExtra("name");
            textView.setText("Xin chào " + emailName + " !");
        }
        //recycler view list of products
        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        RetrofitClient.getInstance().getApiService().getProducts().enqueue(new Callback<List<Product>>() {
            @Override
            public void onResponse(Call<List<Product>> call, Response<List<Product>> response) {
                if(response.isSuccessful()){
                    List<Product> products = response.body();
                    productAdapter = new ProductAdapter(MainActivity.this, products);
                    recyclerView.setAdapter(productAdapter);
                    loading_animation.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
                }else{
                    Log.e("Main activity:", "Error: " +response.code());
                }
            }

            @Override
            public void onFailure(Call<List<Product>> call, Throwable t) {

            }
        });
        //addition feature(well not necessary)
//        logoutButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                FirebaseAuth.getInstance().signOut();
//                Intent intent = new Intent(getApplicationContext(), Login.class);
//                startActivity(intent);
//                finish();
//            }
//        });
//        closeAppButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                finish();
//                System.exit(0);
//            }
//        });
    }
}