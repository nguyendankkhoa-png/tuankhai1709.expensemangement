package com.example.expensemanager.Data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.expensemanager.model.objCategoryReport;
import com.example.expensemanager.model.objExpense;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class DAOExpense {

    DatabaseHelper dbHelper;

    public DAOExpense(Context context) {
        this.dbHelper = new DatabaseHelper(context);
    }

    public int insertExpense(objExpense expense) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("idUser", expense.getIdUser());
        values.put("amount", expense.getAmount());
        values.put("date", expense.getDate());
        values.put("note", expense.getNote());
        values.put("category", expense.getCategory());
        values.put("type", expense.getType());

        long result = db.insert("tbl_transaction", null, values);
        return (int) result;
    }

    public int updateExpense(objExpense expense) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("idUser", expense.getIdUser());
        values.put("amount", expense.getAmount());
        values.put("date", expense.getDate());
        values.put("note", expense.getNote());
        values.put("category", expense.getCategory());
        values.put("type", expense.getType());

        return db.update("tbl_transaction", values, "id = ?", new String[]{String.valueOf(expense.getId())});
    }

    public int deleteExpense(int expenseId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        return db.delete("tbl_transaction", "id = ?", new String[]{String.valueOf(expenseId)});
    }

    public List<objExpense> getExpensesByType(String type) {
        List<objExpense> list = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String query = "SELECT * FROM tbl_transaction WHERE type = ? ORDER BY date DESC";
        Cursor cursor = db.rawQuery(query, new String[]{type});

        if (cursor != null && cursor.moveToFirst()) {
            do {
                objExpense expense = new objExpense();
                expense.setId(cursor.getInt(cursor.getColumnIndexOrThrow("id")));
                expense.setIdUser(cursor.getInt(cursor.getColumnIndexOrThrow("idUser")));
                expense.setAmount(cursor.getDouble(cursor.getColumnIndexOrThrow("amount")));
                expense.setDate(cursor.getString(cursor.getColumnIndexOrThrow("date")));
                expense.setNote(cursor.getString(cursor.getColumnIndexOrThrow("note")));
                expense.setCategory(cursor.getString(cursor.getColumnIndexOrThrow("category")));
                expense.setType(cursor.getString(cursor.getColumnIndexOrThrow("type")));
                list.add(expense);
            } while (cursor.moveToNext());
            cursor.close();
        }
        return list;
    }

    // FIX: Added back the getRecentTransactions method for HomeFragment
    public List<objExpense> getRecentTransactions(int userId, int limit) {
        List<objExpense> list = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        // Query to get the most recent transactions, ordered by date and limited
        String query = "SELECT * FROM tbl_transaction WHERE idUser = ? ORDER BY date DESC LIMIT ?";
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(userId), String.valueOf(limit)});

        if (cursor != null && cursor.moveToFirst()) {
            do {
                objExpense expense = new objExpense();
                expense.setId(cursor.getInt(cursor.getColumnIndexOrThrow("id")));
                expense.setIdUser(cursor.getInt(cursor.getColumnIndexOrThrow("idUser")));
                expense.setAmount(cursor.getDouble(cursor.getColumnIndexOrThrow("amount")));
                expense.setDate(cursor.getString(cursor.getColumnIndexOrThrow("date")));
                expense.setNote(cursor.getString(cursor.getColumnIndexOrThrow("note")));
                expense.setCategory(cursor.getString(cursor.getColumnIndexOrThrow("category")));
                expense.setType(cursor.getString(cursor.getColumnIndexOrThrow("type")));
                list.add(expense);
            } while (cursor.moveToNext());
            cursor.close();
        }
        return list;
    }

    // Thêm hàm này vào DAOExpense.java
    public List<objExpense> getExpensesByUser(int idUser, String type) {
        List<objExpense> list = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        // Query lấy tất cả giao dịch của User theo loại (không lọc theo năm)
        String query = "SELECT * FROM tbl_transaction WHERE idUser = ? AND type = ? ORDER BY date DESC";
        String[] selectionArgs = new String[]{String.valueOf(idUser), type};

        Cursor cursor = db.rawQuery(query, selectionArgs);

        if (cursor != null && cursor.moveToFirst()) {
            do {
                objExpense expense = new objExpense();
                expense.setId(cursor.getInt(cursor.getColumnIndexOrThrow("id")));
                expense.setIdUser(cursor.getInt(cursor.getColumnIndexOrThrow("idUser")));
                expense.setAmount(cursor.getDouble(cursor.getColumnIndexOrThrow("amount")));
                expense.setDate(cursor.getString(cursor.getColumnIndexOrThrow("date")));
                expense.setNote(cursor.getString(cursor.getColumnIndexOrThrow("note")));
                expense.setCategory(cursor.getString(cursor.getColumnIndexOrThrow("category")));
                expense.setType(cursor.getString(cursor.getColumnIndexOrThrow("type")));
                list.add(expense);
            } while (cursor.moveToNext());
            cursor.close();
        }
        return list;
    }

    public List<objExpense> getExpensesByUser(int idUser, String type, int year) {
        List<objExpense> list = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String query;
        String[] selectionArgs;

        String yearStr = String.valueOf(year);

        if (type != null && !type.isEmpty()) {
            query = "SELECT * FROM tbl_transaction WHERE idUser = ? AND type = ? AND strftime('%Y', date) = ? ORDER BY date DESC";
            selectionArgs = new String[]{String.valueOf(idUser), type, yearStr};
        } else {
            query = "SELECT * FROM tbl_transaction WHERE idUser = ? AND strftime('%Y', date) = ? ORDER BY date DESC";
            selectionArgs = new String[]{String.valueOf(idUser), yearStr};
        }

        Cursor cursor = db.rawQuery(query, selectionArgs);

        if (cursor != null && cursor.moveToFirst()) {
            do {
                objExpense expense = new objExpense();
                expense.setId(cursor.getInt(cursor.getColumnIndexOrThrow("id")));
                expense.setIdUser(cursor.getInt(cursor.getColumnIndexOrThrow("idUser")));
                expense.setAmount(cursor.getDouble(cursor.getColumnIndexOrThrow("amount")));
                expense.setDate(cursor.getString(cursor.getColumnIndexOrThrow("date")));
                expense.setNote(cursor.getString(cursor.getColumnIndexOrThrow("note")));
                expense.setCategory(cursor.getString(cursor.getColumnIndexOrThrow("category")));
                expense.setType(cursor.getString(cursor.getColumnIndexOrThrow("type")));
                list.add(expense);
            } while (cursor.moveToNext());
            cursor.close();
        }
        return list;
    }

    public List<objCategoryReport> getCategoryReportByMonthYear(int idUser, int month, int year) {
        List<objCategoryReport> reportList = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        // Tạo string cho tháng/năm (MM/YYYY)
        String monthYearPattern = String.format("%02d/%d", month, year);

        // Query để lấy tất cả category của user trong tháng/năm đó
        String query = "SELECT DISTINCT category FROM tbl_transaction WHERE idUser = ? AND strftime('%m/%Y', date) = ?";
        Cursor categoryCursor = db.rawQuery(query, new String[]{String.valueOf(idUser), monthYearPattern});

        if (categoryCursor != null && categoryCursor.moveToFirst()) {
            do {
                String category = categoryCursor.getString(0);

                // Lấy tổng income cho category này
                String incomeQuery = "SELECT COALESCE(SUM(amount), 0) FROM tbl_transaction WHERE idUser = ? AND type = 'Income' AND category = ? AND strftime('%m/%Y', date) = ?";
                Cursor incomeCursor = db.rawQuery(incomeQuery, new String[]{String.valueOf(idUser), category, monthYearPattern});
                double income = 0;
                if (incomeCursor != null && incomeCursor.moveToFirst()) {
                    income = incomeCursor.getDouble(0);
                    incomeCursor.close();
                }

                // Lấy tổng expense cho category này
                String expenseQuery = "SELECT COALESCE(SUM(amount), 0) FROM tbl_transaction WHERE idUser = ? AND type = 'Expense' AND category = ? AND strftime('%m/%Y', date) = ?";
                Cursor expenseCursor = db.rawQuery(expenseQuery, new String[]{String.valueOf(idUser), category, monthYearPattern});
                double expense = 0;
                if (expenseCursor != null && expenseCursor.moveToFirst()) {
                    expense = expenseCursor.getDouble(0);
                    expenseCursor.close();
                }

                objCategoryReport report = new objCategoryReport(category, income, expense);
                reportList.add(report);
            } while (categoryCursor.moveToNext());
            categoryCursor.close();
        }
        return reportList;
    }

    /**
     * Lấy danh sách các tháng/năm có giao dịch
     */
    public List<String> getAvailableMonths(int idUser) {
        List<String> monthsList = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String query = "SELECT DISTINCT strftime('%m/%Y', date) FROM tbl_transaction WHERE idUser = ? ORDER BY date DESC";
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(idUser)});

        if (cursor != null && cursor.moveToFirst()) {
            do {
                monthsList.add(cursor.getString(0));
            } while (cursor.moveToNext());
            cursor.close();
        }
        return monthsList;
    }

    public List<String> getAvailableYears(int idUser) {
        List<String> yearsList = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String query = "SELECT DISTINCT strftime('%Y', date) FROM tbl_transaction WHERE idUser = ? ORDER BY date DESC";
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(idUser)});

        if (cursor != null && cursor.moveToFirst()) {
            do {
                yearsList.add(cursor.getString(0));
            } while (cursor.moveToNext());
            cursor.close();
        }
        return yearsList;
    }

    public List<objExpense> getExpensesByMonthYear(int idUser, int month, int year) {
        List<objExpense> list = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String yearMonthPattern = String.format("%d-%02d", year, month);
        String query = "SELECT * FROM tbl_transaction WHERE idUser = ? AND strftime('%Y-%m', date) = ? ORDER BY date DESC";
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(idUser), yearMonthPattern});

        if (cursor != null && cursor.moveToFirst()) {
            do {
                objExpense expense = new objExpense();
                expense.setId(cursor.getInt(cursor.getColumnIndexOrThrow("id")));
                expense.setIdUser(cursor.getInt(cursor.getColumnIndexOrThrow("idUser")));
                expense.setAmount(cursor.getDouble(cursor.getColumnIndexOrThrow("amount")));
                expense.setDate(cursor.getString(cursor.getColumnIndexOrThrow("date")));
                expense.setNote(cursor.getString(cursor.getColumnIndexOrThrow("note")));
                expense.setCategory(cursor.getString(cursor.getColumnIndexOrThrow("category")));
                expense.setType(cursor.getString(cursor.getColumnIndexOrThrow("type")));
                list.add(expense);
            } while (cursor.moveToNext());
            cursor.close();
        }
        return list;
    }

    public List<objExpense> getExpensesByYear(int idUser, int year) {
        List<objExpense> list = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String yearStr = String.valueOf(year);
        String query = "SELECT * FROM tbl_transaction WHERE idUser = ? AND strftime('%Y', date) = ? ORDER BY date DESC";
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(idUser), yearStr});

        if (cursor != null && cursor.moveToFirst()) {
            do {
                objExpense expense = new objExpense();
                expense.setId(cursor.getInt(cursor.getColumnIndexOrThrow("id")));
                expense.setIdUser(cursor.getInt(cursor.getColumnIndexOrThrow("idUser")));
                expense.setAmount(cursor.getDouble(cursor.getColumnIndexOrThrow("amount")));
                expense.setDate(cursor.getString(cursor.getColumnIndexOrThrow("date")));
                expense.setNote(cursor.getString(cursor.getColumnIndexOrThrow("note")));
                expense.setCategory(cursor.getString(cursor.getColumnIndexOrThrow("category")));
                expense.setType(cursor.getString(cursor.getColumnIndexOrThrow("type")));
                list.add(expense);
            } while (cursor.moveToNext());
            cursor.close();
        }
        return list;
    }

    public List<String> getCategories(String type) {
        List<String> categories = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String query = "SELECT DISTINCT category FROM tbl_transaction WHERE type = ? ORDER BY category ASC";
        Cursor cursor = db.rawQuery(query, new String[]{type});

        if (cursor != null && cursor.moveToFirst()) {
            do {
                String category = cursor.getString(0);
                if (category != null && !category.isEmpty()) {
                    categories.add(category);
                }
            } while (cursor.moveToNext());
            cursor.close();
        }
        return categories;
    }

    public List<String> getCategoriesByUser(int idUser, String type) {
        List<String> categories = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String query = "SELECT DISTINCT category FROM tbl_transaction WHERE idUser = ? AND type = ? ORDER BY category ASC";
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(idUser), type});

        if (cursor != null && cursor.moveToFirst()) {
            do {
                String category = cursor.getString(0);
                if (category != null && !category.isEmpty()) {
                    categories.add(category);
                }
            } while (cursor.moveToNext());
            cursor.close();
        }

        return categories;
    }

    public double getTotalIncomeByMonth(int idUser, int month, int year) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        // Tạo chuỗi lọc ngày: "2025-12-%"
        String monthStr = (month < 10) ? "0" + month : String.valueOf(month);
        String searchPattern = year + "-" + monthStr + "-%";

        // KHÔI PHỤC LẠI: Lọc theo idUser và dùng LIKE cho ngày tháng
        String query = "SELECT COALESCE(SUM(amount), 0) FROM tbl_transaction " +
                "WHERE idUser = ? AND type = 'Income' AND date LIKE ?";

        // Thêm lại String.valueOf(idUser) vào mảng tham số
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(idUser), searchPattern});

        double total = 0;
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                total = cursor.getDouble(0);
            }
            cursor.close();
        }
        return total;
    }

    public double getTotalExpenseByMonth(int idUser, int month, int year) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String monthStr = (month < 10) ? "0" + month : String.valueOf(month);
        String searchPattern = year + "-" + monthStr + "-%";

        // KHÔI PHỤC LẠI: Lọc theo idUser
        String query = "SELECT COALESCE(SUM(amount), 0) FROM tbl_transaction " +
                "WHERE idUser = ? AND type = 'Expense' AND date LIKE ?";

        // Thêm lại String.valueOf(idUser)
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(idUser), searchPattern});

        double total = 0;
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                total = cursor.getDouble(0);
            }
            cursor.close();
        }
        return total;
    }
}
