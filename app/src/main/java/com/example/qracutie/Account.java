package com.example.qracutie;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * The account activity allows a player to chang contact information associated with
 * their player profile, such as email and phone number
 */
public class Account extends AppCompatActivity {

    public static final String EXTRA_COMMENTS_TYPE = "com.example.qracutie.EXTRA_COMMENTS_TYPE";

    private EditText newEmail;
    private EditText newPhonenumber;
    private Button done;

    private String email = "";
    private String phonenumber = "";

    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.account);

        Intent intent = getIntent();

        newEmail = findViewById(R.id.new_email);
        newPhonenumber = findViewById(R.id.new_phonenumber);
        done = findViewById(R.id.done);

        // prepopulate email and phone number fields
        db.collection("users").document(intent.getStringExtra("username")).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                String oldEmail = task.getResult().get("email").toString();
                newEmail.setText(oldEmail);
                String oldNumber = task.getResult().get("phoneNumber").toString();
                newPhonenumber.setText(oldNumber);
            }
        });

       done.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               email = newEmail.getText().toString();
               phonenumber = newPhonenumber.getText().toString();
               Intent intent = new Intent(getApplicationContext(), MainActivity.class);
               intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
               Bundle info = new Bundle();
               info.putString("email", email);
               info.putString("phonenumber", phonenumber);
               intent.putExtras(info);
               setResult(RESULT_OK, intent);
               finish();
           }
       });

    }

    /**
     * Onclick method for when the user accesses their account info (email and phone number)
     * @param view
     */
    public void userQrButtonClicked(View view) {
        Intent intent = new Intent(Account.this, ShareableQrActivity.class);
        intent.putExtra(EXTRA_COMMENTS_TYPE, "login");
        startActivityIfNeeded(intent, 255);
    }
}
