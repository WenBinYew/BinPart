package com.example.han.tartalk;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.nfc.Tag;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
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
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v4.app.Fragment;
import com.google.android.gms.drive.query.Query;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.TimeZone;
import java.util.concurrent.ExecutionException;

/**
 * Created by han on 21/12/2016.
 */

class PostAdapter extends HFRecyclerViewAdapter<Post, PostAdapter.PostViewHolder> implements ItemTouchHelperAdapter {
    private DatabaseReference databaseComments;
    private DatabaseReference databaseFavourite;
    private FirebaseAuth auth;
    private FirebaseUser user;

    private static final String TAG = "Post Adapter";


    public PostAdapter(Context context) {

        super(context);
        databaseComments = FirebaseDatabase.getInstance().getReference().child("Posts");

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        if (user != null) {
            databaseFavourite = FirebaseDatabase.getInstance().getReference().child("Users").child(user.getUid());
        }
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

        if (getData().get(position).image.toString().equals("null")) {
            holder.imgViewImage.setPadding(0, 0, 0, 0);
            holder.imgViewImage.setBackgroundColor(Color.parseColor("#F5F5F5"));
        } else {
            Picasso.with(mContext)
                    .load(getData().get(position).image)
                    .resize(150, 150)
                    .centerCrop()
                    .into(holder.imgViewImage);
        }

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
                if (auth.getCurrentUser() != null) {
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
                                            HomeFragment.rvPost.smoothScrollToPosition(position);
                                            Toast.makeText(mContext, "Successfully commented!", Toast.LENGTH_SHORT).show();
                                            dialog.dismiss();
                                            if (getData().get(position).comments != null) {
                                                holder.txtViewCommentCount.setText("" + (getData().get(position).comments.size() + 1));
                                            } else {
                                                holder.txtViewCommentCount.setText("1");
                                            }

                                        }
                                    }
                                });


                            }
                        }
                    });


                } else {
                    Toast.makeText(mContext, "Please login to post comment!", Toast.LENGTH_SHORT).show();
                }
            }
        });


        holder.btnLike.setOnClickListener(new View.OnClickListener() {
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
                                Toast.makeText(mContext, "Already liked this post", Toast.LENGTH_SHORT).show();
                                done = true;
                            }
                        }
                        if (done = false) {
//                            int x = getData().get(position).dislikes.size();
//                            holder.txtViewDislikeCount.setText("" + (x + 1));
                            databaseComments.child(getData().get(position).id).child("likes").push().setValue(user.getUid());
                            Toast.makeText(mContext, "Liked", Toast.LENGTH_SHORT).show();
                            HomeFragment.rvPost.smoothScrollToPosition(position);
                        }
                    } else {
                        //                       int x = getData().get(position).dislikes.size();
//                        holder.txtViewDislikeCount.setText("" + (x + 1));
//                        holder.txtViewDislikeCount.setText("1");
                        databaseComments.child(getData().get(position).id).child("likes").push().setValue(user.getUid());
                        Toast.makeText(mContext, "Liked", Toast.LENGTH_SHORT).show();
                        HomeFragment.rvPost.smoothScrollToPosition(position);
                    }
                } else {
                    Toast.makeText(mContext, "Please login to dislike post", Toast.LENGTH_SHORT).show();
                }
            }

        });

        holder.btnDislike.setOnClickListener(new View.OnClickListener() {
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
                                Toast.makeText(mContext, "Already disliked this post", Toast.LENGTH_SHORT).show();
                                done = true;
                            }
                        }
                        if (done = false) {
//                            int x = getData().get(position).dislikes.size();
//                            holder.txtViewDislikeCount.setText("" + (x + 1));
                            databaseComments.child(getData().get(position).id).child("dislikes").push().setValue(user.getUid());
                            Toast.makeText(mContext, ""+position, Toast.LENGTH_SHORT).show();
                            HomeFragment.rvPost.smoothScrollToPosition(position);
                        }
                    } else {
 //                       int x = getData().get(position).dislikes.size();
//                        holder.txtViewDislikeCount.setText("" + (x + 1));
//                        holder.txtViewDislikeCount.setText("1");
                        databaseComments.child(getData().get(position).id).child("dislikes").push().setValue(user.getUid());
                        Toast.makeText(mContext, "" + position, Toast.LENGTH_SHORT).show();
                        HomeFragment.rvPost.smoothScrollToPosition(position);
                    }
                } else {
                    Toast.makeText(mContext, "Please login to dislike post", Toast.LENGTH_SHORT).show();
                }
            }
        });

        holder.rlLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                holder.btnLike.performClick();
            }
        });

        holder.rlDislike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                holder.btnDislike.performClick();
            }
        });

        holder.rlComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                holder.btnComment.performClick();
            }
        });


        holder.rlFavourite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                holder.btnFavourite.performClick();
            }
        });

        holder.btnFavourite.setOnClickListener(new View.OnClickListener() {
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
                    boolean done = false;
                    for (int i = 0; i < check.size(); i++) {
                        if (check.get(i).equals(getData().get(position).id)) {
                            Toast.makeText(mContext, "Already favourited this post", Toast.LENGTH_SHORT).show();
                            done = true;
                        }
                    }

                    if (done = false) {
                        databaseFavourite.child("favourite").push().setValue(getData().get(position).id);
                        Toast.makeText(mContext, "You have favourited this post", Toast.LENGTH_SHORT).show();
                    }

                } else {
                    Toast.makeText(mContext, "Please login to favourite this post", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }



    @Override
    public boolean onItemMove(int fromPosition, int toPosition) {
        if (fromPosition < toPosition) {
            for (int i = fromPosition; i < toPosition; i++) {
                Collections.swap(getData(), i, i + 1);
            }
        } else {
            for (int i = fromPosition; i > toPosition; i--) {
                Collections.swap(getData(), i, i - 1);
            }
        }
        notifyItemMoved(fromPosition, toPosition);
        return true;
    }

    @Override
    public void onItemDismiss(final int position) {
        if (auth.getCurrentUser() != null) {
            if (getData().get(position).uid.equals(user.getUid())) {
                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);

                builder.setTitle("Confirmation");
                builder.setMessage("Are you sure you want to delete this post?");

                builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {

                        Toast.makeText(mContext, getData().get(position).content, Toast.LENGTH_SHORT).show();

                        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
                        com.google.firebase.database.Query removeQuery = ref.child("Posts").orderByChild("id").equalTo(getData().get(position).id);

                        removeQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                                    ds.getRef().removeValue();
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                Log.e(TAG, "onCancelled", databaseError.toException());
                            }
                        });

                        getData().remove(position);
                        notifyItemRemoved(position);
                        dialog.dismiss();
                    }
                });

                builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        // Do nothing
                        dialog.dismiss();
                        notifyDataSetChanged();
                    }
                });

                AlertDialog alert = builder.create();
                alert.show();

            } else {

                notifyDataSetChanged();
                //notifyItemRemoved(position);
                Toast.makeText(mContext, "Not authorized to delete", Toast.LENGTH_SHORT).show();
            }
        } else {
            notifyDataSetChanged();
            //notifyItemRemoved(position);
            Toast.makeText(mContext, "Not authorized to delete", Toast.LENGTH_SHORT).show();
        }

    }


    //View Holder class
    public static class PostViewHolder extends RecyclerView.ViewHolder implements ItemTouchHelperViewHolder {

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

        public RelativeLayout rlComment;
        public RelativeLayout rlLike;
        public RelativeLayout rlDislike;
        public RelativeLayout rlFavourite;


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

            rlFavourite = (RelativeLayout) itemView.findViewById(R.id.btntoClickFavourite);
            rlDislike = (RelativeLayout) itemView.findViewById(R.id.btntoClickDislike);
            rlLike = (RelativeLayout) itemView.findViewById(R.id.btntoClickLike);
            rlComment = (RelativeLayout) itemView.findViewById(R.id.btntoClickComment);

//            textViewPostNickname = (TextView) itemView.findViewById(R.id.textViewPostNickname);
//            textViewPostDate = (TextView) itemView.findViewById(R.id.textViewPostDate);
//            textViewUpVote = (TextView) itemView.findViewById(R.id.textViewVote);
//            textViewContent = (TextView) itemView.findViewById(R.id.textViewContent);
//            imageViewPostImage = (ImageView) itemView.findViewById(R.id.imageViewPostImage);


        }

        @Override
        public void onItemSelected() {
            //Toast.makeText(itemView.getContext(), "Onitem selected", Toast.LENGTH_SHORT).show();
            itemView.setBackgroundColor(Color.rgb(245, 245, 245));

        }

        @Override
        public void onItemClear() {
            //Toast.makeText(itemView.getContext(), "Onitem clear", Toast.LENGTH_SHORT).show();
            itemView.setBackgroundColor(Color.rgb(245, 245, 245));
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
