package com.example.qracutie;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.RelativeLayout;

import androidx.appcompat.app.AppCompatActivity;

public class AfterCamera extends AppCompatActivity{

    private Button capture;
    private Button done;
    private Button checkbox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.after_qr_code_scanned);
        Button capture = (Button) findViewById(R.id.capture);
        Button done = (Button) findViewById(R.id.done);
        Button checkbox = (Button) findViewById(R.id.checkBox);
    }


}
