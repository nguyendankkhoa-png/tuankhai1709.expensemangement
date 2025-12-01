package com.example.expensemanager.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.expensemanager.R;
import com.example.expensemanager.model.objCategoryReport;

import java.text.DecimalFormat;
import java.util.List;

public class CategoryReportAdapter extends RecyclerView.Adapter<CategoryReportAdapter.ViewHolder> {

    private List<objCategoryReport> reportList;

    public CategoryReportAdapter(List<objCategoryReport> reportList) {
        this.reportList = reportList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_category_report, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        objCategoryReport report = reportList.get(position);
        DecimalFormat df = new DecimalFormat("#,###.##");

        holder.tvCategory.setText(report.getCategory());
        holder.tvAmount.setText("$" + df.format(report.getIncome() + report.getExpense()));
    }

    @Override
    public int getItemCount() {
        return reportList.size();
    }

    public void updateList(List<objCategoryReport> newList) {
        reportList = newList;
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvCategory, tvAmount;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCategory = itemView.findViewById(R.id.tv_category);
            tvAmount = itemView.findViewById(R.id.tv_amount);
        }
    }
}

