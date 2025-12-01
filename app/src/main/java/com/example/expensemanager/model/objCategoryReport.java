package com.example.expensemanager.model;

/**
 * Model để chứa dữ liệu báo cáo theo danh mục
 */
public class objCategoryReport {
    private String category;
    private double income;      // Thu nhập
    private double expense;     // Chi tiêu
    private double total;       // Total = Income - Expense

    public objCategoryReport() {
    }

    public objCategoryReport(String category, double income, double expense) {
        this.category = category;
        this.income = income;
        this.expense = expense;
        this.total = income - expense;
    }

    // Getters and Setters
    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public double getIncome() {
        return income;
    }

    public void setIncome(double income) {
        this.income = income;
    }

    public double getExpense() {
        return expense;
    }

    public void setExpense(double expense) {
        this.expense = expense;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public void calculateTotal() {
        this.total = this.income - this.expense;
    }
}

