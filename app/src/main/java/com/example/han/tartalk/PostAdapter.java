package com.example.han.tartalk;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.drive.query.Query;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import java.util.concurrent.ExecutionException;

/**
 * Created by han on 21/12/2016.
 */

class PostAdapter extends HFRecyclerViewAdapter<Post, PostAdapter.PostViewHolder> {
    private DatabaseReference databaseComments;


    public PostAdapter(Context context) {

        super(context);
        databaseComments = FirebaseDatabase.getInstance().getReference().child("Posts");
    }

    @Override
    public void footerOnVisibleItem() {
    }


    @Override
    public PostViewHolder onCreateDataItemViewHolder(ViewGroup parent, int viewType) {
        // LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.post_cardview, parent, false);

        PostViewHolder itemViewHolder = new PostViewHolder(view);

        return itemViewHolder;
    }

    @Override
    public void onBindDataItemViewHolder(final PostViewHolder holder, final int position) {


        // Post post = postList.get(position);
        holder.txtViewTitle.setText(getData().get(position).title);
        holder.txtViewContent.setText(getData().get(position).content);


        holder.txtViewPostName.setText(getData().get(position).name);
        Picasso.with(mContext)
                .load(getData().get(position).image)
                .resize(150, 150)
                .centerCrop()
                .into(holder.imgViewImage);

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
                holder.txtViewDate.setText("" + "Today");
            } else if (result == 1) {
                holder.txtViewDate.setText(result.toString() + " day ago");
            } else {
                holder.txtViewDate.setText(result.toString() + " days ago");
            }

        } catch (ParseException e) {
            e.printStackTrace();
        }

        if (getData().get(position).likes != null) {
            holder.txtViewLikeCount.setText("" + getData().get(position).likes.size());
        } else {
            holder.txtViewLikeCount.setText("" + 0);
        }

        if (getData().get(position).dislikes != null) {
            holder.txtViewDislikeCount.setText("" + getData().get(position).dislikes.size());
        } else {
            holder.txtViewDislikeCount.setText("" + 0);
        }

        if (getData().get(position).comments != null) {
            holder.txtViewCommentCount.setText("" + getData().get(position).comments.size());
        } else {
            holder.txtViewCommentCount.setText("" + 0);
        }


        holder.cvPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent postDetails = new Intent(view.getContext(), PostDetail.class);
                postDetails.putExtra("PostID", getData().get(position).id);
                mContext.startActivity(postDetails);

            }
        });

        holder.btnComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                View v = LayoutInflater.from(mContext).inflate(R.layout.post_comment_dialog, null);
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


                            final DatabaseReference newComment = databaseComments.child(getData().get(position).id).child("comments").push();

                            comment.comment = txtComment.getText().toString();
                            comment.date = strDate;
                            comment.uid = getData().get(position).uid;
                            comment.name = getData().get(position).name;
                            comment.id = newComment.getKey();

                            newComment.setValue(comment).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {

                                        Toast.makeText(mContext, "Successfully commented!", Toast.LENGTH_SHORT).show();
                                        dialog.dismiss();
                                        if(getData().get(position).comments != null) {
                                            holder.txtViewCommentCount.setText("" + getData().get(position).comments.size() + 1);
                                        }else{
                                            holder.txtViewCommentCount.setText("1");
                                        }

                                    }
                                }
                            });


                        }
                    }
                });


            }
        });

        holder.txtViewCommentCount.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                View v = LayoutInflater.from(mContext).inflate(R.layout.post_comment_dialog, null);
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


                            final DatabaseReference newComment = databaseComments.child(getData().get(position).id).child("comments").push();

                            comment.comment = txtComment.getText().toString();
                            comment.date = strDate;
                            comment.uid = getData().get(position).uid;
                            comment.name = getData().get(position).name;
                            comment.id = newComment.getKey();

                            newComment.setValue(comment).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {

                                        Toast.makeText(mContext, "Successfully commented!", Toast.LENGTH_SHORT).show();
                                        dialog.dismiss();
                                        if(getData().get(position).comments != null) {
                                            holder.txtViewCommentCount.setText("" + getData().get(position).comments.size() + 1);
                                        }else{
                                            holder.txtViewCommentCount.setText("1");
                                        }

                                    }
                                }
                            });


                        }
                    }
                });


            }
        });

        holder.btnLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ArrayList<String> check = new ArrayList<String>();
                if (getData().get(position).likes != null) {
                    for (Object value : getData().get(position).likes.values()) {
                        check.add(value.toString());
                    }

                    for (int i = 0; i < check.size(); i++) {
                        if (check.get(i).toString().equals(getData().get(position).uid)) {
                            Toast.makeText(mContext, "Already liked this post", Toast.LENGTH_SHORT).show();
                        } else {
                            int x = getData().get(position).likes.size();
                            holder.txtViewLikeCount.setText("" + (x + 1));
                            databaseComments.child(getData().get(position).id).child("likes").push().setValue(getData().get(position).uid);
                        }
                    }
                } else {

                    holder.txtViewLikeCount.setText("1");
                    databaseComments.child(getData().get(position).id).child("likes").push().setValue(getData().get(position).uid);
                }
            }

        });

        holder.btnDislike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ArrayList<String> check = new ArrayList<String>();
                if (getData().get(position).dislikes != null) {
                    for (Object value : getData().get(position).dislikes.values()) {
                        check.add(value.toString());
                    }

                    for (int i = 0; i < check.size(); i++) {
                        if (check.get(i).toString().equals(getData().get(position).uid)) {
                            Toast.makeText(mContext, "Already disliked this post", Toast.LENGTH_SHORT).show();
                        } else {
                            int x = getData().get(position).dislikes.size();
                            holder.txtViewLikeCount.setText("" + (x + 1));
                            databaseComments.child(getData().get(position).id).child("dislikes").push().setValue(getData().get(position).uid);
                        }
                    }
                } else {

                    holder.txtViewLikeCount.setText("1");
                    databaseComments.child(getData().get(position).id).child("dislikes").push().setValue(getData().get(position).uid);
                }
            }
        });


    }


    //View Holder class
    public static class PostViewHolder extends RecyclerView.ViewHolder {

        public CardView cvPost;
        public RecyclerView rvPost;
        public TextView txtViewID;
        public TextView txtViewContent;
        public TextView txtViewDate;
        public TextView txtViewPostName;
        public TextView txtViewTitle;
        public TextView txtViewCommentCount;
        public TextView txtViewLikeCount;
        public TextView txtViewDislikeCount;
        public ImageView imgViewImage;

        public Button btnComment;
        public Button btnLike;
        public Button btnDislike;
        public Button btnFavourite;


//        public TextView textViewPostNickname;
//        public TextView textViewPostDate;
//        public ImageView imageViewPostImage;
//        public TextView textViewUpVote;
//        public TextView textViewContent;

        public PostViewHolder(View itemView) {
            super(itemView);
            cvPost = (CardView) itemView.findViewById(R.id.cvPost);
            rvPost = (RecyclerView) itemView.findViewById(R.id.rvPost);
            txtViewContent = (TextView) itemView.findViewById(R.id.txtViewContent);
            txtViewID = (TextView) itemView.findViewById(R.id.txtViewPostID);
            txtViewDate = (TextView) itemView.findViewById(R.id.txtViewPostDate);
            txtViewPostName = (TextView) itemView.findViewById(R.id.txtViewPostName);
            txtViewTitle = (TextView) itemView.findViewById(R.id.txtViewTitle);
            txtViewCommentCount = (TextView) itemView.findViewById(R.id.txtViewCommentCount);
            txtViewLikeCount = (TextView) itemView.findViewById(R.id.txtViewLikeCount);
            txtViewDislikeCount = (TextView) itemView.findViewById(R.id.txtViewDislikeCount);
            imgViewImage = (ImageView) itemView.findViewById(R.id.imgViewImage);
            btnComment = (Button) itemView.findViewById(R.id.btnComment);
            btnLike = (Button) itemView.findViewById(R.id.btnLike);
            btnDislike = (Button) itemView.findViewById(R.id.btnDisike);
            btnFavourite = (Button) itemView.findViewById(R.id.btnFavourite);


//            textViewPostNickname = (TextView) itemView.findViewById(R.id.textViewPostNickname);
//            textViewPostDate = (TextView) itemView.findViewById(R.id.textViewPostDate);
//            textViewUpVote = (TextView) itemView.findViewById(R.id.textViewVote);
//            textViewContent = (TextView) itemView.findViewById(R.id.textViewContent);
//            imageViewPostImage = (ImageView) itemView.findViewById(R.id.imageViewPostImage);


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
