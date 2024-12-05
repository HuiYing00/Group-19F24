package com.example.myapplication;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.content.Intent;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {
    ImageButton btnProfile;
    ImageButton btnMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        btnProfile = (ImageButton) findViewById(R.id.btn_profile);
//        btnProfile.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                openProfile(v);
//            }
//        });
//
//        btnMap = (ImageButton) findViewById(R.id.btn_drive);
//        btnMap.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                openMap(v);
//            }
//        });

        btnProfile = findViewById(R.id.btn_profile);
        btnProfile.setOnClickListener(this::openProfile);

        btnMap = findViewById(R.id.btn_drive);
        btnMap.setOnClickListener(this::openMap);
    }


    public void openProfile(View view){
        Intent intent = new Intent(this, Profile.class);
        startActivity(intent);
    }
    public void openMap(View view){
        Intent intent = new Intent(this, Map.class);
        startActivity(intent);
    }

    public void openCarInfo(View view){
        Intent intent = new Intent(this, Car_Activity.class);
        startActivity(intent);
    }
}