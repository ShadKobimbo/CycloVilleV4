package com.example.cyclovillev4;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

/**
    * Created by Shad Kobimbo on 7/4/2019.
        */

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent i = new Intent(SplashActivity.this, LandingActivity.class);
                //Intent is used to switch from one activity to another.

                SplashActivity.this.startActivity(i);
                //invoke the SecondActivity.

                SplashActivity.this.finish();
                //the current activity will get finished.
            }
        }, 3000);
    }
}
