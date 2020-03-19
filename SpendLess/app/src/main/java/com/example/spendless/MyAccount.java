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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

public class MyAccount extends AppCompatActivity {

    TextView tvname,tvemail,tvcurrency,tvrating,tvincome,tvexpense;
    String name,email,a,b;
    Double totalincome=0.0,totalexpense=0.0,rating=0.0;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myref = database.getReference(Constants.TBL_USER_DATA);

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
                        email = ""+ post.child("email").getValue();

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        final Query gettotalQuery = FirebaseDatabase.getInstance().getReference(Constants.TBL_TRANSACTIONS).child(Constants.uid);
        gettotalQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot post : dataSnapshot.getChildren()) {
                        for (DataSnapshot post1 : post.getChildren()) {
                            for (DataSnapshot post2 : post1.getChildren()) {
                                Log.e("*-*-", post2.getValue() + "");
                                String type = "" + post2.child("type").getValue();
                                Log.e("*-*-", post2.child("amount").getValue() + "");
                                if (type.equals("Income")){
                                    a = "" + post2.child("amount").getValue();
                                    totalincome += Double.parseDouble(a);
                                }
                                else if (type.equals("Expense")) {
                                    b = "" + post2.child("amount").getValue();
                                    totalexpense -= Double.parseDouble(b);
                                }
                            }
                        }
                    }
                    if(totalexpense==0)
                        rating = 99.9;
                    else
                        rating = totalincome/totalexpense;
                    tvexpense.setText(getString(R.string.totalexpense) + totalexpense);
                    tvincome.setText(getString(R.string.totalincom) + totalincome);
                    tvrating.setText(getString(R.string.rating) + new DecimalFormat("##.##").format(rating));


                    Map<String, Object> updates = new HashMap<String, Object>();
                        updates.put("rating",  rating);
                        updates.put("totalexpense", totalexpense);
                        updates.put("totalincome", totalincome);
                        myref.child(Constants.uid).updateChildren(updates);
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
                i.putExtra("email",email);
                startActivity(i);
            }
        });

    }
}
