package com.example.qracutie;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;

/**
 * The account activity allows a player to chang contact information associated with
 * their player profile, such as email and phone number
 */
public class Account extends AppCompatActivity {

    public static final String EXTRA_COMMENTS_TYPE = "com.example.qracutie.EXTRA_COMMENTS_TYPE";
    private static final String SHARED_PREFS = "sharedPrefs";
    private static final String TEXT = "username";

    private String username = "";

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

        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        username = sharedPreferences.getString(TEXT, "");

        Intent intent = getIntent();

        newEmail = findViewById(R.id.new_email);
        newPhonenumber = findViewById(R.id.new_phonenumber);
        done = findViewById(R.id.done);

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
