package com.example.expensemanager.Adapter;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.expensemanager.Data.DAOExpense;
import com.example.expensemanager.R;
import com.example.expensemanager.model.objExpense;
import com.google.android.material.textfield.TextInputEditText;

import java.text.NumberFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.ExpenseViewHolder> {

    private Context context;
    private List<objExpense> expenseList;
    private DAOExpense daoExpense;

    // Constructor
    public TransactionAdapter(Context context, List<objExpense> expenseList) {
        this.context = context;
        this.expenseList = expenseList;
        this.daoExpense = new DAOExpense(context);
    }


    @NonNull
    @Override
    public ExpenseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_transaction, parent, false);
        return new ExpenseViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull ExpenseViewHolder holder, int position) {
        objExpense expense = expenseList.get(position);

        if (expense == null) return;

        holder.tvName.setText(expense.getNote());
        holder.tvDate.setText(expense.getDate());
        holder.tvCategory.setText(expense.getCategory());


        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("en", "US"));
        holder.tvAmount.setText(currencyFormat.format(expense.getAmount()));

        if ("Income".equalsIgnoreCase(expense.getType())) {
            holder.tvType.setText("Income");
            holder.tvType.setBackgroundColor(ContextCompat.getColor(context, R.color.income_color));
            holder.tvAmount.setTextColor(ContextCompat.getColor(context, R.color.income_color));
        } else {
            holder.tvType.setText("Expense");
            holder.tvType.setBackgroundColor(ContextCompat.getColor(context, R.color.expense_color));
            holder.tvAmount.setTextColor(ContextCompat.getColor(context, R.color.expense_color));
        }

        holder.tvType.setTextColor(Color.WHITE);

        // Sự kiện click vào nút menu (mũi tên)
        holder.btnMore.setOnClickListener(v -> {
            showPopupMenu(v, expense, position);
        });
    }

    @Override
    public int getItemCount() {
        if (expenseList != null) {
            return expenseList.size();
        }
        return 0;
    }

    // Hiển thị popup menu với 2 option: Cập nhật và Xóa
    private void showPopupMenu(View v, objExpense expense, int position) {
        PopupMenu popupMenu = new PopupMenu(context, v);
        popupMenu.getMenuInflater().inflate(R.menu.menu_transaction, popupMenu.getMenu());

        popupMenu.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.menu_edit) {
                showEditDialog(expense, position);
                return true;
            } else if (item.getItemId() == R.id.menu_delete) {
                showDeleteConfirmDialog(expense, position);
                return true;
            }
            return false;
        });

        popupMenu.show();
    }

    // Hiển thị dialog để cập nhật giao dịch (sử dụng lại layout add_transaction)
    private void showEditDialog(objExpense expense, int position) {
        Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_add_transaction);

        Window window = dialog.getWindow();
        if (window != null) {
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            window.setDimAmount(0.5f);
            window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        }

        // Find views
        TextView tvTitle = dialog.findViewById(R.id.tv_dialog_title);
        RadioGroup rgType = dialog.findViewById(R.id.rgType);
        Spinner spnCategory = dialog.findViewById(R.id.spnCategory);
        TextInputEditText edAmount = dialog.findViewById(R.id.edAmount);
        TextInputEditText edDate = dialog.findViewById(R.id.edDate);
        TextInputEditText edNote = dialog.findViewById(R.id.edNote);
        Button btnSubmit = dialog.findViewById(R.id.btnAddTransaction);

        // Set title
        if (tvTitle != null) {
            tvTitle.setText("Update Transaction");
        }

        // Set current data
        if (edAmount != null) edAmount.setText(String.valueOf(expense.getAmount()));
        if (edDate != null) edDate.setText(expense.getDate());
        if (edNote != null) edNote.setText(expense.getNote());


        List<String> expenseCategoryList = Arrays.asList("Food & Drinks", "Gasoline & Transportation", "Entertainment", "Groceries", "Monthly utility bills");
        List<String> incomeCategoryList = Arrays.asList("Salary", "Bonus", "Interest");

        ArrayAdapter<String> expenseAdapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, expenseCategoryList);
        expenseAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        ArrayAdapter<String> incomeAdapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, incomeCategoryList);
        incomeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Set radio button based on current type
        if (rgType != null) {
            if ("Income".equals(expense.getType())) {
                rgType.check(R.id.rbIncome);
                if (spnCategory != null) spnCategory.setAdapter(incomeAdapter);
            } else {
                rgType.check(R.id.rbExpense);
                if (spnCategory != null) spnCategory.setAdapter(expenseAdapter);
            }
        }

        // Set category selection
        List<String> categories = "Income".equals(expense.getType()) ? incomeCategoryList : expenseCategoryList;
        int categoryIndex = categories.indexOf(expense.getCategory());
        if (categoryIndex >= 0 && spnCategory != null) {
            spnCategory.setSelection(categoryIndex);
        }

        // Handle type change
        if (rgType != null) {
            rgType.setOnCheckedChangeListener((group, checkedId) -> {
                if (spnCategory != null) {
                    if (checkedId == R.id.rbExpense) {
                        spnCategory.setAdapter(expenseAdapter);
                    } else {
                        spnCategory.setAdapter(incomeAdapter);
                    }
                }
            });
        }

        // Set up DatePicker for Date field
        if (edDate != null) {
            edDate.setOnClickListener(v -> showDatePickerDialog(edDate));
        }

        // Set button text and action
        if (btnSubmit != null) {
            btnSubmit.setText("Update");
            btnSubmit.setOnClickListener(v -> {
                String type = (rgType != null && rgType.getCheckedRadioButtonId() == R.id.rbExpense) ? "Expense" : "Income";
                String category = (spnCategory != null) ? spnCategory.getSelectedItem().toString() : "";
                String strAmount = (edAmount != null) ? edAmount.getText().toString().trim() : "";
                String date = (edDate != null) ? edDate.getText().toString() : "";
                String note = (edNote != null) ? edNote.getText().toString() : "";

                if (strAmount.isEmpty()) {
                    if (edAmount != null) edAmount.setError("Please enter amount");
                    return;
                }

                try {
                    double amount = Double.parseDouble(strAmount);
                    expense.setAmount(amount);
                    expense.setDate(date);
                    expense.setNote(note);
                    expense.setCategory(category);
                    expense.setType(type);

                    int result = daoExpense.updateExpense(expense);
                    if (result > 0) {
                        Toast.makeText(context, "Transaction updated successfully!", Toast.LENGTH_SHORT).show();
                        expenseList.set(position, expense);
                        notifyItemChanged(position);
                        dialog.dismiss();
                    } else {
                        Toast.makeText(context, "Failed to update transaction!", Toast.LENGTH_SHORT).show();
                    }
                } catch (NumberFormatException e) {
                    if (edAmount != null) edAmount.setError("Invalid amount format");
                } catch (Exception e) {
                    Toast.makeText(context, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }

        dialog.setCancelable(true);
        dialog.show();
    }

    // Hiển thị dialog xác nhận xóa
    private void showDeleteConfirmDialog(objExpense expense, int position) {
        new AlertDialog.Builder(context)
                .setTitle("Delete Transaction")
                .setMessage("Are you sure you want to delete this transaction?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    int result = daoExpense.deleteExpense(expense.getId());
                    if (result > 0) {
                        Toast.makeText(context, "Transaction deleted successfully!", Toast.LENGTH_SHORT).show();
                        expenseList.remove(position);
                        notifyItemRemoved(position);
                        notifyItemRangeChanged(position, expenseList.size());
                    } else {
                        Toast.makeText(context, "Failed to delete transaction!", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                .show();
    }

    // ViewHolder class
    public class ExpenseViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvDate, tvAmount, tvType, tvCategory;
        ImageButton btnMore;

        public ExpenseViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tv_expense_name);
            tvDate = itemView.findViewById(R.id.tv_expense_date);
            tvAmount = itemView.findViewById(R.id.tv_expense_amount);
            tvType = itemView.findViewById(R.id.tv_expense_type);
            tvCategory = itemView.findViewById(R.id.tv_expense_category);
            btnMore = itemView.findViewById(R.id.btn_option_edit);
        }
    }

    // Method to show DatePickerDialog
    private void showDatePickerDialog(TextInputEditText edDate) {
        Calendar calendar = Calendar.getInstance();

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                context,
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
