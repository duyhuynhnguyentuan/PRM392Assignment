package com.example.prm392assignment.network;

import com.example.prm392assignment.model.Product;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface ApiService {

    @GET("products")
    Call<List<Product>> getProducts();

}
