package com.example.han.tartalk;


import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.OnMenuTabSelectedListener;


public class MainActivity extends AppCompatActivity {

    BottomBar bottomBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


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
}
