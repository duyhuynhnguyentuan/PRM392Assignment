package com.example.prm392assignment.network;

import com.example.prm392assignment.model.GetDataResponse;
import com.example.prm392assignment.model.LoginRequest;
import com.example.prm392assignment.model.LoginResponse;
import com.example.prm392assignment.model.PaymentRequest;
import com.example.prm392assignment.model.Product;
import com.example.prm392assignment.model.ProductResponse;
import com.example.prm392assignment.model.User;
import com.example.prm392assignment.model.UserResponse;
import com.google.gson.JsonObject;

import org.json.JSONObject;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface ApiService {
    @POST("/user")
    Call<UserResponse> createUser(@Body User user);

    @POST("/user/login")
    Call<LoginResponse> loginUser(@Body LoginRequest loginRequest);

    @GET("/user/data")
    Call<GetDataResponse> getDataResponse(@Header("auth-token") String authToken);

    @GET("/product/show")
    Call<ProductResponse> getProducts();

    @POST("/payment")
    Call<JsonObject> fetchPayment(@Body PaymentRequest paymentRequest);

}
