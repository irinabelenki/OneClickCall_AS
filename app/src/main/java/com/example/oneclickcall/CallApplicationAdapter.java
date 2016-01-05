package com.example.oneclickcall;

/**
 * Created by Irina on 11/4/2015.
 */

import java.util.ArrayList;
import java.util.Iterator;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class CallApplicationAdapter extends ArrayAdapter<CallAppItem> {

    private Context context;
    private ArrayList data;
    CallAppItem callAppItem = null;
    LayoutInflater inflater;

    public CallApplicationAdapter(
            Context context,
            int textViewResourceId,
            ArrayList objects
    ) {
        super(context, textViewResourceId, objects);

        this.context = context;
        data = objects;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }

    public View getCustomView(int position, View convertView, ViewGroup parent) {
        View row = inflater.inflate(R.layout.call_app_spinner_rows, parent, false);
        callAppItem = (CallAppItem) data.get(position);

        TextView nameTextView = (TextView) row.findViewById(R.id.call_app_name);
        ImageView iconImageView = (ImageView) row.findViewById(R.id.call_app_icon);
        nameTextView.setText(callAppItem.getName());
        //iconImageView.setImageDrawable(callAppItem.getImage());

        int ICON_SIZE = 72;
        iconImageView.setImageBitmap(Bitmap.createScaledBitmap(callAppItem.getImage(), ICON_SIZE, ICON_SIZE, false));
        return row;
    }

    public int getPosition(CallAppItem other) {
        Iterator<CallAppItem> it = data.iterator();
        for(int i=0; i<data.size(); i++) {
            CallAppItem item = (CallAppItem)data.get(i);
            if(item.compareTo(other) == 0) {
                return i;
            }
        }
        return -1;
    }
}
