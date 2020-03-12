package com.example.spendless;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class ShowToast {
    public ShowToast(Context context, String info) {
        Toast toast = Toast.makeText(context,info, Toast.LENGTH_SHORT);
        View view = toast.getView();
        view.getBackground().setColorFilter(Color.parseColor("#ffc15e"), PorterDuff.Mode.SRC_IN);
        TextView text = view.findViewById(android.R.id.message);
        text.setTextColor(Color.BLACK);
        toast.show();
    }
}
