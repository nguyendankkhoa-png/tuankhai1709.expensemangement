package com.example.expensemanager.model;

public class objBudgetAlert {
    private int budgetId;
    private String category;
    private double budgetAmount;
    private double spentAmount;
    private int percentage;

    public objBudgetAlert() {
    }

    public objBudgetAlert(int budgetId, String category, double budgetAmount, double spentAmount, int percentage) {
        this.budgetId = budgetId;
        this.category = category;
        this.budgetAmount = budgetAmount;
        this.spentAmount = spentAmount;
        this.percentage = percentage;
    }

    public int getBudgetId() {
        return budgetId;
    }

    public void setBudgetId(int budgetId) {
        this.budgetId = budgetId;
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

    public void setBudgetAmount(double budgetAmount) {
        this.budgetAmount = budgetAmount;
    }

    public double getSpentAmount() {
        return spentAmount;
    }

    public void setSpentAmount(double spentAmount) {
        this.spentAmount = spentAmount;
    }

    public int getPercentage() {
        return percentage;
    }

    public void setPercentage(int percentage) {
        this.percentage = percentage;
    }
}

