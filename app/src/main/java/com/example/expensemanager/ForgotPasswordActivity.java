package com.example.expensemanager;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.expensemanager.Data.DAOUser;
import com.example.expensemanager.model.objUser;

public class ForgotPasswordActivity extends AppCompatActivity {

    EditText edEmail, edNewPassword, edConfirmPassword;
    Button btnReset, btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_forgot_password);

        edEmail = findViewById(R.id.edEmail);
        edNewPassword = findViewById(R.id.edNewPassword);
        edConfirmPassword = findViewById(R.id.edConfirmPassword);
        btnReset = findViewById(R.id.btnReset);
        btnBack = findViewById(R.id.btnBack);

        btnBack.setOnClickListener(v -> {
            finish();
        });

        btnReset.setOnClickListener(v -> {
            String email = edEmail.getText().toString().trim();
            String newPassword = edNewPassword.getText().toString().trim();
            String confirmPassword = edConfirmPassword.getText().toString().trim();

            // Validation
            if (email.isEmpty()) {
                FunctionRecycle.showAlert(this, "Error", "Please enter your email");
                return;
            }

            if (newPassword.isEmpty() || confirmPassword.isEmpty()) {
                FunctionRecycle.showAlert(this, "Error", "Please enter new password and confirm password");
                return;
            }

            if (newPassword.length() < 3) {
                FunctionRecycle.showAlert(this, "Error", "Password must be at least 3 characters long");
                return;
            }

            if (!newPassword.equals(confirmPassword)) {
                FunctionRecycle.showAlert(this, "Error", "Password and Confirm Password do not match");
                return;
            }

            // Check if email exists in database
            DAOUser daoUser = new DAOUser(this);
            objUser user = daoUser.getUserByEmail(email);

            if (user == null) {
                FunctionRecycle.showAlert(this, "Error", "Email not found in the system");
                return;
            }

            // Update password
            boolean updateSuccess = daoUser.updatePasswordByEmail(email, newPassword);

            if (updateSuccess) {
                FunctionRecycle.showAlert(this, "Success", "Password has been reset successfully");
                // Redirect to login
                Intent intent = new Intent(ForgotPasswordActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            } else {
                FunctionRecycle.showAlert(this, "Error", "Failed to reset password. Please try again");
            }
        });
    }
}

