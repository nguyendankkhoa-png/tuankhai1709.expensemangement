package com.example.expensemanager.model;

public class objExpense {
    private int id;
    private int idUser;
    private double amount;
    private String date;
    private String note;
    private String category; //Food, Transport, etc.
    private String type; // income or expense

    public objExpense() {
    }

    public objExpense(int id, int idUser, double amount, String date, String note, String category, String type) {
        this.id = id;
        this.idUser = idUser;
        this.amount = amount;
        this.date = date;
        this.note = note;
        this.category = category;
        this.type = type;
    }

    public int getId() { return id; }

    public void setId(int id) {
        this.id = id;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public int getIdUser() {
        return idUser;
    }

    public void setIdUser(int idUser) {
        this.idUser = idUser;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}

