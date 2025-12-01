package com.example.expensemanager.Data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.expensemanager.model.objBudget;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DAOBudget {

    private final DatabaseHelper dbHelper;
    private final DAOExpense daoExpense;

    public DAOBudget(Context context) {
        dbHelper = new DatabaseHelper(context);
        daoExpense = new DAOExpense(context);
    }

    public int insertOrUpdateBudget(objBudget budget) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("idUser", budget.getIdUser());
        values.put("category", budget.getCategory());
        values.put("budgetAmount", budget.getBudgetAmount());
        values.put("createdDate", budget.getCreatedDate());
        values.put("startDate", budget.getStartDate());
        values.put("endDate", budget.getEndDate());

        if (budget.getId() > 0) {
            return db.update("tbl_budget", values, "id = ?", new String[]{String.valueOf(budget.getId())});
        }

        Cursor cursor = db.rawQuery(
                "SELECT id FROM tbl_budget WHERE idUser = ? AND category = ?",
                new String[]{String.valueOf(budget.getIdUser()), budget.getCategory()}
        );

        if (cursor.moveToFirst()) {
            int existId = cursor.getInt(0);
            cursor.close();
            return db.update("tbl_budget", values, "id = ?", new String[]{String.valueOf(existId)});
        }
        cursor.close();

        return (int) db.insert("tbl_budget", null, values);
    }

    public List<objBudget> getAllBudgetsForUser(int idUser) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        List<objBudget> list = new ArrayList<>();

        Cursor cursor = db.rawQuery(
                "SELECT * FROM tbl_budget WHERE idUser = ? ORDER BY category ASC",
                new String[]{String.valueOf(idUser)}
        );

        if (cursor.moveToFirst()) {
            do {
                objBudget b = new objBudget();
                b.setId(cursor.getInt(cursor.getColumnIndexOrThrow("id")));
                b.setIdUser(cursor.getInt(cursor.getColumnIndexOrThrow("idUser")));
                b.setCategory(cursor.getString(cursor.getColumnIndexOrThrow("category")));
                b.setBudgetAmount(cursor.getDouble(cursor.getColumnIndexOrThrow("budgetAmount")));
                b.setStartDate(cursor.getString(cursor.getColumnIndexOrThrow("startDate")));
                b.setEndDate(cursor.getString(cursor.getColumnIndexOrThrow("endDate")));
                list.add(b);
            } while (cursor.moveToNext());
        }

        cursor.close();
        return list;
    }

    public objBudget getBudgetByCategory(int idUser, String category) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        objBudget b = null;

        Cursor cursor = db.rawQuery(
                "SELECT * FROM tbl_budget WHERE idUser = ? AND category = ?",
                new String[]{String.valueOf(idUser), category}
        );

        if (cursor.moveToFirst()) {
            b = new objBudget();
            b.setId(cursor.getInt(cursor.getColumnIndexOrThrow("id")));
            b.setIdUser(idUser);
            b.setCategory(category);
            b.setBudgetAmount(cursor.getDouble(cursor.getColumnIndexOrThrow("budgetAmount")));
            b.setStartDate(cursor.getString(cursor.getColumnIndexOrThrow("startDate")));
            b.setEndDate(cursor.getString(cursor.getColumnIndexOrThrow("endDate")));
        }

        cursor.close();
        return b;
    }

    public int deleteBudget(int budgetId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        return db.delete("tbl_budget", "id = ?", new String[]{String.valueOf(budgetId)});
    }

    public List<Map<String, Object>> getBudgetsExceeding90Percent(int idUser, Context context) {
        List<Map<String, Object>> alertList = new ArrayList<>();
        List<objBudget> allBudgets = getAllBudgetsForUser(idUser);

        for (objBudget budget : allBudgets) {
            double spentAmount = calculateSpentAmount(idUser, budget.getCategory(), budget.getStartDate(), budget.getEndDate(), daoExpense);
            double budgetAmount = budget.getBudgetAmount();
            int percentage = (budgetAmount > 0) ? (int) ((spentAmount / budgetAmount) * 100) : 0;

            if (percentage >= 80) {
                Map<String, Object> alert = new HashMap<>();
                alert.put("budgetId", budget.getId());
                alert.put("category", budget.getCategory());
                alert.put("budgetAmount", budgetAmount);
                alert.put("spentAmount", spentAmount);
                alert.put("percentage", percentage);
                alertList.add(alert);
            }
        }
        return alertList;
    }

    private double calculateSpentAmount(int idUser, String category, String startDate, String endDate, DAOExpense dao) {
        SQLiteDatabase db = dao.dbHelper.getReadableDatabase();
        double result = 0;
        String query;
        String[] args;

        if (category.equalsIgnoreCase("All")) {
            query = "SELECT SUM(amount) FROM tbl_transaction WHERE idUser = ? AND type = 'Expense' AND date BETWEEN ? AND ?";
            args = new String[]{String.valueOf(idUser), startDate, endDate};
        } else {
            query = "SELECT SUM(amount) FROM tbl_transaction WHERE idUser = ? AND type = 'Expense' AND category = ? AND date BETWEEN ? AND ?";
            args = new String[]{String.valueOf(idUser), category, startDate, endDate};
        }

        Cursor cursor = db.rawQuery(query, args);
        if (cursor.moveToFirst()) {
            result = cursor.getDouble(0);
        }
        cursor.close();
        return result;
    }
}
