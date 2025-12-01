package com.example.expensemanager.fragments;

import android.app.DatePickerDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import com.example.expensemanager.Adapter.TransactionAdapter;
import com.example.expensemanager.Data.DAOExpense;
import com.example.expensemanager.R;
import com.example.expensemanager.model.objExpense;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public class IncomeFragment extends Fragment {

    private static final String ARG_USER_ID = "user_id";
    private int currentUserId;

    RecyclerView recyclerView;
    TransactionAdapter adapter;
    DAOExpense daoExpense;
    List<objExpense> incomeList = new ArrayList<>();
    List<objExpense> filteredList = new ArrayList<>();

    Spinner spn_filter_category;
    TextInputEditText edFilterDate;
    Button btnClearFilter;
    String selectedCategory = null;
    String selectedDate = null;
    ArrayAdapter<String> categoryAdapter;

    public static IncomeFragment newInstance(int userId) {
        IncomeFragment fragment = new IncomeFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_USER_ID, userId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            currentUserId = getArguments().getInt(ARG_USER_ID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_income, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = view.findViewById(R.id.recycler_view_transactions);
        spn_filter_category = view.findViewById(R.id.spn_filter_category);
        edFilterDate = view.findViewById(R.id.edFilterdate);
        btnClearFilter = view.findViewById(R.id.btn_clear_filter);

        if (getContext() != null) {
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            daoExpense = new DAOExpense(getContext());

            adapter = new TransactionAdapter(getContext(), filteredList);
            recyclerView.setAdapter(adapter);

            setupCategoryFilter();
            setupDateFilter();
            setupClearButton();

            loadInitialData();
        }
    }

    private void loadInitialData() {
        incomeList.clear();
        incomeList.addAll(daoExpense.getExpensesByUser(currentUserId, "Income"));
        applyFilters();
        updateCategorySpinner();
    }

    private void setupDateFilter() {
        if (edFilterDate != null) {
            edFilterDate.setOnClickListener(v -> showDatePickerDialog());
        }
    }

    private void setupClearButton() {
        btnClearFilter.setOnClickListener(v -> {
            selectedCategory = null;
            selectedDate = null;
            edFilterDate.setText("");
            spn_filter_category.setSelection(0);
            applyFilters();
        });
    }

    private void updateCategorySpinner() {
        if (getContext() == null) return;
        List<String> categories = new ArrayList<>();
        categories.add("All");
        categories.addAll(daoExpense.getCategoriesByUser(currentUserId, "Income"));
        
        categoryAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, categories);
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spn_filter_category.setAdapter(categoryAdapter);
    }
    
    private void setupCategoryFilter() {
        updateCategorySpinner();
        spn_filter_category.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selected = parent.getItemAtPosition(position).toString();
                selectedCategory = selected.equals("All") ? null : selected;
                applyFilters();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private void applyFilters() {
        filteredList.clear();
        List<objExpense> tempList = incomeList.stream()
                .filter(e -> (selectedCategory == null || e.getCategory().equals(selectedCategory)))
                .filter(e -> (selectedDate == null || e.getDate().equals(selectedDate)))
                .collect(Collectors.toList());
        filteredList.addAll(tempList);
        adapter.notifyDataSetChanged();
    }

    public void refreshList() {
        if (daoExpense != null && adapter != null) {
            loadInitialData();
        }
    }

    private void showDatePickerDialog() {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(),
                (view, year1, month1, dayOfMonth) -> {
                    selectedDate = String.format(Locale.getDefault(), "%04d-%02d-%02d", year1, month1 + 1, dayOfMonth);
                    edFilterDate.setText(selectedDate);
                    applyFilters();
                }, year, month, day);
        datePickerDialog.show();
    }
}
