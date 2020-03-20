package com.example.spendless;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class LvTransactionsAdapter extends BaseAdapter {
    Context mContext;
    LayoutInflater inflater;
    List<ModelTransactions> modelList;
    ArrayList<ModelTransactions> arrayList;


    public LvTransactionsAdapter(Context context, List<ModelTransactions> modelData) {
        this.mContext = context;
        this.modelList = modelData;
        inflater = LayoutInflater.from( mContext );
        this.arrayList = new ArrayList<ModelTransactions>(  );
        this.arrayList.addAll( modelList );

    }
    public class ViewHolder
    {
        TextView date,category,amount;
    }

    @Override
    public int getCount() {
        return modelList.size();
    }

    @Override
    public Object getItem(int i) {
        return modelList.get( i );
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(final int i, View view, final ViewGroup viewGroup) {
        final ViewHolder holder;
        if(view == null)
        {
            holder = new ViewHolder();
            view = inflater.inflate( R.layout.sticker_transaction,  null);

            holder.date = view.findViewById( R.id.sttvdate);
            holder.category = view.findViewById( R.id.stTvcategory);
            holder.amount = view.findViewById( R.id.stTvamount);

            view.setTag( holder );

        }
        else
        {
            holder = (LvTransactionsAdapter.ViewHolder)view.getTag();
        }
        try {
            holder.date.setText(modelList.get(i).getDate());
            holder.category.setText(modelList.get(i).getCategory());
            holder.amount.setText(""+new DecimalFormat("##.##").format(modelList.get(i).getAmount()));

            if(modelList.get(i).getType().equals("Income"))
                holder.amount.setTextColor(Color.parseColor("#288102"));
            else
                holder.amount.setTextColor(Color.parseColor("#8A0303"));

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Snackbar snackbar;
                    snackbar = Snackbar.make(v,modelList.get(i).getDescription(),Snackbar.LENGTH_SHORT);
                    View snackBarView = snackbar.getView();
                    snackBarView.setBackgroundColor(Color.parseColor("#692db7"));
                    snackbar.show();
                }
            });

        }
        catch (Exception e){}

        return view;
    }
}
