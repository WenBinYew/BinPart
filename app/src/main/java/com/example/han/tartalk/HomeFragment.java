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

    public static RecyclerView rvPost;
    private CardView cvPost;
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
        cvPost = (CardView) v.findViewById(R.id.cvPost);
        //FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        database = FirebaseDatabase.getInstance().getReference().child("Posts");
        swipeRefresh = (SwipeRefreshLayout) v.findViewById(R.id.swipeRefresh);
        //retrieve();
        rvPost.setHasFixedSize(true);
        final LinearLayoutManager manager = new LinearLayoutManager(getContext());
        rvPost.setLayoutManager(manager);
        //rvPost.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter =new PostAdapter(getContext());
        rvPost.setAdapter(adapter);
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {

            @Override
            public void onRefresh() {
                retrieve();

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
        }, ProgressDownloadView.ANIMATION_DURATION_BASE);

        return v;
    }


    public void retrieve() {
        postList = new ArrayList<>();
        database.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

               // int position = rvPost.getChildAdapterPosition();
//                    int position = rvPost.view
//                if(position <= 0){
//                    rvPost.smoothScrollToPosition(0);
//                }else{
//                    rvPost.smoothScrollToPosition(position);
//                }

                fetchData(dataSnapshot);
                LinearLayoutManager layoutManager = ((LinearLayoutManager)rvPost.getLayoutManager());
                int firstVisiblePosition = layoutManager.findFirstCompletelyVisibleItemPosition();
                rvPost.scrollToPosition(firstVisiblePosition);
                rvPost.setAdapter(adapter);
                adapter.setData(postList);
                swipeRefresh.setRefreshing(false);
                mElasticDownloadView.setVisibility(View.INVISIBLE);

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

//
//    public static int getCenterXChildPosition(RecyclerView recyclerView) {
//        int childCount = recyclerView.getChildCount();
//        if (childCount > 0) {
//            for (int i = 0; i < childCount; i++) {
//                View child = recyclerView.getChildAt(i);
//                if (isChildInCenterX(recyclerView, child)) {
//                    return recyclerView.getChildAdapterPosition(child);
//                }
//            }
//        }
//        return childCount;
//    }
//
//    public static boolean isChildInCenterX(RecyclerView recyclerView, View view) {
//        int childCount = recyclerView.getChildCount();
//        int[] lvLocationOnScreen = new int[2];
//        int[] vLocationOnScreen = new int[2];
//        recyclerView.getLocationOnScreen(lvLocationOnScreen);
//        int middleX = lvLocationOnScreen[0] + recyclerView.getWidth() / 2;
//        if (childCount > 0) {
//            view.getLocationOnScreen(vLocationOnScreen);
//            if (vLocationOnScreen[0] <= middleX && vLocationOnScreen[0] + view.getWidth() >= middleX) {
//                return true;
//            }
//        }
//        return false;
//    }


}
