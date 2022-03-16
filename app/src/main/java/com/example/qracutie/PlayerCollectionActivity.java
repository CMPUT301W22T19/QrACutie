package com.example.qracutie;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

/**
 * The player collection activity display's a players statistics and
 * any QR codes which have been added to their code. QR codes can be selected to view
 * who has commented on them, and also who has scanned them.
 */
public class PlayerCollectionActivity extends AppCompatActivity {

    public static final String EXTRA_COMMENTS_USERNAME = "com.example.qracutie.EXTRA_COMMENTS_USERNAME";
    public static final String EXTRA_COMMENTS_QRCODE = "com.example.qracutie.EXTRA_COMMENTS_QRCODE";

    ListView qrCodeList;
    ArrayAdapter<GameQRCode> qrCodeAdapter;
    ArrayList<GameQRCode> qrCodeDataList;

    Player player;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player_collection);

        // load player from db
        Intent intent = getIntent();
        String username = intent.getStringExtra(MainActivity.EXTRA_PLAYER_COLLECTION_USERNAME);
        player = new Player(username);
        db.collection("users").document(username).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                player.setEmail(task.getResult().get("email").toString());
                player.setPhoneNumber(task.getResult().get("phoneNumber").toString());
            }
        });
        Bitmap defaultPic = BitmapFactory.decodeResource(this.getResources(), R.drawable.default_profile_pic);
        player.setProfilePic(defaultPic);

        // TODO remove
        // Add GameQR codes to player so that there is visible content on the screen in testing
        GameQRCode testCode1 = new GameQRCode("258f43b98430f4b5e50822bbb1070038233e286d6315ce19cb6fc0c02794eb97", 20);
        GameQRCode testCode2 = new GameQRCode("2f0eb1859e295bcd183127558f3c205270e7a8004ad362e5123bd5b2774e0f9c", 50);
        GameQRCode testCode3 = new GameQRCode("11", 10);

        player.addGameQRCode(testCode1, null);
        player.addGameQRCode(testCode2, null);
        player.addGameQRCode(testCode3, null);
        // TODO remove ^

        // initialize player username and image
        ImageView profileImage = findViewById(R.id.collection_player_image);
        profileImage.setImageBitmap(player.getProfilePic());

        TextView usernameView = findViewById(R.id.collection_username);
        usernameView.setText(player.getUsername());

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
                viewCommentsActivity(qrCodeDataList.get(i));
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
    protected void viewCommentsActivity(GameQRCode qrCode) {
        Intent intent = new Intent(this, CommentsPage.class);
        intent.putExtra(EXTRA_COMMENTS_USERNAME, player.getUsername());
        intent.putExtra(EXTRA_COMMENTS_QRCODE, qrCode.getHash());
        startActivity(intent);
        return;
    }
}