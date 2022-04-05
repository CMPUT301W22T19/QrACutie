package com.example.qracutie;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * The player collection activity display's a players statistics and
 * any QR codes which have been added to their code. QR codes can be selected to view
 * who has commented on them, and also who has scanned them.
 */
public class PlayerCollectionActivity extends AppCompatActivity {

    public static final String EXTRA_COMMENTS_TYPE = "com.example.qracutie.EXTRA_COMMENTS_TYPE";
    private static final String SHARED_PREFS = "sharedPrefs";
    private static final String TEXT = "username";
    private String username = "";

    public static final String EXTRA_COMMENTS_USERNAME = "com.example.qracutie.EXTRA_COMMENTS_USERNAME";
    public static final String EXTRA_COMMENTS_QRCODE = "com.example.qracutie.EXTRA_COMMENTS_QRCODE";

    ListView qrCodeList;
    ArrayAdapter<GameQRCode> qrCodeAdapter;
    ArrayList<GameQRCode> qrCodeDataList;

    String viewer;
    String personToView;

    Player player;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player_collection);

        // load player from db
        Intent intent = getIntent();
        viewer = intent.getStringExtra(MainActivity.EXTRA_PLAYER_USERNAME);
        personToView = intent.getStringExtra(MainActivity.EXTRA_PLAYER_COLLECTION_USERNAME);

        // load player from database
        loadUser(this, personToView, new MyCallBack() {
            @Override
            public void onCallBack(Context context, Player player) {
                // initialize player username
                TextView usernameView = findViewById(R.id.collection_username);
                usernameView.setText(player.getUsername());

                // initialize player image
                ImageView playerImage = findViewById(R.id.collection_player_image);
                Glide.with(context).clear(playerImage);
                if (!player.getProfileImage().equals("")) {
                    Glide.with(context).asBitmap().load(Uri.parse(player.getProfileImage()))
                            .diskCacheStrategy(DiskCacheStrategy.NONE)
                            .skipMemoryCache(true)
                            .into(playerImage);
                } else {
                    playerImage.setImageResource(R.drawable.default_profile_pic);
                }

                // initialize player statistics
                setPlayerStats();

                // initialize page with player qr codes
                qrCodeList = findViewById(R.id.qr_code_list);
                qrCodeDataList = player.getGameQRCodes();
                boolean enableOptions = viewer.equals(player.getUsername());
                qrCodeAdapter = new GameQRCodeAdapter(context, qrCodeDataList, player.getGameQRCodeImages(), enableOptions);
                qrCodeList.setAdapter(qrCodeAdapter);
            }
        });
    }

    /**
     * Defines an onCallBack method for initializing the collections page with data
     */
    private interface MyCallBack {
        /**
         * Initializes the player collections page with data
         */
        void onCallBack(Context context, Player player);
    }

    /**
     * Loads a user profile in from firebase, including all user statistics, user QR codes,
     * and images associated to QR codes. Accepts a callback.
     * @param context context of caller
     * @param username profile to load from firestore
     * @param myCallBack a callback class
     */
    private void loadUser(Context context, String username, MyCallBack myCallBack) {
        player = new Player(username);
        db.collection("users").document(username).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                player.setEmail(task.getResult().get("email").toString());
                player.setPhoneNumber(task.getResult().get("phoneNumber").toString());
                player.setProfileImage(task.getResult().get("profileImage").toString());
                player.setGameQRCodeImages((HashMap<String, String>) task.getResult().get("gameQRCodeImages"));
                player.setGameQRCodes((ArrayList<GameQRCode>) task.getResult().get("gameQRCode"));
                myCallBack.onCallBack(context, player);
            }
        });
    }

    /**
     * Sets player statistics to be visible on the page
     */
    private void setPlayerStats() {
        // initialize page with player statistics
        TextView totalCodes = findViewById(R.id.collection_total_codes_val);
        totalCodes.setText(String.valueOf(player.getTotalCodes()));

        TextView totalPoints = findViewById(R.id.collection_total_points_val);
        totalPoints.setText(String.valueOf(player.getPointTotal()));

        TextView highestScore = findViewById(R.id.collection_highest_score_val);
        highestScore.setText(String.valueOf(player.getHighestQRCode()));

        TextView lowestScore = findViewById(R.id.collection_lowest_score_val);
        lowestScore.setText(String.valueOf(player.getLowestQRCode()));
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
        intent.putExtra(EXTRA_COMMENTS_USERNAME, viewer);
        intent.putExtra(EXTRA_COMMENTS_QRCODE, qrCode.getHash());
        startActivity(intent);
        return;
    }

    /**
     * displays a fragment that shows more information about a QR code,
     * and allows the player to delete the QR code if they own it
     */
    protected void showOptions(GameQRCode qrCode) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder
            .setMessage("Would you like to delete this QR Code?")
            .setNegativeButton("No", null)
            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    // remove qr code from player profile
                    player.deleteGameQRCode(qrCode);

                    // update page statistics
                    setPlayerStats();

                    // remove qr code from screen
                    qrCodeAdapter.remove(qrCode);
                    qrCodeAdapter.notifyDataSetChanged();
                }
            }).create().show();
    }

    /**
     * Onclick method for when the user accesses their account info (email and phone number)
     * @param view
     */
    public void userQrButtonClicked(View view) {
        Intent intent = new Intent(PlayerCollectionActivity.this, ShareableQrActivity.class);
        intent.putExtra(EXTRA_COMMENTS_TYPE, "information");
        startActivityIfNeeded(intent, 255);
    }
}