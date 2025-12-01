package com.example.expensemanager;

import static com.example.expensemanager.FunctionRecycle.showAlert;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.expensemanager.Data.DAOUser;
import com.example.expensemanager.Data.DatabaseHelper;
import com.example.expensemanager.model.objUser;


public class UserInfoActivity extends AppCompatActivity {

    EditText edUsernameReg, edUserbirthday, edPhone, edEmail;
    Button btnSaveNewUInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_userinfo);

        edUsernameReg = (EditText) findViewById(R.id.edUsernameReg);
        edUserbirthday = (EditText) findViewById(R.id.edUserbirthday);
        edPhone = (EditText) findViewById(R.id.edPhone);
        edEmail = (EditText) findViewById(R.id.edEmail);
        btnSaveNewUInfo = (Button) findViewById(R.id.btnSaveNewUInfo);


        DAOUser db = new DAOUser(this);
        int idUser = getIntent().getIntExtra("idUser", 0);
        objUser obj = db.getUserByID(idUser);

        if (obj == null) {
            showAlert(this, "Error", "Could not load user data.");
            Log.e("UserInfoActivity", "User with ID " + idUser + " not found.");
            finish(); // Close the activity if the user is not found
            return;   // Stop executing the rest of the code in onCreate
        }


        edUsernameReg.setText(obj.getName());
        edUserbirthday.setText(String.valueOf(obj.getAge()));
        edPhone.setText(String.valueOf(obj.getPhone()));
        edEmail.setText(obj.getEmail());


        btnSaveNewUInfo.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Update Information");
            builder.setMessage("Confirm update this user?");
            builder.setPositiveButton("Save", (dialog, which) -> {
                obj.setName(edUsernameReg.getText().toString());
                obj.setAge(Integer.parseInt(edUserbirthday.getText().toString()));
                obj.setPhone(Integer.parseInt(edPhone.getText().toString()));
                db.updateUser(obj);
                finish();

            });
            builder.setNegativeButton("Cancel", (dialog, which) -> {
            });
            builder.setCancelable(false);
            builder.show();

        });
    }
}
