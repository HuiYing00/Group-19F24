package com.example.myapplication;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.content.Intent;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.Task;
import com.google.maps.android.PolyUtil;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class Map extends AppCompatActivity implements OnMapReadyCallback {

    private final int PERM_FINE_LOCATION = 1;
    private final LatLng destinationLatLng = new LatLng(43.452969, -80.495064); // Replace with your destination

    Location currentLocation;
    FusedLocationProviderClient fusedLocationProviderClient;
    //private FusedLocationProviderClient fusedLocationClient;
    private LocationCallback locationCallback;

    private GoogleMap myMap;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) return;

                // Update user location dynamically
                Location location = locationResult.getLastLocation();
                LatLng currentLatLng = new LatLng(location.getLatitude(), location.getLongitude());
                //myMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 19));
            }
        };
        getLastLocation();
        startLocationUpdates();
    }

    private void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERM_FINE_LOCATION);
            return;
        }

        fusedLocationProviderClient.requestLocationUpdates(
                LocationRequest.create().setInterval(5000).setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY),
                locationCallback,
                Looper.getMainLooper()
        );
    }

    private void getLastLocation()
    {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERM_FINE_LOCATION);
            return;
        }
        Task<Location> task = fusedLocationProviderClient.getLastLocation();
        task.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null)
                {
                    currentLocation = location;

                    SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);

                    if (mapFragment == null) {
                        Log.e("MapInitialization", "Map Fragment is null");
                        return;
                    }
                    else {
                        Log.e("MapInitialization", "mapFragment has a value");
                    }

                    mapFragment.getMapAsync(Map.this);
                }
            }
        });
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap)
    {
        myMap = googleMap;
        LatLng myLoc = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
        myMap.addMarker(new MarkerOptions().position(myLoc).title(""));
        myMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myLoc, 19));
        //fetchRoute();
    }

    private void fetchRoute() {
        String url = "https://maps.googleapis.com/maps/api/directions/json?origin=" +
                currentLocation.getLatitude() + "," + currentLocation.getLongitude() +
                "&destination=" + destinationLatLng.latitude + "," + destinationLatLng.longitude +
                "&key=" + "AIzaSyChliqG4c6ZN4VvfpHGRe1n8HPEf-0-hlA";

        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(url)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("DirectionsAPI", "Request failed", e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) {
                    Log.e("DirectionsAPI", "Request failed: " + response);
                    return;
                }

                String responseData = response.body().string();

                try {
                    // Parse the response to extract the route polyline
                    JSONObject json = new JSONObject(responseData);
                    JSONArray routes = json.getJSONArray("routes");
                    if (routes.length() > 0) {
                        JSONObject route = routes.getJSONObject(0);
                        JSONArray legs = route.getJSONArray("legs");
                        JSONArray steps = legs.getJSONObject(0).getJSONArray("steps");


                        // Decode the polyline points and draw the route on the map
                        String polyline = route.getJSONObject("overview_polyline").getString("points");
                        List<LatLng> decodedPath = PolyUtil.decode(polyline);
                        Log.d("Polyline", "Encoded Polyline: " + polyline);

                        for (LatLng point : decodedPath) {
                            Log.d("DecodedPoint", "Lat: " + point.latitude + ", Lng: " + point.longitude);
                        }

                        runOnUiThread(() -> myMap.addPolyline(new PolylineOptions()
                                .addAll(decodedPath)
                                .color(0xFF0000FF)
                                .width(10)));

//                        LatLngBounds.Builder builder = new LatLngBounds.Builder();
//                        for (LatLng point : decodedPath) {
//                            builder.include(point);
//                        }
//                        myMap.moveCamera(CameraUpdateFactory.newLatLngBounds(builder.build(), 100));

                    }
                } catch (Exception e) {
                    Log.e("DirectionsAPI", "Error parsing JSON", e);
                }
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERM_FINE_LOCATION)
        {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {
                getLastLocation();

            }
            else
            {
                Toast.makeText(this, "Location permission is denied. Please allow location to use this feature.", Toast.LENGTH_SHORT).show();
            }
        }
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
//        final TextView textTitle=findViewById(R.id.text_title);
//        textTitle.setText("The trip has started!");
//        final TextView textScore=findViewById(R.id.text_score);
//        textScore.setText("Loading...");
        fetchRoute();
    }
    public void endTrip(View view){
        // logic to calculate and display score here
//        final TextView textTitle=findViewById(R.id.text_title);
//        textTitle.setText("Insurance Application");
//        final TextView textScore=findViewById(R.id.text_score);
//        textScore.setText("50%");
        myMap.clear();
    }

}