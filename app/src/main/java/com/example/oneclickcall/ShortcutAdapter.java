package com.example.oneclickcall;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class ShortcutAdapter extends ArrayAdapter<ShortcutItem> {
    Context context;

    public ShortcutAdapter(Context context, int resourceId, List<ShortcutItem> items) {
        super(context, resourceId, items);
        this.context = context;
    }

    private class ViewHolder {
        TextView shortcutName;
        TextView shortcutApplication;
        TextView shortcutPhone;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        ShortcutItem rowItem = getItem(position);
        
        Log.i(MainActivity.TAG, rowItem.getName());
        LayoutInflater mInflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.shortcut_list_item, null);
            holder = new ViewHolder();
            holder.shortcutName = (TextView) convertView.findViewById(R.id.shortcut_name);
            holder.shortcutApplication = (TextView) convertView.findViewById(R.id.shortcut_application);
            holder.shortcutPhone = (TextView) convertView.findViewById(R.id.shortcut_phone);
            convertView.setTag(holder);
        }
        else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.shortcutName.setText(rowItem.getName());
        holder.shortcutApplication.setText(rowItem.getApplication());
        holder.shortcutPhone.setText(rowItem.getPhone());
        
        return convertView;
    }

}
