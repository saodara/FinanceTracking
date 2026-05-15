package com.dara.app;

public class Transaction {
    private int id;
    private String category;
    private String date;
    private double amount;
    private String type;

    // Required for Firestore toObject()
    public Transaction() {}

    public Transaction(String category, String date, double amount, String type) {
        this.category = category;
        this.date = date;
        this.amount = amount;
        this.type = type;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
