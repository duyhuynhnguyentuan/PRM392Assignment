package com.example.prm392assignment.ui.viewholder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.prm392assignment.R;

public class BestProductViewHolder extends RecyclerView.ViewHolder {
    public ImageView imageView;
    public TextView titleTextView;
    public TextView priceTextView;
    public TextView categoryTextView;
    public BestProductViewHolder(View itemView){
        super(itemView);
        imageView = itemView.findViewById(R.id.bestProductImage);
        titleTextView = itemView.findViewById(R.id.bestProductName);
        priceTextView = itemView.findViewById(R.id.bestProductPrice);
        categoryTextView = itemView.findViewById(R.id.bestProductCategory);

    }
}
