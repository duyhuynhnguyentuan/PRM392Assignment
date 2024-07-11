package com.example.prm392assignment.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.prm392assignment.R;
import com.example.prm392assignment.ui.viewholder.BestProductViewHolder;
import com.example.prm392assignment.ui.viewholder.CategoryViewHolder;

import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryViewHolder> {
    private Context context;
    private List<String> categoryList;

    public CategoryAdapter(Context context, List<String> categoryList) {
        this.context = context;
        this.categoryList = categoryList;
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.viewholder_category, parent, false);
        return new CategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        holder.nameTextView.setText(categoryList.get(position));
        switch (position){
            case 0: {
                holder.imageView.setBackgroundResource(R.drawable.cat_1_background);
                holder.imageView.setImageResource(R.drawable.baseline_computer_24);
                break;
            }
            case 1:{
                holder.imageView.setBackgroundResource(R.drawable.cat_2_background);
                holder.imageView.setImageResource(R.drawable.baseline_tablet_mac_24);
                break;
            }
            case 2: {
                holder.imageView.setBackgroundResource(R.drawable.cat_3_background);
                holder.imageView.setImageResource(R.drawable.baseline_phone_iphone_24);
                break;
            }
            case 3:{
                holder.imageView.setBackgroundResource(R.drawable.cat_4_background);
                holder.imageView.setImageResource(R.drawable.baseline_monitor_24);
                break;
            }
            case 4:{
                holder.imageView.setBackgroundResource(R.drawable.cat_5_background);
                holder.imageView.setImageResource(R.drawable.baseline_watch_24);
                break;
            }
        }

    }

    @Override
    public int getItemCount() {
        return categoryList.size();
    }
}
