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
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

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
    private static final int REQUEST_LOCATION_PERMISSION = 1;
    ActivityResultLauncher<Intent> activityResultLauncher;
    LocationManager locationManager;
    GameQRCode scannedQrCode;
    double scannedLatitude;
    double scannedLongitude;
    Boolean boxChecked = false;
    ImageView imageView;
    String currentPhotoPath;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_save_qr);

        imageView = findViewById(R.id.QRView);
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        username = sharedPreferences.getString(TEXT, "");


        Intent intent = getIntent();
        String qrCodeString = intent.getStringExtra("qrcode");
        String qrCodeHash = shaHash(qrCodeString);
        int points = computeHashScore(qrCodeHash);
        scannedQrCode = new GameQRCode(qrCodeHash, points);

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
        activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                     if(result.getResultCode()== RESULT_OK && result.getData() != null){
                         Bundle bundle = result.getData().getExtras();
                         Bitmap bitmap = (Bitmap) bundle.get("data");
                         imageView.setImageBitmap(bitmap);
                         uploadQRImage(bitmap);
                     }
            }
        });
        captureQR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent1 = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (intent1.resolveActivity(getPackageManager()) != null) {
                    activityResultLauncher.launch(intent1);
                } else {
                    Toast.makeText(SaveQRActivity.this, "Error capturing image", Toast.LENGTH_SHORT).show();
                }
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
            Intent intentMain = new Intent(this, MainActivity.class);
            startActivity(intentMain);
        });
    }

    /**
     * Begins the process of uploading the player's profile image to firebase storage
     * @param bitmap
     */
    private void uploadQRImage(Bitmap bitmap){
        // From: Youtube
        // URL: https://www.youtube.com/watch?v=CDv05EP45JQ&ab_channel=yoursTRULY
        // Author: yoursTruly
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);

        storageReference.child(username).child(scannedQrCode.getHash()).putBytes(stream.toByteArray()).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Log.d("TAG", "onSuccess: ");
            }
        });
    }



    private void isUniqueCheck(){
        db.collection("GameQRCodes").document(scannedQrCode.getHash()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.getResult().exists()){
                    // this is a duplicated username
                    scannedQrCode = task.getResult().toObject(GameQRCode.class);
                    scannedQrCode.incrementAmountOfScans();
                    db.collection("GameQRCodes").document(scannedQrCode.getHash()).set(scannedQrCode);
                }else{
                    // create the user
                    db.collection("GameQRCodes").document(scannedQrCode.getHash()).set(scannedQrCode);
                }
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
    public static String shaHash(final String base) {
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