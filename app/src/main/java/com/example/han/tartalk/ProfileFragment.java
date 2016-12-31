package com.example.han.tartalk;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * Created by han on 26/11/2016.
 */

public class ProfileFragment extends android.support.v4.app.Fragment implements View.OnClickListener {

    private Button btnLogout;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener authListener;
    private boolean from = false;
    Fragment fragment = new HomeFragment();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final ListView listViewProfile;
        String[] profile;
        View view = inflater.inflate(R.layout.profile_fragment, container, false);
        listViewProfile = (ListView)view.findViewById(R.id.listViewProfile);
        profile = getResources().getStringArray(R.array.Profile);
        Integer[] icon = {
                R.drawable.ic_action_chgpassword,
                R.drawable.ic_action_post,

        };
        btnLogout = (Button)view.findViewById(R.id.btnLogout);
        mAuth = FirebaseAuth.getInstance();
        btnLogout.setOnClickListener(this);

//        if (mAuth.getCurrentUser()!=null){
//
//        }

        //Toast.makeText(getActivity(),"user Email 1: " + user.getEmail(),Toast.LENGTH_SHORT).show();
//        authListener = new FirebaseAuth.AuthStateListener() {
//            @Override
//            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                //Toast.makeText(getActivity(),"user Email 2: " + mAuth.hrentUser().getEmail(),Toast.LENGTH_SHORT).show();
                if (mAuth.getCurrentUser() == null) {
//                    if(from == true){
//                        getFragmentManager().beginTransaction().replace(R.id.main_container,fragment).commit();
//                    }

                    startActivity(new Intent(getActivity(),LoginActivity.class));
                }
                //Toast.makeText(getActivity(),"user Email 3: " + mAuth.getCurrentUser().getEmail(),Toast.LENGTH_SHORT).show();
//            }
//        };


        CustomListAdapter adapter = new CustomListAdapter(getActivity(),profile, icon);
        // listViewProfile.setAdapter(new ArrayAdapter<String>(view.getContext(),android.R.layout.simple_list_item_1 , profile));
        listViewProfile.setAdapter(adapter);
        listViewProfile.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //Toast.makeText(getActivity().getApplicationContext(),"Position get " + position, Toast.LENGTH_SHORT).show();

                switch(position){
                    case 0:
                        
                        startActivity(new Intent(getActivity(), ChangePasswordActivity.class));
                        break;
                    case 1:
                        startActivity(new Intent(getActivity(), MyHistoryActivity.class));
                        break;
//                    case 2:
//                        startActivity(new Intent(getActivity(), UploadedImageActivity.class));
//                        break;
//                    case 3:
//                        startActivity(new Intent(getActivity(), MyCommentActivity.class));
//                        break;
                }
            };
        });
        setHasOptionsMenu(true);
        return view;
    }


    @Override
    public void onClick(View view) {
        if(view == btnLogout){
            from =true;
            mAuth.signOut();
            getFragmentManager().beginTransaction().replace(R.id.main_container,fragment).commit();

        }

    }

    @Override
    public void onStart() {
        super.onStart();
        //mAuth.addAuthStateListener(authListener);
    }


}
