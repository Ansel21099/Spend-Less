package com.example.spendless;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class EditAccount extends AppCompatActivity {

    EditText etname;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myref = database.getReference(Constants.TBL_USER_DATA);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_account);

        Intent intent = getIntent();
        String name = intent.getStringExtra("name");
        etname = findViewById(R.id.eaEtName);
        etname.setText(name);
        final Spinner spinner = findViewById(R.id.spinner);
        ArrayList<String> arrayList = new ArrayList<>();
        arrayList.add("INR");
        arrayList.add("USD");
        arrayList.add("KWD");
        arrayList.add("EUR");
        arrayList.add("CAD");
        arrayList.add("CNY");
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, arrayList);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(arrayAdapter);

        findViewById(R.id.eaBtnUpdate).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (etname.getText().toString().isEmpty()) {
                    etname.setError("Enter Name"); }
                else {
                    Map<String, Object> updates = new HashMap<String, Object>();
                    updates.put("name", etname.getText().toString());
                    updates.put("currency",spinner.getSelectedItem().toString());
                    myref.child(Constants.uid).updateChildren(updates);
                }
            }
        });
    }
}
