package com.nearur.payrec;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;

import com.victor.loading.newton.NewtonCradleLoading;

public class Splash extends AppCompatActivity {

    NewtonCradleLoading progress;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        progress=(NewtonCradleLoading) findViewById(R.id.newton_cradle_loading);
        progress.start();
        h.sendEmptyMessageDelayed(1234,2000);

    }

    Handler h =new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            startActivity(new Intent(getApplicationContext(),MainActivity.class));
            finish();
        }
    };
}
