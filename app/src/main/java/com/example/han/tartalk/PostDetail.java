package com.example.han.tartalk;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class PostDetail extends AppCompatActivity {
    private RecyclerView rvComment;
    private DatabaseReference database;
    private ArrayList<Comment> commentList = new ArrayList<Comment>();
    private static final String TAG = "Post Detail Activity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_detail);

        rvComment = (RecyclerView) findViewById(R.id.rvComment);
        database = FirebaseDatabase.getInstance().getReference().child("Posts").child("comments");
        retrieve();

    }

    public void retrieve() {
        commentList = new ArrayList<>();
        database.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                fetchData(dataSnapshot);
                CommentAdapter adapter = new CommentAdapter(PostDetail.this);

                rvComment.setAdapter(adapter);
//                if (getActivity() != null) {
//                    View headerView = LayoutInflater.from(getActivity()).inflate(R.layout.testing, rvPost, false);
//                    adapter.setHeaderView(headerView);
//                }
                adapter.setData(commentList);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
                // [START_EXCLUDE]
                Toast.makeText(PostDetail.this, "Failed to load post.",
                        Toast.LENGTH_LONG).show();
            }
        });

    }

    private void fetchData(DataSnapshot dataSnapShot) {
        commentList = new ArrayList<>();
        for (DataSnapshot ds : dataSnapShot.getChildren()) {


            final Comment comment = ds.getValue(Comment.class);
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
            commentList.add(comment);

        }
    }
}