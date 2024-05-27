package com.example.prm392assignment.ui;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.prm392assignment.R;
import com.example.prm392assignment.model.Product;
import com.squareup.picasso.Picasso;
import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductViewHolder> {
    private Context context;
    private List<Product> productList;

    public ProductAdapter(Context context, List<Product> productList) {
        this.context = context;
        this.productList = productList;
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_product, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Product product = productList.get(position);
        holder.titleTextView.setText(product.getTitle());
        holder.priceTextView.setText("$" + product.getPrice());
        holder.ratingTextView.setText(""+ product.getRating().getRate());
        holder.ratingCountTextView.setText("(" + product.getRating().getCount() + ")");
        Picasso.get().load(product.getImage()).into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }
}

