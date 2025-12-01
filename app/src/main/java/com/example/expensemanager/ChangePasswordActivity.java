package com.example.expensemanager;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.expensemanager.Data.DAOUser;
import com.example.expensemanager.model.objUser;

public class ChangePasswordActivity extends AppCompatActivity {
    EditText oldPass, edNewPass, edPassConfirm;
    Button btnSubmitChange;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_change_password);
        oldPass = (EditText) findViewById(R.id.edCurrentPass);
        edNewPass = (EditText) findViewById(R.id.edNewPass);
        edPassConfirm = (EditText) findViewById(R.id.edConfirmNewPass);
        btnSubmitChange = (Button) findViewById(R.id.btnSubmitChange);

        int iduser = getIntent().getIntExtra("idUser", 0);
        DAOUser db = new DAOUser(this);
        objUser obj = db.getUserByID(iduser);

        btnSubmitChange.setOnClickListener(v -> {

            if (!oldPass.getText().toString().equals(obj.getPassword())) {
                FunctionRecycle.showAlert(this, "Error", "Please check your Current Password again!");
                return;
            }
            if (!edNewPass.getText().toString().equals(edPassConfirm.getText().toString())) {
                FunctionRecycle.showAlert(this, "Error", "New Password and Confirm Password do not match");
                return;
            }
            obj.setPassword(edNewPass.getText().toString());
            db.updatePassword(obj);
            FunctionRecycle.showAlert(this, "Success", "Change Password Success");
            //return login activity
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);

        });

    }
}