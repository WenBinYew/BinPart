package com.example.han.tartalk;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by han on 21/12/2016.
 */

class CommentAdapter extends HFRecyclerViewAdapter<Comment, CommentAdapter.CommentViewHolder> {

    // private DatabaseReference databaseComments;
    //public String postID;

    private FirebaseAuth auth;
    private FirebaseUser user;

    public CommentAdapter(Context context) {

        super(context);

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        //DatabaseReference databaseComments = FirebaseDatabase.getInstance().getReference().child("Post");
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
        // LayoutInflater inflater = LayoutInflater.from(parent.getContext());


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


        if (getData().get(position).dislikes != null) {
            holder.txtViewDislikeCountComment.setText("" + getData().get(position).dislikes.size());
        } else {
            holder.txtViewDislikeCountComment.setText("" + 0);
        }

        if (getData().get(position).likes != null) {
            holder.txtViewLikeCountComment.setText("" + getData().get(position).likes.size());
        } else {
            holder.txtViewLikeCountComment.setText("" + 0);
        }


        holder.btnLikeComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (auth.getCurrentUser() != null) {
                    ArrayList<String> check = new ArrayList<String>();
                    if (getData().get(position).likes != null) {
                        for (Object value : getData().get(position).likes.values()) {
                            check.add(value.toString());
                        }
                        Boolean done = false;
                        for (int i = 0; i < check.size(); i++) {
                            if (check.get(i).toString().equals(getData().get(position).uid)) {
                                Toast.makeText(mContext, "Already liked this comment", Toast.LENGTH_SHORT).show();
                                done = true;
                            }
                        }
                        if (done = false) {
//                            int x = getData().get(position).likes.size();
//                            holder.txtViewLikeCountComment.setText("" + (x + 1));
                            PostDetail.databaseComments.child(getData().get(position).id).child("likes").push().setValue(user.getUid());
                            PostDetail.rvComment.smoothScrollToPosition(position);

                        }

                    } else {

                        //holder.txtViewLikeCountComment.setText("1");
                        PostDetail.databaseComments.child(getData().get(position).id).child("likes").push().setValue(user.getUid());
                        PostDetail.rvComment.smoothScrollToPosition(position);
                    }
                } else {
                    Toast.makeText(mContext, "Please login to like comment", Toast.LENGTH_SHORT).show();
                }
            }

        });

        holder.btnDisikeComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (auth.getCurrentUser() != null) {
                    ArrayList<String> check = new ArrayList<String>();
                    if (getData().get(position).dislikes != null) {
                        for (Object value : getData().get(position).dislikes.values()) {
                            check.add(value.toString());
                        }
                        Boolean done = false;
                        for (int i = 0; i < check.size(); i++) {
                            if (check.get(i).toString().equals(getData().get(position).uid)) {
                                Toast.makeText(mContext, "Already disliked this comment", Toast.LENGTH_SHORT).show();
                                done = true;
                            }
                        }

                        if (done = false) {
//                            int x = getData().get(position).dislikes.size();
//                            holder.txtViewDislikeCountComment.setText("" + (x + 1));
                            PostDetail.databaseComments.child(getData().get(position).id).child("dislikes").push().setValue(user.getUid());
                            PostDetail.rvComment.smoothScrollToPosition(position);
                        }
                    } else {

//                        holder.txtViewDislikeCountComment.setText("1");
                        PostDetail.databaseComments.child(getData().get(position).id).child("dislikes").push().setValue(user.getUid());
                        PostDetail.rvComment.smoothScrollToPosition(position);
                    }
                } else {
                    Toast.makeText(mContext, "Please login to dislike comment", Toast.LENGTH_SHORT).show();
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

}
