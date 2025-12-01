package com.example.expensemanager.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.expensemanager.R;
import com.example.expensemanager.model.objFixedExpense;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class RecurringExpenseAdapter extends RecyclerView.Adapter<RecurringExpenseAdapter.RecurringExpenseViewHolder> {

    private Context context;
    private List<objFixedExpense> recurringExpenseList;
    private OnRecurringExpenseListener listener;

    public interface OnRecurringExpenseListener {
        void onEdit(objFixedExpense expense);
        void onDelete(objFixedExpense expense);
    }

    public RecurringExpenseAdapter(Context context, List<objFixedExpense> recurringExpenseList, OnRecurringExpenseListener listener) {
        this.context = context;
        this.recurringExpenseList = recurringExpenseList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public RecurringExpenseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_recurring_expense, parent, false);
        return new RecurringExpenseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecurringExpenseViewHolder holder, int position) {
        objFixedExpense expense = recurringExpenseList.get(position);

        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("en", "US"));

        holder.tvCategory.setText("Category: " + expense.getCategory());
        holder.tvAmount.setText(currencyFormat.format(expense.getAmount()));
        holder.tvNote.setText("Note: " + (expense.getNote() != null && !expense.getNote().isEmpty() ? expense.getNote() : "N/A"));

        holder.btnEdit.setOnClickListener(v -> {
            if (listener != null) {
                listener.onEdit(expense);
            }
        });

        holder.btnDelete.setOnClickListener(v -> {
            if (listener != null) {
                listener.onDelete(expense);
            }
        });
    }

    @Override
    public int getItemCount() {
        return recurringExpenseList != null ? recurringExpenseList.size() : 0;
    }

    public static class RecurringExpenseViewHolder extends RecyclerView.ViewHolder {
        TextView tvCategory, tvAmount, tvNote;
        Button btnEdit, btnDelete;

        public RecurringExpenseViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCategory = itemView.findViewById(R.id.tv_recurring_category);
            tvAmount = itemView.findViewById(R.id.tv_recurring_amount);
            tvNote = itemView.findViewById(R.id.tv_recurring_note);
            btnEdit = itemView.findViewById(R.id.btn_recurring_edit);
            btnDelete = itemView.findViewById(R.id.btn_recurring_delete);
        }
    }
}
