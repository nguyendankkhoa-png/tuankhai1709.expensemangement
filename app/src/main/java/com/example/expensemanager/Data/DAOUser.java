package com.example.expensemanager.Data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.expensemanager.model.objUser;

public class DAOUser {
    DatabaseHelper dbHelper;

    public DAOUser(Context context) {
        dbHelper = new DatabaseHelper(context);
    }

    // This method seems to be missing from your provided file, but your LoginActivity uses it.
    // I'm adding a basic implementation. You may need to adjust it.

    public void insertUser(objUser obj) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("name", obj.getName());
        values.put("age", obj.getAge());
        values.put("phone", obj.getPhone());
        values.put("email", obj.getEmail());
        values.put("password", obj.getPassword());
        db.insert("tbl_user", null, values);
        db.close();
    }

    public void updateUser(objUser obj) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("name", obj.getName());
        values.put("age", obj.getAge());
        values.put("phone", obj.getPhone());
        values.put("email", obj.getEmail());

        db.update("tbl_user", values, "id = ?",
                new String[]{String.valueOf(obj.getId())});
    }

    public void updatePassword(objUser obj) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("password", obj.getPassword());

        db.update("tbl_user", values, "id = ?",
                new String[]{String.valueOf(obj.getId())});
    }

    public void deleteUser(int id) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete("tbl_user", "id = ?",
                new String[]{String.valueOf(id)});
        db.close();
    }


    public objUser getUserByID(int id) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        objUser obj = null;
        String selectQuery = "SELECT * FROM tbl_user WHERE id = ?";
        Cursor cursor = db.rawQuery(selectQuery, new String[]{String.valueOf(id)});
        if (cursor.moveToFirst()) {
            obj = new objUser();
            obj.setId(cursor.getInt(cursor.getColumnIndexOrThrow("id")));
            obj.setName(cursor.getString(cursor.getColumnIndexOrThrow("name")));
            obj.setAge(cursor.getInt(cursor.getColumnIndexOrThrow("age")));
            obj.setPhone(cursor.getInt(cursor.getColumnIndexOrThrow("phone")));
            obj.setEmail(cursor.getString(cursor.getColumnIndexOrThrow("email")));
        }
        cursor.close();
        db.close();
        return obj;
    }

    // Corrected method to accept an email String
    public objUser getUserByEmail(String email, String password) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        objUser obj = null;
        String selectQuery = "SELECT * FROM tbl_user WHERE email = ? AND password = ?";
        Cursor cursor = db.rawQuery(selectQuery, new String[]{email, password});
        if (cursor.moveToFirst()) {
            obj = new objUser();
            obj.setId(cursor.getInt(cursor.getColumnIndexOrThrow("id")));
            obj.setName(cursor.getString(cursor.getColumnIndexOrThrow("name")));
            obj.setAge(cursor.getInt(cursor.getColumnIndexOrThrow("age")));
            obj.setPhone(cursor.getInt(cursor.getColumnIndexOrThrow("phone")));
            obj.setEmail(cursor.getString(cursor.getColumnIndexOrThrow("email")));
        }
        cursor.close();
        db.close();
        return obj;
    }

    public boolean checkLogin(String email, String password) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String selectQuery = "SELECT * FROM tbl_user WHERE email = ? AND password = ?";
        Cursor cursor = db.rawQuery(selectQuery, new String[]{email, password});
        boolean result = cursor.moveToFirst();
        cursor.close();
        db.close();
        return result;
    }

    // New method for forgot password - get user by email only
    public objUser getUserByEmail(String email) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        objUser obj = null;
        String selectQuery = "SELECT * FROM tbl_user WHERE email = ?";
        Cursor cursor = db.rawQuery(selectQuery, new String[]{email});
        if (cursor.moveToFirst()) {
            obj = new objUser();
            obj.setId(cursor.getInt(cursor.getColumnIndexOrThrow("id")));
            obj.setName(cursor.getString(cursor.getColumnIndexOrThrow("name")));
            obj.setAge(cursor.getInt(cursor.getColumnIndexOrThrow("age")));
            obj.setPhone(cursor.getInt(cursor.getColumnIndexOrThrow("phone")));
            obj.setEmail(cursor.getString(cursor.getColumnIndexOrThrow("email")));
            obj.setPassword(cursor.getString(cursor.getColumnIndexOrThrow("password")));
        }
        cursor.close();
        db.close();
        return obj;
    }

    // Update password by email
    public boolean updatePasswordByEmail(String email, String newPassword) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("password", newPassword);

        int result = db.update("tbl_user", values, "email = ?", new String[]{email});
        db.close();
        return result > 0;
    }
}
