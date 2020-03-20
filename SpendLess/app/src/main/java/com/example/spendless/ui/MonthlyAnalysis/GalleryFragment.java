package com.example.spendless.ui.MonthlyAnalysis;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Switch;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.example.spendless.Constants;
import com.example.spendless.ModelTransactions;
import com.example.spendless.R;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Objects;

public class GalleryFragment extends Fragment {

    private GalleryViewModel galleryViewModel;
    ArrayList<PieEntry> items = new ArrayList<PieEntry>();
    PieChart pieChart;
    Spinner sp;
    int a=0,b=0,c=0,d=0,e=0,f=0;
    ArrayList<String> months = new ArrayList<>();

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        galleryViewModel =
                ViewModelProviders.of(this).get(GalleryViewModel.class);
        View root = inflater.inflate(R.layout.fragment_gallery, container, false);

       pieChart = root.findViewById(R.id.pieChart);
       sp = root.findViewById(R.id.spMonth);

        Query getmonthquery = FirebaseDatabase.getInstance().getReference(Constants.TBL_TRANSACTIONS).child(Constants.uid);
        getmonthquery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                months.clear();
                for (DataSnapshot post : dataSnapshot.getChildren())
                {
                    months.add(""+post.getKey());
                }
                ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(Objects.requireNonNull(getActivity()),   android.R.layout.simple_spinner_dropdown_item,months);
                sp.setAdapter(spinnerArrayAdapter);
               sp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                   @Override
                   public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                       Query getdataQuery = FirebaseDatabase.getInstance().getReference(Constants.TBL_TRANSACTIONS).child(Constants.uid).child(sp.getSelectedItem()+"");
                       getdataQuery.addValueEventListener(new ValueEventListener() {
                           @Override
                           public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                               a=0;b=0;c=0;d=0;e=0;f=0;
                               for (DataSnapshot post : dataSnapshot.getChildren())
                               {
                                   for (DataSnapshot post1 : post.getChildren())
                                   {
                                       String type =""+ post1.child("type").getValue();
                                       String category = ""+post1.child("category").getValue();
                                       if (type.equals("Expense"))
                                       {
                                           int amt = Math.abs( Integer.parseInt(""+ post1.child("amount").getValue()));
                                           switch (category)
                                           {
                                               case "Food": a+=amt ;
                                                   break;
                                               case "Clothes":b+=amt ;
                                                   break;
                                               case "Bills" : c+=amt ;
                                                   break;
                                               case "Maintenance": d+=amt ;
                                                   break;
                                               case "Travel": e+=amt ;
                                                   break;
                                               case "Other":f+=amt ;
                                                   break;
                                           }
                                       }

                                   }
                               }

                               if(pieChart.getData()!=null)
                               pieChart.getData().clearValues();
                               pieChart.clear();
                               if (items!=null)
                               items.clear();
                               if (a>0)
                                   items.add(new PieEntry(a,"Food"));
                               if (b>0)
                                   items.add(new PieEntry(b,"Clothes"));
                               if (c>0)
                                   items.add(new PieEntry(c,"Bills"));
                               if (d>0)
                                   items.add(new PieEntry(d,"Maintenance"));
                               if (e>0)
                                   items.add(new PieEntry(e,"Travel"));
                               if (f>0)
                                   items.add(new PieEntry(f,"Other"));

                                   PieDataSet dataSet = new PieDataSet(items, "Expenses");
                                   PieData data = new PieData();
                                   data.addDataSet(dataSet);
                                   pieChart.setData(data);
                                   dataSet.setColors(ColorTemplate.MATERIAL_COLORS);
                                   dataSet.setSliceSpace(2f);
                                   dataSet.setValueTextColor(Color.WHITE);
                                   dataSet.setValueTextSize(20f);
                                   dataSet.setSliceSpace(5f);
                                   pieChart.animateXY(3000, 3000);
                                   Legend l = pieChart.getLegend();
                                   l.setEnabled(false);
                                   pieChart.getDescription().setEnabled(false);
                           }

                           @Override
                           public void onCancelled(@NonNull DatabaseError databaseError) {

                           }
                       });
                   }

                   @Override
                   public void onNothingSelected(AdapterView<?> parent) {

                   }
               });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
                return root;
    }
}
