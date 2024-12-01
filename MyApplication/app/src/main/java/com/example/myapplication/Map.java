package com.example.myapplication;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothProfile;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.content.SharedPreferences;
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
import android.widget.TextView;
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

public class Map extends AppCompatActivity implements OnMapReadyCallback, SensorEventListener {

    private final int PERM_FINE_LOCATION = 1;
    private final LatLng destinationLatLng = new LatLng(43.452969, -80.495064); // Replace with your destination
    private final int BATCH_SIZE = 30;
    private final int DEFAULT_SPEED = 50;

    private static final float HARD_STOP_ACCEL_THRESHOLD = -10.0f;
    private static final float GYRO_ROTATION_THRESHOLD = 10.0f;
    private static final long EVENT_COOLDOWN_MS = 1000; // 1 second cooldown
    private long lastEventTimestamp = 0;

    private final int SPEED_LOW = 4;
    private final int SPEED_MED = 10;
    private final int SPEED_HI = 15;

    private final int PEN_SMALL = 20;
    private final int PEN_MED = 50;
    private final int PEN_HI = 100;

    private final int START_SCORE = 1000;

    private final int INTERVAL = 10000;
    private int score = START_SCORE;

    Location currentLocation;
    private Location previousLocation = null;
    private long previousTimestamp = 0;
    FusedLocationProviderClient fusedLocationProviderClient;
    private LocationCallback locationCallback;
    private GoogleMap myMap;
    private int speedLimit;
    private TextView tvSpeedLimit;
    private TextView tvyourSpeed;
    private TextView tvyourScore;
    List<LatLng> decodedLatLng;
    private boolean roads = true;
    private String currentMac;
    private String storedMac;
    private SensorManager sensorManager;
    private Sensor accelerometer;
    private Sensor gyroscope;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        tvSpeedLimit = findViewById(R.id.tv_maxSpeed);
        tvyourSpeed = findViewById(R.id.tv_yourSpeed);
        tvyourScore = findViewById(R.id.tv_score);

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        if (sensorManager != null) {
            accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            gyroscope = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        }

        // Register Listeners
        if (accelerometer != null) {
            sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        }

        if (gyroscope != null) {
            sensorManager.registerListener(this, gyroscope, SensorManager.SENSOR_DELAY_NORMAL);
        }

        decodedLatLng = new ArrayList<>();

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) return;

                // Update user location dynamically
                Location location = locationResult.getLastLocation();
                LatLng currentLatLng = new LatLng(location.getLatitude(), location.getLongitude());
                if (currentLocation != null)
                {
                    calculateSpeed(currentLocation);
                }
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
                LocationRequest.create().setInterval(INTERVAL).setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY),
                locationCallback,
                Looper.getMainLooper()
        );

    }

    private void getLastLocation() {
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
    public void onMapReady(@NonNull GoogleMap googleMap) {
        myMap = googleMap;
        LatLng myLoc = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
        myMap.addMarker(new MarkerOptions().position(myLoc).title(""));
        myMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myLoc, 19));
        //fetchRoute();
    }

    private void calculateSpeed(Location currentLocation) {
        long currentTimestamp = System.currentTimeMillis(); // Current time in milliseconds

        if (previousTimestamp > 0) {
            long timeDiff = currentTimestamp - previousTimestamp; // Time difference in milliseconds
            Log.d("Time", "DeltaTime: " + timeDiff + " ms");

            if (timeDiff > 0) {
                float distance = previousLocation.distanceTo(currentLocation); // Distance in meters

                // Speed in meters per second (m/s)
                float speed = distance / (timeDiff / 1000.0f); // Convert milliseconds to seconds

                // Convert to kilometers per hour (km/h)
                float speedKmh = speed * 3.6f;

                tvyourSpeed.setText(String.valueOf((int)speedKmh));
                formatSpeedTextViews(speedKmh);
            }
        }

        // Update the previous location and timestamp
        previousLocation = currentLocation;
        previousTimestamp = currentTimestamp;
    }


//    @Override
//    protected void onResume() {
//        super.onResume();
//
//        // Check Bluetooth connection and auto-collect driving data
//        if (isBluetoothDeviceConnected()) {
//            if (autoCollectDrivingData()) {
//                startTrip(null); // Start the trip if conditions are met
//            }
//        }
//    }

    private void detectHardStop(float zAxisAcceleration, float[] rotationRates) {

        long currentTimestamp = System.currentTimeMillis();

        // Ignore readings if cooldown has not passed
        if ((currentTimestamp - lastEventTimestamp) < EVENT_COOLDOWN_MS) {
            return;
        }

        boolean isHardStop = zAxisAcceleration < HARD_STOP_ACCEL_THRESHOLD;
        boolean isSignificantRotation = Math.abs(rotationRates[0]) > GYRO_ROTATION_THRESHOLD ||
                Math.abs(rotationRates[1]) > GYRO_ROTATION_THRESHOLD ||
                Math.abs(rotationRates[2]) > GYRO_ROTATION_THRESHOLD;

        if (isHardStop || isSignificantRotation) {
            score -= SPEED_LOW;
            tvyourScore.setText(String.valueOf(score));

//            Log.d("HardStop", "Hard stop detected with acceleration: " + zAxisAcceleration +
//                    " m/s² and rotation rates: X=" + rotationRates[0] +
//                    ", Y=" + rotationRates[1] + ", Z=" + rotationRates[2]);
        }

        // Update last event timestamp
        lastEventTimestamp = currentTimestamp;
    }

    private boolean isBluetoothDeviceConnected() {
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null || !bluetoothAdapter.isEnabled()) {
            return false; // Bluetooth is not supported or enabled
        }

        // Check connected devices
        try {
            // Get connected devices for the Bluetooth A2DP profile (example)
            boolean connectedDevices = bluetoothAdapter.getProfileProxy(
                    this, new BluetoothProfile.ServiceListener() {

                        @Override
                        public void onServiceConnected(int profile, BluetoothProfile proxy) {
                            List<BluetoothDevice> devices = proxy.getConnectedDevices();

                            if(devices.size() > 1 || devices.isEmpty())
                            {
                                Log.e("Error", "More/No than one device connected!");
                            }
                            else {
                                for (BluetoothDevice device : devices) {
                                    currentMac = device.getAddress();
                                }
                            }

                        }

                        @Override
                        public void onServiceDisconnected(int profile) {
                            Log.d("BluetoothCheck", "Profile disconnected.");
                        }
                    }, BluetoothProfile.A2DP); // Use A2DP, GATT, or other profiles

            return !connectedDevices;

        } catch (Exception e) {
            Log.e("BluetoothCheck", "Error checking connected devices", e);
            return false;
        }
    }

    private boolean autoCollectDrivingData() {
        boolean match = false;

        if (getSavedMacAddress().equals(currentMac))
            match = true;

        return match;
    }

    private String getSavedMacAddress() {
        SharedPreferences sharedPreferences = getSharedPreferences("DrivingAppPrefs", MODE_PRIVATE);
        return sharedPreferences.getString("paired_device_mac", null);
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

                        decodedLatLng = decodedPath;

                        //Log.d("Polyline", "Encoded Polyline: " + polyline);

//                        for (LatLng point : decodedPath) {
//                            Log.d("DecodedPoint", "Lat: " + point.latitude + ", Lng: " + point.longitude);
//                        }

                        runOnUiThread(() -> myMap.addPolyline(new PolylineOptions()
                                .addAll(decodedPath)
                                .color(0xFF0000FF)
                                .width(15)));

                        fetchSpeedLimits(decodedLatLng);
                    }
                } catch (Exception e) {
                    Log.e("DirectionsAPI", "Error parsing JSON", e);
                }
            }
        });
    }

    private void fetchSpeedLimits(List<LatLng> routePoints) {
        // Split route points into batches of 100
        List<List<LatLng>> batches = batchRoutePoints(routePoints, BATCH_SIZE);

        for (List<LatLng> batch : batches) {
            StringBuilder pathParam = new StringBuilder();
            for (LatLng point : batch) {
                if (pathParam.length() > 0) pathParam.append("|");
                pathParam.append(point.latitude).append(",").append(point.longitude);
            }

            String url = "https://roads.googleapis.com/v1/speedLimits?path=" + pathParam + "&key=AIzaSyChliqG4c6ZN4VvfpHGRe1n8HPEf-0-hlA"; // Replace with your API key

            OkHttpClient client = new OkHttpClient();

            Request request = new Request.Builder()
                    .url(url)
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    Log.e("RoadsAPI", "Request failed", e);
                    speedLimit = DEFAULT_SPEED;
                    runOnUiThread(() -> tvSpeedLimit.setText(String.valueOf(DEFAULT_SPEED)));
                    roads = false;

                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (!response.isSuccessful()) {
                        Log.e("RoadsAPI", "Request failed: " + response);
                        speedLimit = DEFAULT_SPEED;
                        runOnUiThread(() -> tvSpeedLimit.setText(String.valueOf(DEFAULT_SPEED)));
                        return;
                    }

                    //check if getting road speed was successful
                    if (roads)
                    {
                        String responseData = response.body().string();
                        runOnUiThread(() -> parseSpeedLimit(responseData)); // Parse and display speed limits
                    }
                }
            });
        }
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

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            float zAxisAcceleration = event.values[2];
            detectHardStop(zAxisAcceleration, new float[]{0, 0, 0});
        } else if (event.sensor.getType() == Sensor.TYPE_GYROSCOPE) {
            float[] rotationRates = event.values;
            detectHardStop(0, rotationRates);
        }
    }

    private void handleAccelerometer(float[] values) {
        // Get linear acceleration in the forward/backward direction (Z-axis typically)
        float zAxisAcceleration = values[2]; // Adjust axis depending on device orientation

        // Detect hard stop
        if (zAxisAcceleration < HARD_STOP_ACCEL_THRESHOLD) {
            Log.d("HardStop", "Hard stop detected! Acceleration: " + zAxisAcceleration + " m/s²");
        }
    }
    private void handleGyroscope(float[] values) {
        // Get rotational velocity
        float rotationRateX = values[0];
        float rotationRateY = values[1];
        float rotationRateZ = values[2];

        // Detect significant rotation (hard braking might involve skidding or rotational movement)
        if (Math.abs(rotationRateX) > GYRO_ROTATION_THRESHOLD ||
                Math.abs(rotationRateY) > GYRO_ROTATION_THRESHOLD ||
                Math.abs(rotationRateZ) > GYRO_ROTATION_THRESHOLD) {
            Log.d("Gyroscope", "Significant rotation detected! Rates: X=" + rotationRateX
                    + ", Y=" + rotationRateY + ", Z=" + rotationRateZ);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Not needed for this functionality
    }

    private void parseSpeedLimit(String jsonResponse) {
        try {
            JSONObject jsonObject = new JSONObject(jsonResponse);
            JSONArray speedLimits = jsonObject.getJSONArray("speedLimits");

            for (int i = 0; i < speedLimits.length(); i++) {
                JSONObject speedLimitInfo = speedLimits.getJSONObject(i);
                int speedLimit = speedLimitInfo.getInt("speedLimit");
                String units = speedLimitInfo.getString("units");

                // Log speed limit
                Log.d("SpeedLimitParser", "Speed Limit: " + speedLimit + " " + units);

                // Show speed limit on the UI (optional: use a TextView or map overlay)
                Toast.makeText(this, "Speed Limit: " + speedLimit + " " + units, Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Log.e("SpeedLimitParser", "Error parsing speed limit JSON", e);
        }
    }

    private List<List<LatLng>> batchRoutePoints(List<LatLng> routePoints, int batchSize) {
        List<List<LatLng>> batches = new ArrayList<>();
        for (int i = 0; i < routePoints.size(); i += batchSize) {
            int end = Math.min(routePoints.size(), i + batchSize);
            batches.add(routePoints.subList(i, end));
        }
        return batches;
    }

    private void formatSpeedTextViews(float currentSpeed){
        if (currentSpeed - speedLimit > SPEED_LOW)
        {
            //low penalty, speeding between 5-10 over speed limit
            if (currentSpeed - speedLimit < SPEED_MED)
            {
                tvyourSpeed.setTextColor(getResources().getColor(R.color.alert, null));
                score -= PEN_SMALL;
                tvyourScore.setText(String.valueOf(score));
            }

            if (currentSpeed - speedLimit > SPEED_MED)
            {
                if (currentSpeed - speedLimit < SPEED_HI)
                {
                    tvyourSpeed.setTextColor(getResources().getColor(R.color.alert, null));
                    score -= PEN_MED;
                    tvyourScore.setText(String.valueOf(score));
                }
            }

            if (currentSpeed - speedLimit > SPEED_HI)
            {
                tvyourSpeed.setTextColor(getResources().getColor(R.color.alert, null));
                score -= PEN_HI;
                tvyourScore.setText(String.valueOf(score));
            }
        }
        else {
            tvyourSpeed.setTextColor(getResources().getColor(R.color.black, null));
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Unregister listeners to avoid memory leaks
        sensorManager.unregisterListener(this);
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

    public void openCarInfo(View view){
        Intent intent = new Intent(this, Car_Activity.class);
        startActivity(intent);
    }

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