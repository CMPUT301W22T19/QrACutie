package com.example.qracutie;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
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

    public static final String EXTRA_COMMENTS_USERNAME = "com.example.qracutie.EXTRA_COMMENTS_USERNAME";

    private Button userAccountButton;
    private ImageButton userQrButton;
    private TextView nameDisplayed;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("profileImages");
    private static final String SHARED_PREFS = "sharedPrefs";
    private static final String TEXT = "username";

    public static final String EXTRA_PLAYER_COLLECTION_USERNAME = "com.example.qracutie.EXTRA_PLAYER_COLLECTION_USERNAME";
    public static final String EXTRA_PLAYER_COLLECTION_ISOWNER = "com.example.qracutie.EXTRA_PLAYER_COLLECTION_ISOWNER";

    private Player player;
    private String username = "";
    private String email = "";
    private String phonenumber = "";

    private ImageView profile;
    private Button userCollectionButton;
    private String profile_image = "profileImage";
    private String profile_image_uri = "";
    private String profile_image_stored = "";
    private Uri uri;
    private String url = "";

    private Boolean onCreated = false;

    private Bitmap bitmap;

    private ArrayList<Player> playerList = new ArrayList<>();

    ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if(result.getData() != null){
                        uri = result.getData().getData();
                        profile_image_uri = uri.toString(); //added now
                        draw_profile_image();
                        uploadProfileImage(bitmap);
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

        // SharedPreferences sharedPreferences  = getApplicationContext().getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        //sharedPreferences.edit().clear().commit();

        mapButton = (ImageButton)findViewById(R.id.mapButton);
        mapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), MapsActivity.class);
                startActivity(intent);
            }
        });
        userAccountButton = (Button) findViewById(R.id.user_account_button);
        userQrButton = (ImageButton) findViewById(R.id.user_qr_button);
        nameDisplayed = (TextView) findViewById(R.id.display_name);

        onCreated = true;
        profile = (ImageView) findViewById(R.id.profile_picture);
        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getProfileFromGallery();
            }
        });

        // add on click listener for my collection button
        Button myCollection = findViewById(R.id.collection_button);
        myCollection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openPlayerCollectionActivity(player);
            }
        });

        Button button1 = (Button)findViewById(R.id.cameraButton);
        button1.setOnClickListener(view -> {
            Intent intent = new Intent(view.getContext(), CameraActivity.class);
            view.getContext().startActivity(intent);});

        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        username = sharedPreferences.getString(TEXT,"");
        // if username isn't stored in shared preferences, then generate a username
        // someone is opening our app for the first time
        if (username.equals("")){
            generateUniqueUsername();
        }else{
            // user already exists in the database
            nameDisplayed.setText(username);
            player = new Player(username);
            getPlayerInfo();
            // From: Youtube
            // URL: https://www.youtube.com/watch?v=xzCsJF9WtPU&ab_channel=EasyLearn
            // Author: EasyLearn
            StorageReference ref = storageReference.child(username+".jpeg");
            try {
                final File localFile = File.createTempFile(username, ".jeg");
                ref.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                        Bitmap bmap = BitmapFactory.decodeFile(localFile.getAbsolutePath());
                        float fwidth = bmap.getWidth();
                        float fheight = bmap.getHeight();
                        float ratio;
                        Integer width;
                        Integer height;
                        if(fwidth < fheight){
                            ratio = fwidth/120;
                            fheight = fheight/ratio;
                            width = 120;
                            height = (Integer) Math.round(fheight);
                            profile.setImageBitmap(Bitmap.createScaledBitmap(bmap, width, height, false));
                        }else if(fheight < fwidth){
                            ratio = fheight/120;
                            fwidth = fwidth/ratio;
                            width = (Integer) Math.round(fwidth);
                            height = 120;
                            profile.setImageBitmap(Bitmap.createScaledBitmap(bmap, width, height, false));
                        }else{
                            profile.setImageBitmap(Bitmap.createScaledBitmap(bmap, 120, 120, false));
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        sharedPreferences = getApplicationContext().getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        sharedPreferences.edit().clear().commit();
    }

    /**
     * switches to a PlayerCollection activity wherein the user will be able to see the
     * details of a specific player profile, including player statistics and a list of all
     * QR Codes which have been collected
     */
    private void openPlayerCollectionActivity(Player playerToView) {

        Intent intent = new Intent(this, PlayerCollectionActivity.class);

        intent.putExtra(EXTRA_PLAYER_COLLECTION_USERNAME, playerToView.getUsername());

        if (playerToView.getUsername().equals(this.player.getUsername())) {
            // player would like to view their own collection
            intent.putExtra(EXTRA_PLAYER_COLLECTION_ISOWNER, true);
        } else {
            // player would like to view someone else's collection
            intent.putExtra(EXTRA_PLAYER_COLLECTION_ISOWNER, false);
        }

        startActivity(intent);
    }

    /**
     * Draws the player's profile image
     */
    private void draw_profile_image(){
        try{
            bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
            profile.setImageBitmap(bitmap);
        }catch (IOException e){
        }
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
                if(task.getResult().exists()){
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
        player = new Player(username);
        db.collection("users").document(username).set(player);
    }

    /**
     * Saves player data
     */
    @Override
    protected void onPause() {
        super.onPause();
        saveUser();
    }

    /**
     * Saves player name
     */
    protected void saveUser(){
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(TEXT, username);
        editor.apply();
        savePlayerInfo();
    }

    /**
     * Saves player attributes
     */
    private void savePlayerInfo(){
        db.collection("users").document(username).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                profile_image_uri = player.getProfileImage();
                if(!profile_image_stored.equals(profile_image_uri)){
                    db.collection("users").document(username).update(profile_image, profile_image_uri);
                }
                if(!email.equals(player.getEmail())){
                    db.collection("users").document(username).update("email", player.getEmail());

                }
                if(!phonenumber.equals(player.getPhoneNumber())){
                    db.collection("users").document(username).update("phoneNumber", player.getPhoneNumber());
                }

                if(!username.equals(player.getUsername())){
                    db.collection("users").document(username).update("username", player.getUsername());
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
                player.setEmail(task.getResult().get("email").toString());
                player.setPhoneNumber(task.getResult().get("phoneNumber").toString());
                email = player.getEmail();
                phonenumber = player.getPhoneNumber();
                url = task.getResult().get("profileImage").toString();
            }
        });
    }

    /**
     * Onclick method for when the user accesses their account info (email and phone number)
     * @param view
     */
    public void userAccountButtonClicked(View view) {
        Intent intent = new Intent(MainActivity.this,Account.class);

        startActivityIfNeeded(intent, 255);
    }

    /**
     * Onclick method for when the user accesses their account info (email and phone number)
     * @param view
     */
    public void userQrButtonClicked(View view) {
        Intent intent = new Intent(MainActivity.this, ShareableQrActivity.class);
        intent.putExtra(EXTRA_COMMENTS_USERNAME, username);
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
        }
    }
}
