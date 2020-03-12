package com.example.spendless;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.CountDownTimer;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SplashScreen extends AppCompatActivity {

    String email,pass,phone,name;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        boolean isConnected=false;
        try {
            ConnectivityManager cm = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
            isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();
        }catch(NullPointerException e)
        {
            e.printStackTrace();
        }
        if(!isConnected)
            new ShowToast(getApplicationContext(),"No active internet connection");

        mAuth = FirebaseAuth.getInstance();

        SharedPreferences sh = getSharedPreferences("login",MODE_PRIVATE);

        email = sh.getString("email","null" );
        pass =  sh.getString("password","");
        name = sh.getString("name","");

        if(email.equals("null"))
        {
            Intent i = new Intent(getApplicationContext(),Login.class);
            startActivity(i);
            finish();
        }
        else {
            mAuth.signInWithEmailAndPassword(email, pass).addOnCompleteListener(SplashScreen.this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        Constants.NAME = name;
                        Constants.EMAIL = email;
                        FirebaseUser user = mAuth.getCurrentUser();
                        Intent intent = new Intent(getApplicationContext(), Home_Page.class);
                        finishAffinity();
                        startActivity(intent);
                    } else {
                        // If sign in fails, display a message to the user.
                        new ShowToast(SplashScreen.this, "Authentication Failed");
                        Intent i = new Intent(getApplicationContext(),Login.class);
                        startActivity(i);
                        finish();
                    }
                }
            });
        }
    }

}
