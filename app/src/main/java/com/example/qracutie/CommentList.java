package com.example.qracutie;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

public class CommentList extends ArrayAdapter<Comment> {

    private ArrayList<Comment> cities;
    private Context context;

    public CommentList(Context context, ArrayList<Comment> cities){
        super(context,0, cities);
        this.cities = cities;
        this.context = context;
    }


    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
//        return super.getView(position, convertView, parent);
        View view = convertView;

        if(view == null){
            view = LayoutInflater.from(context).inflate(R.layout.content, parent,false);
        }

        Comment city = cities.get(position);

        TextView cityName = view.findViewById(R.id.comment_text);
        TextView provinceName = view.findViewById(R.id.uid_text);

        cityName.setText(city.getCityName());
        provinceName.setText(city.getProvinceName());

        return view;

    }
}
