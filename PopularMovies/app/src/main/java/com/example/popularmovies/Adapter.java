package com.example.popularmovies;
import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class Adapter extends BaseAdapter {
    private Context mContext;
    LayoutInflater inflater;
    List<ModelDataMain> modelList;
    ArrayList<ModelDataMain> arrayList;

    public Adapter(Context c, List<ModelDataMain> modelData) {
        this.mContext = c;
        this.modelList = modelData;
        inflater = LayoutInflater.from( mContext );
        this.arrayList = new ArrayList<ModelDataMain>(  );
        this.arrayList.addAll( modelList );
    }

    public class ViewHolder
    {
        ImageView mPackImg;
    }

    @Override
    public int getCount() {
        return modelList.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {

        final ViewHolder holder;
        if(view == null)
        {
            holder = new ViewHolder();
            view = inflater.inflate( R.layout.sticker_poster,  null);
            holder.mPackImg = view.findViewById(R.id.IvPoster);
            view.setTag( holder );

        }
        else
        {
            holder = (ViewHolder)view.getTag();
        }
        try {
            Picasso.get().load(modelList.get(position).getImg()).into(holder.mPackImg);
        }
        catch (Exception e){

        }
        return view;
    }

}
