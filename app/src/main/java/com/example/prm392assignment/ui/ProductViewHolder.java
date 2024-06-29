package com.example.prm392assignment.ui;


import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.example.prm392assignment.R;

public class ProductViewHolder extends RecyclerView.ViewHolder {
    public ImageView imageView;
    public TextView titleTextView;
    public TextView priceTextView;
    public TextView categoryTextView;

    public ProductViewHolder(View itemView) {
        super(itemView);
        imageView = itemView.findViewById(R.id.product_image);
        titleTextView = itemView.findViewById(R.id.product_title);
        priceTextView = itemView.findViewById(R.id.product_price);
       categoryTextView = itemView.findViewById(R.id.product_category);
    }
}

