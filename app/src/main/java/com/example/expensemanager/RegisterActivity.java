package com.example.expensemanager;

import static com.example.expensemanager.FunctionRecycle.showAlert;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.expensemanager.Data.DAOUser;
import com.example.expensemanager.model.objUser;

public class RegisterActivity extends AppCompatActivity {

    EditText edUsernameReg, edUserbirthday, edPhone, edEmail, edPassRegister, edConfirmPass, edPin;
    Button btnRegisterForm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);

        edUsernameReg = findViewById(R.id.edUsernameReg);
        edUserbirthday = findViewById(R.id.edUserbirthday);
        edPhone = findViewById(R.id.edPhone);
        edEmail = findViewById(R.id.edEmail);
        edPassRegister = findViewById(R.id.edPassRegister);
        edConfirmPass = findViewById(R.id.edConfirmPass);
        edPin = findViewById(R.id.edPin); // Add this line
        btnRegisterForm = findViewById(R.id.btnRegisterForm);

        btnRegisterForm.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Register User");
            builder.setMessage("Confirm register user ?");
            builder.setPositiveButton("Save", (dialog, which) -> {

                String name = edUsernameReg.getText().toString();
                String yearAge = edUserbirthday.getText().toString();
                String phone = edPhone.getText().toString();
                String email = edEmail.getText().toString();
                String pass = edPassRegister.getText().toString();
                String passConfirm = edConfirmPass.getText().toString();
                String pin = edPin.getText().toString(); // Add this line

                if (name.isEmpty()
                        || yearAge.isEmpty()
                        || phone.isEmpty()
                        || email.isEmpty()
                        || pass.isEmpty()
                        || passConfirm.isEmpty()
                        || pin.isEmpty()) { // Add this check
                    showAlert(this, "Error", "Please fill in all fields");
                    return;
                }
                if (!pass.equals(passConfirm)) {
                    showAlert(this, "Error", "Password and confirm password do not match");
                    return;
                }
                if (pin.length() != 4) { // Check the pin code
                    showAlert(this, "Error", "PIN must be 4 digits");
                    return;
                }

                objUser obj = new objUser();
                obj.setName(name);
                obj.setAge(Integer.parseInt(yearAge));
                obj.setPhone(Integer.parseInt(phone));
                obj.setEmail(email);
                obj.setPassword(pass);
                obj.setPin(pin); // Add this line

                DAOUser dao = new DAOUser(this);
                dao.insertUser(obj);
                showAlert(this, "Success", "Register User Success");

                Intent intent = new Intent(this, LoginActivity.class);
                startActivity(intent);

            });

            builder.setNegativeButton("Cancel", (dialog, which) -> {
            });
            builder.setCancelable(false);
            builder.show();

        });
    }
}
