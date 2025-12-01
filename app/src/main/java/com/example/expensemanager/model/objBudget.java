package com.example.expensemanager.model;

public class objBudget {
    private int id;
    private int idUser;
    private String category;
    private double budgetAmount;
    private String createdDate;
    private String startDate;
    private String endDate;

    public objBudget() {
    }

    public objBudget(int id, int idUser, String category, double budgetAmount, String createdDate, String startDate, String endDate) {
        this.id = id;
        this.idUser = idUser;
        this.category = category;
        this.budgetAmount = budgetAmount;
        this.createdDate = createdDate;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIdUser() {
        return idUser;
    }

    public void setIdUser(int idUser) {
        this.idUser = idUser;
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

    public String getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(String createdDate) {
        this.createdDate = createdDate;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }
}
