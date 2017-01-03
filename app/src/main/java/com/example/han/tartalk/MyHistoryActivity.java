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

import com.google.firebase.auth.FirebaseAuth;
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
    private DatabaseReference mDatabase,pDatabase;
    private ArrayList<Post> postList = new ArrayList<Post>();;
    private ArrayList<String> postIDList;
    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_history);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(mAuth.getCurrentUser().getUid());


        rvPost = (RecyclerView)findViewById(R.id.rvPost);
        cvPost = (CardView)findViewById(R.id.cvPost);
        retrieve();
        rvPost.setHasFixedSize(true);
        rvPost.setLayoutManager(new LinearLayoutManager(this));

    }
    @Override
    protected void onStart() {
        super.onStart();


}

    public void retrieve() {
        postList = new ArrayList<>();
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                fetchData(dataSnapshot);
                findData();

                PostAdapter adapter = new PostAdapter(getParent());

                adapter.setData(postList);
                rvPost.setAdapter(adapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void fetchData(DataSnapshot dataSnapShot) {
        postIDList = new ArrayList<>();
        postList = new ArrayList<>();
            final User post = dataSnapShot.getValue(User.class);
            for(Object value : post.postID.values()){
                postIDList.add(value.toString());
            }
    }
    public void findData(){
        for(int i = 0; i<postIDList.size(); i++){
            final int finalI1 = i;
        pDatabase = FirebaseDatabase.getInstance().getReference().child("Posts").child(postIDList.get(finalI1));
            pDatabase.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    final Post post = dataSnapshot.getValue(Post.class);
                    postList.add(post);

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
      }

    }


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
