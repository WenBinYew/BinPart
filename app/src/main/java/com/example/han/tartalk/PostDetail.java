package com.example.han.tartalk;

import android.app.FragmentManager;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
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
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
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
    private ArrayList<Comment> commentList = new ArrayList<Comment>();
    private static final String TAG = "Post Detail Activity";
    private SwipeRefreshLayout swipeRefreshPostDetail;
    private CommentAdapter adapter;
    private FirebaseAuth auth;
    private FirebaseUser user;
    private Post post;
    private DatabaseReference databaseFavourite;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_detail);


        //swipeRefreshPostDetail = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshPostDetail);

        rvComment = (RecyclerView) findViewById(R.id.rvComment);
        final Intent intent = getIntent();
        final String id = intent.getStringExtra("PostID");
        database = FirebaseDatabase.getInstance().getReference().child("Posts").child(id);
        databaseComments = FirebaseDatabase.getInstance().getReference().child("Comments").child(id);
        databaseFavourite = FirebaseDatabase.getInstance().getReference().child("Users");
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        rvComment.setHasFixedSize(true);
        rvComment.setLayoutManager(new LinearLayoutManager(this));


        adapter = new CommentAdapter(PostDetail.this);
        retrieve();


    }

    public void retrieve() {
        commentList = new ArrayList<>();
        database.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //fetchData(dataSnapshot);
                post = dataSnapshot.getValue(Post.class);

                databaseComments.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        commentList.clear();

                        for (DataSnapshot ds : dataSnapshot.getChildren()) {
                            final Comment comment = ds.getValue(Comment.class);
                            commentList.add(comment);
                            adapter.setData(commentList);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Toast.makeText(PostDetail.this, "Please login", Toast.LENGTH_SHORT).show();
                    }
                });


                LinearLayoutManager layoutManager = ((LinearLayoutManager) rvComment.getLayoutManager());
                int firstVisiblePosition = layoutManager.findFirstCompletelyVisibleItemPosition();
                //adapter.setData(commentList);
                rvComment.setAdapter(adapter);

                rvComment.scrollToPosition(firstVisiblePosition);


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
                txtViewLikeCount.setText("" + post.likeCount);
                txtViewDislikeCount.setText("" + post.dislikeCount);
                txtViewCommentCount.setText("" + post.commentCount);


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
                            database.runTransaction(new Transaction.Handler() {
                                @Override
                                public Transaction.Result doTransaction(MutableData mutableData) {
                                    Post p = mutableData.getValue(Post.class);
                                    if (p == null) {

                                        return Transaction.success(mutableData);
                                    }
                                    if (p.likes != null) {
                                        if (p.likes.containsKey(auth.getCurrentUser().getUid())) {
                                            // Unstar the post and remove self from stars
                                            p.likeCount = p.likeCount - 1;
                                            p.likes.remove(auth.getCurrentUser().getUid());


                                        } else {
                                            // Star the post and add self to stars
                                            p.likeCount = p.likeCount + 1;
                                            p.likes.put(auth.getCurrentUser().getUid(), true);
                                            new CustomTaskLike().execute((Void[]) null);


                                        }
                                    } else {
                                        p.likes = new HashMap<String, Boolean>();
                                        p.likeCount = p.likeCount + 1;
                                        p.likes.put(auth.getCurrentUser().getUid(), true);
                                        new CustomTaskLike().execute((Void[]) null);
                                    }

                                    // Set value and report transaction success
                                    mutableData.setValue(p);
                                    return Transaction.success(mutableData);
                                }

                                @Override
                                public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {
                                    Log.d(TAG, "postTransaction:onComplete:" + databaseError);
                                }
                            });


                        } else {
                            Toast.makeText(PostDetail.this, "Please login to like post", Toast.LENGTH_SHORT).show();
                        }

//                        if (auth.getCurrentUser() != null) {
//                            ArrayList<String> check = new ArrayList<String>();
//                            if (post.likes != null) {
//                                for (Object value : post.likes.values()) {
//                                    check.add(value.toString());
//                                }
//                                Boolean done = false;
//                                for (int i = 0; i < check.size(); i++) {
//                                    if (check.get(i).toString().equals(user.getUid())) {
//                                        Toast.makeText(PostDetail.this, "Already liked this post", Toast.LENGTH_SHORT).show();
//                                        done = true;
//                                    }
//                                }
//                                    if (done = false) {
//                                        int x = post.likes.size();
//                                        txtViewLikeCount.setText("" + (x + 1));
//                                        database.child("likes").push().setValue(user.getUid());
//                                    }
//                            } else {
//                                txtViewLikeCount.setText("1");
//                                database.child("likes").push().setValue(user.getUid());
//                            }
//                        } else {
//                            Toast.makeText(PostDetail.this, "Please login to dislike post", Toast.LENGTH_SHORT).show();
//                        }
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
                            database.runTransaction(new Transaction.Handler() {
                                @Override
                                public Transaction.Result doTransaction(MutableData mutableData) {
                                    Post p = mutableData.getValue(Post.class);
                                    if (p == null) {

                                        return Transaction.success(mutableData);
                                    }
                                    if (p.dislikes != null) {
                                        if (p.dislikes.containsKey(auth.getCurrentUser().getUid())) {
                                            // Unstar the post and remove self from stars
                                            p.dislikeCount = p.dislikeCount - 1;
                                            p.dislikes.remove(auth.getCurrentUser().getUid());


                                        } else {
                                            // Star the post and add self to stars
                                            p.dislikeCount = p.dislikeCount + 1;
                                            p.dislikes.put(auth.getCurrentUser().getUid(), true);
                                            new CustomTaskDislike().execute((Void[]) null);


                                        }
                                    } else {
                                        p.dislikes = new HashMap<String, Boolean>();
                                        p.dislikeCount = p.dislikeCount + 1;
                                        p.dislikes.put(auth.getCurrentUser().getUid(), true);
                                        new CustomTaskDislike().execute((Void[]) null);
                                    }

                                    // Set value and report transaction success
                                    mutableData.setValue(p);
                                    return Transaction.success(mutableData);
                                }

                                @Override
                                public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {
                                    Log.d(TAG, "postTransaction:onComplete:" + databaseError);
                                }
                            });


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

                                        database.runTransaction(new Transaction.Handler() {
                                            @Override
                                            public Transaction.Result doTransaction(MutableData mutableData) {
                                                Post p = mutableData.getValue(Post.class);
                                                if (p == null) {
                                                    return Transaction.success(mutableData);
                                                }
                                                if (p.comments != null) {
                                                    p.commentCount = p.commentCount + 1;
                                                    p.comments.put(newComment.getKey(), true);
                                                } else {
                                                    p.comments = new HashMap<String, Boolean>();
                                                    p.commentCount = p.commentCount + 1;
                                                    p.comments.put(newComment.getKey(), true);
                                                }

                                                // Set value and report transaction success
                                                mutableData.setValue(p);
                                                return Transaction.success(mutableData);
                                            }

                                            @Override
                                            public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {
                                                Log.d(TAG, "postTransaction:onComplete:" + databaseError);
                                            }
                                        });

                                        comment.comment = txtComment.getText().toString();
                                        comment.date = strDate;
                                        comment.uid = post.uid;
                                        comment.name = post.name;
                                        comment.id = newComment.getKey();
                                        comment.postid = post.id;

                                        newComment.setValue(comment).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    //HomeFragment.rvPost.smoothScrollToPosition(position);
                                                    Toast.makeText(PostDetail.this, "Successfully commented!", Toast.LENGTH_SHORT).show();
                                                    dialog.dismiss();

                                                }
                                            }
                                        });


                                    }
                                }
                            });


                        } else {
                            Toast.makeText(PostDetail.this, "Please login to post comment!", Toast.LENGTH_SHORT).show();
                        }
//                        if (auth.getCurrentUser() != null) {
//                            AlertDialog.Builder builder = new AlertDialog.Builder(PostDetail.this);
//                            View v = LayoutInflater.from(PostDetail.this).inflate(R.layout.post_comment_dialog, null);
//                            final EditText txtComment = (EditText) v.findViewById(R.id.editTxtComments);
//                            Button postComment = (Button) v.findViewById(R.id.btnPostComment);
//
//                            builder.setView(v);
//                            final AlertDialog dialog = builder.create();
//                            dialog.show();
//
//                            postComment.setOnClickListener(new View.OnClickListener() {
//                                @Override
//                                public void onClick(View view) {
//                                    if (!txtComment.getText().toString().isEmpty()) {
//
//                                        final Calendar c = Calendar.getInstance();
//                                        SimpleDateFormat sdf = new SimpleDateFormat("dd:MMMM:yyyy HH:mm:ss a");
//                                        final String strDate = sdf.format(c.getTime());
//                                        final Comment comment = new Comment();
//
//
//                                        final DatabaseReference newComment = databaseComments.push();
//
//                                        comment.comment = txtComment.getText().toString();
//                                        comment.date = strDate;
//                                        comment.uid = post.uid;
//                                        comment.name = post.name;
//                                        comment.id = newComment.getKey();
//
//                                        newComment.setValue(comment).addOnCompleteListener(new OnCompleteListener<Void>() {
//                                            @Override
//                                            public void onComplete(@NonNull Task<Void> task) {
//                                                if (task.isSuccessful()) {
//
//                                                    Toast.makeText(PostDetail.this, "Successfully commented!", Toast.LENGTH_SHORT).show();
//                                                    dialog.dismiss();
//                                                    if (post.comments != null) {
//                                                        txtViewCommentCount.setText("" + (post.comments.size() + 1));
//                                                    } else {
//                                                        txtViewCommentCount.setText("1");
//                                                    }
//
//                                                }
//                                            }
//                                        });
//
//
//                                    }
//                                }
//                            });
//                        } else {
//                            Toast.makeText(PostDetail.this, "Please login to post comment!", Toast.LENGTH_SHORT).show();
//                        }

                    }
                });


                btnFavourite.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (auth.getCurrentUser() != null) {
                            databaseFavourite.child(user.getUid()).runTransaction(new Transaction.Handler() {
                                @Override
                                public Transaction.Result doTransaction(MutableData mutableData) {
                                    User u = mutableData.getValue(User.class);
                                    if (u == null) {

                                        return Transaction.success(mutableData);

                                    }

                                    if (u.favourite != null) {
                                        if (u.favourite.containsKey(post.id)) {
                                            u.favourite.remove(post.id);
                                            new CustomTaskUnFavourite().execute((Void[]) null);

                                        } else {

                                            u.favourite.put(post.id, true);
                                            new CustomTaskFavourite().execute((Void[]) null);

                                        }
                                    } else {
                                        u.favourite = new HashMap<String, Boolean>();
                                        u.favourite.put(post.id, true);
                                        new CustomTaskFavourite().execute((Void[]) null);
                                    }

                                    // Set value and report transaction success
                                    mutableData.setValue(u);
                                    return Transaction.success(mutableData);
                                }

                                @Override
                                public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {
                                    Log.d(TAG, "postTransaction:onComplete:" + databaseError);
                                }
                            });

                        } else {
                            Toast.makeText(PostDetail.this, "Please login to favourite post", Toast.LENGTH_SHORT).show();
                        }

//                        if (auth.getCurrentUser() != null) {
//                            final ArrayList<String> check = new ArrayList<String>();
//                            databaseFavourite.addValueEventListener(new ValueEventListener() {
//                                @Override
//                                public void onDataChange(DataSnapshot dataSnapshot) {
//                                    User user = dataSnapshot.getValue(User.class);
//                                    if (user.favourite != null) {
//                                        for (Object value : user.favourite.values()) {
//                                            check.add(value.toString());
//                                        }
//                                    }
//                                }
//
//                                @Override
//                                public void onCancelled(DatabaseError databaseError) {
//
//                                }
//                            });
//
//                            Boolean done = false;
//                            for (int i = 0; i < check.size(); i++) {
//                                if (check.get(i).toString().equals(post.id)) {
//                                    Toast.makeText(PostDetail.this, "Already favourited this post", Toast.LENGTH_SHORT).show();
//                                    done = true;
//                                }
//                            }
//
//                            if (done = false) {
//                                databaseFavourite.child("favourite").push().setValue(post.id);
//                                Toast.makeText(PostDetail.this, "You have favourited this post", Toast.LENGTH_SHORT).show();
//
//                            }
//
//                        } else {
//                            Toast.makeText(PostDetail.this, "Please login to favourite this post", Toast.LENGTH_SHORT).show();
//                        }
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


    private class CustomTaskLike extends AsyncTask<Void, Void, Void> {

        protected Void doInBackground(Void... param) {
            //Do some work
            return null;
        }

        protected void onPostExecute(Void param) {
            Toast.makeText(PostDetail.this, "Liked ", Toast.LENGTH_SHORT).show();
        }
    }

    private class CustomTaskDislike extends AsyncTask<Void, Void, Void> {

        protected Void doInBackground(Void... param) {
            //Do some work
            return null;
        }

        protected void onPostExecute(Void param) {
            Toast.makeText(PostDetail.this, "Disliked ", Toast.LENGTH_SHORT).show();
        }
    }

    private class CustomTaskFavourite extends AsyncTask<Void, Void, Void> {

        protected Void doInBackground(Void... param) {
            //Do some work
            return null;
        }

        protected void onPostExecute(Void param) {
            Toast.makeText(PostDetail.this, "Favourited ", Toast.LENGTH_SHORT).show();
        }
    }

    private class CustomTaskUnFavourite extends AsyncTask<Void, Void, Void> {

        protected Void doInBackground(Void... param) {
            //Do some work
            return null;
        }

        protected void onPostExecute(Void param) {
            Toast.makeText(PostDetail.this, "Unfavourited ", Toast.LENGTH_SHORT).show();
        }
    }
}