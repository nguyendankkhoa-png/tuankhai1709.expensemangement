package com.example.expensemanager;

import android.app.AlertDialog;
import android.content.Context;

import androidx.annotation.OpenForTesting;

public class FunctionRecycle {
    public static void showAlert(Context context, String title, String message) {
        // Implementation for showing an alert dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setPositiveButton("OK", (dialog, which) -> {
            dialog.dismiss();
        });
        builder.show();
    }
}
