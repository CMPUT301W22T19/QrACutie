package com.example.qracutie;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;

public class Account extends AppCompatActivity {
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
}
