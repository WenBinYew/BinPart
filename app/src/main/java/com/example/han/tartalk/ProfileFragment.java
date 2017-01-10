package com.example.han.tartalk;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Objects;

import static android.R.attr.data;
import static android.R.attr.name;


public class ProfileFragment extends android.support.v4.app.Fragment implements View.OnClickListener {

    private Button btnLogout;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener authListener;
    private boolean from = false;
    Fragment fragment = new HomeFragment();
    private EditText editTextEmail;
    private EditText editTextPassword;
    private Button buttonSignIn;
    private Button buttonCancel;
    private TextView textViewRegister;
    private TextView textViewForgetPassword;
    private TextView textViewNickname;
    private ProgressDialog progressDialog;
    private AlertDialog alertDialog;
    private DatabaseReference database = FirebaseDatabase.getInstance().getReference().child("Users");
    private ArrayList<String> postIDList;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final ListView listViewProfile;
        progressDialog = new ProgressDialog(getActivity());

        String[] profile;
        View view = inflater.inflate(R.layout.profile_fragment, container, false);
        listViewProfile = (ListView) view.findViewById(R.id.listViewProfile);
        profile = getResources().getStringArray(R.array.Profile);
        Integer[] icon = {
                R.drawable.ic_action_chgpassword,
                R.drawable.ic_action_post,

        };
        btnLogout = (Button) view.findViewById(R.id.btnLogout);
        textViewNickname = (TextView) view.findViewById(R.id.textViewNickname);

        mAuth = FirebaseAuth.getInstance();
        btnLogout.setOnClickListener(this);

        if (mAuth.getCurrentUser() == null) {
            LayoutInflater li = LayoutInflater.from(getActivity());
            View promptsView = li.inflate(R.layout.activity_login, null);
            editTextEmail = (EditText) promptsView.findViewById(R.id.editTextEmail);
            editTextPassword = (EditText) promptsView.findViewById(R.id.editTextPassword);
            textViewRegister = (TextView) promptsView.findViewById(R.id.textViewRegister);
            textViewForgetPassword = (TextView) promptsView.findViewById(R.id.textViewForgetPassword);
            buttonSignIn = (Button) promptsView.findViewById(R.id.buttonSignIn);
            buttonCancel = (Button) promptsView.findViewById(R.id.buttonCancel);

            final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
            alertDialog = alertDialogBuilder.create();


            alertDialogBuilder.setCancelable(false);
            alertDialog.setCancelable(false);
            textViewRegister.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActivity(new Intent(getActivity(), SignUpActivity.class));
                }
            });
            textViewForgetPassword.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActivity(new Intent(getActivity(), ResetPasswordActivity.class));
                }
            });
            buttonSignIn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    chkLogin();


                }
            });
            buttonCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    alertDialog.dismiss();
                    HomeFragment fragment = new HomeFragment();
                    getFragmentManager().beginTransaction().replace(R.id.main_container, new HomeFragment()).commit();
                    MainActivity.bottomBar.selectTabAtPosition(0, true);

                }
            });
            alertDialog.setView(promptsView);
            alertDialog.show();
        }
        if (mAuth.getCurrentUser() != null) {

            final TextView textViewPost = (TextView) view.findViewById(R.id.textViewPost);
            database.child(mAuth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    postIDList = new ArrayList<>();
                    final User u = dataSnapshot.getValue(User.class);
                    textViewNickname.setText(u.Name);

                    if (u.postID != null) {
                        textViewPost.setText(""+u.postID.size());
                    } else {
                        textViewPost.setText("" + 0);
                    }

                    for(String value : u.postID.keySet()){
                        postIDList.add(u.postID.get(value));
                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }


            });



        }




        CustomListAdapter adapter = new CustomListAdapter(getActivity(), profile, icon);
        listViewProfile.setAdapter(adapter);
        listViewProfile.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                switch (position) {
                    case 0:
                        startActivity(new Intent(getActivity(), ChangePasswordActivity.class));
                        break;
                    case 1:
                        Intent mypost = new Intent(view.getContext(), MyHistoryActivity.class);
                        mypost.putStringArrayListExtra("PostID", postIDList );
                        getContext().startActivity(mypost);


                        break;
                }
            }

            ;
        });
        setHasOptionsMenu(true);
        return view;
    }

    private void chkLogin() {
        final String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();

        if (TextUtils.isEmpty(email)) {
            Toast.makeText(getActivity(), "Please enter email", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(password)) {
            Toast.makeText(getActivity(), "Please enter password", Toast.LENGTH_SHORT).show();
            return;
        }
        progressDialog.setMessage("Loading...");
        progressDialog.show();

        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    progressDialog.dismiss();
                    alertDialog.dismiss();

                } else {
                    Toast.makeText(getActivity(), "Fail to Login. please try again. ", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                }
            }
        });
    }

    @Override
    public void onClick(View view) {
        if (view == btnLogout) {
            from = true;
            mAuth.signOut();
            getFragmentManager().beginTransaction().replace(R.id.main_container, fragment).commit();
            MainActivity.bottomBar.selectTabAtPosition(0, true);

        }

    }

    @Override
    public void onStart() {
        super.onStart();
    }


}
