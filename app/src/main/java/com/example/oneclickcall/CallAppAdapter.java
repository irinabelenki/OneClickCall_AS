package com.example.oneclickcall;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class CallAppAdapter extends ArrayAdapter<CallAppItem> {
    Context context;

    public CallAppAdapter(Context context, int resourceId, List<CallAppItem> items) {
        super(context, resourceId, items);
        this.context = context;
    }

    private class ViewHolder {
        ImageView callAppImage;
        TextView callAppName;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        CallAppItem rowItem = getItem(position);
        
        Log.i(MainActivity.TAG, rowItem.getName());
        LayoutInflater mInflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.call_app_list_item, null);
            holder = new ViewHolder();
            holder.callAppName = (TextView) convertView.findViewById(R.id.call_app_name);
            holder.callAppImage = (ImageView) convertView.findViewById(R.id.call_app_image);
            convertView.setTag(holder);
        }
        else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.callAppName.setText(rowItem.getName());
        holder.callAppImage.setImageBitmap(rowItem.getImage());

        return convertView;
    }
}