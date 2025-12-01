package com.example.expensemanager;

import android.os.Bundle;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.expensemanager.Data.DAOBudget;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class BudgetAlertActivity extends AppCompatActivity {

    private ListView lvAlerts;
    private TextView tvNoAlerts;
    private int idUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_budget_notification);

        lvAlerts = findViewById(R.id.lv_budget_alerts);
        tvNoAlerts = findViewById(R.id.tv_no_alerts);

        if (getIntent() != null && getIntent().hasExtra("idUser")) {
            idUser = getIntent().getIntExtra("idUser", 0);
        }

        loadAlerts();
    }

    private void loadAlerts() {
        DAOBudget daoBudget = new DAOBudget(this);
        List<Map<String, Object>> alerts = daoBudget.getBudgetsExceeding90Percent(idUser, this);

        if (alerts == null || alerts.isEmpty()) {
            lvAlerts.setVisibility(ListView.GONE);
            tvNoAlerts.setVisibility(TextView.VISIBLE);
            tvNoAlerts.setText("No budget alerts");
            return;
        }

        lvAlerts.setVisibility(ListView.VISIBLE);
        tvNoAlerts.setVisibility(TextView.GONE);

        List<String> alertStrings = new ArrayList<>();
        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("en", "US"));

        for (Map<String, Object> alert : alerts) {
            String category = (String) alert.get("category");
            double spentAmount = (double) alert.get("spentAmount");
            double budgetAmount = (double) alert.get("budgetAmount");
            int percentage = (int) alert.get("percentage");

            String alertText = String.format(
                    "%s\nBudget: %s | Spent: %s (%d%%)",
                    category,
                    currencyFormat.format(budgetAmount),
                    currencyFormat.format(spentAmount),
                    percentage
            );
            alertStrings.add(alertText);
        }

        lvAlerts.setAdapter(new android.widget.ArrayAdapter<>(
                this,
                android.R.layout.simple_list_item_1,
                alertStrings
        ));
    }
}

