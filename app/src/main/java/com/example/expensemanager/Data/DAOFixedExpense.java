package com.example.expensemanager.Data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.expensemanager.model.objFixedExpense;

import java.util.ArrayList;
import java.util.List;

public class DAOFixedExpense {

    private DatabaseHelper dbHelper;

    public DAOFixedExpense(Context context) {
        this.dbHelper = new DatabaseHelper(context);
    }


    public long insertFixedExpense(objFixedExpense fixedExpense) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("idUser", fixedExpense.getIdUser());
        values.put("amount", fixedExpense.getAmount());
        values.put("category", fixedExpense.getCategory());
        values.put("note", fixedExpense.getNote());
        values.put("dayOfMonth", fixedExpense.getDayOfMonth());
        values.put("lastAddedMonth", -1);
        values.put("lastAddedYear", -1);
        long result = db.insert("tbl_fixed_expense", null, values);
        return result;
    }

    public int updateFixedExpense(objFixedExpense fixedExpense) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("amount", fixedExpense.getAmount());
        values.put("category", fixedExpense.getCategory());
        values.put("note", fixedExpense.getNote());
        values.put("dayOfMonth", fixedExpense.getDayOfMonth()); // Update day of month
        values.put("lastAddedMonth", fixedExpense.getLastAddedMonth());
        values.put("lastAddedYear", fixedExpense.getLastAddedYear());

        int result = db.update("tbl_fixed_expense", values, "id = ?",
                new String[]{String.valueOf(fixedExpense.getId())});
        return result;
    }

    public int deleteFixedExpense(int id) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int result = db.delete("tbl_fixed_expense", "id = ?",
                new String[]{String.valueOf(id)});
        return result;
    }

    public List<objFixedExpense> getAllFixedExpenses(int idUser) {
        List<objFixedExpense> list = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String query = "SELECT * FROM tbl_fixed_expense WHERE idUser = ?";
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(idUser)});

        if (cursor != null && cursor.moveToFirst()) {
            do {
                objFixedExpense fe = new objFixedExpense();
                fe.setId(cursor.getInt(cursor.getColumnIndexOrThrow("id")));
                fe.setIdUser(cursor.getInt(cursor.getColumnIndexOrThrow("idUser")));
                fe.setAmount(cursor.getDouble(cursor.getColumnIndexOrThrow("amount")));
                fe.setCategory(cursor.getString(cursor.getColumnIndexOrThrow("category")));
                fe.setNote(cursor.getString(cursor.getColumnIndexOrThrow("note")));
                fe.setDayOfMonth(cursor.getInt(cursor.getColumnIndexOrThrow("dayOfMonth")));
                fe.setLastAddedMonth(cursor.getInt(cursor.getColumnIndexOrThrow("lastAddedMonth")));
                fe.setLastAddedYear(cursor.getInt(cursor.getColumnIndexOrThrow("lastAddedYear")));
                list.add(fe);
            } while (cursor.moveToNext());
            cursor.close();
        }
        return list;
    }

}
