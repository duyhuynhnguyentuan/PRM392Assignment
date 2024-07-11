package com.example.prm392assignment.model;

import android.os.Build;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Date;
import java.util.Locale;
public class Product implements Serializable {
    private int quantity;
    private String name;
    private double price;
    private String description;
    private String productImage;
    private Date createdAt;
    private Category category;

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getProductImage() {
        return productImage;
    }

    public void setProductImage(String productImage) {
        this.productImage = productImage;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public void setCreatedAt(String dateString) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Use DateTimeFormatter for API level 26 and above
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSX")
                    .withZone(ZoneId.of("UTC"));
            try {
                Instant instant = Instant.from(formatter.parse(dateString));
                this.createdAt = Date.from(instant);
            } catch (DateTimeParseException e) {
                e.printStackTrace();
                // Handle error
            }
        } else {
            // Use SimpleDateFormat for older API levels
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault());
            try {
                this.createdAt = format.parse(dateString);
            } catch (ParseException e) {
                e.printStackTrace();
                // Handle error
            }
        }
    }

    public Date getCreatedAt() {
        return createdAt;
    }
}

