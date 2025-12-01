package com.example.expensemanager;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.expensemanager.Data.DAOUser;
import com.example.expensemanager.model.objUser;

public class LoginActivity extends AppCompatActivity {

    EditText edUsername, edPassword;
    Button btnLogin, btnRegister;
    TextView tvForgotPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

        edUsername = (EditText)  findViewById(R.id.edUsername);
        edPassword = (EditText)  findViewById(R.id.edPassword);
        btnLogin = (Button) findViewById(R.id.btnLogin);
        btnRegister = (Button) findViewById(R.id.btnRegister);
        tvForgotPassword = (TextView) findViewById(R.id.tvForgotPassword);

        tvForgotPassword.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, ForgotPasswordActivity.class);
            startActivity(intent);
        });

        btnRegister.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });


        btnLogin.setOnClickListener(v->{
            String user = edUsername.getText().toString();
            String password = edPassword.getText().toString();

            if (user.isEmpty() || password.isEmpty()) {
                FunctionRecycle.showAlert(this,
                        "Error",
                        "Please fill in all fields");
                return;
            }

            DAOUser db = new DAOUser(this);
            objUser obj = db.getUserByEmail(user, password);

            if (obj != null) {
                // login successfully
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                intent.putExtra("idUser", obj.getId());
                startActivity(intent);
                finish();
            } else {
                // login failed
                FunctionRecycle.showAlert(this,
                        "Error",
                        "Please check your Username and Password again!");
            }
        });


    }
}
