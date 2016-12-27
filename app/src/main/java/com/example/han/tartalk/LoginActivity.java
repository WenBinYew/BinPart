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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity {

    private EditText emailField;
    private EditText passwordField;
    private Button btnLogin;

    private FirebaseAuth auth;
    private DatabaseReference database;
    private ProgressDialog progress;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        emailField = (EditText) findViewById(R.id.txtViewEmailLogin);
        passwordField = (EditText) findViewById(R.id.txtViewPasswordLogin);
        btnLogin = (Button) findViewById(R.id.btnLogin);
        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance().getReference().child("Users");
        progress = new ProgressDialog(this);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progress.setMessage("Login in");
                progress.show();
                checkLogin();
            }
        });
    }

    private void checkLogin() {

        String email = emailField.getText().toString().trim();
        String password = passwordField.getText().toString().trim();

        if (!TextUtils.isEmpty(email) && !TextUtils.isEmpty(password)) {
            auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        progress.dismiss();
                        checkUserExist();
                    } else {
                        Toast.makeText(LoginActivity.this, "Error Login", Toast.LENGTH_LONG).show();
                    }
                }
            });
        }

    }

    private void checkUserExist() {
        final String user_id = auth.getCurrentUser().getUid();


        database.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild(user_id)) {


                    Intent mainIntent = new Intent(LoginActivity.this, MainActivity.class);
                    mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(mainIntent);

                } else {
                    Toast.makeText(LoginActivity.this, "Error login", Toast.LENGTH_LONG).show();

                }
            }


            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }
}
