package com.example.prm392assignment.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.prm392assignment.R;
import com.example.prm392assignment.model.Product;
import com.squareup.picasso.Picasso;

import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.Currency;
import java.util.Locale;

public class DetailActivity extends AppCompatActivity {
    // Format Vietnam dong currency
    Locale locale = new Locale("vi", "VN");
    Currency currency = Currency.getInstance("VND");
    DecimalFormatSymbols df = DecimalFormatSymbols.getInstance(locale);
    NumberFormat numberFormat = NumberFormat.getCurrencyInstance(locale);

    {
        df.setCurrency(currency);
        numberFormat.setCurrency(currency);
    }

    private ImageView backBtn;
    private ImageView imageView;
    private TextView detailName;
    private TextView detailPrice;
    private TextView detailDescription;
    private TextView detailTotalPrice;
    private Product product;
    private TextView quantityNumber;
    private int num = 1;

    // Get intent extra
    private void getIntentExtra() {
        Intent intent = getIntent();
        if (intent != null) {
            product = (Product) intent.getSerializableExtra("PRODUCT");
        }
    }

    // Set variables
    private void setVariables() {
        if (product != null) {
            Picasso.get().load(product.getProductImage()).into(imageView);
            detailName.setText(product.getName());
            detailPrice.setText(numberFormat.format(product.getPrice()));
            detailDescription.setText(product.getDescription());
            detailTotalPrice.setText(numberFormat.format(num * product.getPrice()));
            quantityNumber.setText(String.valueOf(num));  // Convert int to String
        }
        backBtn.setOnClickListener(v -> finish());
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

        setContentView(R.layout.activity_detail);

        // Initialize views
        backBtn = findViewById(R.id.backBtn);
        imageView = findViewById(R.id.detailImageView);
        detailName = findViewById(R.id.detailName);
        detailPrice = findViewById(R.id.detailPrice);
        detailDescription = findViewById(R.id.detailDescription);
        detailTotalPrice = findViewById(R.id.detailTotalPrice);
        quantityNumber = findViewById(R.id.quantityNumber);

        // Apply window insets
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        getIntentExtra();
        setVariables();
    }
}
