package com.telemed.dto;

public class PaymentRequest {
    private String email;
    private int amount; // in kobo (1000 = ₦10.00)

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public int getAmount() { return amount; }
    public void setAmount(int amount) { this.amount = amount; }
}
