package com.example.han.tartalk;

import android.app.FragmentManager;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

public class MyHistoryActivity extends AppCompatActivity {

    private RecyclerView myHistoryRecycleView;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_history);
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Posts");

        myHistoryRecycleView = (RecyclerView)findViewById(R.id.myHistoryRecycleView);
        myHistoryRecycleView.setHasFixedSize(true);
        myHistoryRecycleView.setLayoutManager(new LinearLayoutManager(this));




}


    @Override
    protected void onStart() {
        super.onStart();
        FirebaseRecyclerAdapter<recycleViewAdapter,ViewHolder> firebaseRecycleAdapter = new FirebaseRecyclerAdapter<recycleViewAdapter,ViewHolder>(recycleViewAdapter.class, R.layout.history_row,ViewHolder.class,mDatabase) {

            @Override
            protected void populateViewHolder(ViewHolder viewHolder, recycleViewAdapter model, final int position) {

                viewHolder.setTitle(model.getTitle());
                viewHolder.setContent(model.getContent());
                viewHolder.setDate(model.getDate());
                viewHolder.setNickname(model.getName());
                viewHolder.setImage(getApplicationContext(),model.getImage());
                viewHolder.view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        /*if(position == 1){

                            startActivity(new Intent(view.getContext(),LoginActivity.class));
                        }*/
                        Toast.makeText(MyHistoryActivity.this,"Position : " + position , Toast.LENGTH_SHORT).show();
                    }
                });
            }


        };



        myHistoryRecycleView.setAdapter(firebaseRecycleAdapter);

}


    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                getSupportFragmentManager().popBackStack("ProfileFragment", FragmentManager.POP_BACK_STACK_INCLUSIVE);

                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        View view;

        public ViewHolder(View itemView) {
            super(itemView);
            view = itemView;
        }

        public void setNickname(String Name){
            TextView textViewNickname;
            textViewNickname = (TextView)itemView.findViewById(R.id.textViewNickname);
            textViewNickname.setText(Name);

        }
        public void setTitle(String Title){
            TextView textViewTitle;
            textViewTitle = (TextView)itemView.findViewById(R.id.textViewTitle);
            textViewTitle.setText(Title);

        }
        public void setContent(String Content){
            TextView textViewContent;
            textViewContent = (TextView)itemView.findViewById(R.id.textViewContent);
            textViewContent.setText(Content);

        }
        public void setDate(String Date){
            TextView textViewDate;
            textViewDate = (TextView)itemView.findViewById(R.id.textViewDate);
            textViewDate.setText(Date);
        }

        public void setImage(Context c , String Image){
            ImageView imageHistory;
            imageHistory = (ImageView)itemView.findViewById(R.id.imageHistory);
            Picasso.with(c).load(Image).into(imageHistory);

        }
    }

}
