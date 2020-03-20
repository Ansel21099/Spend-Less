package com.example.spendless.ui.home;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.RemoteViews;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.spendless.Add;
import com.example.spendless.AppWidget;
import com.example.spendless.Constants;
import com.example.spendless.LvTransactionsAdapter;
import com.example.spendless.ModelTransactions;
import com.example.spendless.R;
import com.example.spendless.model.MBAddTransaction;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;
    private int mYear, mMonth, mDay;
    private double income,totalincome,expense,totalexpense;
    TextView tvincome, tvtotalincome,tvexpense,tvtotalexpense;
    ArrayList<ModelTransactions> arrayList = new ArrayList<ModelTransactions>();
    LvTransactionsAdapter adapter;
    ModelTransactions model;
    ListView listView;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel = ViewModelProviders.of(this).get(HomeViewModel.class);
        final View root = inflater.inflate(R.layout.fragment_home, container, false);

        root.findViewById(R.id.fhLoading).setVisibility(View.VISIBLE);
        root.findViewById(R.id.fhProgress).setVisibility(View.VISIBLE);

        final Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);

        tvincome = root.findViewById(R.id.fhTvIncome);
        tvtotalincome = root.findViewById(R.id.fhTvTotalIncome);
        tvexpense = root.findViewById(R.id.fhTvExpense);
        tvtotalexpense = root.findViewById(R.id.fhTvTotalExpense);
        listView = root.findViewById(R.id.fhLvTransactions);

        root.findViewById(R.id.fab).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), Add.class));
            }
        });

        Query getdataQuery = FirebaseDatabase.getInstance().getReference(Constants.TBL_TRANSACTIONS).child(Constants.uid).child((mMonth+1)+"-"+mYear);
        getdataQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                income = 0;
                expense = 0;
                totalincome=0;
                totalexpense=0;
                for (DataSnapshot post : dataSnapshot.child(mDay+"-"+(mMonth+1)+"-"+mYear).getChildren())
                {
                    String type =""+ post.child("type").getValue();
                    if (type.equals("Income"))
                        income += Double.parseDouble(""+ post.child("amount").getValue());
                    else if (type.equals("Expense"))
                        expense -= Double.parseDouble(""+ post.child("amount").getValue());
                }
                for (DataSnapshot post : dataSnapshot.getChildren())
                {
                    for (DataSnapshot post1 : post.getChildren())
                    {
                        String type =""+ post1.child("type").getValue();
                        if (type.equals("Income"))
                            totalincome += Double.parseDouble(""+ post1.child("amount").getValue());
                        else if (type.equals("Expense"))
                            totalexpense -= Double.parseDouble(""+ post1.child("amount").getValue());

                        String category,date,description,typ;
                        Double amount;
                        category = ""+ post1.child("category").getValue();
                        date = ""+ post1.child("date").getValue();
                        description = ""+ post1.child("description").getValue();
                        typ = ""+ post1.child("type").getValue();
                        amount = Double.parseDouble(""+ post1.child("amount").getValue());
                        model = new ModelTransactions(category,date,description,typ,amount);
                        arrayList.add(model);
                    }
                }
                if (getActivity()!=null)
                    {
                        adapter = new LvTransactionsAdapter(getActivity(), arrayList);
                        listView.setAdapter(adapter);
                    }

                tvincome.setText("+"+new DecimalFormat("##.##").format(income));
                tvexpense.setText("-"+new DecimalFormat("##.##").format(expense));
                tvtotalexpense.setText("-"+new DecimalFormat("##.##").format(totalexpense));
                tvtotalincome.setText("+"+new DecimalFormat("##.##").format(totalincome));

                try
                    {
                        Context context = getActivity();
                        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
                        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.app_widget);
                        ComponentName thisWidget = new ComponentName(context, AppWidget.class);
                        remoteViews.setTextViewText(R.id.awTvExpense, "-"+new DecimalFormat("##.##").format(expense));
                        remoteViews.setTextViewText(R.id.awTvIncome, "+"+new DecimalFormat("##.##").format(income));
                        appWidgetManager.updateAppWidget(thisWidget, remoteViews);
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }

                root.findViewById(R.id.fhLoading).setVisibility(View.GONE);
                root.findViewById(R.id.fhProgress).setVisibility(View.GONE);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        return root;
    }
}
