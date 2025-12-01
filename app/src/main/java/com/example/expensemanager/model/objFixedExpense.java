package com.example.expensemanager.model;

public class objFixedExpense {
    private int id;
    private int idUser;
    private double amount;
    private String category;
    private String note;
    private int dayOfMonth; // FIX: Added day of month
    private int lastAddedMonth;
    private int lastAddedYear;

    public objFixedExpense() {
    }

    // Getters and Setters
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

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    // FIX: Added getter and setter for dayOfMonth
    public int getDayOfMonth() {
        return dayOfMonth;
    }

    public void setDayOfMonth(int dayOfMonth) {
        this.dayOfMonth = dayOfMonth;
    }

    public int getLastAddedMonth() {
        return lastAddedMonth;
    }

    public void setLastAddedMonth(int lastAddedMonth) {
        this.lastAddedMonth = lastAddedMonth;
    }

    public int getLastAddedYear() {
        return lastAddedYear;
    }

    public void setLastAddedYear(int lastAddedYear) {
        this.lastAddedYear = lastAddedYear;
    }
}
