package com.example.prm392assignment.ui;

import android.Manifest;
import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.prm392assignment.R;
import com.example.prm392assignment.helper.ManagmentCart;
import com.example.prm392assignment.model.GetDataResponse;
import com.example.prm392assignment.model.Product;
import com.example.prm392assignment.model.ProductResponse;
import com.example.prm392assignment.network.ApiService;
import com.example.prm392assignment.network.RetrofitClient;
import com.example.prm392assignment.ui.adapter.BestProductAdapter;
import com.example.prm392assignment.ui.adapter.CategoryAdapter;
import com.example.prm392assignment.utility.SharedPrefManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
    private static final String CHANNEL_ID = "activity_channel";
    private static final int LOCATION_REFRESH_TIME = 15000; // 15 seconds to update
    private static final int LOCATION_REFRESH_DISTANCE = 500; // 500 meters to update
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private ManagmentCart managmentCart;
    private LocationManager mLocationManager;
    private RecyclerView bestProductView;
    private BestProductAdapter bestProductAdapter;
    private ProgressBar progressBarBestProduct;
    private ImageView logoutButton;
    private AlertDialog.Builder builder;
    private TextView welcomeName;
    private TextView locationText;
    private List<Product> bestProductList = new ArrayList<>();

    private List<String> categoryList = new ArrayList<>();

    private RecyclerView categoryView;

    private CategoryAdapter categoryAdapter;
    private ProgressBar progressBarCategory;
    private TextView viewListText;
    private ImageView cartBtn;

    private final LocationListener mLocationListener = new LocationListener() {
        @Override
        public void onLocationChanged(final Location location) {
            if (location != null) {
                double latitude = location.getLatitude();
                double longitude = location.getLongitude();
                Geocoder geocoder = new Geocoder(MainActivity.this);
                try {
                    List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
                    if (addresses != null && !addresses.isEmpty()) {
                        Address address = addresses.get(0);
                        String addressText = address.getLocality(); // Get town/city name
                        if (addressText == null) {
                            addressText = address.getSubAdminArea(); // Get sub-admin area if locality is null
                        }
                        locationText.setText(addressText);
                    } else {
                        locationText.setText("Lat: " + latitude + ", Lon: " + longitude);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    locationText.setText("Lat: " + latitude + ", Lon: " + longitude);
                }
            }
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
        }

        @Override
        public void onProviderEnabled(@NonNull String provider) {
        }

        @Override
        public void onProviderDisabled(@NonNull String provider) {
        }
    };

    private void initCategory() {
        categoryList.add("laptop");
        categoryList.add("tablet");
        categoryList.add("phone");
        categoryList.add("monitor");
        categoryList.add("watch");
        categoryAdapter.notifyDataSetChanged();
        progressBarCategory.setVisibility(View.GONE);
    }

    private void fetchProducts() {
        progressBarBestProduct.setVisibility(View.VISIBLE);

        ApiService apiService = RetrofitClient.getInstance().getApiService();
        Call<ProductResponse> call = apiService.getProducts();
        call.enqueue(new Callback<ProductResponse>() {
            @Override
            public void onResponse(Call<ProductResponse> call, Response<ProductResponse> response) {
                progressBarBestProduct.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null) {
                    bestProductList.addAll(response.body().getProducts());
                    bestProductAdapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(MainActivity.this, "Failed to fetch products", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ProductResponse> call, Throwable t) {
                progressBarBestProduct.setVisibility(View.GONE);
                Toast.makeText(MainActivity.this, "An error occurred", Toast.LENGTH_SHORT).show();
            }
        });
    }

    //create notification channel
    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Activity Channel";
            String description = "Channel for activity creation notifications";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    //send notification function
    private void sendNotification() {
        Intent intent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.baseline_shopping_cart_24) // your notification icon
                .setContentTitle("Thông báo!")
                .setContentText("Giỏ hàng đang có: " + managmentCart.getListCart().size() + " sản phẩm")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
           Toast.makeText(this, "Hãy bật cho phép thông báo!",Toast.LENGTH_SHORT).show();
            return;
        }
        notificationManager.notify(0, builder.build());
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Remove title bar
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getSupportActionBar().hide();

        // Make activity full screen
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);
        welcomeName = findViewById(R.id.welcomeName);
        locationText = findViewById(R.id.locationTextView);
        logoutButton = findViewById(R.id.logoutBtn);
        bestProductView = findViewById(R.id.bestProductView);
        progressBarBestProduct = findViewById(R.id.progressBarBestProduct);
        categoryView = findViewById(R.id.CategoryView); // Initialize categoryView
        progressBarCategory = findViewById(R.id.progressBarCategory);
        viewListText = findViewById(R.id.viewListText);
        cartBtn = findViewById(R.id.cartBtn);
        //recyclerView for bestProduct
        bestProductView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        bestProductAdapter = new BestProductAdapter(this, bestProductList);
        bestProductView.setAdapter(bestProductAdapter);

        //recyclerView for category
        categoryView.setLayoutManager(new GridLayoutManager(MainActivity.this, 4));
        categoryAdapter = new CategoryAdapter(this, categoryList);
        categoryView.setAdapter(categoryAdapter);
        managmentCart = new ManagmentCart(this);

        createNotificationChannel();
        sendNotification();
//        Toast.makeText(this,managmentCart.getListCart().size()+"cc",Toast.LENGTH_SHORT ).show();
        initCategory();
        // Fetch products from the API
        fetchProducts();

        //alert
        builder = new AlertDialog.Builder(this);

        // Initialize LocationManager
        mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        // Check for location permissions
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
            return;
        }

        // Request location updates
        mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, LOCATION_REFRESH_TIME, LOCATION_REFRESH_DISTANCE, mLocationListener);

        // Get user data from API
        ApiService apiService = RetrofitClient.getInstance().getApiService();
        Call<GetDataResponse> call = apiService.getDataResponse(SharedPrefManager.getInstance(this).getAuthToken());
        call.enqueue(new Callback<GetDataResponse>() {
            @Override
            public void onResponse(Call<GetDataResponse> call, Response<GetDataResponse> response) {
                if (response.isSuccessful()) {
                    GetDataResponse getDataResponse = response.body();
                    if (getDataResponse != null) {
                        boolean isEmailSaved = SharedPrefManager.getInstance(getApplicationContext()).saveEmail(getDataResponse.getEmail());
                        if(isEmailSaved){
                            Log.d("Email saved", "OK");
                        }

                        welcomeName.setText(getDataResponse.getFirstName() + " " + getDataResponse.getLastName());
                    }
                } else {
                    Toast.makeText(MainActivity.this, "Can't get data", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<GetDataResponse> call, Throwable t) {
                Log.e("MainActivity", "Network error", t);
                Toast.makeText(MainActivity.this, "An error occurred", Toast.LENGTH_SHORT).show();
            }
        });


        // Set the status bar color
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            getWindow().getAttributes().layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES;
        }
        //on clicking view show all products
        viewListText.setOnClickListener(v -> {
            Intent intent = new Intent(this, ProductListActivity.class);
            startActivity(intent);
        });
        //cart screen
        cartBtn.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, CartActivity.class)));
        // Logout button functionality
        logoutButton.setOnClickListener(v -> builder.setTitle("Logout?")
                .setMessage("Bạn có muốn đăng xuất?")
                .setCancelable(true)
                .setPositiveButton("Có", (dialog, which) -> {
                    boolean isDataCleared = SharedPrefManager.getInstance(getApplicationContext()).clearData();
                    if (isDataCleared) {
                        Toast.makeText(getApplicationContext(), "Logout successful", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(getApplicationContext(), Login.class);
                        startActivity(intent);
                        finish();
                    }
                })
                .setNegativeButton("Không", (dialog, which) -> dialog.cancel())
                .show());

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigation);
        bottomNavigationView.setSelectedItemId(R.id.bottom_home);
        bottomNavigationView.setOnApplyWindowInsetsListener(null);
        bottomNavigationView.setPadding(0, 0, 0, 0);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.bottom_home) {
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
                startActivity(new Intent(getApplicationContext(), ProfileActivity.class));
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                finish();
                return true;
            }
            return false;
        });

        Objects.requireNonNull(getSupportActionBar()).hide();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (SharedPrefManager.getInstance(getApplicationContext()).getAuthToken() == null ||
                SharedPrefManager.getInstance(getApplicationContext()).getUserId() == null) {
            Toast.makeText(this, "Wtf", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(getApplicationContext(), Login.class);
            startActivity(intent);
            finish();
        }
    }

    // Move hideSystemUI method inside MainActivity and remove static keyword


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, request location updates
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                        ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, LOCATION_REFRESH_TIME, LOCATION_REFRESH_DISTANCE, mLocationListener);
                }
            } else {
                // Permission denied
                Toast.makeText(this, "Location permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
