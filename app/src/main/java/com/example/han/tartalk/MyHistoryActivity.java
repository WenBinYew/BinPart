package com.example.han.tartalk;

import android.app.FragmentManager;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.fitness.data.Value;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class MyHistoryActivity extends AppCompatActivity {

    private RecyclerView rvPost;
    private CardView cvPost;
    private DatabaseReference mDatabase;
    private DatabaseReference pDatabase;
    private ArrayList<Post> postList = new ArrayList<Post>();
    private ArrayList<Post> postListFinal = new ArrayList<Post>();
    ;
    private ArrayList<String> postIDList;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_history);

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(mUser.getUid()).child("postID");
        pDatabase = FirebaseDatabase.getInstance().getReference().child("Posts");
        postIDList = new ArrayList<>();
        postList = new ArrayList<>();

        rvPost = (RecyclerView) findViewById(R.id.rvPost);
        cvPost = (CardView) findViewById(R.id.cvPost);
        retrieve();
        PostAdapter adapter = new PostAdapter(MyHistoryActivity.this);
        adapter.setData(postListFinal);
        rvPost.setAdapter(adapter);
        rvPost.setHasFixedSize(true);
        rvPost.setLayoutManager(new LinearLayoutManager(this));

    }

    @Override
    protected void onStart() {
        super.onStart();


    }

    public void retrieve() {
        postList = new ArrayList<>();

        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (Object value : dataSnapshot.getChildren()) {
                    postIDList.add(value.toString());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };

        mDatabase.addValueEventListener(eventListener);

//        mDatabase.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//
//                for (Object value : dataSnapshot.getChildren()) {
//                    postIDList.add(value.toString());
//                }
//
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        });

        mDatabase.removeEventListener(eventListener);


        pDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for(DataSnapshot ds : dataSnapshot.getChildren()){
                    final Post post = ds.getValue(Post.class);
                    postList.add(post);
                }

//                final Post post = dataSnapshot.getValue(Post.class);
//                postList.add(post);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        for (int i = 0; i < postList.size(); i++) {
            for (int x = 0; x < postIDList.size(); x++) {
                if (postList.get(i).id.toString().equals(postIDList.get(x).toString())) {
                    postListFinal.add(postList.get(i));
                }

            }
        }


    }

//    private void fetchData(DataSnapshot dataSnapShot) {
//
//
//
//        pDatabase.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//
//                for(DataSnapshot ds : dataSnapshot.getChildren()){
//                    final Post post = ds.getValue(Post.class);
//                    postList.add(post);
//                }
//
////                final Post post = dataSnapshot.getValue(Post.class);
////                postList.add(post);
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        });
//
//
//        for (int i = 0; i < postList.size(); i++) {
//            for (int x = 0; x < postIDList.size(); x++) {
//                if (postList.get(i).id.toString().equals(postIDList.get(x).toString())) {
//                    postListFinal.add(postList.get(i));
//                }
//
//            }
//        }
//
//    }
//
//    public void findData() {
//
//
//        pDatabase.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//
//                for(DataSnapshot ds : dataSnapshot.getChildren()){
//                    final Post post = ds.getValue(Post.class);
//                    postList.add(post);
//                }
//
////                final Post post = dataSnapshot.getValue(Post.class);
////                postList.add(post);
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        });
//
//
//        for (int i = 0; i < postList.size(); i++) {
//            for (int x = 0; x < postIDList.size(); x++) {
//                if (postList.get(i).id.toString().equals(postIDList.get(x).toString())) {
//                    postListFinal.add(postList.get(i));
//                }
//
//            }
//        }
//
//
//    }


    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                getSupportFragmentManager().popBackStack("ProfileFragment", FragmentManager.POP_BACK_STACK_INCLUSIVE);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }


}
