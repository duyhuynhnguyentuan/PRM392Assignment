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
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.Currency;
import java.util.List;
import java.util.Locale;

public class ProductAdapter extends RecyclerView.Adapter<ProductViewHolder> {
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
        holder.titleTextView.setText(product.getName());
        holder.priceTextView.setText(numberFormat.format(product.getPrice()));
        holder.categoryTextView.setText(product.getCategory().getName());
        Picasso.get().load(product.getProductImage()).into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }
}
