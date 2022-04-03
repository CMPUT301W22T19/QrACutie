package com.example.qracutie;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Layout;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class OwnerAllPlayers extends AppCompatActivity {

    private StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("profileImages");
    private ArrayList<String> playerNames = new ArrayList<>();
    private ArrayList<String> playerImages = new ArrayList<>();
    private ArrayList<String> QRNames = new ArrayList<>();

    private RecyclerView recyclerView;
    private OwnerRecyclerviewAdapter adapter;

    private RecyclerView QRRecyclerView;
    private OwnerQRRecyclerviewAdapter QRAdapter;

    private Integer selectedQR = -1;
    private String username;

    LinearLayout L_Player;
    LinearLayout L_Qr;
    Button toPlayerList;
    Button toQRList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_owner_all_players);

        Intent intent = getIntent();
        username = intent.getStringExtra("username");

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        L_Player = (LinearLayout) findViewById(R.id.L_player);
        L_Qr = (LinearLayout) findViewById(R.id.L_Qr);
        toPlayerList = (Button) findViewById(R.id.to_player_list);
        toQRList = (Button) findViewById(R.id.to_qr_list);

        FirebaseFirestore.getInstance().collection("users").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    Integer numPlayers = task.getResult().size();
                    for(int i=0; i<numPlayers; i++){
                        playerNames.add(task.getResult().getDocuments().get(i).getId().toString());
                        String imStr = task.getResult().getDocuments().get(i).get("profileImage").toString();
                        if(!imStr.equals("")){
                            playerImages.add(imStr);
                        }else{
                            playerImages.add("default image");
                        }
                    }
                    if(playerNames.size() == numPlayers){
                        recyclerViewToScreen(numPlayers);
                    }
                }
            }
        });
        FirebaseFirestore.getInstance().collection("GameQRCodes").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    Integer numQRNames = task.getResult().size();
                    for(int i=0; i<numQRNames; i++){
                        QRNames.add(task.getResult().getDocuments().get(i).getId().toString());
                    }
                    if(numQRNames == QRNames.size()){
                        QRRecyclerViewToScreen();
                    }
                }
            }
        });
    }

    private void recyclerViewToScreen(Integer num){
        Toast.makeText(getApplicationContext(), Integer.toString(num), Toast.LENGTH_SHORT).show();
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        adapter = new OwnerRecyclerviewAdapter(username, playerNames, playerImages, getApplicationContext());
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        recyclerView.setAdapter(adapter);
    }

    private void QRRecyclerViewToScreen(){
        QRRecyclerView = (RecyclerView) findViewById(R.id.QR_recycler_view);
        QRAdapter = new OwnerQRRecyclerviewAdapter(QRNames, getApplicationContext());
        QRRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        QRRecyclerView.setAdapter(QRAdapter);
    }

    public void toQRListClicked(View view) {
        L_Player.setVisibility(View.GONE);
        L_Qr.setVisibility(View.VISIBLE);
        toQRList.setVisibility(View.GONE);
        toPlayerList.setVisibility(View.VISIBLE);
    }

    public void toPlayerListClicked(View view) {
        L_Qr.setVisibility(View.GONE);
        L_Player.setVisibility(View.VISIBLE);
        toPlayerList.setVisibility(View.GONE);
        toQRList.setVisibility(View.VISIBLE);
    }

    @Override
    public void onBackPressed() {
        sendClassInfo();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            sendClassInfo();
        }
        return super.onOptionsItemSelected(item);
    }

    private void sendClassInfo(){
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("activity", "ownerspage");
        startActivity(intent);
    }
}
