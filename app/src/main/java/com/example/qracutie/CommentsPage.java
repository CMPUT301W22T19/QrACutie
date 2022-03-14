package com.example.qracutie;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.google.firebase.firestore.FirebaseFirestore;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class CommentsPage extends AppCompatActivity {

    // Declare the variables so that you will be able to reference it later.
    ListView cityList;
    ArrayAdapter<Comment> cityAdapter;
    ArrayList<Comment> cityDataList;

    final String TAG = "Sample";
    Button addCommentButton;
    EditText addCommentEditText;
    FirebaseFirestore db;

    CommentList customList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments);

        cityList = findViewById(R.id.comment_list);

        addCommentButton = findViewById(R.id.add_comment_button);
        addCommentEditText = findViewById(R.id.add_comment_field);

        String []commentText ={"Wow this QR code is so pretty", "Almost as pretty as me", "WTF", "You heard me", "I did not", "Text carries no sound"};
        String []userID = {"batiuk", "batiuk", "hindle", "batiuk", "hindle", "hindle"};
        String []timeStamp = {"1988-03-21", "1988-03-21", "1988-03-21", "1988-03-21", "1988-03-21", "1988-03-21"};

        cityDataList = new ArrayList<>();

        for(int i=0;i<commentText.length;i++){
            cityDataList.add((new Comment(commentText[i], userID[i], timeStamp[i])));
        }

        cityAdapter = new CommentList(this, cityDataList);
        cityList.setAdapter(cityAdapter);

        addCommentButton.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Retrieving the city name and the province name from the EditText fields
                final String cityName = addCommentEditText.getText().toString();
                final String provinceName = addCommentEditText.getText().toString();

                HashMap<String, String> data = new HashMap<>();

                if (cityName.length() > 0 && provinceName.length() > 0) {

                    // If there’s some data in the EditText field, then we create a new key-value pair.
                    data.put("Province Name", provinceName);
                }
            }
        });
    }
}
