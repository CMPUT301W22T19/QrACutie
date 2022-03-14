package com.example.qracutie;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class PlayerCollectionActivity extends AppCompatActivity {

    ListView qrCodeList;
    ArrayAdapter<GameQRCode> qrCodeAdapter;
    ArrayList<GameQRCode> qrCodeDataList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player_collection);

        // load player from db
        Player player = new Player("Austinstestplayer1"); // TODO remove ....
        Bitmap defaultPic = BitmapFactory.decodeResource(this.getResources(), R.drawable.default_profile_pic);
        player.setProfilePic(defaultPic);
        GameQRCode testCode1 = new GameQRCode("TestCode1Hash", 20);
        GameQRCode testCode2 = new GameQRCode("TestCode2Hash", 50);
        GameQRCode testCode3 = new GameQRCode("TestCode3Hash", 10);

        player.addGameQRCode(testCode1, null);
        player.addGameQRCode(testCode2, null);
        player.addGameQRCode(testCode3, null); // TODO remove ^

        // initialize player username and image
        ImageView profileImage = findViewById(R.id.collection_player_image);
        profileImage.setImageBitmap(player.getProfilePic());

        TextView username = findViewById(R.id.collection_username);
        username.setText(player.getUsername());

        // initialize page with player statistics
        TextView totalCodes = findViewById(R.id.collection_total_codes_val);
        totalCodes.setText(String.valueOf(player.getTotalCodes()));

        TextView totalPoints = findViewById(R.id.collection_total_points_val);
        totalPoints.setText(String.valueOf(player.getPointTotal()));

        TextView highestScore = findViewById(R.id.collection_highest_score_val);
        highestScore.setText(String.valueOf(player.getHighestQRCode()));

        TextView lowestScore = findViewById(R.id.collection_lowest_score_val);
        lowestScore.setText(String.valueOf(player.getLowestQRCode()));


        // initialize page with player qr codes
        qrCodeList = findViewById(R.id.qr_code_list);
        qrCodeDataList = player.getGameQRCodes();
        qrCodeAdapter = new GameQRCodeAdapter(this, qrCodeDataList, player.getGameQRCodeImages());
        qrCodeList.setAdapter(qrCodeAdapter);

        // add a click listener for any element of the QR Code list
        qrCodeList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                viewQRCode(qrCodeDataList.get(i));
            }
        });
    }

    /**
     * Transitions a player to the "QRCodeActivity", which allows player to
     * see more information related to QR code, see comments on QR code, as
     * well as add comments to QR Code
     *
     * @param qrCode a GameQRCode object to view
     */
    protected void viewQRCode(GameQRCode qrCode) {
        // TODO implement
        return;
    }
}