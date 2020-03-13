package com.example.spendless;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class Login extends AppCompatActivity {

    Button btnLogin;
    EditText etEmail, etPassword;
    TextView forgotpassword;
    String sEmail, sPassword,fbEmail,fbName,email;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mAuth = FirebaseAuth.getInstance();
        btnLogin = findViewById(R.id.alBtnLogin);
        //BtnCreate = findViewById(R.id.btnCreateAccount);
        etEmail = findViewById(R.id.alTiEmail);
        etPassword = findViewById(R.id.alTiPassword);
        forgotpassword = findViewById(R.id.forgotpassword);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new ShowToast(getApplicationContext(), "Authenticating...Please Wait");
                btnLogin.setEnabled(false);
                getdata();
            }
        });

        findViewById(R.id.alBtnRegister).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),CreateAccount.class));
            }
        });

/*
        findViewById(R.id.tvhavingtrouble).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent Email = new Intent(Intent.ACTION_SEND);
                Email.setType("text/email");
                Email.putExtra(Intent.EXTRA_EMAIL, new String[]{"milagresapp@gmail.com"});
                Email.putExtra(Intent.EXTRA_SUBJECT, "Trouble Report - " + Build.MANUFACTURER + ", " + Build.MODEL + ", Android Version : " + Build.VERSION.RELEASE);
                startActivity(Intent.createChooser(Email, "Choose Mail Client : "));
            }
        });*/

        forgotpassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(Login.this);
                final EditText input = new EditText(Login.this);
                input.setInputType(InputType.TYPE_TEXT_VARIATION_WEB_EMAIL_ADDRESS);
                input.setHint("Email ID");
                FrameLayout container = new FrameLayout(Login.this);
                FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                params.leftMargin = getResources().getDimensionPixelSize(R.dimen.dp_19);
                params.rightMargin = getResources().getDimensionPixelSize(R.dimen.dp_19);
                input.setLayoutParams(params);
                container.addView(input);
                builder.setTitle("Reset Password");
                builder.setMessage("An email will be sent to the registered Email ID.");
                builder.setView(container);
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        email = input.getText().toString();
                        FirebaseAuth.getInstance().sendPasswordResetEmail(email)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            new ShowToast(Login.this, "Reset email has been sent");
                                        } else
                                            new ShowToast(Login.this, "Something went wrong");
                                    }
                                });
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                builder.show();
            }
        });



    }


    private void getdata() {
        boolean flag = true;

        if (etEmail.getText().toString().isEmpty()) {
            etEmail.setError("Enter Email");
            flag=false;
            btnLogin.setEnabled(true);
        }

        if (etPassword.getText().toString().isEmpty()) {
            etPassword.setError("Enter Password");
            flag=false;
            btnLogin.setEnabled(true);
        }


        if (flag==true)
        {
            sPassword = etPassword.getText().toString();
            sEmail = etEmail.getText().toString().toLowerCase();
            Log.e("-*-*-*-*-*-*-* Login : ", sEmail + sPassword);
            final Query checkuserquery = FirebaseDatabase.getInstance().getReference( Constants.TBL_USER_DATA ).orderByChild( "email" ).equalTo( sEmail );
            checkuserquery.addValueEventListener( chkUserVLE );
        }

    }

    ValueEventListener chkUserVLE = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            try {
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    fbEmail = "" + postSnapshot.child( "email" ).getValue();
                    fbName = "" + postSnapshot.child( "name" ).getValue();
                }
                if (sEmail.equals(fbEmail)){
                    mAuth.signInWithEmailAndPassword(sEmail,sPassword )
                            .addOnCompleteListener(Login.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        // Sign in success, update UI with the signed-in user's information
                                        Constants.NAME=fbName;
                                        Constants.EMAIL=fbEmail;
                                        Constants.uid = mAuth.getUid();
                                        SharedPreferences sp = getSharedPreferences("login",MODE_PRIVATE);
                                        SharedPreferences.Editor myEdit = sp.edit();
                                        myEdit.putString("email",sEmail);
                                        myEdit.putString("password",sPassword);
                                        myEdit.putString("name",Constants.NAME);
                                        myEdit.commit();
                                        FirebaseUser user = mAuth.getCurrentUser();
                                        Intent intent = new Intent(getApplicationContext(),Home_Page.class);
                                        startActivity(intent);
                                        finish();
                                    } else {
                                        // If sign in fails, display a message to the user.
                                        new ShowToast(Login.this, "Authentication Failed");
                                        btnLogin.setEnabled(true);
                                    }
                                }
                            });
                }
                else {
                    etEmail.setError("Email Not Registered");
                    btnLogin.setEnabled(true);
                }
            } catch (Exception e) {
                Toast.makeText(Login.this, e.toString(), Toast.LENGTH_SHORT).show();
            }
        }
        @Override
        public void onCancelled(DatabaseError databaseError) {
            Toast.makeText(Login.this, databaseError.toString(), Toast.LENGTH_SHORT).show();
        }
    };

}
