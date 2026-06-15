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

    public void insertUser(objUser obj) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("name", obj.getName());
        values.put("age", obj.getAge());
        values.put("phone", obj.getPhone());
        values.put("email", obj.getEmail());
        values.put("password", obj.getPassword());
        values.put("pin", obj.getPin());
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
            obj.setPin(cursor.getString(cursor.getColumnIndexOrThrow("pin"))); // Get pin
        }
        cursor.close();
        db.close();
        return obj;
    }

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
            obj.setPin(cursor.getString(cursor.getColumnIndexOrThrow("pin"))); // Get pin
        }
        cursor.close();
        return obj;
    }

    public void updatePassword(objUser obj) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("password", obj.getPassword());

        db.update("tbl_user", values, "id = ?",
                new String[]{String.valueOf(obj.getId())});
    }

    public boolean updatePasswordByEmail(String email, String newPassword) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("password", newPassword);

        int result = db.update("tbl_user", values, "email = ?", new String[]{email});
        db.close();
        return result > 0;
    }

    // Method use to reset password via PIN code
    public objUser getUserByEmailAndPin(String email, String pin) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        objUser obj = null;
        String selectQuery = "SELECT * FROM tbl_user WHERE email = ? AND pin = ?";
        Cursor cursor = db.rawQuery(selectQuery, new String[]{email, pin});
        if (cursor.moveToFirst()) {
            obj = new objUser();
            obj.setId(cursor.getInt(cursor.getColumnIndexOrThrow("id")));
            obj.setName(cursor.getString(cursor.getColumnIndexOrThrow("name")));
            obj.setAge(cursor.getInt(cursor.getColumnIndexOrThrow("age")));
            obj.setPhone(cursor.getInt(cursor.getColumnIndexOrThrow("phone")));
            obj.setEmail(cursor.getString(cursor.getColumnIndexOrThrow("email")));
            obj.setPassword(cursor.getString(cursor.getColumnIndexOrThrow("password")));
            obj.setPin(cursor.getString(cursor.getColumnIndexOrThrow("pin")));
        }
        cursor.close();
        return obj;
    }
}
