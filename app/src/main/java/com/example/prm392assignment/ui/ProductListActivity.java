package com.example.prm392assignment.ui;

import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.prm392assignment.R;
import com.example.prm392assignment.model.Product;
import com.example.prm392assignment.model.ProductResponse;
import com.example.prm392assignment.network.ApiService;
import com.example.prm392assignment.network.RetrofitClient;
import com.example.prm392assignment.ui.adapter.ProductAdapter;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProductListActivity extends AppCompatActivity {
    private RecyclerView listOfProducts;
    private ImageView backBtn;
    private ProgressBar progressBarListProduct;
    private List<Product> productList = new ArrayList<>();
    private ProductAdapter productAdapter;
    private void fetchProducts() {
        progressBarListProduct.setVisibility(View.VISIBLE);

        ApiService apiService = RetrofitClient.getInstance().getApiService();
        Call<ProductResponse> call = apiService.getProducts();
        call.enqueue(new Callback<ProductResponse>() {
            @Override
            public void onResponse(Call<ProductResponse> call, Response<ProductResponse> response) {
                progressBarListProduct.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null) {
                    productList.addAll(response.body().getProducts());
                    productAdapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(ProductListActivity.this, "Failed to fetch products", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ProductResponse> call, Throwable t) {
                progressBarListProduct.setVisibility(View.GONE);
                Toast.makeText(ProductListActivity.this, "An error occurred", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void setVariables(){
        backBtn.setOnClickListener(v -> finish());
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Remove title bar before setting content view
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getSupportActionBar().hide();

        // Make activity full screen
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_product_list);

//        // Enable EdgeToEdge
//        EdgeToEdge.enable(this);
//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
//            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
//            return insets;
//        });

        // Initialize views
        backBtn = findViewById(R.id.backBtn);
        listOfProducts = findViewById(R.id.listOfProducts);
        progressBarListProduct = findViewById(R.id.progressBarListProduct);

        // Set up RecyclerView
        listOfProducts.setLayoutManager(new GridLayoutManager(ProductListActivity.this, 2));
        productAdapter = new ProductAdapter(this, productList);
        listOfProducts.setAdapter(productAdapter);

        // Fetch products and set variables
        fetchProducts();
        setVariables();
    }

}