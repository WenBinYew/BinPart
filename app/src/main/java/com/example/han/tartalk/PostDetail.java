package com.example.han.tartalk;

import android.app.FragmentManager;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.vision.text.Text;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.lang.reflect.Array;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class PostDetail extends AppCompatActivity {
    public static RecyclerView rvComment;
    private DatabaseReference database;
    public static DatabaseReference databaseComments;
    private DatabaseReference databaseFavourite;
    private ArrayList<Comment> commentList = new ArrayList<Comment>();
    private static final String TAG = "Post Detail Activity";
    private SwipeRefreshLayout swipeRefreshPostDetail;
    private CommentAdapter adapter;
    private FirebaseAuth auth;
    private FirebaseUser user;

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
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        if (user != null) {
            databaseFavourite = FirebaseDatabase.getInstance().getReference().child("Users").child(user.getUid());
        }
        //databaseFavourite = FirebaseDatabase.getInstance().getReference().child("Users").child();

        rvComment.setHasFixedSize(true);
        rvComment.setLayoutManager(new LinearLayoutManager(this));


        adapter = new CommentAdapter(PostDetail.this);
        retrieve();


    }

    public void retrieve() {
        commentList = new ArrayList<>();
        database.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //fetchData(dataSnapshot);
                final Post post = dataSnapshot.getValue(Post.class);

                databaseComments.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        commentList.clear();
                        for (DataSnapshot ds : dataSnapshot.getChildren()) {
                            final Comment comment = ds.getValue(Comment.class);
                            commentList.add(comment);
                        }

                        //CommentAdapter adapter = new CommentAdapter(PostDetail.this);
                        rvComment.setAdapter(adapter);
                        adapter.setData(commentList);

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Toast.makeText(PostDetail.this, "Please login", Toast.LENGTH_LONG).show();
                    }
                });


                View headerView = LayoutInflater.from(PostDetail.this).inflate(R.layout.post_cardview_detail, rvComment, false);
                headerView.layout(0, 0, 0, 0);
                headerView.setPadding(0, 0, 0, 0);
                TextView txtViewPostName = (TextView) headerView.findViewById(R.id.txtViewPostName);
                TextView txtViewDate = (TextView) headerView.findViewById(R.id.txtViewPostDate);
                TextView txtViewTitle = (TextView) headerView.findViewById(R.id.txtViewTitle);
                TextView txtViewContent = (TextView) headerView.findViewById(R.id.txtViewContent);
                final TextView txtViewCommentCount = (TextView) headerView.findViewById(R.id.txtViewCommentCount);
                final TextView txtViewLikeCount = (TextView) headerView.findViewById(R.id.txtViewLikeCount);
                final TextView txtViewDislikeCount = (TextView) headerView.findViewById(R.id.txtViewDislikeCount);
                ImageView imgViewImage = (ImageView) headerView.findViewById(R.id.imgViewImage);
                final Button btnComment = (Button) headerView.findViewById(R.id.btnComment);
                final Button btnLike = (Button) headerView.findViewById(R.id.btnLike);
                final Button btnDislike = (Button) headerView.findViewById(R.id.btnDisike);
                final Button btnFavourite = (Button) headerView.findViewById(R.id.btnFavourite);
                RelativeLayout rlComment = (RelativeLayout) headerView.findViewById(R.id.btntoClickComment);
                RelativeLayout rlLike = (RelativeLayout) headerView.findViewById(R.id.btntoClickLike);
                RelativeLayout rlDislike = (RelativeLayout) headerView.findViewById(R.id.btntoClickDislike);
                RelativeLayout rlFavourite = (RelativeLayout) headerView.findViewById(R.id.btntoClickFavourite);
                adapter.setHeaderView(headerView);


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
                    if (result == 0) {
                        txtViewDate.setText("" + "Today");
                    } else if (result == 1) {
                        txtViewDate.setText(result.toString() + " day ago");
                    } else {
                        txtViewDate.setText(result.toString() + " days ago");
                    }

                } catch (ParseException e) {
                    e.printStackTrace();
                }


                txtViewPostName.setText(post.name);
                txtViewTitle.setText(post.title);
                txtViewContent.setText(post.content);

                if (post.likes != null) {
                    txtViewLikeCount.setText("" + post.likes.size());
                } else {
                    txtViewLikeCount.setText("" + 0);
                }

                if (post.dislikes != null) {
                    txtViewDislikeCount.setText("" + post.dislikes.size());
                } else {
                    txtViewDislikeCount.setText("" + 0);
                }
                if (post.comments != null) {
                    txtViewCommentCount.setText("" + post.comments.size());
                } else {
                    txtViewCommentCount.setText("" + 0);
                }


                if (post.image.toString().equals("null")) {
                    imgViewImage.setPadding(0, 0, 0, 0);
                    imgViewImage.setBackgroundColor(Color.parseColor("#F5F5F5"));
                } else {
                    Picasso.with(PostDetail.this)
                            .load(post.image)
                            .resize(150, 150)
                            .centerCrop()
                            .into(imgViewImage);

                }

                btnLike.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (auth.getCurrentUser() != null) {
                            ArrayList<String> check = new ArrayList<String>();
                            if (post.likes != null) {
                                for (Object value : post.likes.values()) {
                                    check.add(value.toString());
                                }
                                Boolean done = false;
                                for (int i = 0; i < check.size(); i++) {
                                    if (check.get(i).toString().equals(user.getUid())) {
                                        Toast.makeText(PostDetail.this, "Already liked this post", Toast.LENGTH_SHORT).show();
                                        done = true;
                                    }
                                }
                                    if (done = false) {
                                        int x = post.likes.size();
                                        txtViewLikeCount.setText("" + (x + 1));
                                        database.child("likes").push().setValue(user.getUid());
                                    }
                            } else {
                                txtViewLikeCount.setText("1");
                                database.child("likes").push().setValue(user.getUid());
                            }
                        } else {
                            Toast.makeText(PostDetail.this, "Please login to dislike post", Toast.LENGTH_SHORT).show();
                        }
                    }

                });

                rlLike.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        btnLike.performClick();
                    }
                });

                rlDislike.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        btnDislike.performClick();
                    }
                });

                rlComment.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        btnComment.performClick();
                    }
                });


                rlFavourite.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        btnFavourite.performClick();
                    }
                });

                btnDislike.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (auth.getCurrentUser() != null) {
                            ArrayList<String> check = new ArrayList<String>();
                            if (post.dislikes != null) {
                                for (Object value : post.dislikes.values()) {
                                    check.add(value.toString());
                                }

                                boolean done = false;

                                for (int i = 0; i < check.size(); i++) {
                                    if (check.get(i).toString().equals(user.getUid())) {
                                        Toast.makeText(PostDetail.this, "Already disliked this post", Toast.LENGTH_SHORT).show();
                                        done = true;
                                    }

                                }
                                if (done = false) {
                                    int x = post.dislikes.size();
                                    txtViewDislikeCount.setText("" + (x + 1));
                                    database.child("dislikes").push().setValue(user.getUid());
                                }
                            } else {
                                txtViewDislikeCount.setText("1");
                                database.child("dislikes").push().setValue(user.getUid());
                            }
                        } else {
                            Toast.makeText(PostDetail.this, "Please login to dislike post", Toast.LENGTH_SHORT).show();
                        }
                    }

                });


                btnComment.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        if (auth.getCurrentUser() != null) {
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
                                                    if (post.comments != null) {
                                                        txtViewCommentCount.setText("" + (post.comments.size() + 1));
                                                    } else {
                                                        txtViewCommentCount.setText("1");
                                                    }

                                                }
                                            }
                                        });


                                    }
                                }
                            });
                        } else {
                            Toast.makeText(PostDetail.this, "Please login to post comment!", Toast.LENGTH_SHORT).show();
                        }

                    }
                });


                btnFavourite.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (auth.getCurrentUser() != null) {
                            final ArrayList<String> check = new ArrayList<String>();
                            databaseFavourite.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    User user = dataSnapshot.getValue(User.class);
                                    if (user.favourite != null) {
                                        for (Object value : user.favourite.values()) {
                                            check.add(value.toString());
                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });

                            Boolean done = false;
                            for (int i = 0; i < check.size(); i++) {
                                if (check.get(i).toString().equals(post.id)) {
                                    Toast.makeText(PostDetail.this, "Already favourited this post", Toast.LENGTH_SHORT).show();
                                    done = true;
                                }
                            }

                            if (done = false) {
                                databaseFavourite.child("favourite").push().setValue(post.id);
                                Toast.makeText(PostDetail.this, "You have favourited this post", Toast.LENGTH_SHORT).show();

                            }

                        } else {
                            Toast.makeText(PostDetail.this, "Please login to favourite this post", Toast.LENGTH_SHORT).show();
                        }
                    }
                });


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



//    @Override
//    public android.support.v4.app.FragmentManager getSupportFragmentManager() {
//
//       HomeFragment getView =  (HomeFragment) getSupportFragmentManager().findFragmentById(R.layout.home_fragment);
//
//        return super.getSupportFragmentManager();
//    }
}