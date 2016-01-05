package com.example.oneclickcall;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class ShortcutAdapter extends ArrayAdapter<ShortcutItem> {
    Context context;

    public ShortcutAdapter(Context context, int resourceId, List<ShortcutItem> items) {
        super(context, resourceId, items);
        this.context = context;
    }

    private class ViewHolder {
        ImageView contactIcon;
        TextView contactName;
        TextView contactPhone;
        ImageView applicationIcon;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        ShortcutItem rowItem = getItem(position);
        
        Log.i(MainActivity.TAG, rowItem.getName());
        LayoutInflater mInflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.shortcut_list_item, null);
            holder = new ViewHolder();
            holder.contactIcon = (ImageView) convertView.findViewById(R.id.contact_icon);
            holder.contactName = (TextView) convertView.findViewById(R.id.contact_name);
            holder.contactPhone = (TextView) convertView.findViewById(R.id.contact_phone);
            holder.applicationIcon = (ImageView) convertView.findViewById(R.id.application_icon);
            convertView.setTag(holder);
        }
        else {
            holder = (ViewHolder) convertView.getTag();
        }

        int CONTACT_ICON_SIZE = 90;
        int APP_ICON_SIZE = 50;
        holder.contactIcon.setImageBitmap(Bitmap.createScaledBitmap(rowItem.getContactIcon(), CONTACT_ICON_SIZE, CONTACT_ICON_SIZE, false));
        holder.contactName.setText(rowItem.getName());
        holder.contactPhone.setText(rowItem.getPhone());
        holder.applicationIcon.setImageBitmap(Bitmap.createScaledBitmap(rowItem.getApplicationIcon(), APP_ICON_SIZE, APP_ICON_SIZE, false));
        
        return convertView;
    }

}
