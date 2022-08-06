package com.syrnnik.mapapp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
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

    @Override
    public View getView(int pos, View convertView, ViewGroup parent) {

        @SuppressLint("ViewHolder")
        View view = inflater.inflate(this.layout, parent, false);

        Toilet toilet = pointsList.get(pos);
        TextView titleView = view.findViewById(R.id.title);
        titleView.setText(toilet.getTitle());
        view.setContentDescription(String.valueOf(toilet.getId()));

        return view;
    }

}
