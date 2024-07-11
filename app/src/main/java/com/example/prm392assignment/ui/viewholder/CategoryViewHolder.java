package com.example.prm392assignment.ui.viewholder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.prm392assignment.R;
import com.example.prm392assignment.ui.adapter.CategoryAdapter;

public class CategoryViewHolder extends RecyclerView.ViewHolder
{
    public ImageView imageView;
    public TextView nameTextView;
    public CategoryViewHolder(View itemView){
        super(itemView);
        imageView = itemView.findViewById(R.id.categoryImage);
        nameTextView = itemView.findViewById(R.id.categoryNameText);
    }
}
