package com.syrnnik.mapapp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class PointsListAdapter extends ArrayAdapter<Toilet> {

    private final LayoutInflater inflater;
    private final int layout;
    private final ArrayList<Toilet> pointsList;

    public PointsListAdapter(Context context, int resource, ArrayList<Toilet> pointsList) {
        super(context, resource, pointsList);
        this.inflater = LayoutInflater.from(context);
        this.layout = resource;
        this.pointsList = pointsList;
    }

    public View getView(int pos, View convertView, ViewGroup parent) {

        @SuppressLint("ViewHolder") View view = inflater.inflate(this.layout, parent, false);

//        ImageView iconView = view.findViewById(R.id.icon);
        TextView titleView = view.findViewById(R.id.title);
        ImageView arrowView = view.findViewById(R.id.arrow);
        TextView descView = view.findViewById(R.id.desc);

        Toilet toilet = pointsList.get(pos);

        titleView.setText(toilet.getTitle());
        descView.setText(toilet.getDesc() + '\n');

        descView.setVisibility(View.GONE);

        return view;
    }
}
