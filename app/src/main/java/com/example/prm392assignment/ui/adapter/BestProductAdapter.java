package com.example.prm392assignment.ui.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.prm392assignment.R;
import com.example.prm392assignment.model.Product;
import com.example.prm392assignment.ui.DetailActivity;
import com.example.prm392assignment.ui.viewholder.BestProductViewHolder;
import com.squareup.picasso.Picasso;

import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.Currency;
import java.util.List;
import java.util.Locale;

public class BestProductAdapter extends RecyclerView.Adapter<BestProductViewHolder> {
    //truncate the title because it is long as f
    private String truncateTitle(String title, int maxLength) {
        if (title.length() > maxLength) {
            return title.substring(0, maxLength) + "...";
        } else {
            return title;
        }
    }

    Locale locale = new Locale("vi", "VN");
    Currency currency = Currency.getInstance("VND");
    DecimalFormatSymbols df = DecimalFormatSymbols.getInstance(locale);
    NumberFormat numberFormat = NumberFormat.getCurrencyInstance(locale);

    {
        df.setCurrency(currency);
        numberFormat.setCurrency(currency);
    }
    private Context context;
    private List<Product> productList;

    public BestProductAdapter(Context context, List<Product> productList) {
        this.context = context;
        this.productList = productList;
    }

    @NonNull
    @Override
    public BestProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.best_product, parent, false);
        return new BestProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BestProductViewHolder holder, int position) {
        Product product = productList.get(position);
        String truncatedTitle = truncateTitle(product.getName(), 20);
        holder.titleTextView.setText(truncatedTitle);
        holder.priceTextView.setText(numberFormat.format(product.getPrice()));
        holder.categoryTextView.setText(product.getCategory().getName());
        Picasso.get().load(product.getProductImage()).into(holder.imageView);
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, DetailActivity.class);
            intent.putExtra("PRODUCT", productList.get(position));
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }
}
