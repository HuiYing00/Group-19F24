package com.example.myapplication;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.Button;
import android.content.Intent;

public class MainActivity extends AppCompatActivity {
    Button buttonProfile;
    Button buttonMap;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        buttonProfile = (Button) findViewById(R.id.button_id_profile);
        buttonProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openProfile();
            }
        });

        buttonMap = (Button) findViewById(R.id.button_id_map);
        buttonMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openMap();
            }
        });
    }

    public void openProfile(){
        Intent intent = new Intent(this, Profile.class);
        startActivity(intent);
    }
    public void openMap(){
        Intent intent = new Intent(this, Map.class);
        startActivity(intent);
    }
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