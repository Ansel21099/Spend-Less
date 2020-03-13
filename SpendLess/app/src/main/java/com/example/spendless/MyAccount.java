package com.example.spendless;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class MyAccount extends AppCompatActivity {

    TextView tvname,tvemail,tvcurrency,tvrating,tvincome,tvexpense;
    String name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_account);

        tvname = findViewById(R.id.maTvName);
        tvcurrency = findViewById(R.id.maTvCurrency);
        tvemail = findViewById(R.id.maTvEmail);
        tvexpense = findViewById(R.id.maTvExpense);
        tvincome = findViewById(R.id.maTvIncome);
        tvrating = findViewById(R.id.maTvExpenseRating);
        findViewById(R.id.maPbLoading).setVisibility(View.VISIBLE);
        findViewById(R.id.maViewloading).setVisibility(View.VISIBLE);
        findViewById(R.id.maBtnEditProfile).setVisibility(View.INVISIBLE);



        final Query getAccountDetailsQuery = FirebaseDatabase.getInstance().getReference(Constants.TBL_USER_DATA);
        getAccountDetailsQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot post : dataSnapshot.getChildren())
                {
                    if(post.getKey().equals(Constants.uid)) {
                        findViewById(R.id.maPbLoading).setVisibility(View.GONE);
                        findViewById(R.id.maViewloading).setVisibility(View.GONE);
                        findViewById(R.id.maBtnEditProfile).setVisibility(View.VISIBLE);
                        tvname.setText(getString(R.string.namesd) + post.child("name").getValue());
                        name = ""+post.child("name").getValue();
                        tvcurrency.setText(getString(R.string.currency) + post.child("currency").getValue());
                        tvemail.setText(getString(R.string.emal) + post.child("email").getValue());
                        tvexpense.setText(getString(R.string.totalexpense) + post.child("totalexpense").getValue());
                        tvincome.setText(getString(R.string.totalincom) + post.child("totalincome").getValue());
                        tvrating.setText(getString(R.string.rating) + post.child("rating").getValue());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        findViewById(R.id.maBtnEditProfile).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(),EditAccount.class);
                i.putExtra("name",name);
                startActivity(i);
            }
        });

    }
}
