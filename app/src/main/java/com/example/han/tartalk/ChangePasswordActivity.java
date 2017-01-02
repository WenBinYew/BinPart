package com.example.han.tartalk;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.nfc.Tag;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class ChangePasswordActivity extends AppCompatActivity implements View.OnClickListener {


    private ProgressDialog progressDialog;
    private Button buttonChgPassword;
    private EditText editTextOldPassword;
    private EditText editTextNewPasswordConfirm;
    private EditText editTextNewPassword;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);


        firebaseAuth = FirebaseAuth.getInstance();

        editTextNewPassword = (EditText) findViewById(R.id.editTextNewPassword);
        editTextNewPasswordConfirm = (EditText) findViewById(R.id.editTextNewPasswordConfirm);
        buttonChgPassword = (Button) findViewById(R.id.buttonChgPassword);
        progressDialog = new ProgressDialog(this);
        buttonChgPassword.setOnClickListener(this);

        LayoutInflater li = LayoutInflater.from(this);
        View promptsView = li.inflate(R.layout.pop_up_prompt_password, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setView(promptsView);
        editTextOldPassword = (EditText) promptsView.findViewById(R.id.editTextOldPassword);

        alertDialogBuilder.setCancelable(false).setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(final DialogInterface dialog, int id) {
                chkPassword();
            }
        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                finish();
            }
        });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    private void chgPassword() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String oldPassword = editTextOldPassword.getText().toString().trim();
        String newPassword = editTextNewPassword.getText().toString().trim();
        String newPasswordConfirm = editTextNewPasswordConfirm.getText().toString().trim();

        if (TextUtils.isEmpty(oldPassword)) {
            Toast.makeText(this, "Please enter email", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(newPassword)) {
            //password is empty
            Toast.makeText(this, "Please enter password", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!(newPassword.contentEquals(newPasswordConfirm))) {
            Toast.makeText(this, "The password are not consistent. Please try again.", Toast.LENGTH_SHORT).show();
            return;
        }
//        AuthCredential credential = EmailAuthProvider
//                .getCredential(firebaseAuth.getCurrentUser().getEmail(),newPassword);

        user.updatePassword(newPassword).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(getApplicationContext(), "Complete!!", Toast.LENGTH_SHORT).show();
                            finish();
                        }else{
                            Toast.makeText(getApplicationContext(), "Fail!!", Toast.LENGTH_SHORT).show();
                        }

                    }
                });
        //Toast.makeText(getApplicationContext(), "Fail!!", Toast.LENGTH_SHORT).show();
    }

    public void chkPassword() {


        progressDialog.setMessage("Loading...");
        progressDialog.show();
        final String oldPassword = editTextOldPassword.getText().toString();
        firebaseAuth.signInWithEmailAndPassword(firebaseAuth.getCurrentUser().getEmail(), oldPassword).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(ChangePasswordActivity.this, "Correct password ", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                } else {
                    progressDialog.dismiss();
                    Toast.makeText(ChangePasswordActivity.this, "Password incorrect. ", Toast.LENGTH_SHORT).show();
                    finish();
                    startActivity(new Intent(getApplicationContext(), ChangePasswordActivity.class));

                }
            }
        });

    }

    @Override
    public void onClick(View view) {
        if (view == buttonChgPassword) {
            chgPassword();
        }
    }


}