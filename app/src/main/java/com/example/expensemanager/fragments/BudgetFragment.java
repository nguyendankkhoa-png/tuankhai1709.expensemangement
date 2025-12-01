package com.example.expensemanager.fragments;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatSpinner;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Toast;

import com.example.expensemanager.Adapter.BudgetAdapter;
import com.example.expensemanager.Data.DAOBudget;
import com.example.expensemanager.R;
import com.example.expensemanager.model.objBudget;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;

import java.text.SimpleDateFormat;
import java.util.*;

public class BudgetFragment extends Fragment
        implements BudgetAdapter.OnBudgetEditListener, BudgetAdapter.OnBudgetDeleteListener {

    private RecyclerView recyclerView;
    private BudgetAdapter adapter;
    private DAOBudget daoBudget;
    private final List<objBudget> budgetList = new ArrayList<>();

    private FloatingActionButton fabAddBudget;
    private int currentIdUser = 1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_budget, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = view.findViewById(R.id.recycler_view_budgets);
        fabAddBudget = view.findViewById(R.id.fab_add_budget);

        if (getContext() == null) return;

        daoBudget = new DAOBudget(getContext());
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        if (getActivity() != null)
            currentIdUser = getActivity().getIntent().getIntExtra("idUser", 1);

        adapter = new BudgetAdapter(getContext(), budgetList, this, this);
        recyclerView.setAdapter(adapter);

        loadBudgets();

        fabAddBudget.setOnClickListener(v -> openBudgetDialog(null));
    }

    // Load budgets & auto-delete expired
    private void loadBudgets() {
        List<objBudget> data = daoBudget.getAllBudgetsForUser(currentIdUser);

        String today = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        data.removeIf(b -> today.compareTo(b.getEndDate()) > 0);

        budgetList.clear();
        budgetList.addAll(data);
        adapter.notifyDataSetChanged();
    }

    // Reusable Dialog for Add + Edit
    private void openBudgetDialog(@Nullable objBudget editObj) {
        if (getContext() == null) return;

        Dialog dialog = new Dialog(getContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_add_budget);

        Window window = dialog.getWindow();
        if (window != null) {
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        }

        AppCompatSpinner spnCategory = dialog.findViewById(R.id.spn_budget_category);
        TextInputEditText edAmount = dialog.findViewById(R.id.ed_budget_amount);
        TextInputEditText edStartDate = dialog.findViewById(R.id.ed_budget_start_date);
        TextInputEditText edEndDate = dialog.findViewById(R.id.ed_budget_end_date);
        Button btnCancel = dialog.findViewById(R.id.btn_budget_cancel);
        Button btnSave = dialog.findViewById(R.id.btn_budget_save);

        List<String> categories = new ArrayList<>(Arrays.asList(
                "All", "Food & Drinks", "Gasoline & Transportation",
                "Entertainment", "Groceries", "Monthly utility bills"
        ));

        spnCategory.setAdapter(new ArrayAdapter<>(getContext(),
                android.R.layout.simple_spinner_item, categories));

        // If editing => fill
        if (editObj != null) {
            spnCategory.setSelection(categories.indexOf(editObj.getCategory()));
            edAmount.setText(String.valueOf(editObj.getBudgetAmount()));
            edStartDate.setText(editObj.getStartDate());
            edEndDate.setText(editObj.getEndDate());
        } else {
            Calendar c = Calendar.getInstance();
            String start = String.format("%04d-%02d-01", c.get(Calendar.YEAR), c.get(Calendar.MONTH) + 1);
            edStartDate.setText(start);

            String end = String.format("%04d-%02d-%02d",
                    c.get(Calendar.YEAR), c.get(Calendar.MONTH) + 1,
                    c.getActualMaximum(Calendar.DAY_OF_MONTH));
            edEndDate.setText(end);
        }

        edStartDate.setOnClickListener(v -> showDatePickerDialog(edStartDate));
        edEndDate.setOnClickListener(v -> showDatePickerDialog(edEndDate));

        btnCancel.setOnClickListener(v -> dialog.dismiss());

        btnSave.setOnClickListener(v -> {
            String amountStr = edAmount.getText().toString().trim();
            if (amountStr.isEmpty()) {
                edAmount.setError("Required");
                return;
            }

            double amount = Double.parseDouble(amountStr);
            if (amount <= 0) {
                edAmount.setError("Must be > 0");
                return;
            }

            objBudget b = (editObj != null) ? editObj : new objBudget();
            b.setIdUser(currentIdUser);
            b.setCategory(spnCategory.getSelectedItem().toString());
            b.setBudgetAmount(amount);
            b.setStartDate(edStartDate.getText().toString());
            b.setEndDate(edEndDate.getText().toString());

            if (editObj == null)
                b.setCreatedDate(new SimpleDateFormat("dd-MM-yyy", Locale.getDefault()).format(new Date()));

            int result = daoBudget.insertOrUpdateBudget(b);

            if (result > 0) {
                Toast.makeText(getContext(), editObj == null ?
                        "Budget added!" : "Updated!", Toast.LENGTH_SHORT).show();
                loadBudgets();
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    private void showDatePickerDialog(TextInputEditText ed) {
        Calendar c = Calendar.getInstance();
        new DatePickerDialog(getContext(), (view, y, m, d) ->
                ed.setText(String.format(Locale.getDefault(), "%d-%02d-%02d", y, m + 1, d)),
                c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)
        ).show();
    }

    @Override
    public void onEditBudget(objBudget b) {
        openBudgetDialog(b);
    }

    @Override
    public void onDeleteBudget(objBudget b) {
        daoBudget.deleteBudget(b.getId());
        loadBudgets();
        Toast.makeText(getContext(), "Deleted!", Toast.LENGTH_SHORT).show();
    }
}
