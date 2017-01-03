package com.example.han.tartalk;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;

import java.util.concurrent.TimeUnit;

/**
 * Created by Bin on 6/12/2016.
 */

public class SplashActivity extends AppCompatActivity {

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //SystemClock.sleep(TimeUnit.SECONDS.toMillis(2));
        finish();
        startActivity(new Intent(this,MainActivity.class));

    }

    class BackgroundTask extends AsyncTask<Void ,Void,Void>{
        @Override
        protected Void doInBackground(Void... voids) {
            return null;
        }
    }
}
