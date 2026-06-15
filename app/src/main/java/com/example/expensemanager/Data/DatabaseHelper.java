package com.example.expensemanager.Data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "ExpenseManager.db";
    // FIX: Incremented version to 10 for the new pin column
    private static final int DATABASE_VERSION = 10;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create all tables from scratch
        String CREATE_TABLE_user = "CREATE TABLE tbl_user(id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "name TEXT, " +
                "age INTEGER, " +
                "phone int, " +
                "email TEXT, password TEXT, pin TEXT)"; // Add pin column
        db.execSQL(CREATE_TABLE_user);

        String CREATE_TABLE_expense = "CREATE TABLE tbl_transaction(id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "idUser INTEGER, " +
                "amount REAL, " +
                "date TEXT, " +
                "note TEXT, " +
                "category TEXT, type TEXT)";
        db.execSQL(CREATE_TABLE_expense);

        String CREATE_TABLE_budget = "CREATE TABLE tbl_budget(id INTEGER PRIMARY KEY AUTOINCREMENT, idUser INTEGER, category TEXT, budgetAmount REAL, createdDate TEXT, startDate TEXT, endDate TEXT)";
        db.execSQL(CREATE_TABLE_budget);

        String CREATE_TABLE_fixed_expense = "CREATE TABLE tbl_fixed_expense(id INTEGER PRIMARY KEY AUTOINCREMENT, idUser INTEGER, amount REAL, category TEXT, note TEXT, dayOfMonth INTEGER, lastAddedMonth INTEGER, lastAddedYear INTEGER)";
        db.execSQL(CREATE_TABLE_fixed_expense);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 10) {
            try {
                db.execSQL("ALTER TABLE tbl_user ADD COLUMN pin TEXT");
                db.execSQL("DROP TABLE IF EXISTS tbl_fixed_expense");
                String CREATE_TABLE_fixed_expense = "CREATE TABLE tbl_fixed_expense(id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "idUser INTEGER, amount REAL, category TEXT," +
                        " note TEXT, dayOfMonth INTEGER," +
                        " lastAddedMonth INTEGER, lastAddedYear INTEGER)";
                db.execSQL(CREATE_TABLE_fixed_expense);
            } catch (Exception e) {
                // If the column already exists, do nothing
            }
        }
    }
}
