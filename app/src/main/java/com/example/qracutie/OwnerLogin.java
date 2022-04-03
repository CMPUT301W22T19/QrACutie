package com.example.qracutie;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class OwnerLogin extends AppCompatActivity {
    private Button log_in_clicked;
    private Button reset_password;
    private String username;
    private FirebaseAuth mAuth;
    private EditText email;
    private EditText password;
    private String emailstr = "";
    private String passwordstr = "";
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_owner_login);
        Intent intent = getIntent();
        username = intent.getStringExtra("username");

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        mAuth = FirebaseAuth.getInstance();
        email = (EditText) findViewById(R.id.email);
        password = (EditText) findViewById(R.id.password);
        log_in_clicked = (Button) findViewById(R.id.owner_login);
        reset_password = (Button) findViewById(R.id.reset_password);
        // From: stackoverflow
        // URL: https://stackoverflow.com/a/44260541
        // Author: mohit singh
        reset_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                emailstr = email.getText().toString();
                if (!emailstr.equals("")) {
                    mAuth.sendPasswordResetEmail(emailstr).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                Toast.makeText(getApplicationContext(), "Password reset email sent", Toast.LENGTH_SHORT).show();
                            }else{
                                Toast.makeText(getApplicationContext(), "Please try again", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }else{
                    Toast.makeText(getApplicationContext(), "Please input your email", Toast.LENGTH_SHORT).show();
                }
            }
        });

        log_in_clicked.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                emailstr = email.getText().toString();
                passwordstr = password.getText().toString();
                if(!emailstr.equals("") && !passwordstr.equals("")){
                    mAuth.signInWithEmailAndPassword(emailstr,passwordstr).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                gotToOwnersPage();
                            }else{
                                Toast.makeText(getApplicationContext(), "login failed", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });
    }

    private void gotToOwnersPage(){
        Intent intent = new Intent(this, OwnerAllPlayers.class);
        intent.putExtra("username", username);
        startActivity(intent);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }
}
