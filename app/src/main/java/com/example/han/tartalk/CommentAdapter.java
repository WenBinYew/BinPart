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

    public CommentAdapter(Context context) {

        super(context);
        DatabaseReference databaseComments = FirebaseDatabase.getInstance().getReference().child("Comments");
    }

    @Override
    public void footerOnVisibleItem() {
    }


    @Override
    public CommentViewHolder onCreateDataItemViewHolder(ViewGroup parent, int viewType) {
        // LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.post_cardview, parent, false);

        CommentViewHolder itemViewHolder = new CommentViewHolder(view);

        return itemViewHolder;
    }

    @Override
    public void onBindDataItemViewHolder(final CommentViewHolder holder, int position) {

//        holder.txtViewTitle.setText(getData().get(position).title);
//        holder.txtViewContent.setText(getData().get(position).content);
//
//
//        holder.txtViewPostName.setText(getData().get(position).name);
//        Picasso.with(mContext)
//                .load(getData().get(position).image)
//                .resize(150, 150)
//                .centerCrop()
//                .into(holder.imgViewImage);
//
//        Calendar c = Calendar.getInstance();
//        SimpleDateFormat sdf = new SimpleDateFormat("dd:MMMM:yyyy HH:mm:ss a");
//        final String strDate = sdf.format(c.getTime());
//        Date d1 = null;
//        Date d2 = null;
//        try {
//            d1 = sdf.parse(getData().get(position).date);
//            d2 = sdf.parse(strDate);
//
//            Calendar cal1 = Calendar.getInstance();
//            cal1.setTime(d1);
//            Calendar cal2 = Calendar.getInstance();
//            cal2.setTime(d2);
//
//            Long result = daysBetween(cal1.getTime(), cal2.getTime());
//            if(result == 0){
//                holder.txtViewDate.setText(""+ "Today");
//            }else if(result == 1) {
//                holder.txtViewDate.setText(result.toString() + " day ago");
//            }else{
//                holder.txtViewDate.setText(result.toString() + " days ago");
//            }
//
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }
//
//        if(getData().get(position).likes != null){
//            holder.txtViewLikeCount.setText(""+getData().get(position).likes.size());
//        }else{
//            holder.txtViewLikeCount.setText(""+ 0);
//        }
//
//        if(getData().get(position).dislikes != null){
//            holder.txtViewDislikeCount.setText(""+getData().get(position).dislikes.size());
//        }else{
//            holder.txtViewDislikeCount.setText(""+ 0);
//        }
//
//        if(getData().get(position).comments != null){
//            holder.txtViewCommentCount.setText(""+getData().get(position).comments.size());
//        }else{
//            holder.txtViewCommentCount.setText(""+ 0);
//        }



//        holder.cvPost.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent postDetails = new Intent(view.getContext(), PostDetail.class);
//                mContext.startActivity(postDetails);
//
//            }
//        });
//
//        holder.btnComment.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent postDetails = new Intent(view.getContext(), PostDetail.class);
//                mContext.startActivity(postDetails);
//            }
//        });
//
//        holder.txtViewCommentCount.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent postDetails = new Intent(view.getContext(), PostDetail.class);
//                mContext.startActivity(postDetails);
//            }
//        });


    }

    //View Holder class
    public static class CommentViewHolder extends RecyclerView.ViewHolder {

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



        public CommentViewHolder(View itemView) {
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
