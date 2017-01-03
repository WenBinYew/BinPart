package com.example.han.tartalk;

import android.content.Intent;
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
import com.google.firebase.auth.FirebaseAuth;

public class ResetPasswordActivity extends AppCompatActivity implements View.OnClickListener {

    private Button buttonResetPassword;
    private Button buttonBack;
    private EditText editTextEmail;
    private FirebaseAuth firebaseAuth;
    private TextView textViewResent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        firebaseAuth = FirebaseAuth.getInstance();

        buttonResetPassword = (Button)findViewById(R.id.buttonResetPassword);
        buttonBack = (Button)findViewById(R.id.buttonBack);
        editTextEmail = (EditText)findViewById(R.id.editTextEmail);
        textViewResent = (TextView)findViewById(R.id.textViewResent);
        buttonResetPassword.setOnClickListener(this);
        buttonBack.setOnClickListener(this);
        textViewResent.setOnClickListener(this);
    }

    private void resetpassword(){
        String email = editTextEmail.getText().toString().trim();
        if(TextUtils.isEmpty(email)){
            //email is empty
            Toast.makeText(this, "Please enter email", Toast.LENGTH_SHORT).show();
            return;
        }

        firebaseAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    Toast.makeText(ResetPasswordActivity.this, "Verification link will send to " + editTextEmail.getText().toString().trim() , Toast.LENGTH_LONG).show();
                }
            }
        });

    }

    @Override
    public void onClick(View view) {
        if(view == buttonResetPassword){
            resetpassword();
        }if(view == buttonBack){
            finish();
//            startActivity(new Intent(this,LoginActivity.class));
        }if(view == textViewResent){
            resetpassword();
         }
    }
}
