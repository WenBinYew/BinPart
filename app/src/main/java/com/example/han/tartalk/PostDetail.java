package com.example.han.tartalk;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.vision.text.Text;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class PostDetail extends AppCompatActivity {
    private RecyclerView rvComment;
    private DatabaseReference database;
    public static DatabaseReference databaseComments;
    private DatabaseReference databaseFavourite;
    private ArrayList<Comment> commentList = new ArrayList<Comment>();
    private static final String TAG = "Post Detail Activity";
    private SwipeRefreshLayout swipeRefreshPostDetail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_detail);

        //swipeRefreshPostDetail = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshPostDetail);

        rvComment = (RecyclerView) findViewById(R.id.rvComment);
        final Intent intent = getIntent();
        final String id = intent.getStringExtra("PostID");
        database = FirebaseDatabase.getInstance().getReference().child("Posts").child(id);
        databaseComments = FirebaseDatabase.getInstance().getReference().child("Posts").child(id).child("comments");
        //databaseFavourite = FirebaseDatabase.getInstance().getReference().child("Users").child();

        rvComment.setHasFixedSize(true);
        rvComment.setLayoutManager(new LinearLayoutManager(this));
//        swipeRefreshPostDetail.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
//
//            @Override
//            public void onRefresh() {
//                retrieve();
//
//            }
//        });
        retrieve();


    }

    public void retrieve() {
        commentList = new ArrayList<>();
        database.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //fetchData(dataSnapshot);
                final Post post = dataSnapshot.getValue(Post.class);

                databaseComments.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        for(DataSnapshot ds : dataSnapshot.getChildren()){
                            final Comment comment = ds.getValue(Comment.class);
                            String omgdsfds = ds.getValue().toString();
                            Toast.makeText(PostDetail.this, omgdsfds, Toast.LENGTH_LONG).show();
                            commentList.add(comment);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });


                CommentAdapter adapter = new CommentAdapter(PostDetail.this);

                rvComment.setAdapter(adapter);
//                if (getActivity() != null) {
//                    View headerView = LayoutInflater.from(getActivity()).inflate(R.layout.testing, rvPost, false);
//                    adapter.setHeaderView(headerView);
//                }
                adapter.setData(commentList);


                View headerView = LayoutInflater.from(PostDetail.this).inflate(R.layout.post_cardview, rvComment, false);
                headerView.layout(0,0,0,0);
                headerView.setPadding(0,0,0,0);
                TextView txtViewPostName = (TextView) headerView.findViewById(R.id.txtViewPostName);
                TextView txtViewDate = (TextView) headerView.findViewById(R.id.txtViewPostDate);
                TextView txtViewTitle = (TextView) headerView.findViewById(R.id.txtViewTitle);
                final TextView txtViewCommentCount = (TextView) headerView.findViewById(R.id.txtViewCommentCount);
                final TextView txtViewLikeCount = (TextView) headerView.findViewById(R.id.txtViewLikeCount);
                TextView txtViewDislikeCount = (TextView) headerView.findViewById(R.id.txtViewDislikeCount);
                ImageView imgViewImage = (ImageView) headerView.findViewById(R.id.imgViewImage);
                Button btnComment = (Button) headerView.findViewById(R.id.btnComment);
                Button btnLike = (Button) headerView.findViewById(R.id.btnLike);
                Button btnDislike = (Button) headerView.findViewById(R.id.btnDisike);
                Button btnFavourite = (Button) headerView.findViewById(R.id.btnFavourite);

                final Calendar c = Calendar.getInstance();
                SimpleDateFormat sdf = new SimpleDateFormat("dd:MMMM:yyyy HH:mm:ss a");
                final String strDate = sdf.format(c.getTime());
                Date d1 = null;
                Date d2 = null;
                try {
                    d1 = sdf.parse(post.date);
                    d2 = sdf.parse(strDate);

                    Calendar cal1 = Calendar.getInstance();
                    cal1.setTime(d1);
                    Calendar cal2 = Calendar.getInstance();
                    cal2.setTime(d2);

                    Long result = daysBetween(cal1.getTime(), cal2.getTime());
                    if(result == 0){
                        txtViewDate.setText(""+ "Today");
                    }else if(result == 1) {
                        txtViewDate.setText(result.toString() + " day ago");
                    }else{
                        txtViewDate.setText(result.toString() + " days ago");
                    }

                } catch (ParseException e) {
                    e.printStackTrace();
                }


                txtViewPostName.setText(post.name);
                txtViewTitle.setText(post.title);
                txtViewCommentCount.setText("" + post.comments.size());
                txtViewLikeCount.setText("" +post.likes.size());
                txtViewDislikeCount.setText("" +post.dislikes.size());
                Picasso.with(PostDetail.this)
                        .load(post.image)
                        .resize(150, 150)
                        .centerCrop()
                        .into(imgViewImage);

                btnLike.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        ArrayList<String> check = new ArrayList<String>();
                        if (post.likes != null) {
                            for (Object value : post.likes.values()) {
                                check.add(value.toString());
                            }

                            for (int i = 0; i < check.size(); i++) {
                                if (check.get(i).toString().equals(post.uid)) {
                                    Toast.makeText(PostDetail.this, "Already liked this post", Toast.LENGTH_SHORT).show();
                                } else {
                                    int x = post.likes.size();
                                    txtViewLikeCount.setText("" + (x + 1));
                                    database.child(post.id).child("likes").push().setValue(post.uid);
                                }
                            }
                        } else {

                            txtViewLikeCount.setText("1");
                            database.child(post.id).child("likes").push().setValue(post.uid);
                        }
                    }

                });



                btnDislike.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        ArrayList<String> check = new ArrayList<String>();
                        if (post.likes != null) {
                            for (Object value : post.dislikes.values()) {
                                check.add(value.toString());
                            }

                            for (int i = 0; i < check.size(); i++) {
                                if (check.get(i).toString().equals(post.uid)) {
                                    Toast.makeText(PostDetail.this, "Already disliked this post", Toast.LENGTH_SHORT).show();
                                } else {
                                    int x = post.likes.size();
                                    txtViewLikeCount.setText("" + (x + 1));
                                    database.child(post.id).child("dislikes").push().setValue(post.uid);
                                }
                            }
                        } else {

                            txtViewLikeCount.setText("1");
                            database.child(post.id).child("dislikes").push().setValue(post.uid);
                        }
                    }

                });


                btnComment.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(PostDetail.this);
                        View v = LayoutInflater.from(PostDetail.this).inflate(R.layout.post_comment_dialog, null);
                        final EditText txtComment = (EditText) v.findViewById(R.id.editTxtComments);
                        Button postComment = (Button) v.findViewById(R.id.btnPostComment);

                        builder.setView(v);
                        final AlertDialog dialog = builder.create();
                        dialog.show();

                        postComment.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                if (!txtComment.getText().toString().isEmpty()) {

                                    final Calendar c = Calendar.getInstance();
                                    SimpleDateFormat sdf = new SimpleDateFormat("dd:MMMM:yyyy HH:mm:ss a");
                                    final String strDate = sdf.format(c.getTime());
                                    final Comment comment = new Comment();


                                    final DatabaseReference newComment = databaseComments.push();

                                    comment.comment = txtComment.getText().toString();
                                    comment.date = strDate;
                                    comment.uid = post.uid;
                                    comment.name = post.name;
                                    comment.id = newComment.getKey();

                                    newComment.setValue(comment).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {

                                                Toast.makeText(PostDetail.this, "Successfully commented!", Toast.LENGTH_SHORT).show();
                                                dialog.dismiss();
                                                if(post.comments != null) {
                                                    txtViewCommentCount.setText("" + post.comments.size() + 1);
                                                }else{
                                                    txtViewCommentCount.setText("1");
                                                }

                                            }
                                        }
                                    });


                                }
                            }
                        });


                    }
                });

                txtViewCommentCount.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(PostDetail.this);
                        View v = LayoutInflater.from(PostDetail.this).inflate(R.layout.post_comment_dialog, null);
                        final EditText txtComment = (EditText) v.findViewById(R.id.editTxtComments);
                        Button postComment = (Button) v.findViewById(R.id.btnPostComment);

                        builder.setView(v);
                        final AlertDialog dialog = builder.create();
                        dialog.show();

                        postComment.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                if (!txtComment.getText().toString().isEmpty()) {

                                    final Calendar c = Calendar.getInstance();
                                    SimpleDateFormat sdf = new SimpleDateFormat("dd:MMMM:yyyy HH:mm:ss a");
                                    final String strDate = sdf.format(c.getTime());
                                    final Comment comment = new Comment();


                                    final DatabaseReference newComment = databaseComments.push();

                                    comment.comment = txtComment.getText().toString();
                                    comment.date = strDate;
                                    comment.uid = post.uid;
                                    comment.name = post.name;
                                    comment.id = newComment.getKey();

                                    newComment.setValue(comment).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {

                                                Toast.makeText(PostDetail.this, "Successfully commented!", Toast.LENGTH_SHORT).show();
                                                dialog.dismiss();
                                                if(post.comments != null) {
                                                    txtViewCommentCount.setText("" + post.comments.size() + 1);
                                                }else{
                                                    txtViewCommentCount.setText("1");
                                                }

                                            }
                                        }
                                    });


                                }
                            }
                        });


                    }
                });


                btnFavourite.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                    }
                });
                adapter.setHeaderView(headerView);

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

//    private void fetchData(DataSnapshot dataSnapShot) {
//        commentList = new ArrayList<>();
//        for (DataSnapshot ds : dataSnapShot.getChildren()) {
//
//
//            final Post post = ds.getValue(Post.class);
////            Query myComments = databaseComments.orderByChild(post.comments);
////            myComments.addValueEventListener(new ValueEventListener() {
////                @Override
////                public void onDataChange(DataSnapshot dataSnapshot) {
////                    post.commentsCount = (int) dataSnapshot.getChildrenCount();
////                }
////
////                @Override
////                public void onCancelled(DatabaseError databaseError) {
////
////                }
////            });
//            //post.likes = (int) ds.child("likes").getChildrenCount();
//            //ds.child("likes").getChildren();
//            commentList.add(post);
//
//        }
//    }

    public static Calendar getDatePart(Date date) {
        Calendar cal = Calendar.getInstance();       // get calendar instance
        cal.setTime(date);
        cal.set(Calendar.HOUR_OF_DAY, 0);            // set hour to midnight
        cal.set(Calendar.MINUTE, 0);                 // set minute in hour
        cal.set(Calendar.SECOND, 0);                 // set second in minute
        cal.set(Calendar.MILLISECOND, 0);            // set millisecond in second

        return cal;                                  // return the date part
    }

    public static long daysBetween(Date startDate, Date endDate) {
        Calendar sDate = getDatePart(startDate);
        Calendar eDate = getDatePart(endDate);

        long daysBetween = 0;
        while (sDate.before(eDate)) {
            sDate.add(Calendar.DAY_OF_MONTH, 1);
            daysBetween++;
        }
        return daysBetween;
    }
}