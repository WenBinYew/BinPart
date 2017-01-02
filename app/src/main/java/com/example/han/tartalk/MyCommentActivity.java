package com.example.han.tartalk;

import android.app.FragmentManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

public class MyCommentActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_comment);
    }
    public void onResume() {
        super.onResume();
    }

    @Override
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
}

