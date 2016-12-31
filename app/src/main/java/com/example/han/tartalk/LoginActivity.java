package com.example.han.tartalk;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
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

public class LoginActivity extends AppCompatActivity implements View.OnClickListener  {

    private Button buttonSignIn;
    private Button buttonCancel;
    private EditText editTextEmail;
    private EditText editTextPassword;
    private TextView textViewRegister;
    private TextView textViewForgetPassword;

    private ProgressDialog progressDialog;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference database;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

//        if(firebaseAuth.getCurrentUser() != null){
//            finish();
//            startActivity(new Intent(getApplicationContext(),MainActivity.class));
//        }

        progressDialog = new ProgressDialog(this);


        buttonSignIn = (Button)findViewById(R.id.buttonSignIn);
        editTextEmail = (EditText)findViewById(R.id.editTextEmail);
        editTextPassword = (EditText)findViewById(R.id.editTextPassword);
        textViewRegister = (TextView)findViewById(R.id.textViewRegister);
        textViewForgetPassword = (TextView)findViewById(R.id.textViewForgetPassword);
        buttonSignIn.setOnClickListener(this);
        textViewRegister.setOnClickListener(this);
        textViewForgetPassword.setOnClickListener(this);

        firebaseAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance().getReference().child("Users");
    }

    private void userLogin(){
        final String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();

        if(TextUtils.isEmpty(email)){
            //email is empty
            Toast.makeText(this, "Please enter email", Toast.LENGTH_SHORT).show();
            return;
        }
        if(TextUtils.isEmpty(password)){
            //password is empty
            Toast.makeText(this, "Please enter password", Toast.LENGTH_SHORT).show();
            return;
        }
        progressDialog.setMessage("Loading...");
        progressDialog.show();

        firebaseAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()) {
                    progressDialog.setMessage("Login to TarTalk.");
                    progressDialog.show();
                    progressDialog.dismiss();
                    finish();

                }else{
                    Toast.makeText(LoginActivity.this, "Fail to Login. please try again. ", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                }
            }
        });
    }

    private void checkUserExist() {
        final String user_id = firebaseAuth.getCurrentUser().getUid();

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
            @Override
        public void onClick(View view) {
            if(view == buttonSignIn){
                userLogin();

            }
            if(view == textViewRegister){
                //will open login activity activity
                finish();
                startActivity(new Intent(this,SignUpActivity.class));
            }
            if(view == textViewForgetPassword){
                finish();
                startActivity(new Intent(this,ResetPasswordActivity.class));
            }

        }
}

