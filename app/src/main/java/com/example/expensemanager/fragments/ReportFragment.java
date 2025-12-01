package com.example.expensemanager.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.RadioGroup;

import com.example.expensemanager.Adapter.CategoryReportAdapter;
import com.example.expensemanager.Data.DAOExpense;
import com.example.expensemanager.R;
import com.example.expensemanager.model.objCategoryReport;
import com.example.expensemanager.model.objExpense;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ReportFragment extends Fragment {

    private Spinner spinnerMonth;
    private Button btnViewReport;
    private Spinner spinnerYear;
    private RadioGroup rgReportType;
    private RecyclerView recyclerViewExpense, recyclerViewIncome;
    private TextView tvEmptyExpense, tvEmptyIncome;
    private TextView tvTotalIncome, tvTotalExpense, tvTotalBalance;
    private TextView tvExpenseTotal, tvIncomeTotal, tvFinalTotal;
    private ReportViewModel viewModel;
    private DAOExpense daoExpense;
    private CategoryReportAdapter expenseAdapter, incomeAdapter;
    private int currentIdUser = 1;
    private static final String ARG_USER_ID = "userId";


    public ReportFragment() {
    }

    public static ReportFragment newInstance(int userId) {
        ReportFragment fragment = new ReportFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_USER_ID, userId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            currentIdUser = getArguments().getInt(ARG_USER_ID, 1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_report, container, false);

        // Initialize UI components
        spinnerMonth = view.findViewById(R.id.spinner_month);
        spinnerYear = view.findViewById(R.id.spinner_year);
        btnViewReport = view.findViewById(R.id.btn_view_report);
        rgReportType = view.findViewById(R.id.rg_report_type);
        recyclerViewExpense = view.findViewById(R.id.recyclerview_expense);
        recyclerViewIncome = view.findViewById(R.id.recyclerview_income);
        tvEmptyExpense = view.findViewById(R.id.tv_empty_expense);
        tvEmptyIncome = view.findViewById(R.id.tv_empty_income);
        tvTotalIncome = view.findViewById(R.id.tv_total_income);
        tvTotalExpense = view.findViewById(R.id.tv_total_expense);
        tvTotalBalance = view.findViewById(R.id.tv_total_balance);
        tvExpenseTotal = view.findViewById(R.id.tv_expense_total);
        tvIncomeTotal = view.findViewById(R.id.tv_income_total);
        tvFinalTotal = view.findViewById(R.id.tv_final_total);

        // Initialize DAO
        daoExpense = new DAOExpense(requireContext());
        viewModel = new ViewModelProvider(requireActivity()).get(ReportViewModel.class);

        // Get current user ID từ Bundle hoặc mặc định
        if (currentIdUser == 1 && getArguments() != null && getArguments().containsKey(ARG_USER_ID)) {
            currentIdUser = getArguments().getInt(ARG_USER_ID, 1);
        }

        // Setup RecyclerView for Expense
        recyclerViewExpense.setLayoutManager(new LinearLayoutManager(requireContext()));
        expenseAdapter = new CategoryReportAdapter(new ArrayList<>());
        recyclerViewExpense.setAdapter(expenseAdapter);

        // Setup RecyclerView for Income
        recyclerViewIncome.setLayoutManager(new LinearLayoutManager(requireContext()));
        incomeAdapter = new CategoryReportAdapter(new ArrayList<>());
        recyclerViewIncome.setAdapter(incomeAdapter);

        // Set listener for RadioGroup
        rgReportType.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.rb_monthly) {
                spinnerMonth.setVisibility(View.VISIBLE);
                spinnerYear.setVisibility(View.GONE);
                loadMonthsToSpinner();
            } else if (checkedId == R.id.rb_yearly) {
                spinnerMonth.setVisibility(View.GONE);
                spinnerYear.setVisibility(View.VISIBLE);
                loadYearsToSpinner();
            }
        });

        // Load available months
        loadMonthsToSpinner();

        // Set click listener for View Report button
        btnViewReport.setOnClickListener(v -> loadReportForSelectedMonth());

        // Load report for current month by default
        loadReportForSelectedMonth();

        return view;
    }

    /**
     * Load all available months to spinner
     */
    private void loadMonthsToSpinner() {
        List<String> monthsList = daoExpense.getAvailableMonths(currentIdUser);

        if (monthsList.isEmpty()) {
            // Add current month as default
            Calendar calendar = Calendar.getInstance();
            String currentMonth = String.format(Locale.getDefault(), "%02d/%d",
                    calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.YEAR));
            monthsList.add(currentMonth);
        } else {
            // Sort in descending order (newest first)
            Collections.sort(monthsList, Collections.reverseOrder());
        }

        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_spinner_item,
                monthsList
        );
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerMonth.setAdapter(spinnerAdapter);
    }

    /**
     * Load all available years to spinner
     */
    private void loadYearsToSpinner() {
        List<String> yearsList = daoExpense.getAvailableYears(currentIdUser);

        if (yearsList.isEmpty()) {
            // Add current year as default
            Calendar calendar = Calendar.getInstance();
            String currentYear = String.valueOf(calendar.get(Calendar.YEAR));
            yearsList.add(currentYear);
        } else {
            // Sort in descending order (newest first)
            Collections.sort(yearsList, Collections.reverseOrder());
        }

        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_spinner_item,
                yearsList
        );
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerYear.setAdapter(spinnerAdapter);
    }

    /**
     * Load report for selected month or year
     */
    private void loadReportForSelectedMonth() {
        List<objExpense> allTransactions = new ArrayList<>();

        // Check which report type is selected
        if (rgReportType.getCheckedRadioButtonId() == R.id.rb_monthly) {
            String selectedMonth = (String) spinnerMonth.getSelectedItem();

            if (selectedMonth == null || selectedMonth.isEmpty()) {
                tvEmptyExpense.setVisibility(View.VISIBLE);
                tvEmptyIncome.setVisibility(View.VISIBLE);
                recyclerViewExpense.setVisibility(View.GONE);
                recyclerViewIncome.setVisibility(View.GONE);
                return;
            }

            // Parse month and year from "MM/YYYY" format
            String[] parts = selectedMonth.split("/");
            int month = Integer.parseInt(parts[0]);
            int year = Integer.parseInt(parts[1]);

            // Get all transactions for the month
            allTransactions = daoExpense.getExpensesByMonthYear(currentIdUser, month, year);
        } else if (rgReportType.getCheckedRadioButtonId() == R.id.rb_yearly) {
            String selectedYear = (String) spinnerYear.getSelectedItem();

            if (selectedYear == null || selectedYear.isEmpty()) {
                tvEmptyExpense.setVisibility(View.VISIBLE);
                tvEmptyIncome.setVisibility(View.VISIBLE);
                recyclerViewExpense.setVisibility(View.GONE);
                recyclerViewIncome.setVisibility(View.GONE);
                return;
            }

            int year = Integer.parseInt(selectedYear);

            // Get all transactions for the year
            allTransactions = daoExpense.getExpensesByYear(currentIdUser, year);
        }

        // Separate into Expense and Income
        List<objCategoryReport> expenseReports = new ArrayList<>();
        List<objCategoryReport> incomeReports = new ArrayList<>();

        groupByCategory(allTransactions, expenseReports, incomeReports);

        // Update Expense section
        if (expenseReports.isEmpty()) {
            tvEmptyExpense.setVisibility(View.VISIBLE);
            recyclerViewExpense.setVisibility(View.GONE);
        } else {
            tvEmptyExpense.setVisibility(View.GONE);
            recyclerViewExpense.setVisibility(View.VISIBLE);
            expenseAdapter.updateList(expenseReports);
        }

        // Update Income section
        if (incomeReports.isEmpty()) {
            tvEmptyIncome.setVisibility(View.VISIBLE);
            recyclerViewIncome.setVisibility(View.GONE);
        } else {
            tvEmptyIncome.setVisibility(View.GONE);
            recyclerViewIncome.setVisibility(View.VISIBLE);
            incomeAdapter.updateList(incomeReports);
        }

        // Calculate and display totals
        updateTotals(expenseReports, incomeReports);
    }

    private void groupByCategory(List<objExpense> transactions,
                                 List<objCategoryReport> expenseReports,
                                 List<objCategoryReport> incomeReports) {

        Map<String, Double> expenseMap = new HashMap<>();
        Map<String, Double> incomeMap = new HashMap<>();

        for (objExpense transaction : transactions) {
            String category = transaction.getCategory();
            double amount = transaction.getAmount();

            if ("Expense".equals(transaction.getType())) {
                expenseMap.put(category, expenseMap.getOrDefault(category, 0.0) + amount);
            } else if ("Income".equals(transaction.getType())) {
                incomeMap.put(category, incomeMap.getOrDefault(category, 0.0) + amount);
            }
        }

        // Create expense reports
        for (Map.Entry<String, Double> entry : expenseMap.entrySet()) {
            objCategoryReport report = new objCategoryReport(entry.getKey(), 0, entry.getValue());
            expenseReports.add(report);
        }

        // Create income reports
        for (Map.Entry<String, Double> entry : incomeMap.entrySet()) {
            objCategoryReport report = new objCategoryReport(entry.getKey(), entry.getValue(), 0);
            incomeReports.add(report);
        }
    }

    private void updateTotals(List<objCategoryReport> expenseReports, List<objCategoryReport> incomeReports) {
        double totalIncome = 0;
        double totalExpense = 0;

        for (objCategoryReport report : expenseReports) {
            totalExpense += report.getExpense();
        }

        for (objCategoryReport report : incomeReports) {
            totalIncome += report.getIncome();
        }

        double balance = totalIncome - totalExpense;
        DecimalFormat df = new DecimalFormat("#,###.##");

        tvTotalIncome.setText("$" + df.format(totalIncome));
        tvTotalExpense.setText("$" + df.format(totalExpense));
        tvTotalBalance.setText("$" + df.format(balance));
        tvExpenseTotal.setText("$" + df.format(totalExpense));
        tvIncomeTotal.setText("$" + df.format(totalIncome));
        tvFinalTotal.setText("$" + df.format(balance));

        if (balance >= 0) {
            tvTotalBalance.setTextColor(requireContext().getColor(android.R.color.holo_green_dark));
            tvFinalTotal.setTextColor(requireContext().getColor(android.R.color.white));
        } else {
            tvTotalBalance.setTextColor(requireContext().getColor(android.R.color.holo_red_dark));
            tvFinalTotal.setTextColor(requireContext().getColor(android.R.color.white));
        }

        // Update ViewModel with totals Income and Expense
        ReportViewModel viewModel = new ViewModelProvider(requireActivity()).get(ReportViewModel.class);
        viewModel.setTotalIncome(totalIncome);
        viewModel.setTotalExpense(totalExpense);
    }

    public void setUserId(int userId) {
        this.currentIdUser = userId;
    }
    public void refreshList() {
        loadMonthsToSpinner();
        loadReportForSelectedMonth();
    }
}

