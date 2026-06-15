package com.example.expensemanager;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.GravityCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.expensemanager.Data.DAOBudget;
import com.example.expensemanager.Data.DAOExpense;
import com.example.expensemanager.Data.DAOFixedExpense;
import com.example.expensemanager.Data.DAOUser;
import com.example.expensemanager.fragments.ExpenseFragment;
import com.example.expensemanager.fragments.HomeFragment;
import com.example.expensemanager.fragments.IncomeFragment;
import com.example.expensemanager.fragments.ReportFragment;
import com.example.expensemanager.fragments.BudgetFragment;
import com.example.expensemanager.model.objExpense;
import com.example.expensemanager.model.objFixedExpense;
import com.example.expensemanager.model.objUser;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.navigation.NavigationView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        NavigationBarView.OnItemSelectedListener {

    DAOUser db;
    Toolbar toolbar;
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    BottomNavigationView bottomNavigationView;
    FloatingActionButton fabAdd;
    private int currentIdUser;

    private boolean shouldShowAlertIcon = false;

    private ActivityResultLauncher<Intent> addTransactionLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        db = new DAOUser(this);

        toolbar = findViewById(R.id.tool_bar);
        bottomNavigationView = findViewById(R.id.bottomNav);
        drawerLayout = findViewById(R.id.main);
        navigationView = findViewById(R.id.nav_view);
        fabAdd = findViewById(R.id.fabAdd);

        setSupportActionBar(toolbar);

        if (getIntent() != null && getIntent().hasExtra("idUser")) {
            currentIdUser = getIntent().getIntExtra("idUser", 0);
        }

        if (currentIdUser != 0) {
            processRecurringExpenses();
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, 0, systemBars.right, 0);
            toolbar.setPadding(0, systemBars.top, 0, 0);
            bottomNavigationView.setPadding(0, 0, 0, systemBars.bottom);
            return insets;
        });

        if (savedInstanceState == null) {
            loadFragment(HomeFragment.newInstance(currentIdUser));
            bottomNavigationView.setSelectedItemId(R.id.nav_home);
        }

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar,
                R.string.OpenDrawer,
                R.string.CloseDrawer);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        bottomNavigationView.setOnItemSelectedListener(this);
        navigationView.setNavigationItemSelectedListener(this);

        addTransactionLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        refreshCurrentFragment();
                        updateBadgeCount();
                    }
                });

        fabAdd.setOnClickListener(view -> {
            Intent intent = new Intent(this, AddTransactionActivity.class);
            intent.putExtra("idUser", currentIdUser);
            addTransactionLauncher.launch(intent);
        });
    }

    private void processRecurringExpenses() {
        DAOFixedExpense daoFixed = new DAOFixedExpense(this);
        DAOExpense daoExpense = new DAOExpense(this);
        List<objFixedExpense> fixedExpenses = daoFixed.getAllFixedExpenses(currentIdUser);

        if (fixedExpenses == null || fixedExpenses.isEmpty()) {
            return;
        }

        Calendar calendar = Calendar.getInstance();
        int currentDay = calendar.get(Calendar.DAY_OF_MONTH);
        int currentMonth = calendar.get(Calendar.MONTH) + 1;
        int currentYear = calendar.get(Calendar.YEAR);

        // Add a recurring expense for that month base on the day of month
        for (objFixedExpense fixed : fixedExpenses) {
            if (currentDay >= fixed.getDayOfMonth()) {
                if (fixed.getLastAddedYear() < currentYear || (fixed.getLastAddedYear() == currentYear && fixed.getLastAddedMonth() < currentMonth)) {
                    objExpense newExpense = new objExpense();
                    newExpense.setIdUser(currentIdUser);
                    newExpense.setAmount(fixed.getAmount());
                    newExpense.setCategory(fixed.getCategory());
                    newExpense.setNote("Recurring: " + fixed.getNote());
                    newExpense.setType("Expense");

                    String date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
                    newExpense.setDate(date);

                    daoExpense.insertExpense(newExpense);

                    fixed.setLastAddedMonth(currentMonth);
                    fixed.setLastAddedYear(currentYear);
                    daoFixed.updateFixedExpense(fixed);
                }
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_notification, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem budgetAlertItem = menu.findItem(R.id.mnBudgetAlert);
        if (budgetAlertItem != null) {
            budgetAlertItem.setVisible(shouldShowAlertIcon);
        }
        return super.onPrepareOptionsMenu(menu);
    }

    public void updateBadgeCount() {
        if (currentIdUser == 0) return;

        try {
            DAOBudget daoBudget = new DAOBudget(this);
            List<Map<String, Object>> alerts = daoBudget.getBudgetsExceeding80Percent(currentIdUser, this);

            boolean hasAlerts = (alerts != null && !alerts.isEmpty());

            if (shouldShowAlertIcon != hasAlerts) {
                shouldShowAlertIcon = hasAlerts;
                invalidateOptionsMenu();
                if (hasAlerts) {
                    showBudgetAlertDialog();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    private void refreshCurrentFragment() {
        Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        if (currentFragment instanceof HomeFragment) {
            ((HomeFragment) currentFragment).refreshList();
        } else if (currentFragment instanceof IncomeFragment) {
            ((IncomeFragment) currentFragment).refreshList();
        } else if (currentFragment instanceof ExpenseFragment) {
            ((ExpenseFragment) currentFragment).refreshList();
        } else if (currentFragment instanceof ReportFragment) {
            ((ReportFragment) currentFragment).refreshList();
        }
        updateBadgeCount();
    }

    @Override
    public void onResume() {
        super.onResume();
        int idUser = getIntent().getIntExtra("idUser", 0);
        currentIdUser = idUser;
        objUser obj = db.getUserByID(idUser);
        if (obj != null) { toolbar.setTitle("Welcome " + obj.getName() + "!"); }
        updateBadgeCount();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.nav_home || id == R.id.nav_expense || id == R.id.nav_income || id == R.id.nav_budget || id == R.id.nav_report) {
            return handleBottomNav(id);
        }
        if (id == R.id.mnUserInfo) {
            startActivity(new Intent(this, UserInfoActivity.class).putExtra("idUser", currentIdUser));
        } else if (id == R.id.mnUserGuide) {
            startActivity(new Intent(this, UserGuideActivity.class).putExtra("idUser", currentIdUser));
        } else if (id == R.id.mnChangePass) {
            startActivity(new Intent(this, ChangePasswordActivity.class).putExtra("idUser", currentIdUser));
        } else if (id == R.id.mnLogout) {
            FunctionRecycle.showAlert(this, "Logout", "Logout Successfully");
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }


    private void showBudgetAlertDialog() {
        DAOBudget daoBudget = new DAOBudget(this);
        List<Map<String, Object>> alerts = daoBudget.getBudgetsExceeding80Percent(currentIdUser, this);

        if (alerts == null || alerts.isEmpty()) {
            FunctionRecycle.showAlert(this, "BUDGET STATUS", "No budget alerts at this time.");
            return;
        }

        StringBuilder alertMessage = new StringBuilder();
        for (Map<String, Object> alert : alerts) {
            String category = (String) alert.get("category");
            double budgetAmount = (double) alert.get("budgetAmount");
            double spentAmount = (double) alert.get("spentAmount");
            int percentage = (int) alert.get("percentage");
            alertMessage.append("").append(category).append("\n");
                alertMessage.append("Budget: $").append(String.format("%.1f", budgetAmount)).append("");
            alertMessage.append("\nSpent: $").append(String.format("%.1f", spentAmount)).append(" (").append(percentage).append("%) \n\n");
        }

        FunctionRecycle.showAlert(this, "🚨 OVER BUDGET!!!", alertMessage.toString().trim());
    }

    private boolean handleBottomNav(int id) {
        Fragment fragment = null;
        if (id == R.id.nav_home) {
            fragment = HomeFragment.newInstance(currentIdUser);
        } else if (id == R.id.nav_expense) {
            fragment = new ExpenseFragment();
        } else if (id == R.id.nav_income) {
            fragment = IncomeFragment.newInstance(currentIdUser);
        } else if (id == R.id.nav_budget) {
            fragment = new BudgetFragment();
        } else if (id == R.id.nav_report) {
            fragment = ReportFragment.newInstance(currentIdUser);
        }
        if (fragment != null) {
            loadFragment(fragment);
            return true;
        }
        return false;
    }

    public boolean onItemSelected(@NonNull MenuItem item) {
        return onNavigationItemSelected(item);
    }
}
