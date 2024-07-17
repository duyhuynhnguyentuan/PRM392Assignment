package com.example.prm392assignment.helper;

import android.content.Context;
import android.widget.Toast;

import com.example.prm392assignment.model.Product;
import com.example.prm392assignment.utility.SharedPrefManager;

import java.util.ArrayList;
import java.util.List;

public class ManagmentCart {
    private Context context;
    private SharedPrefManager sharedPrefManager;

    public ManagmentCart(Context context) {
        this.context = context;
        this.sharedPrefManager = SharedPrefManager.getInstance(context);

    }

    public void insertProduct(Product product, int numberInCart) {
        List<Product> listpop = getListCart();
        if (listpop == null) {
            listpop = new ArrayList<>();
        }
        boolean existAlready = false;
        int n = 0;
        for (int i = 0; i < listpop.size(); i++) {
            if (listpop.get(i).getName().equals(product.getName())) {
                existAlready = true;
                n = i;
                break;
            }
        }
        if (existAlready) {
            // Update the quantity in SharedPrefManager
            sharedPrefManager.saveProductQuantity(product.getName(), numberInCart);
        } else {
            // Save the product and its quantity in SharedPrefManager
            listpop.add(product);
            sharedPrefManager.saveProductQuantity(product.getName(), numberInCart);
        }
        // Save the updated product list
        sharedPrefManager.saveProductList("CartList", listpop);
        Toast.makeText(context, "Added to your Cart", Toast.LENGTH_SHORT).show();
    }

    public ArrayList<Product> getListCart() {
        ArrayList<Product> productList = sharedPrefManager.getProductList("CartList");
        if (productList == null) {
            productList = new ArrayList<>();
        }
        return productList;
    }

    public int getProductQuantity(String productName) {
        return sharedPrefManager.getProductQuantity(productName);
    }

    public Double getTotalFee() {
        ArrayList<Product> listItem = getListCart();
        double fee = 0;
        for (Product product : listItem) {
            int quantity = getProductQuantity(product.getName());
            fee += (product.getPrice() * quantity);
        }
        return fee;
    }

    public void minusNumberItem(ArrayList<Product> listItem, int position, ChangeNumberItemsListener changeNumberItemsListener) {
        Product product = listItem.get(position);
        int quantity = getProductQuantity(product.getName());
        if (quantity == 1) {
            listItem.remove(position);
            sharedPrefManager.removeProductQuantity(product.getName());
        } else {
            sharedPrefManager.saveProductQuantity(product.getName(), quantity - 1);
        }
        sharedPrefManager.saveProductList("CartList", listItem);
        changeNumberItemsListener.change();
    }

    public void plusNumberItem(ArrayList<Product> listItem, int position, ChangeNumberItemsListener changeNumberItemsListener) {
        Product product = listItem.get(position);
        int quantity = getProductQuantity(product.getName());
        sharedPrefManager.saveProductQuantity(product.getName(), quantity + 1);
        sharedPrefManager.saveProductList("CartList", listItem);
        changeNumberItemsListener.change();
    }
}
