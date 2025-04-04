package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
public class Profile extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
    }
    public void toHome(View view){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    public void openCarInfo(View view){
        Intent intent = new Intent(this, Car_Activity.class);
        startActivity(intent);
    }

    public void openMap(View view){
        Intent intent = new Intent(this, Map.class);
        startActivity(intent);
    }
}