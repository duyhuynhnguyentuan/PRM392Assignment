package com.example.prm392assignment.ui;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.prm392assignment.R;
import com.example.prm392assignment.helper.ManagmentCart;
import com.example.prm392assignment.model.GetDataResponse;
import com.example.prm392assignment.model.PaymentRequest;
import com.example.prm392assignment.network.ApiService;
import com.example.prm392assignment.network.RetrofitClient;
import com.example.prm392assignment.ui.adapter.CartAdapter;
import com.example.prm392assignment.utility.SharedPrefManager;
import com.google.gson.JsonObject;
import com.stripe.android.PaymentConfiguration;
import com.stripe.android.paymentsheet.PaymentSheet;
import com.stripe.android.paymentsheet.PaymentSheetResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigInteger;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.Currency;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CartActivity extends AppCompatActivity {
    Locale locale = new Locale("vi", "VN");
    Currency currency = Currency.getInstance("VND");
    DecimalFormatSymbols df = DecimalFormatSymbols.getInstance(locale);
    NumberFormat numberFormat = NumberFormat.getCurrencyInstance(locale);

    {
        df.setCurrency(currency);
        numberFormat.setCurrency(currency);
    }
    private TextView subSummaryText;
    private ManagmentCart managmentCart;
    private TextView cartTotalPrice;
    private TextView deliveryPriceText;
    private ImageView cartBackBtn;
    private TextView emptyText;
    private ScrollView scrollViewCart;
    private RecyclerView cartView;
    private RecyclerView.Adapter adapter;
    private AppCompatButton takeOrderBtn;
    PaymentSheet paymentSheet;
    String paymentIntentClientSecret;
    PaymentSheet.CustomerConfiguration configuration;
    private String email;
    private double totalPrice;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        PaymentConfiguration.init(
//                getApplicationContext(),
//                "pk_test_51Pd84HEIlVcrVU6i3IcT5FnGSETojYF66JwHDrd2r7bioRWJAJ67TqmzcqF3Q78aQOCwumbixUd7Pnyu0wV00j6S004pcFyYHm"
//        );
        // Remove title bar
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getSupportActionBar().hide();

        // Make activity full screen
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_cart);
        managmentCart = new ManagmentCart(this);
        setVariable();
        caculateCart();
        initList();
        //handle payment

        ApiService apiService = RetrofitClient.getInstance().getApiService();

        Log.d("Total Price log: ", "" + Long.valueOf((long) totalPrice));
        PaymentRequest paymentRequest = new PaymentRequest(SharedPrefManager.getInstance(getApplicationContext()).getEmail(), Long.valueOf((long) totalPrice));
        Call<JsonObject> call = apiService.fetchPayment(paymentRequest);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful() && response.body() != null) {
                    try {
                        JsonObject responseObject = response.body();
                        Log.d(TAG, "Response received: " + responseObject.toString());

                        if (responseObject.has("customer") && responseObject.has("ephemeralKey") && responseObject.has("paymentIntent") && responseObject.has("publishableKey")) {
                            configuration = new PaymentSheet.CustomerConfiguration(
                                    responseObject.get("customer").getAsString(),
                                    responseObject.get("ephemeralKey").getAsString()
                            );
                            paymentIntentClientSecret = responseObject.get("paymentIntent").getAsString();
                            PaymentConfiguration.init(getApplicationContext(), responseObject.get("publishableKey").getAsString());
                            Toast.makeText(CartActivity.this, "Payment setup successful.", Toast.LENGTH_SHORT).show();
                        } else {
                            Log.e(TAG, "Response does not contain required fields");
                            Toast.makeText(CartActivity.this, "Invalid response from the server.", Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Parsing error", e);
                        Toast.makeText(CartActivity.this, "An error occurred while processing the payment information.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Log.e(TAG, "API response error: " + response.code());
                    Toast.makeText(CartActivity.this, "An error occurred. Please try again later.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Log.e(TAG, "Network error", t);
                Toast.makeText(CartActivity.this, "An error occurred. Please check your network connection and try again.", Toast.LENGTH_SHORT).show();
            }
        });

        takeOrderBtn.setOnClickListener(v -> {
            if(paymentIntentClientSecret != null){
                paymentSheet.presentWithPaymentIntent(paymentIntentClientSecret,
                        new PaymentSheet.Configuration("PRMShopApp", configuration));
            }
        });
        paymentSheet = new PaymentSheet(this, this::onPaymentSheetResult);
    }

    private void onPaymentSheetResult(final PaymentSheetResult paymentSheetResult){
        if(paymentSheetResult instanceof PaymentSheetResult.Canceled){
            Toast.makeText(this, "Đã hủy thanh toán!", Toast.LENGTH_SHORT).show();
        }
        if(paymentSheetResult instanceof PaymentSheetResult.Failed){
            Toast.makeText(this, ((PaymentSheetResult.Failed) paymentSheetResult).getError().getMessage(), Toast.LENGTH_SHORT).show();
        }
        if(paymentSheetResult instanceof PaymentSheetResult.Completed){
            Toast.makeText(this, "Thanh toán thành công", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(CartActivity.this, successOrderActivity.class);
            startActivity(intent);
            finish();
        }
    }

    private void initList(){
        if(managmentCart.getListCart().isEmpty()){
            emptyText.setVisibility(View.VISIBLE);
            scrollViewCart.setVisibility(View.GONE);
        }else{
            emptyText.setVisibility(View.GONE);
            scrollViewCart.setVisibility(View.VISIBLE);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
            cartView.setLayoutManager(linearLayoutManager);
            adapter = new CartAdapter(managmentCart.getListCart(), this, this::caculateCart);
            cartView.setAdapter(adapter);
        }
    }
    private void caculateCart(){
        double delivery = 30000;
        double subTotal = managmentCart.getTotalFee();
        double total = managmentCart.getTotalFee() + delivery;
        totalPrice = total;
        subSummaryText.setText(numberFormat.format(subTotal));
        cartTotalPrice.setText(numberFormat.format(total));
        deliveryPriceText.setText(numberFormat.format(delivery));
    }
    private void setVariable() {
        subSummaryText = findViewById(R.id.subSummaryText);
        cartTotalPrice = findViewById(R.id.cartTotalPrice);
        deliveryPriceText = findViewById(R.id.deliveryPriceText);
        cartBackBtn = findViewById(R.id.cartBackBtn);
        scrollViewCart = findViewById(R.id.scrollViewCart);
        emptyText = findViewById(R.id.emptyText);
        cartView = findViewById(R.id.cartView);
        takeOrderBtn = findViewById(R.id.takeOrderBtn);
        cartBackBtn.setOnClickListener(v -> {
            Log.d("CartActivity", "Back button clicked");
            finish();
        });
    }

}
