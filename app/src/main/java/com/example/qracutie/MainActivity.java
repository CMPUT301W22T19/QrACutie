package com.example.qracutie;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.menu.MenuBuilder;
import androidx.appcompat.widget.SearchView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ThreadLocalRandom;

/**
 * The MainActivity is the landing page for a user when they launch the app. From the
 * main activity, the user can switch to the player collection activity, camera activity,
 * account activity, or map activity.
 *
 * Within the main activity, a player profile will be displayed, as well as a leaderboard
 * of all players, sorted according to some specified criteria.
 */
public class MainActivity extends AppCompatActivity {

    public static final String EXTRA_COMMENTS_TYPE = "com.example.qracutie.EXTRA_COMMENTS_TYPE";

    private Button userAccountButton;
    private TextView nameDisplayed;
    private ImageView profile;
    private Button myCollection;
    private Button cameraButton;
    private SearchView searchView;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("profileImages");

    private static final String SHARED_PREFS = "sharedPrefs";
    private static final String TEXT = "username";
    public static final String EXTRA_PLAYER_USERNAME = "com.example.qracutie.EXTRA_PLAYER_USERNAME";
    public static final String EXTRA_PLAYER_COLLECTION_USERNAME = "com.example.qracutie.EXTRA_PLAYER_COLLECTION_USERNAME";

    private Player player;
    private String username = "";
    private Boolean isPlayerSet = false;

    private ListView playerList;
    private PlayerListAdapter playerListAdapter;
    private ArrayList<Player> playerDataList = new ArrayList<>();

    ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if(result.getData() != null){
                        Uri uri = result.getData().getData();
                        player.setProfileImage(uri.toString());
                        Bitmap bitmap = null;
                        try {
                            bitmap = draw_profile_image(uri);
                            uploadProfileImage(bitmap);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
    );

    ImageButton mapButton;

    /**
     * Loads in existing user info or else generates a new user
     * Contains the on click method for the map and the player's profile image
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //SharedPreferences sharedPreferences  = getApplicationContext().getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        //sharedPreferences.edit().clear().commit();


        mapButton = (ImageButton) findViewById(R.id.mapButton);
        userAccountButton = (Button) findViewById(R.id.user_account_button);
        nameDisplayed = (TextView) findViewById(R.id.display_name);
        profile = (ImageView) findViewById(R.id.profile_picture);
        myCollection = (Button) findViewById(R.id.collection_button);
        searchView = (SearchView) findViewById(R.id.searchView);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // From: tutorialspoint
                // Url: https://www.tutorialspoint.com/How-to-remove-all-whitespace-from-String-in-Java#:~:text=The%20replaceAll()%20method%20of,replacing%20%22%20%22%20with%20%22%22.
                // Author: Anjana
                db.collection("users").document(query.replaceAll(" ","")).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.getResult().exists()){
                            String searched_image = task.getResult().get("profileImage").toString();
                            String searched_username = task.getResult().get("username").toString();
                            String searched_highestQR = task.getResult().get("highestQRCode").toString();
                            String searched_total = task.getResult().get("totalCodes").toString();
                            Intent intent = new Intent(MainActivity.this, SearchPlayer.class);
                            intent.putExtra("searched_image", searched_image);
                            intent.putExtra("searched_username", searched_username);
                            intent.putExtra("searched_highestQR", searched_highestQR);
                            intent.putExtra("searched_total", searched_total);
                            startActivity(intent);
                        }else{
                            Toast.makeText(getApplicationContext(), "User does not exist", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        mapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), MapsActivity.class);
                startActivity(intent);
            }
        });
        userAccountButton = (Button) findViewById(R.id.user_account_button);
        nameDisplayed = (TextView) findViewById(R.id.display_name);

        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getProfileFromGallery();
            }
        });

        // add on click listener for my collection button
        myCollection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openPlayerCollectionActivity(player);
            }
        });


        playerExistence();

        cameraButton = (Button) findViewById(R.id.cameraButton);
        cameraButton.setOnClickListener(view -> {
                Intent intent = new Intent(view.getContext(), CameraActivity.class);
                //intent.putExtra("player", (new Gson()).toJson(player));
                intent.putExtra("username", username);
                view.getContext().startActivity(intent);
        });

        // create leaderboard array adapter
        playerList = findViewById(R.id.leaderboard);
        playerDataList = new ArrayList<>();
        playerListAdapter = new PlayerListAdapter(this, playerDataList);
        playerList.setAdapter(playerListAdapter);

        // create leaderboard sorting buttons
        TabLayout leaderboardTabs = findViewById(R.id.leaderboard_tabs);
        leaderboardTabs.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch(tab.getPosition()) {
                    case 0:
                        updateRank("pointTotal");
                        updateLeaders("pointTotal", 10);
                        break;
                    case 1:
                        updateRank("totalCodes");
                        updateLeaders("totalCodes", 10);
                        break;
                    case 2:
                        updateRank("highestQRCode");
                        updateLeaders("highestQRCode", 10);
                        break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });

        // set the user's rank
        updateRank("pointTotal");

        // set the leaderboard
        updateLeaders("pointTotal", 10); // replaces empty leader board
    }

    private void doesPlayerExist(){
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        username = sharedPreferences.getString(TEXT,"");
        if (username.equals("")){
            generateUniqueUsername();
        }else{
            doesDocExist();
        }
    }

    private void playerExistence(){
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        username = sharedPreferences.getString(TEXT,"");
        Toast.makeText(getApplicationContext(), username, Toast.LENGTH_SHORT).show();
        // if username isn't stored in shared preferences, then generate a username
        // someone is opening our app for the first time
        if (username.equals("")){
            generateUniqueUsername();
        }else{
            // user already exists in the database
            getPlayerInfo();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Intent intent = getIntent();
        String prevActivity = intent.getStringExtra("activity");
        if(prevActivity != null && prevActivity.equals("ownerspage") && intent.getBooleanExtra("ownerDeletedSelf",false) == true){
            clearPlayerFromStorage();
            doesPlayerExist();
        }else if(prevActivity != null && prevActivity.equals("SaveQRActivity") && intent.getStringExtra("action").equals("userLoggingIn")){
            username = intent.getStringExtra("username");

            Toast.makeText(getApplicationContext(), "SABA:"+username, Toast.LENGTH_SHORT).show();


            SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(TEXT, username);
            editor.apply();
            playerExistence();
        }else{
            doesPlayerExist();
        }
    }

    private void clearPlayerFromStorage() {
        FirebaseStorage.getInstance().getReference().child("gameQRcodeImages").child(username).delete();
    }

    @SuppressLint("RestrictedApi")
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.main_menu, menu);

        if(menu instanceof MenuBuilder){
            MenuBuilder m = (MenuBuilder) menu;
            m.setOptionalIconsVisible(true);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.owner_login_button){
            Intent intent = new Intent(MainActivity.this, OwnerLogin.class);
            intent.putExtra("username", player.getUsername());
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * switches to a PlayerCollection activity wherein the user will be able to see the
     * details of a specific player profile, including player statistics and a list of all
     * QR Codes which have been collected
     */
    public void openPlayerCollectionActivity(Player playerToView) {

        Intent intent = new Intent(this, PlayerCollectionActivity.class);
        intent.putExtra(EXTRA_PLAYER_USERNAME, this.player.getUsername());
        intent.putExtra(EXTRA_PLAYER_COLLECTION_USERNAME, playerToView.getUsername());
        startActivity(intent);
    }

    /**
     * Draws the player's profile image
     */
    private Bitmap draw_profile_image(Uri uri) throws IOException {
        Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
        profile.setImageBitmap(bitmap);
        return bitmap;
    }

    /**
     * Begins the process of uploading the player's profile image to firebase storage
     * @param bitmap
     */
    private void uploadProfileImage(Bitmap bitmap){
        // From: Youtube
        // URL: https://www.youtube.com/watch?v=CDv05EP45JQ&ab_channel=yoursTRULY
        // Author: yoursTruly
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);

        storageReference.child(username+".jpeg").putBytes(stream.toByteArray()).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                getProfileImageUri(storageReference);
            }
        });
    }

    /**
     * Retrieves the player's profile image from Firebase storage
     * @param storageReference
     */
    private void getProfileImageUri(StorageReference storageReference){
        storageReference.child(username+".jpeg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                player.setProfileImage(uri.toString());
            }
        });
    }

    /**
     * For the player to choose an image from their photo gallery
     */
    private void getProfileFromGallery(){
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        String picturesPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getPath();
        intent.setDataAndType(Uri.parse(picturesPath), "image/*");
        activityResultLauncher.launch(intent);
    }


    /**
     * Generates a unique username for a new player
     */
    private void generateUniqueUsername(){
        username = "user";
        for(int i = 0; i < 6; i++){
            Integer result = ThreadLocalRandom.current().nextInt(0, 10);
            username = username.concat(result.toString());
        }
        isUniqueCheck();
    }

    /**
     * Checking if the player's generated name is unique
     */
    private void isUniqueCheck(){
        db.collection("users").document(username).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful() && task.getResult().exists()){
                    // this is a duplicated username
                    generateUniqueUsername();
                }else{
                    // create the user
                    createNewUser();
                    nameDisplayed.setText(username);
                }
            }
        });
    }

    /**
     * Creates the player in Firebase
     */
    private void createNewUser(){
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(TEXT, username);
        editor.apply();
        nameDisplayed.setText(username);
        player = new Player(username);
        db.collection("users").document(username).set(player);
    }

    /**
     * Saves player data
     */
    @Override
    protected void onPause() {
        super.onPause();
        saveUserInfo("saveProfileImage");
    }

    /**
     * Saves player name
     */
    protected void saveUserInfo(String called){
        db.collection("users").document(username).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(called.equals("account")){
                    db.collection("users").document(username).update("email", player.getEmail());
                    db.collection("users").document(username).update("phonenumber", player.getPhoneNumber());
                }else{
                    db.collection("users").document(username).update("profileImage", player.getProfileImage());
                }
            }
        });
    }

    private void doesDocExist() {
        db.collection("users").document(username).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful() && task.getResult().exists()) {
                }else{
                    Glide.with(getApplicationContext()).clear(profile);
                    profile.setImageResource(R.drawable.default_profile_pic);
                    clearPlayerFromStorage();
                    generateUniqueUsername();
                }
            }
        });
    }



    /**
     * Retrieves the info of an existing player from the database
     */
    private void getPlayerInfo(){
        db.collection("users").document(username).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful() && task.getResult().exists()){
                    player = new Player(username);
                    ArrayList<GameQRCode> orig_gameQrCodes = (ArrayList<GameQRCode>) task.getResult().get("gameQRCodes");
                    HashMap<String, String> orig_gameQRCodeImages = (HashMap<String, String>) task.getResult().get("gameQRCodeImages");
                    player.setGameQRCodes(orig_gameQrCodes);
                    player.setGameQRCodeImages(orig_gameQRCodeImages);
                    player.highestQRCode = (int) (long) task.getResult().get("highestQRCode");
                    player.lowestQRCode = (int) (long) task.getResult().get("lowestQRCode");
                    player.pointTotal = (int) (long) task.getResult().get("pointTotal");
                    player.totalCodes = (int) (long) task.getResult().get("totalCodes");
                    String orig_profile_image = task.getResult().get("profileImage").toString();
                    String orig_email = task.getResult().get("email").toString();
                    String orig_phonenumber = task.getResult().get("phoneNumber").toString();
                    player.setEmail(orig_email);
                    player.setPhoneNumber(orig_phonenumber);
                    player.setProfileImage(orig_profile_image);
                    if(!player.getProfileImage().equals("")){
                        Glide.with(getApplicationContext()).asBitmap().load(Uri.parse(player.getProfileImage())).into(profile);
                    }else{
                        Glide.with(getApplicationContext()).clear(profile);
                        profile.setImageResource(R.drawable.default_profile_pic);
                    }
                    nameDisplayed.setText(username);
                    isPlayerSet = true;
                }else{
                    generateUniqueUsername();
                    isPlayerSet = true;
                }
            }
        });
    }

    /**
     * Retrieves a set amount of users at the top of some type of leaderboard
     */
    private void updateLeaders(String type, int limit){

        // clear player collection
        playerDataList.clear();

        // update leaderboard type
        playerListAdapter.setDisplay(type);

        // update leaderboard values
        db.collection("users")
                .orderBy(type, Query.Direction.DESCENDING)
                .limit(limit)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        int rank = 1;
                        String rankText = "#";

                        for (DocumentSnapshot doc : task.getResult()) {
                            // load player from database
                            Player newLeader = new Player(doc.get("username").toString());
                            newLeader.pointTotal = doc.getLong("pointTotal").intValue();
                            newLeader.totalCodes = doc.getLong("totalCodes").intValue();
                            newLeader.highestQRCode = doc.getLong("highestQRCode").intValue();
                            if (!doc.getString("profileImage").toString().equals("")) {
                                newLeader.profileImage = doc.getString("profileImage").toString();
                            } else {
                                newLeader.profileImage = "";
                            }

                            // add player to data list
                            playerDataList.add(newLeader);

                            // update user rank
                            if (newLeader.getUsername().equals(username)) {
                                rankText += String.valueOf(rank);
                            }
                            // increment rank
                            rank++;

                        }
                        // update leaderboard
                        playerListAdapter.notifyDataSetChanged();

                        // if user rank was not updated, get an estimate for user rank
                        if (!rankText.equals("#")) {
                            // display user rank
                            TextView playerRank = findViewById(R.id.player_rank);
                            playerRank.setText(rankText);
                        }
                    }
                });
    }

    /**
     * Retrieves a rank for the user based on firebase ranking map. Updates firebase rankings
     * if no update has occurred within the past hour
     */
    private void updateRank(String type) {
        // check when time of last update was
        db.collection("rankings").document(type).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                //HashMap<String, Integer> rankings = (HashMap<String, Integer>) task.getResult().get("ranks");
                int lastUpdate = task.getResult().getLong("lastUpdate").intValue();
                int currentTime = (int) (System.currentTimeMillis() / 1000);

                // if last update is less than an hour before the current time,
                // set the user rank. Otherwise, update all user rankings
                if (currentTime - lastUpdate < 3600 && task.getResult().get(username) != null) {
                    // set rankings value
                    TextView playerRank = findViewById(R.id.player_rank);
                    String rankText = "#" + task.getResult().get(username);
                    playerRank.setText(rankText);
                } else {
                    db.collection("users")
                            .orderBy(type, Query.Direction.DESCENDING)
                            .get()
                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> qstask) {

                                    // build rankings hashmap
                                    int rank = 1;
                                    HashMap<String, Integer> map = new HashMap<>();
                                    map.put("lastUpdate", currentTime);

                                    for (DocumentSnapshot doc : qstask.getResult()) {
                                        map.put(doc.get("username").toString(), rank++);
                                    }

                                    // save hashmap to database
                                    db.collection("rankings").document(type).set(map);

                                    // display user rank
                                    TextView playerRank = findViewById(R.id.player_rank);
                                    String rankText = "#" + map.get(username);
                                    playerRank.setText(rankText);
                                }
                            });
                }
            }
        });
    }

    /**
     * Onclick method for when the user accesses their account info (email and phone number)
     * @param view
     */
    public void userAccountButtonClicked(View view) {
        Intent intent = new Intent(MainActivity.this,Account.class);
        intent.putExtra("username", player.getUsername());
        startActivityIfNeeded(intent, 255);
    }

    /**
     * Updates relevant player atttributes when the player changes their email or phonenumber or both
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 255 && resultCode == RESULT_OK){
            Bundle info = data.getExtras();
            String UEmail = info.getString("email");
            String UPhonenumber = info.getString("phonenumber");

            if(!UEmail.equals("")){
                player.setEmail(info.getString("email"));
            }
            if(!UPhonenumber.equals("")){
                player.setPhoneNumber(info.getString("phonenumber"));
            }
            if(!UEmail.equals("") || !UPhonenumber.equals("")){
                saveUserInfo("account");
            }
        }
    }
}
