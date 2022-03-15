package com.example.qracutie;

import static android.content.Context.MODE_PRIVATE;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

public class MainActivity extends AppCompatActivity {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    public static final String SHARED_PREFS = "sharedPrefs";
    public static final String TEXT = "username";

    public String username;
    public Player player;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        Button button1 = (Button)findViewById(R.id.cameraButton);
//        button1.setOnClickListener(view -> {
//            Intent intent = new Intent(view.getContext(),Camera_.class);
//            view.getContext().startActivity(intent);});
    }
        //SharedPreferences sharedPreferences  = getApplicationContext().getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        //sharedPreferences.edit().clear().commit();


    @Override
    protected void onStart(){
        super.onStart();
        loadData();
    }

    public void generateUniqueUsername(){
        username = "user";
        for(int i = 0; i < 6; i++){
            Integer result = ThreadLocalRandom.current().nextInt(0, 10);
            username = username.concat(result.toString());
        }
        isUniqueCheck();
    }

    public void isUniqueCheck(){

        db.collection("users").document(username).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.getResult().exists()){
                    // This is a duplicated username
                    generateUniqueUsername();
                    //Log.d("ENTERED","duplicate user");
                }else{
                    // create the user
                    //Log.d("ENTERED","create user");
                    createNewUser();
                }
            }
        });
    }

    public void createNewUser(){

        player = new Player(username);

        Map<String, String> data = new HashMap<>();
        data.put("PhoneNumber", "0123456789"); // for example
        db.collection("users").document(username).set(data);
    }

    @Override
    protected void onPause() {
        super.onPause();
        saveUser();
    }

    public void saveUser(){
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        if(sharedPreferences.getString(TEXT,"").equals(username) == false){
            //Log.d("ENTERED", username);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(TEXT, username);
            editor.apply();
        }
    }

    public void loadData(){
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        username = sharedPreferences.getString(TEXT,"");

        // if username isn't stored in shared preferences, then generate a username
        // Someone is opening our app for the first time
        if (username.equals("")){
            //Log.d("ENTERED","generate user");
            generateUniqueUsername();
        }else{
            //Log.d("ENTERED","user exists");
            Toast.makeText(getApplicationContext(), username, Toast.LENGTH_SHORT).show();
            // user already exists in the database
        }
    }
}
