package com.dara.app;

public class Budget {
    private int id;
    private String category;
    private double budgetAmount;
    private double spentAmount;
    private String currency;

    // Required for Firestore toObject()
    public Budget() {}

    public Budget(String category, double budgetAmount, double spentAmount, String currency) {
        this.category = category;
        this.budgetAmount = budgetAmount;
        this.spentAmount = spentAmount;
        this.currency = currency != null ? currency : "USD";
    }

    public Budget(String category, double budgetAmount, double spentAmount) {
        this(category, budgetAmount, spentAmount, "USD");
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

    public double getBudgetAmount() {
        return budgetAmount;
    }

    public double getSpentAmount() {
        return spentAmount;
    }

    public void setBudgetAmount(double budgetAmount) {
        this.budgetAmount = budgetAmount;
    }

    public void setSpentAmount(double spentAmount) {
        this.spentAmount = spentAmount;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }
}
