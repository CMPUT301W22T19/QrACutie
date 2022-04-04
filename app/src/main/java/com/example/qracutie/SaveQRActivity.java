package com.example.qracutie;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.gson.Gson;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.function.Consumer;

/**
 * The save QR activity allows a user to save a QR code to their player profile, and also
 * gives the user two additional options:
 * 1. to track geolocation of found QR code
 * 2. to add an image to their profile associated with the QR code
 */
public class SaveQRActivity extends AppCompatActivity {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("gameQRCodeImages");
    private static final String SHARED_PREFS = "sharedPrefs";
    private static final String TEXT = "username";
    private String username = "";
    private String username1;
    private static final int REQUEST_LOCATION_PERMISSION = 1;
    ActivityResultLauncher<Intent> activityResultLauncher;
    LocationManager locationManager;
    GameQRCode scannedQrCode;
    Player player;
    double scannedLatitude;
    double scannedLongitude;
    Boolean boxChecked = false;
    ImageView imageView;
    Bitmap capturedImage;
    String qrCodeString;
    StorageReference storageRef;
    StorageReference imageQRRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_save_qr);

        Intent intent = getIntent();
        String activity = intent.getStringExtra("activity");
        String playerObject = intent.getStringExtra("player");
        player =  new Gson().fromJson(playerObject, Player.class);
        qrCodeString = intent.getStringExtra("qrcode");
        String qrCodeHash = shaHash(qrCodeString);
        int points = computeHashScore(qrCodeHash);
        scannedQrCode = new GameQRCode(qrCodeHash, points);
        if(activity != null && activity.equals("SaveImageActivity")) {
            imageView = findViewById(R.id.save_image_view);
            String imageObject = intent.getStringExtra("player");
            capturedImage = (Bitmap) intent.getParcelableExtra("image");
            imageView.setImageBitmap(capturedImage);
            Toast.makeText(getApplicationContext(), activity, Toast.LENGTH_SHORT).show();
            storageRef = FirebaseStorage.getInstance().getReference("gameQRcodeImages/"+player.getUsername());
            imageQRRef = storageRef.child(qrCodeHash);
        }

        Toast.makeText(getApplicationContext(), player.getUsername(), Toast.LENGTH_SHORT).show();

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        // This part asks for location access permission from the user
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]
                            {Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_LOCATION_PERMISSION);
        }
        // From: Youtube
        // URL: //https://www.youtube.com/watch?v=qO3FFuBrT2E&t=380s
        // Author: Coding Demos

        // From: Android Studio docs
        // URL:https://developer.android.com/training/camera/photobasics
        // Author: Google

        // uses built-in camera to save image
        Button captureQR = (Button) findViewById(R.id.CapturePic);

        captureQR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), SaveImageActivity.class);
                intent.putExtra("player", (new Gson()).toJson(player));
                intent.putExtra("qrcode", qrCodeString);
                startActivity(intent);
            }
        });

        Button done = findViewById(R.id.done_button);
        done.setOnClickListener(view -> {
            // Main
            if(boxChecked){
                scannedQrCode.setLatitude(scannedLatitude);
                scannedQrCode.setLongitude(scannedLongitude);
            }
            isUniqueCheck();
            if(capturedImage != null) uploadQRImage();
            Intent intentMain = new Intent(this, MainActivity.class);
            intentMain.putExtra("activity", "SaveQRActivity");
            intentMain.putExtra("player", (new Gson()).toJson(player));
            startActivity(intentMain);
        });
    }

    /**
     * This function uses the latitude and longitude from 2 locations to determine their distance
     * and returns that distance as a double
     *
     * The following function was obtained from the answer of this stackoverflow question
     * https://stackoverflow.com/questions/49839437/how-to-show-markers-only-inside-of-radius-circle-on-maps
     * @param LAT1 Location 1 Latitude
     * @param LONG1 Location 1 Longitude
     * @param LAT2 Location 2 Latitude
     * @param LONG2 Location 2 Longitude
     * @return
     */
    public double getDistance(double LAT1, double LONG1, double LAT2, double LONG2) {
        double distance = 2 * 6371000 * Math.asin(Math.sqrt(Math.pow((Math.sin((LAT2 * (3.14159 / 180) - LAT1 * (3.14159 / 180)) / 2)), 2) + Math.cos(LAT2 * (3.14159 / 180)) * Math.cos(LAT1 * (3.14159 / 180)) * Math.sin(Math.pow(((LONG2 * (3.14159 / 180) - LONG1 * (3.14159 / 180)) / 2), 2))));
        return distance;
    }

    private void isUniqueCheck(){
        db.collection("GameQRCodes").document(scannedQrCode.getHash()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.getResult().exists()){
                    // this is a duplicated username
                    scannedQrCode = task.getResult().toObject(GameQRCode.class);
                    scannedQrCode.incrementAmountOfScans();
                    if(boxChecked && getDistance(scannedQrCode.getLatitude(), scannedQrCode.getLongitude(), scannedLatitude, scannedLongitude) >= 5.0) {
                        scannedQrCode.setLongitude(scannedLongitude);
                        scannedQrCode.setLatitude(scannedLatitude);
                    }
                    db.collection("GameQRCodes").document(scannedQrCode.getHash()).set(scannedQrCode);
                    Toast.makeText(getApplicationContext(), "BEFORE: " + (Integer.toString(player.getGameQRCodes().size())), Toast.LENGTH_SHORT).show();
                    player.addGameQRCode(scannedQrCode);
                    Toast.makeText(getApplicationContext(), "AFTER: " + (Integer.toString(player.getGameQRCodes().size())), Toast.LENGTH_SHORT).show();

                }else{
                    db.collection("GameQRCodes").document(scannedQrCode.getHash()).set(scannedQrCode);
                    Toast.makeText(getApplicationContext(), "BEFORE: " + (Integer.toString(player.getGameQRCodes().size())), Toast.LENGTH_SHORT).show();
                    player.addGameQRCode(scannedQrCode);
                    Toast.makeText(getApplicationContext(), "AFTER: " + (Integer.toString(player.getGameQRCodes().size())), Toast.LENGTH_SHORT).show();

                }
            }
        });
    }

    /**
     * Begins the process of uploading the player's profile image to firebase storage
     * From: Firebase Documentation
     * Link: https://firebase.google.com/docs/storage/android/start
     */
    private void uploadQRImage(){

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        capturedImage.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();
        StorageMetadata metadata = new StorageMetadata.Builder()
                .setContentType("image/jpg")
                .build();
        UploadTask uploadTask = imageQRRef.putBytes(data, metadata);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.
                // ...
                Toast.makeText(getApplicationContext(), "SavedTODB", Toast.LENGTH_SHORT).show();
                addImageUri();
            }
        });
    }

    /**
     * Adds image to player object
     */
    private void addImageUri(){
        imageQRRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Toast.makeText(getApplicationContext(), "BEFORE IMAGES: " + player.getGameQRCodeImages().size(), Toast.LENGTH_SHORT).show();
                player.addImage(scannedQrCode.getHash(), uri.toString());
                Toast.makeText(getApplicationContext(), "AFTER IMAGES: " + player.getGameQRCodeImages().size(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * after user checks the enable location box, the method tests to see if user has enabled
     * location permissions on their device, and then requests location from the device
     * @param view check box element
     */
    @SuppressLint("MissingPermission")
    @RequiresApi(api = Build.VERSION_CODES.N)
    public void onCheckboxClicked(View view) {
        // Is the view now checked?
        boolean checked = ((CheckBox) view).isChecked();

        // Check which checkbox was clicked
        switch (view.getId()) {
            case R.id.location:
                if (checked) {
                    // This part asks for location access permission from the user
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(this, new String[]
                                        {Manifest.permission.ACCESS_FINE_LOCATION},
                                REQUEST_LOCATION_PERMISSION);
                    }
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                        locationManager.getCurrentLocation(
                                LocationManager.GPS_PROVIDER,
                                null,
                                SaveQRActivity.this.getMainExecutor(),
                                new Consumer<Location>() {
                                    @Override
                                    public void accept(Location location) {
                                        scannedLatitude = location.getLatitude();
                                        scannedLongitude = location.getLongitude();
                                        boxChecked = true;
                                    }
                                });
                    }
                }
                // Put some meat on the sandwich
                else {
                    // Remove the meat
                    boxChecked = false;
                    break;
                }
        }
    }

    /**
     * hashes a string representation of a QR code using the sha 256 algorithm
     * @param base string representation of QR code
     * @return QR code hash
     */
    // https://stackoverflow.com/questions/5531455/how-to-hash-some-string-with-sha256-in-java
    public String shaHash(final String base) {
        try{
            final MessageDigest digest = MessageDigest.getInstance("SHA-256");
            final byte[] hash = digest.digest(base.getBytes("UTF-8"));
            final StringBuilder hexString = new StringBuilder();
            for (int i = 0; i < hash.length; i++) {
                final String hex = Integer.toHexString(0xff & hash[i]);
                if(hex.length() == 1)
                    hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch(Exception ex){
            throw new RuntimeException(ex);
        }
    }

    private int charToInt(char ch)
    {
        if (ch >= '0' && ch <= '9')
            return ch - '0';
        if (ch >= 'A' && ch <= 'F')
            return ch - 'A' + 10;
        if (ch >= 'a' && ch <= 'f')
            return ch - 'a' + 10;
        return -1;
    }

    private int computeHashScore(String hash) {
        int points = 0;
        int indexIncrement = 0;
        char currChar;
        char nextChar;
        int repeatCounter = 0;
        for(int i = 0; i < hash.length();i++){
            currChar = hash.charAt(i);
            Boolean check = true;
            indexIncrement = i+1;
            while(check) {
                nextChar = '/';
                if(indexIncrement < hash.length()){
                    nextChar = hash.charAt(indexIncrement);
                }
                if(nextChar == currChar){
                    indexIncrement++;
                    repeatCounter++;
                    continue;
                }
                if(repeatCounter > 0){
                    int baseValue = charToInt(currChar);
                    points += (int) Math.pow(baseValue, repeatCounter);
                    repeatCounter = 0;
                    i = indexIncrement;
                }
                check = false;
            }
        }
        return points;
    }
}