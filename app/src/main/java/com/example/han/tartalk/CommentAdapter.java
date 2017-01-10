package com.example.han.tartalk;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.vision.text.Text;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.TimeZone;

/**
 * Created by han on 21/12/2016.
 */

class CommentAdapter extends HFRecyclerViewAdapter<Comment, CommentAdapter.CommentViewHolder> {

    private DatabaseReference databaseComments = FirebaseDatabase.getInstance().getReference().child("Comments");
    //public String postID;
    private static final String TAG = "Comment Adapter";
    private FirebaseAuth auth;
    private FirebaseUser user;

    public CommentAdapter(Context context) {

        super(context);

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        //databaseComments = FirebaseDatabase.getInstance().getReference().child("Comments");
        //DatabaseReference databasePost = FirebaseDatabase.getInstance().getReference().child("Post");
        //databaseComments = FirebaseDatabase.getInstance().getReference().child("Posts").child(postID).child("comments");

    }

    @Override
    public void footerOnVisibleItem() {
    }

//    public void setPostID(String id ){
//        this.postID = id;
//    }

    @Override
    public CommentViewHolder onCreateDataItemViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.post_comment, parent, false);


        CommentViewHolder itemViewHolder = new CommentViewHolder(view);

        return itemViewHolder;
    }

    @Override
    public void onBindDataItemViewHolder(final CommentViewHolder holder, final int position) {


        holder.txtViewNameComment.setText(getData().get(position).name);
        holder.txtViewComment.setText(getData().get(position).comment);

        final Calendar c = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("dd:MMMM:yyyy HH:mm:ss a");
        final String strDate = sdf.format(c.getTime());
        Date d1 = null;
        Date d2 = null;
        try {
            d1 = sdf.parse(getData().get(position).date);
            d2 = sdf.parse(strDate);

            Calendar cal1 = Calendar.getInstance();
            cal1.setTime(d1);
            Calendar cal2 = Calendar.getInstance();
            cal2.setTime(d2);

            Long result = daysBetween(cal1.getTime(), cal2.getTime());
            if (result == 0) {
                holder.txtViewDateComment.setText("" + "Today");
            } else if (result == 1) {
                holder.txtViewDateComment.setText(result.toString() + " day ago");
            } else {
                holder.txtViewDateComment.setText(result.toString() + " days ago");
            }

        } catch (ParseException e) {
            e.printStackTrace();
        }



        holder.txtViewLikeCountComment.setText("" + getData().get(position).likeCount);
        holder.txtViewDislikeCountComment.setText("" + getData().get(position).dislikeCount);

        holder.btnLikeComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (auth.getCurrentUser() != null) {
                    databaseComments.child(getData().get(position).postid).child(getData().get(position).id).runTransaction(new Transaction.Handler() {
                        @Override
                        public Transaction.Result doTransaction(MutableData mutableData) {
                            Comment c = mutableData.getValue(Comment.class);
                            if (c == null) {

                                return Transaction.success(mutableData);
                            }
                            if (c.likes != null) {
                                if (c.likes.containsKey(auth.getCurrentUser().getUid())) {

                                    c.likeCount = c.likeCount - 1;
                                    c.likes.remove(auth.getCurrentUser().getUid());


                                } else {

                                    c.likeCount = c.likeCount + 1;
                                    c.likes.put(auth.getCurrentUser().getUid(), true);
                                    new CustomTaskLike().execute((Void[]) null);

                                }
                            } else {
                                c.likes = new HashMap<String, Boolean>();
                                c.likeCount = c.likeCount + 1;
                                c.likes.put(auth.getCurrentUser().getUid(), true);
                                new CustomTaskLike().execute((Void[]) null);
                            }

                            mutableData.setValue(c);
                            return Transaction.success(mutableData);
                        }

                        @Override
                        public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {
                            Log.d(TAG, "postTransaction:onComplete:" + databaseError);
                        }
                    });


                } else {
                    Toast.makeText(mContext, "Please login to like comment", Toast.LENGTH_SHORT).show();
                }
            }

        });

        holder.btnDisikeComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (auth.getCurrentUser() != null) {
                    databaseComments.child(getData().get(position).postid).child(getData().get(position).id).runTransaction(new Transaction.Handler() {
                        @Override
                        public Transaction.Result doTransaction(MutableData mutableData) {
                            Comment c = mutableData.getValue(Comment.class);
                            if (c == null) {

                                return Transaction.success(mutableData);
                            }
                            if (c.dislikes != null) {
                                if (c.dislikes.containsKey(auth.getCurrentUser().getUid())) {

                                    c.dislikeCount = c.dislikeCount - 1;
                                    c.dislikes.remove(auth.getCurrentUser().getUid());


                                } else {

                                    c.dislikeCount = c.dislikeCount + 1;
                                    c.dislikes.put(auth.getCurrentUser().getUid(), true);
                                    new CustomTaskDislike().execute((Void[]) null);

                                }
                            } else {
                                c.dislikes = new HashMap<String, Boolean>();
                                c.dislikeCount = c.dislikeCount + 1;
                                c.dislikes.put(auth.getCurrentUser().getUid(), true);
                                new CustomTaskDislike().execute((Void[]) null);
                            }

                            mutableData.setValue(c);
                            return Transaction.success(mutableData);
                        }

                        @Override
                        public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {
                            Log.d(TAG, "postTransaction:onComplete:" + databaseError);
                        }
                    });


                } else {
                    Toast.makeText(mContext, "Please login to dislike post", Toast.LENGTH_SHORT).show();
                }
            }

        });


    }

    //View Holder class
    public static class CommentViewHolder extends RecyclerView.ViewHolder {


        public TextView txtViewNameComment;
        public TextView txtViewComment;
        public TextView txtViewDislikeCountComment;
        public TextView txtViewLikeCountComment;
        public Button btnLikeComment;
        public Button btnDisikeComment;
        public TextView txtViewDateComment;


        public CommentViewHolder(View itemView) {
            super(itemView);

            txtViewNameComment = (TextView) itemView.findViewById(R.id.txtViewNameComment);
            txtViewComment = (TextView) itemView.findViewById(R.id.txtViewComment);
            txtViewDislikeCountComment = (TextView) itemView.findViewById(R.id.txtViewDislikeCountComment);
            txtViewLikeCountComment = (TextView) itemView.findViewById(R.id.txtViewLikeCountComment);
            btnLikeComment = (Button) itemView.findViewById(R.id.btnLikeComment);
            btnDisikeComment = (Button) itemView.findViewById(R.id.btnDisikeComment);
            txtViewDateComment = (TextView) itemView.findViewById(R.id.txtViewDateComment);
        }
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
            Toast.makeText(mContext, "Liked ", Toast.LENGTH_SHORT).show();
        }
    }

    private class CustomTaskDislike extends AsyncTask<Void, Void, Void> {

        protected Void doInBackground(Void... param) {
            //Do some work
            return null;
        }

        protected void onPostExecute(Void param) {
            Toast.makeText(mContext, "Disliked ", Toast.LENGTH_SHORT).show();
        }
    }

}
