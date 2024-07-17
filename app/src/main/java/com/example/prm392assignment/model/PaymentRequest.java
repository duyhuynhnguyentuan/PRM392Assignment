package com.example.prm392assignment.model;

import java.math.BigInteger;

public class PaymentRequest {
    private String email;
    private Long amount;

    public PaymentRequest(String email, Long amount) {
        this.email = email;
        this.amount = amount;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Long getAmount() {
        return amount;
    }

    public void setAmount(Long amount) {
        this.amount = amount;
    }
}
