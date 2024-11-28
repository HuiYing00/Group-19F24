package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;

public class Map extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
    }

    public void toHome(View view){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    public void openProfile(View view){
        Intent intent = new Intent(this, Profile.class);
        startActivity(intent);
    }

//    public void openMap(View view){
//        Intent intent = new Intent(this, Map.class);
//        startActivity(intent);
//    }

    public void startTrip(View view){
        // Logic to get data here
        final TextView textTitle=findViewById(R.id.text_title);
        textTitle.setText("The trip has started!");
        final TextView textScore=findViewById(R.id.text_score);
        textScore.setText("Loading...");
    }
    public void endTrip(View view){
        // logic to calculate and display score here
        final TextView textTitle=findViewById(R.id.text_title);
        textTitle.setText("Insurance Application");
        final TextView textScore=findViewById(R.id.text_score);
        textScore.setText("50%");
    }
}