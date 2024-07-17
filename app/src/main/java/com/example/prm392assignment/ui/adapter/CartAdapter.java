package com.example.prm392assignment.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.prm392assignment.R;
import com.example.prm392assignment.helper.ChangeNumberItemsListener;
import com.example.prm392assignment.helper.ManagmentCart;
import com.example.prm392assignment.model.Product;
import com.squareup.picasso.Picasso;

import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Currency;
import java.util.Locale;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.ViewHolder> {
    //truncate to make title shorter
    private String truncateTitle(String title, int maxLength) {
        if (title.length() > maxLength) {
            return title.substring(0, maxLength) + "...";
        } else {
            return title;
        }
    }
    // Format Vietnam dong currency
    Locale locale = new Locale("vi", "VN");
    Currency currency = Currency.getInstance("VND");
    DecimalFormatSymbols df = DecimalFormatSymbols.getInstance(locale);
    NumberFormat numberFormat = NumberFormat.getCurrencyInstance(locale);

    {
        df.setCurrency(currency);
        numberFormat.setCurrency(currency);
    }

    private ArrayList<Product> list;
    private ManagmentCart managmentCart;
    private ChangeNumberItemsListener changeNumberItemsListener;

    public CartAdapter(ArrayList<Product> list, Context context, ChangeNumberItemsListener changeNumberItemsListener) {
        this.list = list;
        this.managmentCart = new ManagmentCart(context);
        this.changeNumberItemsListener = changeNumberItemsListener;
    }

    @NonNull
    @Override
    public CartAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.viewholder_cart, parent, false);
        return new ViewHolder(inflate);
    }

    @Override
    public void onBindViewHolder(@NonNull CartAdapter.ViewHolder holder, int position) {
        Product product = list.get(position);

        // Set product name
        holder.cartItemTitle.setText(truncateTitle(product.getName(),30));
        // Set product price
        holder.priceEachItem.setText(numberFormat.format(product.getPrice()));
        // Set product quantity
        int quantity = managmentCart.getProductQuantity(product.getName());
        holder.quantityNumber.setText(String.valueOf(quantity));
        // Set total price for the item
        holder.totalPriceEachItem.setText(numberFormat.format(product.getPrice() * quantity));
        // Load product image using Picasso
        Picasso.get().load(product.getProductImage()).into(holder.cardImage);
        // Set click listeners for plus and minus buttons
        //if it gets the position error just make it a lambada fund
        holder.plusButton.setOnClickListener(v -> managmentCart.plusNumberItem(list, position, () -> {
            notifyDataSetChanged();
            changeNumberItemsListener.change();
        }));
        holder.minusButton.setOnClickListener(v -> managmentCart.minusNumberItem(list, position, () -> {
            notifyDataSetChanged();
            changeNumberItemsListener.change();
        }));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView cartItemTitle, priceEachItem, totalPriceEachItem, quantityNumber, plusButton, minusButton;
        ImageView cardImage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            cartItemTitle = itemView.findViewById(R.id.cartItemTitle);
            priceEachItem = itemView.findViewById(R.id.priceEachItem);
            totalPriceEachItem = itemView.findViewById(R.id.totalPriceEachItem);
            quantityNumber = itemView.findViewById(R.id.quantityNumber);
            plusButton = itemView.findViewById(R.id.plusButton);
            minusButton = itemView.findViewById(R.id.minusButton);
            cardImage = itemView.findViewById(R.id.cardImage);
        }
    }
}
