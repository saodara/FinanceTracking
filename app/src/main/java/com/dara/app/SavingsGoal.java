package com.dara.app;

public class SavingsGoal {
    private int id;
    private String name;
    private double targetAmount;
    private double savedAmount;

    // Required for Firestore toObject()
    public SavingsGoal() {}

    public SavingsGoal(String name, double targetAmount, double savedAmount) {
        this.name = name;
        this.targetAmount = targetAmount;
        this.savedAmount = savedAmount;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public String getGoalName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getTargetAmount() {
        return targetAmount;
    }

    public void setTargetAmount(double targetAmount) {
        this.targetAmount = targetAmount;
    }

    public double getSavedAmount() {
        return savedAmount;
    }

    public void setSavedAmount(double savedAmount) {
        this.savedAmount = savedAmount;
    }
}
