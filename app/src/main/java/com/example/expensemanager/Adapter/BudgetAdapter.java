package com.example.expensemanager.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.expensemanager.Data.DAOExpense;
import com.example.expensemanager.R;
import com.example.expensemanager.model.objBudget;
import com.example.expensemanager.model.objExpense;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class BudgetAdapter extends RecyclerView.Adapter<BudgetAdapter.BudgetViewHolder> {

    private Context context;
    private List<objBudget> budgetList;
    private OnBudgetEditListener editListener;
    private OnBudgetDeleteListener deleteListener;
    private DAOExpense daoExpense; // DAO để truy vấn chi tiêu

    // Interfaces for listeners
    public interface OnBudgetEditListener {
        void onEditBudget(objBudget budget);
    }

    public interface OnBudgetDeleteListener {
        void onDeleteBudget(objBudget budget);
    }

    // FIX: Updated constructor to accept both listeners explicitly
    public BudgetAdapter(Context context, List<objBudget> budgetList, OnBudgetEditListener editListener, OnBudgetDeleteListener deleteListener) {
        this.context = context;
        this.budgetList = budgetList;
        this.editListener = editListener;
        this.deleteListener = deleteListener;
        this.daoExpense = new DAOExpense(context); // Khởi tạo DAOExpense
    }

    @NonNull
    @Override
    public BudgetViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_budget, parent, false);
        return new BudgetViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BudgetViewHolder holder, int position) {
        objBudget budget = budgetList.get(position);
        if (budget == null) return;

        // Get percent to load on the process bar
        double spentAmount = calculateSpentAmount(budget.getIdUser(), budget.getCategory(), budget.getStartDate(), budget.getEndDate());
        double budgetAmount = budget.getBudgetAmount();
        double remainingAmount = budgetAmount - spentAmount;
        int progress = (budgetAmount > 0) ? (int) ((spentAmount / budgetAmount) * 100) : 0;

        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("en", "US"));

        holder.tvCategory.setText(budget.getCategory());
        holder.tvBudgetAmount.setText(currencyFormat.format(budgetAmount));
        holder.tvSpent.setText(currencyFormat.format(spentAmount));
        holder.tvRemaining.setText(currencyFormat.format(remainingAmount));
        holder.tvPercentage.setText(String.format(Locale.getDefault(), "%d%%", progress));
        holder.pbProgress.setProgress(progress);

        holder.btnEdit.setOnClickListener(v -> {
            if (editListener != null) {
                editListener.onEditBudget(budget);
            }
        });

        holder.btnDelete.setOnClickListener(v -> {
            if (deleteListener != null) {
                deleteListener.onDeleteBudget(budget);
            }
        });
    }

    @Override
    public int getItemCount() {
        return budgetList != null ? budgetList.size() : 0;
    }

    private double calculateSpentAmount(int idUser, String category, String startDate, String endDate) {
        double totalSpent = 0.0;
        
        List<objExpense> userExpenses = new ArrayList<>();
        int startYear = Integer.parseInt(startDate.substring(0, 4));
        int endYear = Integer.parseInt(endDate.substring(0, 4));

        // Get expenses for all years within the budget's date range
        for (int year = startYear; year <= endYear; year++) {
            userExpenses.addAll(daoExpense.getExpensesByUser(idUser, "Expense", year));
        }

        if (userExpenses.isEmpty()) {
            return 0.0;
        }

        for (objExpense expense : userExpenses) {
            // Check if expense date is within budget date range
            if (expense.getDate().compareTo(startDate) >= 0 && expense.getDate().compareTo(endDate) <= 0) {
                if ("All".equalsIgnoreCase(category)) {
                    totalSpent += expense.getAmount();
                } else if (category.equalsIgnoreCase(expense.getCategory())) {
                    totalSpent += expense.getAmount();
                }
            }
        }
        return totalSpent;
    }

    public static class BudgetViewHolder extends RecyclerView.ViewHolder {
        TextView tvCategory, tvBudgetAmount, tvSpent, tvRemaining, tvPercentage;
        ProgressBar pbProgress;
        Button btnEdit, btnDelete;

        public BudgetViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCategory = itemView.findViewById(R.id.tv_budget_category);
            tvBudgetAmount = itemView.findViewById(R.id.tv_budget_amount);
            tvSpent = itemView.findViewById(R.id.tv_budget_spent);
            tvRemaining = itemView.findViewById(R.id.tv_budget_remaining);
            tvPercentage = itemView.findViewById(R.id.tv_budget_percentage);
            pbProgress = itemView.findViewById(R.id.pb_budget_progress);
            btnEdit = itemView.findViewById(R.id.btn_budget_edit);
            btnDelete = itemView.findViewById(R.id.btn_budget_delete);
        }
    }
}
