package com.example.han.tartalk;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
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
    private ArrayList<Post> postList = HomeFragment.postList;
    private ArrayList<Post> postListSearch = new ArrayList<Post>();
    private DatabaseReference database;
    private EditText editTxtSearch;
    //private DatabaseReference databaseComments;
    private static final String TAG = "Search Fragment";
    private PostAdapter adapter;

    public SearchFragment() {


    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.search_fragment, container, false);

        editTxtSearch =  (EditText) v.findViewById(R.id.editTxtSearch);
        searchPost = (RecyclerView) v.findViewById(R.id.searchPost);
        database = FirebaseDatabase.getInstance().getReference().child("Posts");
        //databaseComments = FirebaseDatabase.getInstance().getReference().child("Comments");
        adapter = new PostAdapter(getContext());
        //retrieve();

        searchPost.setAdapter(adapter);
        adapter.setData(postList);

        searchPost.setHasFixedSize(true);
        searchPost.setLayoutManager(new LinearLayoutManager(getActivity()));

        editTxtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                postListSearch.clear();
                String query = charSequence.toString().toLowerCase();
                // String query = searchView.getQuery().toString().toLowerCase();
                for(Post postArr : postList){
                    final String text = postArr.content.toString().toLowerCase();

                    if(text.contains(query)){

                        postListSearch.add(postArr);
                    }
                }

                adapter = new PostAdapter(getContext());
                searchPost.setAdapter(adapter);
                adapter.setData(postListSearch);
            }

            @Override
            public void afterTextChanged(Editable editable) {
                postListSearch.clear();
                String userQuery = editable.toString().toLowerCase();
                for(Post postArr : postList){
                    final String text = postArr.content.toString().toLowerCase();

                    if(text.contains(userQuery)){

                        postListSearch.add(postArr);
                    }
                }

                adapter = new PostAdapter(getContext());
                searchPost.setAdapter(adapter);
                adapter.setData(postListSearch);

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
                searchPost.setAdapter(adapter);
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

            postList.add(post);

        }

    }

}