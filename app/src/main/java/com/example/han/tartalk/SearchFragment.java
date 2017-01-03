package com.example.han.tartalk;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import android.app.SearchManager;
import android.widget.SearchView;
import android.widget.SearchView.OnQueryTextListener;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class SearchFragment extends android.support.v4.app.Fragment {

    private RecyclerView searchPost;
    private CardView cvPost;
    private SearchView searchView;
    private ArrayList<Post> postList = new ArrayList<Post>();
    private ArrayList<Post> postListSearch = new ArrayList<Post>();
    private DatabaseReference database;
    //private DatabaseReference databaseComments;
    private static final String TAG = "HomeFragment";

    public SearchFragment() {


    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.search_fragment, container, false);


        searchView = (SearchView) v.findViewById(R.id.searchView);

        searchPost = (RecyclerView) v.findViewById(R.id.searchPost);
        cvPost = (CardView) v.findViewById(R.id.cvPost);
        database = FirebaseDatabase.getInstance().getReference().child("Posts");
        //databaseComments = FirebaseDatabase.getInstance().getReference().child("Comments");

        retrieve();
        searchPost.setHasFixedSize(true);
        searchPost.setLayoutManager(new LinearLayoutManager(getContext()));

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                postListSearch.clear();
                String userQuery = query.toString().toLowerCase();
                for(Post postArr : postList){
                    final String text = postArr.content.toString().toLowerCase();

                    if(text.contains(userQuery)){

                        postListSearch.add(postArr);
                    }
                }

                final PostAdapter adapter = new PostAdapter(getContext());
                searchPost.setAdapter(adapter);
                adapter.setData(postListSearch);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                postListSearch.clear();
                String query = newText.toString().toLowerCase();
               // String query = searchView.getQuery().toString().toLowerCase();
                for(Post postArr : postList){
                    final String text = postArr.content.toString().toLowerCase();

                    if(text.contains(query)){

                        postListSearch.add(postArr);
                    }
                }

                final PostAdapter adapter = new PostAdapter(getContext());
                searchPost.setAdapter(adapter);
                adapter.setData(postListSearch);
                return true;
            }
        });

        return v;


    }

    public void retrieve() {
        postList = new ArrayList<>();
        database.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                fetchData(dataSnapshot);
                final PostAdapter adapter = new PostAdapter(getContext());
                //adapter.setData(postList);
                //adapter.setDataForArray(postList);

                searchPost.setAdapter(adapter);

//                if (getActivity() != null) {
//                    View headerView = LayoutInflater.from(getActivity()).inflate(R.layout.testing, rvPost, false);
//                    adapter.setHeaderView(headerView);
//                }
                adapter.setData(postList);

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
//            Query myComments = databaseComments.orderByChild(post.comments);
//            myComments.addValueEventListener(new ValueEventListener() {
//                @Override
//                public void onDataChange(DataSnapshot dataSnapshot) {
//                    post.commentsCount = (int) dataSnapshot.getChildrenCount();
//                }
//
//                @Override
//                public void onCancelled(DatabaseError databaseError) {
//
//                }
//            });
            //post.likes = (int) ds.child("likes").getChildrenCount();
            //ds.child("likes").getChildren();
            postList.add(post);

        }

    }




}
