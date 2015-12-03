package com.example.oneclickcall;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by Irina on 11/2/2015.
 */
public class PhoneNumbersAdapter extends ArrayAdapter<PhoneNumber> {

    private ArrayList<PhoneNumber> numberList;
    private Context context;

    public PhoneNumbersAdapter(Context context, int textViewResourceId,
                               ArrayList<PhoneNumber> numberList) {
        super(context, textViewResourceId, numberList);
        this.numberList = new ArrayList<PhoneNumber>();
        this.numberList.addAll(numberList);
        this.context = context;
    }

    private class ViewHolder {
        TextView phoneNumberTextView;
        CheckBox phoneNumberCheckBox;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder = null;

        if (convertView == null) {
            LayoutInflater vi = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            convertView = vi.inflate(R.layout.contacts_list_item, null);

            holder = new ViewHolder();
            holder.phoneNumberTextView = (TextView) convertView.findViewById(R.id.phone_number);
            holder.phoneNumberCheckBox = (CheckBox) convertView.findViewById(R.id.phone_checkBox);
            convertView.setTag(holder);

            holder.phoneNumberCheckBox.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    CheckBox cb = (CheckBox) v;
                    PhoneNumber number = (PhoneNumber) cb.getTag();
                    Toast.makeText(context,
                            "Clicked on Checkbox: " + cb.getText() +
                                    " is " + cb.isChecked(),
                            Toast.LENGTH_LONG).show();
                    number.setSelected(cb.isChecked());
                }
            });
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        PhoneNumber number = numberList.get(position);
        holder.phoneNumberTextView.setText(number.getNumber());
        holder.phoneNumberCheckBox.setChecked(number.isSelected());
        holder.phoneNumberCheckBox.setTag(number);

        return convertView;
    }

}
