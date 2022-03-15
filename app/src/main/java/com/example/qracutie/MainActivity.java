package com.example.qracutie;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.os.Environment;

import android.provider.MediaStore;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

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

public class MainActivity extends AppCompatActivity {
    private Button userAccountButton;
    private TextView nameDisplayed;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("profileImages");
    private static final String SHARED_PREFS = "sharedPrefs";
    private static final String TEXT = "username";

    private Player player;
    private String username = "";
    private String email = "";
    private String phonenumber = "";

    private ImageView profile;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        userAccountButton = (Button) findViewById(R.id.user_account_button);
        nameDisplayed = (TextView) findViewById(R.id.display_name);

        onCreated = true;
        profile = (ImageView) findViewById(R.id.profile_picture);
        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getProfileFromGallery();
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
        // sharedPreferences = getApplicationContext().getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        // sharedPreferences.edit().clear().commit();
    }

    private void draw_profile_image(){
        try{
            bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
            profile.setImageBitmap(bitmap);
        }catch (IOException e){
        }
    }

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

    private void getProfileImageUri(StorageReference storageReference){
        storageReference.child(username+".jpeg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                player.setProfileImage(uri.toString());
            }
        });
    }

    private void getProfileFromGallery(){
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        String picturesPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getPath();
        intent.setDataAndType(Uri.parse(picturesPath), "image/*");
        activityResultLauncher.launch(intent);
    }

    private void generateUniqueUsername(){
        username = "user";
        for(int i = 0; i < 6; i++){
            Integer result = ThreadLocalRandom.current().nextInt(0, 10);
            username = username.concat(result.toString());
        }
        isUniqueCheck();
    }

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

    private void createNewUser(){
        player = new Player(username);
        db.collection("users").document(username).set(player);
    }

    @Override
    protected void onPause() {
        super.onPause();
        saveUser();
    }

    protected void saveUser(){
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(TEXT, username);
        editor.apply();
        savePlayerInfo();
    }

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

    public void userAccountButtonClicked(View view) {
        Intent intent = new Intent(MainActivity.this,Account.class);
        startActivityIfNeeded(intent, 255);
    }

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
