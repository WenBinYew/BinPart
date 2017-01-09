package com.example.han.tartalk;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;
import is.arontibo.library.ElasticDownloadView;
import is.arontibo.library.ProgressDownloadView;


public class HomeFragment extends android.support.v4.app.Fragment {

    @InjectView(R.id.elastic_download_view)
    ElasticDownloadView mElasticDownloadView;

    public RecyclerView rvPost;
    private SwipeRefreshLayout swipeRefresh;
    public static ArrayList<Post> postList = new ArrayList<Post>();
    private DatabaseReference database;

    //private DatabaseReference databaseComments;
    private static final String TAG = "HomeFragment";
    private ItemTouchHelper mItemTouchHelper;
    private PostAdapter adapter;

    public HomeFragment() {


    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, final Bundle savedInstanceState) {
        ButterKnife.inject(getActivity());

        View v = inflater.inflate(R.layout.home_fragment, container, false);
        mElasticDownloadView = (ElasticDownloadView) v.findViewById(R.id.elastic_download_view);

        new Handler().post(new Runnable() {
            @Override
            public void run() {
                mElasticDownloadView.startIntro();
            }

        });


        rvPost = (RecyclerView) v.findViewById(R.id.rvPost);
        database = FirebaseDatabase.getInstance().getReference().child("Posts");
        swipeRefresh = (SwipeRefreshLayout) v.findViewById(R.id.swipeRefresh);

        rvPost.setHasFixedSize(true);
        final LinearLayoutManager manager = new LinearLayoutManager(getContext());
        rvPost.setLayoutManager(manager);
        adapter = new PostAdapter(getContext());
        retrieve();
        rvPost.setAdapter(adapter);
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {

            @Override
            public void onRefresh() {
                HomeFragment fragment = new HomeFragment();
                getFragmentManager().beginTransaction().replace(R.id.main_container, fragment).commit();
                MainActivity.bottomBar.selectTabAtPosition(0, true);

            }
        });

        ItemTouchHelper.Callback callback = new SimpleItemTouchHelperCallback(adapter);
        mItemTouchHelper = new ItemTouchHelper(callback);
        mItemTouchHelper.attachToRecyclerView(rvPost);

        new Handler().postAtTime(new Runnable() {
            @Override
            public void run() {
                mElasticDownloadView.success();
            }
        }, 2 * ProgressDownloadView.ANIMATION_DURATION_BASE);

        return v;
    }


    public void retrieve() {
        postList = new ArrayList<>();
        database.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {


                fetchData(dataSnapshot);

                LinearLayoutManager layoutManager = ((LinearLayoutManager) rvPost.getLayoutManager());
                int firstVisiblePosition = layoutManager.findFirstCompletelyVisibleItemPosition();

                // Toast.makeText(getContext(), ""+firstVisiblePosition , Toast.LENGTH_SHORT).show();
                adapter = new PostAdapter(getContext());
                adapter.setData(postList);
                rvPost.setAdapter(adapter);
//                if(firstVisiblePosition != 0){
//                    rvPost.scrollToPosition(firstVisiblePosition);
//                }else{
//                    rvPost.scrollToPosition(0);
//                }
//
                rvPost.scrollToPosition(firstVisiblePosition);
                swipeRefresh.setRefreshing(false);
                mElasticDownloadView.setVisibility(View.INVISIBLE);


//                fetchData(dataSnapshot);
//                PostAdapter adapter = new PostAdapter(getContext());
//
//
//                rvPost.setAdapter(adapter);
//                adapter.setData(postList);
//                swipeRefresh.setRefreshing(false);
//                mElasticDownloadView.setVisibility(View.INVISIBLE);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
                // [START_EXCLUDE]

            }
        });

    }

    private void fetchData(DataSnapshot dataSnapShot) {
        postList = new ArrayList<>();
        for (DataSnapshot ds : dataSnapShot.getChildren()) {


            final Post post = ds.getValue(Post.class);

            postList.add(post);


        }

    }

}