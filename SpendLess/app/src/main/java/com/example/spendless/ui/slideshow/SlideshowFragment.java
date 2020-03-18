package com.example.spendless.ui.slideshow;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.spendless.Constants;
import com.example.spendless.R;
import com.example.spendless.ShowToast;
import com.example.spendless.model.MBFeedback;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SlideshowFragment extends Fragment {

    private SlideshowViewModel slideshowViewModel;
    String SFeedback, SStar = null;
    Button btnsub;
    EditText etf;
    RatingBar rb1;

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference(Constants.TBL_USER_FEEDBACK);

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        slideshowViewModel =
                ViewModelProviders.of(this).get(SlideshowViewModel.class);
        View root = inflater.inflate(R.layout.fragment_slideshow, container, false);

        btnsub = root.findViewById(R.id.fsBtnSend);
        etf = root.findViewById(R.id.fsEtFeedback);
        rb1 = root.findViewById(R.id.fsRbRating);

        root.findViewById(R.id.fsFabEmail).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent Email = new Intent(Intent.ACTION_SEND);
                Email.setType("text/email");
                Email.putExtra(Intent.EXTRA_EMAIL, new String[]{"anselgons22@gmail.com"});  //developer 's email
                Email.putExtra(Intent.EXTRA_SUBJECT, "Feedback from " + Constants.uid); // Email 's Subject
                startActivity(Intent.createChooser(Email, "Choose Mail Client : "));
            }
        });


        btnsub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getdata();
            }
        });
        rb1.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float v, boolean b) {
                SStar = String.valueOf(v);
            }
        });

        return root;
    }

    public void insertdata() {
        MBFeedback mb = new MBFeedback(Constants.uid, SFeedback, SStar);
        myRef.push().setValue(mb);
        new ShowToast(getActivity(), "Thank you for your feedback.");
        etf.setText("");
    }

    private void getdata() {
        boolean flag = true;
        if (etf.getText().toString().isEmpty()) {
            etf.setError("Please enter your feedback");
            flag = false;
        }
        if (SStar == null) {
            new ShowToast(getActivity(), "Rate us out of 5");
            flag = false;
        }
        if (flag == true) {
            SFeedback = etf.getText().toString();
            insertdata();
        }

    }
}
