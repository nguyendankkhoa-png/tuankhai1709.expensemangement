package com.example.expensemanager.fragments;

import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.expensemanager.Adapter.TransactionAdapter;
import com.example.expensemanager.Data.DAOExpense;
import com.example.expensemanager.R;
import com.example.expensemanager.model.objExpense;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class HomeFragment extends Fragment {

    private static final String ARG_USER_ID = "user_id";

    RecyclerView recyclerView;
    TransactionAdapter adapter;
    DAOExpense daoExpense;
    List<objExpense> recentTransactionsList = new ArrayList<>();
    TextView tvRecentTransactions, tvTotalIncome, tvTotalExpense, tvAvailableBalance;
    private PieChart pieChart;
    private int currentUserId;


    public HomeFragment() {
    }

    public static HomeFragment newInstance(int userId) {
        HomeFragment fragment = new HomeFragment();
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = view.findViewById(R.id.recycler_view_recent_transactions);
        tvRecentTransactions = view.findViewById(R.id.tv_recent_transactions);
        tvTotalIncome = view.findViewById(R.id.tv_total_income);
        tvTotalExpense = view.findViewById(R.id.tv_total_expense);
        tvAvailableBalance = view.findViewById(R.id.tv_available_balance);
        pieChart = view.findViewById(R.id.pie_chart);

        if (getContext() != null) {
            daoExpense = new DAOExpense(getContext());

            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

            setupPieChart();

            // Load data for the home fragment
            loadData();
        }
    }

    private void setupPieChart() {
        pieChart.setUsePercentValues(true);
        pieChart.getDescription().setEnabled(false);
        pieChart.setExtraOffsets(10, 10, 10, 10);
        pieChart.setDragDecelerationFrictionCoef(0.95f);
        pieChart.setDrawHoleEnabled(true);
        pieChart.setHoleColor(Color.LTGRAY);
        pieChart.setHoleRadius(55f);
        pieChart.setTransparentCircleRadius(60f);
        pieChart.setTransparentCircleColor(Color.WHITE);
        pieChart.setTransparentCircleAlpha(140);
        pieChart.setDrawCenterText(true);
        pieChart.setCenterTextSize(11f);
        pieChart.setCenterTextColor(Color.parseColor("#424242"));
        pieChart.getLegend().setEnabled(true);
        pieChart.setDrawEntryLabels(false);
    }

    private void loadData() {
        Calendar calendar = Calendar.getInstance();
        int currentMonth = calendar.get(Calendar.MONTH) + 1;
        int currentYear = calendar.get(Calendar.YEAR);

        // Get total for DAOExpense
        double totalIncome = daoExpense.getTotalIncomeByMonth(currentUserId, currentMonth, currentYear);
        double totalExpense = daoExpense.getTotalExpenseByMonth(currentUserId, currentMonth, currentYear);

        DecimalFormat decimalFormat = new DecimalFormat("#,##0.0");
        tvTotalIncome.setText("$" + decimalFormat.format(totalIncome));
        tvTotalExpense.setText("$" + decimalFormat.format(totalExpense));
        double availableBalance = totalIncome - totalExpense;
        tvAvailableBalance.setText("$" + decimalFormat.format(availableBalance));
        if (availableBalance < 0) {
            tvAvailableBalance.setTextColor(Color.parseColor("#F44336")); // Red
        } else {
            tvAvailableBalance.setTextColor(Color.parseColor("#4CAF50")); // Green
        }

        updateChartData(totalIncome, totalExpense);

        loadRecentTransactions();
    }

    private void updateChartData(double income, double expense) {
        ArrayList<PieEntry> entries = new ArrayList<>();
        ArrayList<Integer> colors = new ArrayList<>();

        PieDataSet dataSet = new PieDataSet(entries, "");
        PieData data = new PieData(dataSet);

        if (income == 0 && expense == 0) {
            entries.add(new PieEntry(1f, ""));
            colors.add(Color.LTGRAY);
            pieChart.setUsePercentValues(false);
            pieChart.setCenterText("No Data");
            data.setDrawValues(false);
        } else {
            pieChart.setUsePercentValues(true);
            pieChart.setCenterText("Income - Expense");

            if (income > 0) {
                entries.add(new PieEntry((float) income, "Income"));
                colors.add(Color.parseColor("#4CAF50")); // Green
            }
            if (expense > 0) {
                entries.add(new PieEntry((float) expense, "Expense"));
                colors.add(Color.parseColor("#F44336")); // Red
            }
            data.setValueFormatter(new PercentFormatter(pieChart));
            data.setDrawValues(true);
        }

        dataSet.setColors(colors);
        dataSet.setSliceSpace(2f);
        dataSet.setValueTextSize(12f);
        dataSet.setValueTextColor(Color.WHITE);

        pieChart.setData(data);
        pieChart.invalidate();
        pieChart.animateY(1000);
    }

    private void loadRecentTransactions() {
        List<objExpense> transactions = daoExpense.getRecentTransactions(currentUserId, 10);
        recentTransactionsList.clear();
        recentTransactionsList.addAll(transactions);

        if (recentTransactionsList.isEmpty()) {
            tvRecentTransactions.setText("No transactions yet");
            recyclerView.setVisibility(View.GONE);
        } else {
            tvRecentTransactions.setText("Recent Transactions");
            recyclerView.setVisibility(View.VISIBLE);
            if(getContext() != null) {
               adapter = new TransactionAdapter(getContext(), recentTransactionsList);
               recyclerView.setAdapter(adapter);
            }
        }
    }

    public void refreshList() {
        if (daoExpense != null) {
            loadData();
            if (adapter != null) {
                if(getContext() != null) {
                    adapter = new TransactionAdapter(getContext(), recentTransactionsList);
                    recyclerView.setAdapter(adapter);
                }
                adapter.notifyDataSetChanged();
            }
        }
    }
}
