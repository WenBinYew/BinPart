package com.example.han.tartalk;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity {

    private EditText nameField;
    private EditText emailField;
    private EditText passwordField;
    private Button registerButton;
    private FirebaseAuth auth;
    private ProgressDialog progress;
    private DatabaseReference database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        nameField = (EditText) findViewById(R.id.txtViewNameRegister);
        emailField = (EditText) findViewById(R.id.txtViewEmailRegister);
        passwordField = (EditText) findViewById(R.id.txtViewPasswordRegister);
        registerButton = (Button) findViewById(R.id.btnRegister);
        auth = FirebaseAuth.getInstance();
        progress = new ProgressDialog(this);
        database = FirebaseDatabase.getInstance().getReference().child("Users");

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startRegister();
            }
        });



    }

    private void startRegister() {

        final String name = nameField.getText().toString().trim();
        String email = emailField.getText().toString().trim();
        String password = passwordField.getText().toString().trim();

        if(!TextUtils.isEmpty(name) && !TextUtils.isEmpty(email) && !TextUtils.isEmpty(password)){
            progress.setMessage("Signing Up");
            progress.show();
            auth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()){

                        String user_id = auth.getCurrentUser().getUid();
                        DatabaseReference current_user_db = database.child(user_id);
                        current_user_db.child("Name").setValue(name);
                        current_user_db.child("Image").setValue("default");

                        progress.dismiss();

                        Intent mainIntent = new Intent(RegisterActivity.this, MainActivity.class);
                        mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        finish();
                        startActivity(mainIntent);

                    }else{
                        Toast.makeText(RegisterActivity.this, "Could not register. please try again. ", Toast.LENGTH_SHORT).show();

                    }
                }
            });
        }
    }
}
