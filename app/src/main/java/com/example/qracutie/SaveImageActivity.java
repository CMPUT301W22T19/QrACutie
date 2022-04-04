package com.example.qracutie;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.gson.Gson;

import java.io.ByteArrayOutputStream;

public class SaveImageActivity extends AppCompatActivity {
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    ActivityResultLauncher<Intent> activityResultLauncher;
    ImageView imageView;
    GameQRCode scannedQrCode;
    String qrCodeHash;
    Player player;
    StorageReference imageQRRef;
    StorageReference storageRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_capture_image);
        imageView = findViewById(R.id.QRView);
        Button SaveButton = findViewById(R.id.SaveQRImage);

        Intent intent = getIntent();
        String username1 = intent.getStringExtra("username");
        qrCodeHash = intent.getStringExtra("QRHash");
        String playerObject = intent.getStringExtra("player");
        player =  new Gson().fromJson(playerObject, Player.class);


        storageRef = FirebaseStorage.getInstance().getReference("gameQRcodeImages/"+username1);
        imageQRRef = storageRef.child(qrCodeHash);
        // From: Youtube
        // URL: //https://www.youtube.com/watch?v=qO3FFuBrT2E&t=380s
        // Author: Coding Demos

        // From: Android Studio docs
        // URL:https://developer.android.com/training/camera/photobasics
        // Author: Google

        activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                if(result.getResultCode()== RESULT_OK && result.getData() != null){
                    Log.d("entered", "onActivityResult: ");
                    Bundle bundle = result.getData().getExtras();
                    Bitmap bitmap = (Bitmap) bundle.get("data");
                    imageView.setImageBitmap(bitmap);
                    // uploadQRImage(bitmap);
                }
            }
        });

        Intent captureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (captureIntent.resolveActivity(getPackageManager()) != null) {
            activityResultLauncher.launch(captureIntent); }
        else {
            Toast.makeText(SaveImageActivity.this, "Error capturing image", Toast.LENGTH_SHORT).show();
            }

        SaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SaveImageActivity.this, SaveQRActivity.class);
                intent.putExtra("player", (new Gson()).toJson(player));
                intent.putExtra("activity", "SaveImageActivity");
                // intent.putExtra("image", (new Gson()).toJson(player));
                startActivity(intent);
            }
        });
        }
    /**
     * Begins the process of uploading the player's profile image to firebase storage
     * From: Firebase Documentation
     * Link: https://firebase.google.com/docs/storage/android/start
     */
    private void uploadQRImage(Bitmap bitmap){
        // Get the data from an ImageView as bytes
        imageView.setDrawingCacheEnabled(true);
        imageView.buildDrawingCache();
        // Bitmap bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        // byte[] data = baos.toByteArray();

//        UploadTask uploadTask = imageQRRef.putBytes(data);
//        uploadTask.addOnFailureListener(new OnFailureListener() {
//            @Override
//            public void onFailure(@NonNull Exception exception) {
//                // Handle unsuccessful uploads
//            }
//        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
//            @Override
//            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.
//                // ...
//                getProfileImageUri(imageQRRef);
//            }
//        });
        imageQRRef.putBytes(baos.toByteArray()).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                getImageUri();
            }
        });
    }

    /**
     * Adds image to player object
     */
    private void getImageUri(){
        imageQRRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                player.addImage(qrCodeHash, uri.toString());
            }
        });
    }

    /**
     * Retrieves the player's profile image from Firebase storage
     * @param storageReference
     */
    private void getProfileImageUri(StorageReference storageReference){
        storageReference.child(qrCodeHash).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                player.setProfileImage(uri.toString());
                getProfileImageUri(storageReference);
            }
        });
    }

};






