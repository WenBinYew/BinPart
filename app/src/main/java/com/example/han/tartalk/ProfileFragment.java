package com.example.han.tartalk;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;

/**
 * Created by han on 26/11/2016.
 */

public class ProfileFragment extends android.support.v4.app.Fragment {

    private Button logout;
    private FirebaseAuth auth;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.profile_fragment,container,false);

        auth = FirebaseAuth.getInstance();
        logout = (Button)v.findViewById(R.id.btnLogout);

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                auth.signOut();
            }
        });

        return v;


    }
}
