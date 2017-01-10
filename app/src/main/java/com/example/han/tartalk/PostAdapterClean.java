package com.example.han.tartalk;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;

import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;



class PostAdapterClean extends HFRecyclerViewAdapter<Post, PostAdapterClean.PostViewHolder> implements ItemTouchHelperAdapter {
    private DatabaseReference databasePosts = FirebaseDatabase.getInstance().getReference().child("Posts");
    private DatabaseReference databaseComments = FirebaseDatabase.getInstance().getReference().child("Comments");
    private DatabaseReference databaseFavourite = FirebaseDatabase.getInstance().getReference().child("Users");
    private FirebaseAuth auth;
    private FirebaseUser user;

    private static final String TAG = "Post Adapter";


    public PostAdapterClean(Context context) {

        super(context);

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

    }


    @Override
    public void footerOnVisibleItem() {
    }


    @Override
    public PostViewHolder onCreateDataItemViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.post_cardview_clean, parent, false);

        PostViewHolder itemViewHolder = new PostViewHolder(view);

        return itemViewHolder;
    }

    @Override
    public void onBindDataItemViewHolder(final PostViewHolder holder, final int position) {
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

        holder.cvPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent postDetails = new Intent(view.getContext(), PostDetail.class);
                postDetails.putExtra("PostID", getData().get(position).id);
                mContext.startActivity(postDetails);

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

            if (getData().get(position).uid.equals(auth.getCurrentUser().getUid())) {
                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);

                builder.setTitle("Confirmation");
                builder.setMessage("Are you sure you want to delete this post?");

                builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {


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

                        dialog.dismiss();
                        notifyDataSetChanged();
                    }
                });

                AlertDialog alert = builder.create();
                alert.show();

            } else {

                notifyDataSetChanged();
                Toast.makeText(mContext, "Not authorized to delete", Toast.LENGTH_SHORT).show();
            }
        } else {
            notifyDataSetChanged();
            Toast.makeText(mContext, "Not authorized to delete", Toast.LENGTH_SHORT).show();
        }

    }


    public static class PostViewHolder extends RecyclerView.ViewHolder implements ItemTouchHelperViewHolder {

        public CardView cvPost;
        public RecyclerView rvPost;
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


        public PostViewHolder(View itemView) {
            super(itemView);
            cvPost = (CardView) itemView.findViewById(R.id.cvPostClean);
            rvPost = (RecyclerView) itemView.findViewById(R.id.rvPost);
            txtViewContent = (TextView) itemView.findViewById(R.id.txtViewContent);
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
        }

        @Override
        public void onItemSelected() {
            itemView.setBackgroundColor(Color.rgb(245, 245, 245));

        }

        @Override
        public void onItemClear() {
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