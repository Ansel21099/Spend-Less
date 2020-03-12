package com.example.spendless;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.example.spendless.model.MBCreateAccount;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class CreateAccount extends AppCompatActivity {

    EditText etName, etEmail,etPass, etConPass;
    String sName, sEmail, sPass, sConPass, fbEmail;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference(Constants.TBL_USER_DATA);
    public FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);

        etName = findViewById(R.id.caTiName);
        etEmail = findViewById(R.id.caTiEmail);
        etPass = findViewById(R.id.caTiPassword);
        etConPass = findViewById(R.id.caTiConfirmPassword);

        mAuth = FirebaseAuth.getInstance();

        findViewById(R.id.caBtnRegister).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getdata();
            }
        });

    }

    private void insertData() {
        MBCreateAccount mb = new MBCreateAccount(sName,sEmail,"INR",0.0,0.0,0.0);
        myRef.child(Objects.requireNonNull(mAuth.getUid())).setValue(mb);
        Constants.uid = mAuth.getUid();
    }

    private void signIn() {
        Log.e("-*-*-*-*-*-*-*", sEmail + sPass);
        mAuth.createUserWithEmailAndPassword(sEmail,sPass).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    // Sign in success, update UI with the signed-in user's information
                    //FirebaseUser user = mAuth.getCurrentUser();
                    insertData();
                    new ShowToast(CreateAccount.this,"Signed In");
                    Intent intent = new Intent(getApplicationContext(),Login.class);
                    startActivity(intent);
                    finish();
                } else {
                    // If sign in fails, display a message to the user.
                    new ShowToast(getApplicationContext(), "Authentication failed.");
                }
            }
        });
    }

    private void getdata() {
        boolean flag = true;

        if (etName.getText().toString().isEmpty()) {
            etName.setError("Enter Name");
            flag=false;}

        if (etEmail.getText().toString().isEmpty()) {
            etEmail.setError("Enter Email");
            flag=false;}

        if (etPass.getText().toString().isEmpty() || etPass.getText().length()<6) {
            etPass.setError("Enter valid Password");
            flag=false;}

        if (etConPass.getText().toString().isEmpty()) {
            etConPass.setError("Enter Confirm Password");
            flag=false;}

        if (flag)
        {
            String p,c;
            p = etPass.getText().toString();
            c = etConPass.getText().toString();
            if(!p.equals(c))
                etConPass.setError("Password does not match");
            else{
                sConPass = etConPass.getText().toString();
                sPass = etPass.getText().toString();
                sName = etName.getText().toString();
                sEmail = etEmail.getText().toString().toLowerCase();
                Log.e("-*-*-*-*-*-*-*gd", sEmail + sPass);
                final Query checkuserquery = FirebaseDatabase.getInstance().getReference( Constants.TBL_USER_DATA ).orderByChild( "email" ).equalTo( sEmail );
                checkuserquery.addValueEventListener( chkUserVLE );

            }}

    }
    ValueEventListener chkUserVLE = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            try {


                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    fbEmail = "" + postSnapshot.child( "email" ).getValue();
                }
                if (sEmail.equals(fbEmail)){
                    etEmail.setError("Email already Registered");
                }
                else {
                    signIn();
                }
            } catch (Exception e) {
                Log.e("-*-**-*-Catch",e.toString());
            }

        }

        @Override
        public void onCancelled(DatabaseError databaseError) {
            Log.e("-*-**-*-Canceled",databaseError.toString());
        }
    };
}
