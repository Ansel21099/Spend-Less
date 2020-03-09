package com.example.spendless;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;

public class SplashScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
       try {
           new CountDownTimer(5000, 1000) {

               public void onTick(long millisUntilFinished) {

               }
               public void onFinish() {
                   Intent intent = new Intent(getApplicationContext(),Login.class);
                   startActivity(intent);
                   finish();
               }

           }.start();

        } catch (Exception e) {
            e.printStackTrace();
        }


    }
}
