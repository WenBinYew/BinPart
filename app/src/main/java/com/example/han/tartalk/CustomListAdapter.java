package com.example.han.tartalk;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by Bin on 5/12/2016.
 */

public class CustomListAdapter extends ArrayAdapter<String> {

    private final Activity context;
    private final String[] profileItem;
    private final Integer[] icon;

    public CustomListAdapter(Activity context, String[] profileItem, Integer[] icon) {
        super(context, R.layout.profile_list, profileItem);
        this.context=context;
        this.profileItem=profileItem;
        this.icon=icon;
    }

    public View getView(int position, View view, ViewGroup parent){

        LayoutInflater inflater=context.getLayoutInflater();
        View rowView=inflater.inflate(R.layout.profile_list, null,true);

        ImageView profileIcon = (ImageView) rowView.findViewById(R.id.profileIcon);
        TextView itemName = (TextView) rowView.findViewById(R.id.profileItem);

        itemName.setText(profileItem[position]);
        profileIcon.setImageResource(icon[position]);

        return rowView;

    };
}
