package com.example.expensemanager.fragments;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.expensemanager.Adapter.RecurringExpenseAdapter;
import com.example.expensemanager.Adapter.TransactionAdapter;
import com.example.expensemanager.Data.DAOExpense;
import com.example.expensemanager.Data.DAOFixedExpense;
import com.example.expensemanager.R;
import com.example.expensemanager.model.objExpense;
import com.example.expensemanager.model.objFixedExpense;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public class ExpenseFragment extends Fragment {

    // Existing views
    RecyclerView recyclerView;
    TransactionAdapter adapter;
    DAOExpense daoExpense;
    DAOFixedExpense daoFixedExpense;
    List<objExpense> expenseList = new ArrayList<>();
    List<objExpense> filteredList = new ArrayList<>();
    Spinner spnFilterCategory;
    TextInputEditText edFilterDate;
    Button btnAddRecurringExpense, btnClearFilter, btnShowRecurring;
    private int currentIdUser = 1;
    String selectedCategory = null;
    String selectedDate = null;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = view.findViewById(R.id.recycler_view_transactions);
        spnFilterCategory = view.findViewById(R.id.spn_filter_category);
        edFilterDate = view.findViewById(R.id.ed_filter_date);
        btnClearFilter = view.findViewById(R.id.btn_clear_filter);
        btnAddRecurringExpense = view.findViewById(R.id.btn_add_recurring_expense);
        btnShowRecurring = view.findViewById(R.id.btn_show_recurring);

        if (getContext() != null) {
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            daoExpense = new DAOExpense(getContext());
            daoFixedExpense = new DAOFixedExpense(getContext());

            if (getActivity() != null) {
                currentIdUser = getActivity().getIntent().getIntExtra("idUser", 1);
            }

            btnAddRecurringExpense.setOnClickListener(v -> showAddRecurringExpenseDialog(null));
            btnShowRecurring.setOnClickListener(v -> showRecurringExpensesDialog());

            List<objExpense> initialData = daoExpense.getExpensesByUser(currentIdUser, "Expense");
            expenseList.clear();
            expenseList.addAll(initialData);
            filteredList.clear();
            filteredList.addAll(initialData);

            adapter = new TransactionAdapter(getContext(), filteredList);
            recyclerView.setAdapter(adapter);

            setupCategoryFilter();
            setupDateFilter();
            setupClearButton();
        }
    }




    @Override
    public void onCreate(Bundle savedInstanceState) { super.onCreate(savedInstanceState); }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    { return inflater.inflate(R.layout.fragment_expense, container, false); }


    private void setupCategoryFilter() {
        List<String> categories = new ArrayList<>();
        categories.add("All");

        if (daoExpense != null) {
            List<String> dbCategories = daoExpense.getCategoriesByUser(currentIdUser, "Expense");
            categories.addAll(dbCategories);
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, categories);
        spnFilterCategory.setAdapter(adapter);
        spnFilterCategory.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(android.widget.AdapterView<?> parent, View view, int position, long id) {
                String selected = parent.getItemAtPosition(position).toString();
                selectedCategory = selected.equals("All") ? null : selected;
                applyFilters();
            }

            @Override
            public void onNothingSelected(android.widget.AdapterView<?> parent) {}
        });
    }

    private void setupDateFilter() { if (edFilterDate != null)
    { edFilterDate.setOnClickListener(v -> showDatePickerDialog()); } }
    private void setupClearButton() {
        btnClearFilter.setOnClickListener(v -> {
            selectedCategory = null;
            selectedDate = null;
            edFilterDate.setText("");
            spnFilterCategory.setSelection(0);
            applyFilters();
        });
    }

    private void showDatePickerDialog() {
        Calendar calendar = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                getContext(),
                (view, year, month, dayOfMonth) -> {
                    String date = String.format(Locale.getDefault(), "%04d-%02d-%02d", year, month + 1, dayOfMonth);
                    edFilterDate.setText(date);
                    selectedDate = date;
                    applyFilters();
                },
                calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)
        );
        datePickerDialog.show();
    }

    private void applyFilters() {
        filteredList.clear();
        List<objExpense> tempList = expenseList.stream()
                .filter(e -> (selectedCategory == null || e.getCategory().equals(selectedCategory)))
                .filter(e -> (selectedDate == null || e.getDate().equals(selectedDate)))
                .collect(Collectors.toList());
        filteredList.addAll(tempList);
        adapter.notifyDataSetChanged();
    }
    public void refreshList() {
        if (daoExpense != null && adapter != null) {
            List<objExpense> updatedList = daoExpense.getExpensesByUser(currentIdUser, "Expense");
            expenseList.clear();
            expenseList.addAll(updatedList);
            selectedCategory = null;
            selectedDate = null;
            edFilterDate.setText("");
            spnFilterCategory.setSelection(0);
            applyFilters();
        }
    }

    private void showRecurringExpensesDialog() {
        Dialog dialog = new Dialog(getContext());
        dialog.setContentView(R.layout.dialog_show_recurring);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        RecyclerView rvRecurring = dialog.findViewById(R.id.rv_recurring_expenses);
        Button btnClose = dialog.findViewById(R.id.btn_close_recurring_dialog);

        // Show recurring expenses by getAllFixedExpenses method
        rvRecurring.setLayoutManager(new LinearLayoutManager(getContext()));
        List<objFixedExpense> recurringList = daoFixedExpense.getAllFixedExpenses(currentIdUser);

        RecurringExpenseAdapter recurringAdapter = new RecurringExpenseAdapter(getContext(), recurringList,
                new RecurringExpenseAdapter.OnRecurringExpenseListener() {
            @Override
            public void onEdit(objFixedExpense expense) {
                showAddRecurringExpenseDialog(expense);
                dialog.dismiss();
            }

            @Override
            public void onDelete(objFixedExpense expense) {
                daoFixedExpense.deleteFixedExpense(expense.getId());
                Toast.makeText(getContext(), "Recurring expense deleted!", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
                showRecurringExpensesDialog();
            }
        });

        rvRecurring.setAdapter(recurringAdapter);
        btnClose.setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }

    private void showAddRecurringExpenseDialog(@Nullable objFixedExpense existingExpense) {
        Dialog dialog = new Dialog(getContext());
        dialog.setContentView(R.layout.dialog_add_recurring_expense);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);

        Spinner spnCategory = dialog.findViewById(R.id.spn_recurring_category);
        TextInputEditText edAmount = dialog.findViewById(R.id.ed_recurring_amount);
        TextInputEditText edDayOfMonth = dialog.findViewById(R.id.ed_recurring_day_of_month); // Find day input
        TextInputEditText edNote = dialog.findViewById(R.id.ed_recurring_note);
        Button btnCancel = dialog.findViewById(R.id.btn_recurring_cancel);
        Button btnSave = dialog.findViewById(R.id.btn_recurring_save);

        // Categories for recurring expenses
        List<String> categories = Arrays.asList("Food & Drinks", "Gasoline & Transportation",
                "Entertainment", "Groceries", "Monthly utility bills");
        ArrayAdapter<String> categoryAdapter = new ArrayAdapter<>(getContext(),
                android.R.layout.simple_spinner_item, categories);
        spnCategory.setAdapter(categoryAdapter);

        if (existingExpense != null) {
            edAmount.setText(String.valueOf(existingExpense.getAmount()));
            edDayOfMonth.setText(String.valueOf(existingExpense.getDayOfMonth())); // Pre-fill day
            edNote.setText(existingExpense.getNote());
            int categoryIndex = categories.indexOf(existingExpense.getCategory());
            if (categoryIndex != -1) {
                spnCategory.setSelection(categoryIndex);
            }
        }

        btnCancel.setOnClickListener(v -> dialog.dismiss());
        btnSave.setOnClickListener(v -> {
            String category = spnCategory.getSelectedItem().toString();
            String amountStr = edAmount.getText().toString().trim();
            String dayStr = edDayOfMonth.getText().toString().trim(); // Get day string
            String note = edNote.getText().toString().trim();

            if (amountStr.isEmpty() || dayStr.isEmpty()) {
                Toast.makeText(getContext(), "Amount and Day of Month cannot be empty", Toast.LENGTH_SHORT).show();
                return;
            }

            try {
                double amount = Double.parseDouble(amountStr);
                int day = Integer.parseInt(dayStr);

                if (day < 1 || day > 31) {
                    Toast.makeText(getContext(), "Day of Month must be between 1 and 31", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (existingExpense == null) {
                    objFixedExpense newExpense = new objFixedExpense();
                    newExpense.setIdUser(currentIdUser);
                    newExpense.setCategory(category);
                    newExpense.setAmount(amount);
                    newExpense.setDayOfMonth(day); // Set day
                    newExpense.setNote(note);
                    daoFixedExpense.insertFixedExpense(newExpense);
                    Toast.makeText(getContext(), "Recurring expense added!", Toast.LENGTH_SHORT).show();
                } else {
                    existingExpense.setCategory(category);
                    existingExpense.setAmount(amount);
                    existingExpense.setDayOfMonth(day); // Set day
                    existingExpense.setNote(note);
                    daoFixedExpense.updateFixedExpense(existingExpense);
                    Toast.makeText(getContext(), "Recurring expense updated!", Toast.LENGTH_SHORT).show();
                }
                dialog.dismiss();
            } catch (NumberFormatException e) {
                Toast.makeText(getContext(), "Invalid number format for amount or day", Toast.LENGTH_SHORT).show();
            }
        });
        dialog.show();
    }
}
