package com.example.expensemanager;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.expensemanager.Data.DAOExpense;
import com.example.expensemanager.model.objExpense;
import com.google.android.material.textfield.TextInputEditText;

import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class AddTransactionActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        showAddTransactionDialog();
    }

    private void showAddTransactionDialog() {
        Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_add_transaction);

        Window window = dialog.getWindow();
        if (window != null) {
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            window.setDimAmount(0.5f);
            window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        }

        RadioGroup rgType = dialog.findViewById(R.id.rgType);
        Spinner spnCategory = dialog.findViewById(R.id.spnCategory);
        TextInputEditText edAmount = dialog.findViewById(R.id.edAmount);
        TextInputEditText edDate = dialog.findViewById(R.id.edDate);
        TextInputEditText edNote = dialog.findViewById(R.id.edNote);
        Button btnAdd = dialog.findViewById(R.id.btnAddTransaction);

        List<String> expenseList = Arrays.asList("Food & Drinks", "Gasoline & Transportation", "Entertainment", "Groceries", "Monthly utility bills");
        List<String> incomeList = Arrays.asList("Salary", "Bonus", "Interest");

        ArrayAdapter<String> expenseAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, expenseList);
        expenseAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        ArrayAdapter<String> incomeAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, incomeList);
        incomeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spnCategory.setAdapter(expenseAdapter);

        rgType.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.rbExpense) {
                spnCategory.setAdapter(expenseAdapter);
            } else {
                spnCategory.setAdapter(incomeAdapter);
            }
        });

        // Set up DatePicker for Date field
        if (edDate != null) {
            edDate.setOnClickListener(v -> showDatePickerDialog(edDate));
        }

        btnAdd.setOnClickListener(v -> {
            String type = (rgType.getCheckedRadioButtonId() == R.id.rbExpense) ? "Expense" : "Income";
            String category = spnCategory.getSelectedItem().toString();
            String strAmount = edAmount.getText().toString().trim();
            String date = edDate.getText().toString();
            String note = edNote.getText().toString();

            if (strAmount.isEmpty()) {
                edAmount.setError("Please enter amount");
                return;
            }

            if (date.isEmpty()) {
                edDate.setError("Please select date");
                return;
            }

            try {
                DAOExpense dao = new DAOExpense(this);
                objExpense expense = new objExpense();

                int idUser = getIntent().getIntExtra("idUser", 1);
                expense.setIdUser(idUser);
                expense.setType(type);
                expense.setCategory(category);
                expense.setDate(date);
                expense.setNote(note);

                double amountValue = Double.parseDouble(strAmount);
                expense.setAmount(amountValue);

                int result = dao.insertExpense(expense);

                if (result > 0) {
                    Toast.makeText(this, "Transaction added successfully!", Toast.LENGTH_SHORT).show();
                    Intent resultIntent = new Intent();
                    resultIntent.putExtra("transactionType", type);
                    setResult(Activity.RESULT_OK, resultIntent);
                    dialog.dismiss();
                    finish();
                } else {
                    Toast.makeText(this, "Failed to add transaction!", Toast.LENGTH_SHORT).show();
                }

            } catch (NumberFormatException e) {
                edAmount.setError("Invalid amount format");
            } catch (Exception e) {
                Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        dialog.setCancelable(true);
        dialog.show();
    }

    // Method to show DatePickerDialog
    private void showDatePickerDialog(TextInputEditText edDate) {
        Calendar calendar = Calendar.getInstance();

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, year, month, dayOfMonth) -> {
                    // Format: YYYY-MM-DD
                    String selectedDate = String.format(Locale.getDefault(), "%04d-%02d-%02d", year, month + 1, dayOfMonth);
                    edDate.setText(selectedDate);
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );

        datePickerDialog.show();
    }
}
