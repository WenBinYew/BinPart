package com.example.han.tartalk;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class HomeFragment extends android.support.v4.app.Fragment {

    private RecyclerView rvPost;
    private CardView cvPost;
    private SwipeRefreshLayout swipeRefresh;
    private ArrayList<Post> postList = new ArrayList<Post>();
    private DatabaseReference database;
    //private DatabaseReference databaseComments;
    private static final String TAG = "HomeFragment";

    public HomeFragment() {


    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, final Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.home_fragment, container, false);

        rvPost = (RecyclerView) v.findViewById(R.id.rvPost);
        cvPost = (CardView) v.findViewById(R.id.cvPost);
        database = FirebaseDatabase.getInstance().getReference().child("Posts");
        swipeRefresh = (SwipeRefreshLayout) v.findViewById(R.id.swipeRefresh);
        //databaseComments = FirebaseDatabase.getInstance().getReference().child("Comments");

        retrieve();
        rvPost.setHasFixedSize(true);
        rvPost.setLayoutManager(new LinearLayoutManager(getContext()));

        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {

            @Override
            public void onRefresh() {
                retrieve();

            }
        });

        return v;
    }


    public void retrieve() {
        postList = new ArrayList<>();
        database.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                fetchData(dataSnapshot);
                PostAdapter adapter = new PostAdapter(getContext());
                //adapter.setData(postList);
                //adapter.setDataForArray(postList);

                rvPost.setAdapter(adapter);

//                if (getActivity() != null) {
//                    View headerView = LayoutInflater.from(getActivity()).inflate(R.layout.testing, rvPost, false);
//                    adapter.setHeaderView(headerView);
//                }
                adapter.setData(postList);
                swipeRefresh.setRefreshing(false);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
                // [START_EXCLUDE]
                Toast.makeText(getContext(), "Failed to load post.",
                        Toast.LENGTH_LONG).show();
            }
        });

    }

    private void fetchData(DataSnapshot dataSnapShot) {
        postList = new ArrayList<>();
        for (DataSnapshot ds : dataSnapShot.getChildren()) {


            final Post post = ds.getValue(Post.class);

            postList.add(post);


        }

    }


}
