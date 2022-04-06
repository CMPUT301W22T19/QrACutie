package com.example.qracutie;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;

public class SearchPlayer extends AppCompatActivity {
    private ImageView image;
    private TextView username;
    private TextView highestQR;
    private TextView total;
    private TextView currRank;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_player);

        image = (ImageView) findViewById(R.id.searched_player_image);
        username = (TextView) findViewById(R.id.search_username);
        highestQR = (TextView) findViewById(R.id.search_highestQR);
        total = (TextView) findViewById(R.id.search_totalQRs);
        currRank = (TextView) findViewById(R.id.search_ranking);

        Intent intent = getIntent();
        if(!intent.getStringExtra("searched_image").equals("")){
            Glide.with(getApplicationContext()).asBitmap().load(Uri.parse(intent.getStringExtra("searched_image"))).into(image);
        }else{
            Glide.with(getApplicationContext()).clear(image);
            image.setImageResource(R.drawable.default_profile_pic);
        }
        username.setText(intent.getStringExtra("searched_username"));
        highestQR.setText(intent.getStringExtra("searched_highestQR"));
        total.setText(intent.getStringExtra("searched_total"));
        currRank.setText(intent.getStringExtra("currentRanking"));
    }
}
