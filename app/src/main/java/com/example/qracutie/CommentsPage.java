package com.example.qracutie;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;

public class CommentsPage extends AppCompatActivity {

    // Declare the variables so that you will be able to reference it later.
    ListView cityList;
    ArrayAdapter<Comment> cityAdapter;
    ArrayList<Comment> cityDataList;

    CommentList customList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments);

        cityList = findViewById(R.id.city_list);

        String []commentText ={"Wow this QR code is so pretty", "Almost as pretty as me", "WTF", "You heard me", "I did not", "Text carries no sound"};
        String []userID = {"batiuk", "batiuk", "hindle", "batiuk", "hindle", "hindle"};
        String []timeStamp = {"1988-03-21", "1988-03-21", "1988-03-21", "1988-03-21", "1988-03-21", "1988-03-21"};


        cityDataList = new ArrayList<>();

        for(int i=0;i<commentText.length;i++){
            cityDataList.add((new Comment(commentText[i], userID[i], timeStamp[i])));
        }

        cityAdapter = new CommentList(this, cityDataList);

        cityList.setAdapter(cityAdapter);

//        dataList = new ArrayList<>();
//        dataList.addAll(Arrays.asList(cities));
//
//        cityAdapter = new ArrayAdapter<>(this, R.layout.content, dataList);
//
//        cityList.setAdapter(cityAdapter);



    }


}
