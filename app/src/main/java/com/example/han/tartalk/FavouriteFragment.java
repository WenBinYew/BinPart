package com.example.han.tartalk;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/**
 * Created by han on 26/11/2016.
 */

public class FavouriteFragment extends android.support.v4.app.Fragment {
    private DatabaseReference databaseFavourite = FirebaseDatabase.getInstance().getReference().child("Users");
    private ArrayList<Post> postList = HomeFragment.postList;
    private FirebaseAuth auth;
    private FirebaseUser user;
    ArrayList<String> favouriteID = new ArrayList<>();
    private ArrayList<Post> postListFinal = new ArrayList<Post>();
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View v = inflater.inflate(R.layout.favourite_fragment,container,false);
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        if(auth.getCurrentUser() != null) {
            databaseFavourite.child(user.getUid()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                   favouriteID = new ArrayList<>();
                    postListFinal = new ArrayList<Post>();
                   User u = dataSnapshot.getValue(User.class);

                    for(String key : u.favourite.keySet()){
                        favouriteID.add(key);
                    }


                    for (int i = 0; i < postList.size(); i++) {
                        for (int x = 0; x < favouriteID.size(); x++) {
                            if (postList.get(i).id.equals(favouriteID.get(x))) {
                                postListFinal.add(postList.get(i));
                            }

                        }
                    }



                    RecyclerView rvFavourite = (RecyclerView) v.findViewById(R.id.rvPostFavourite);
                    PostAdapterClean adapter = new PostAdapterClean(getContext());
                    adapter.setData(postListFinal);
                    rvFavourite.setHasFixedSize(true);
                    rvFavourite.setLayoutManager(new LinearLayoutManager(getContext()));
                    rvFavourite.setAdapter(adapter);


                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });



        }else{
            Toast.makeText(getContext(), "Please login to view favourites", Toast.LENGTH_SHORT).show();
        }

        return v;

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.favourite,menu);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }
}
