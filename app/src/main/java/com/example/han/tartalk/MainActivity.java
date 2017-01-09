package com.example.han.tartalk;


import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Handler;
import android.support.annotation.IntegerRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.multidex.MultiDex;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.OnMenuTabSelectedListener;

import butterknife.ButterKnife;
import butterknife.InjectView;
import is.arontibo.library.ElasticDownloadView;
import is.arontibo.library.ProgressDownloadView;


public class MainActivity extends AppCompatActivity {


    public static BottomBar bottomBar;

    private FirebaseAuth auth;
    private boolean userLogin;
    private final static String TAG = "Main Activity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        MultiDex.install(this);


        auth = FirebaseAuth.getInstance();


        if (auth.getCurrentUser() == null) {
            Toast.makeText(this, "View as guest ", Toast.LENGTH_SHORT).show();
            userLogin = false;


        } else {
            Toast.makeText(this, "Email : " + auth.getCurrentUser().getEmail(), Toast.LENGTH_SHORT).show();
            userLogin = true;
        }


        HomeFragment fragment = new HomeFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.main_container, fragment).commit();


        bottomBar = BottomBar.attach(this, savedInstanceState);
        bottomBar.setItemsFromMenu(R.menu.five_buttons_menu, new OnMenuTabSelectedListener() {
            @Override
            public void onMenuItemSelected(int menuItemId) {
                if (menuItemId == R.id.bottomBarItemOne) {
                    HomeFragment fragment = new HomeFragment();
                    getSupportFragmentManager().beginTransaction().replace(R.id.main_container, fragment).commit();
                } else if (menuItemId == R.id.bottomBarItemTwo) {
                    SearchFragment fragment = new SearchFragment();
                    getSupportFragmentManager().beginTransaction().replace(R.id.main_container, fragment).commit();
                } else if (menuItemId == R.id.bottomBarItemThree) {
                    PostFragment fragment = new PostFragment();
                    getSupportFragmentManager().beginTransaction().replace(R.id.main_container, fragment).commit();
                } else if (menuItemId == R.id.bottomBarItemFour) {
                    FavouriteFragment fragment = new FavouriteFragment();
                    getSupportFragmentManager().beginTransaction().replace(R.id.main_container, fragment).commit();
                } else if (menuItemId == R.id.bottomBarItemFive) {
                    ProfileFragment fragment = new ProfileFragment();
                    getSupportFragmentManager().beginTransaction().replace(R.id.main_container, fragment).commit();

                }
            }
        });


    }


    @Override
    protected void onStart() {
        super.onStart();


    }
}