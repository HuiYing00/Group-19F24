package com.example.myapplication;

import android.Manifest;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;

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
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.os.CountDownTimer;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.content.Intent;
import android.widget.Button;
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
import com.google.android.gms.maps.model.Marker;
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
    private final int DEFAULT_SPEED = 30;

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

    private final int ICON_SIZE = 150;

    private final int START_SCORE = 1000;
    private final int PERSON_SPEED = 10;
    private final int INTERVAL = 1000;
    private int score = START_SCORE;
    private static final long COUNTDOWN_TIMER = 10000; // 10 seconds
    private final String mac = "20:19:10:86:3D:F1";
    private CountDownTimer countDownTimer;

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
    private boolean optOut = false;
    private boolean collectData = false;
    private Marker marker;
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

        speedLimit = DEFAULT_SPEED;

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
                if (location != null)
                {
                    calculateSpeed(location);
                    updateLocationMarker(currentLatLng);
                }
            }
        };
        getLastLocation();
        startLocationUpdates();

        checkBluetoothDevices();
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
        createLocationMarker();
        if (collectData)
        {
            startTrip();
        }
    }

    private void calculateSpeed(Location currentLocation) {
        long currentTimestamp = System.currentTimeMillis(); // Current time in milliseconds

        if (previousTimestamp > 0) {
            long timeDiff = currentTimestamp - previousTimestamp; // Time difference in milliseconds
            //Log.d("Time", "DeltaTime: " + timeDiff + " ms");

            if (timeDiff > 0) {
                float distance = previousLocation.distanceTo(currentLocation); // Distance in meters
                //Log.d("Speed", "Km/hr " + distance + " km/h");
                // Speed in meters per second (m/s)
                float speed = distance / (timeDiff / 1000.0f); // Convert milliseconds to seconds

                // Convert to kilometers per hour (km/h)
                float speedKmh = speed * 3.6f;
//                Log.d("Speed", "Km/hr " + speedKmh + " km/h");
                tvyourSpeed.setText(String.valueOf((int)speedKmh));
                formatSpeedTextViews(speedKmh);

                if (speedKmh > PERSON_SPEED && !collectData)
                {
                    showDrivingPopup(this);
                }
            }
        }

        // Update the previous location and timestamp
        previousLocation = currentLocation;
        previousTimestamp = currentTimestamp;

    }

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

    private void checkBluetoothDevices() {
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null || !bluetoothAdapter.isEnabled()) {
            Log.d("BluetoothCheck", "Bluetooth is not supported or not enabled.");
            return;
        }

        // Connect to A2DP profile to get connected devices
        bluetoothAdapter.getProfileProxy(this, new BluetoothProfile.ServiceListener() {
            @Override
            public void onServiceConnected(int profile, BluetoothProfile proxy) {
                if (profile == BluetoothProfile.A2DP) {
                    // Get the list of connected devices
                    List<BluetoothDevice> connectedDevices = proxy.getConnectedDevices();

                    if (connectedDevices.isEmpty()) {
                        Log.d("BluetoothCheck", "No devices are connected.");
                    } else {
                        for (BluetoothDevice device : connectedDevices) {
                            currentMac = device.getAddress();
                            Log.d("BluetoothCheck", "Connected device: " + currentMac);
                        }
                        if (mac.equals(currentMac))
                            collectData = true;
                    }

                    // Release the profile proxy when done
                    BluetoothAdapter.getDefaultAdapter().closeProfileProxy(BluetoothProfile.A2DP, proxy);
                }
            }

            @Override
            public void onServiceDisconnected(int profile) {
                Log.d("BluetoothCheck", "A2DP profile disconnected.");
            }
        }, BluetoothProfile.A2DP);
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

    private void formatSpeedTextViews(float currentSpeed) {
        int speedDiff = (int)(currentSpeed - speedLimit);
        //Log.d("Speeding detection: ", "Current Speed: " + currentSpeed + ", Speed Limit: " + speedLimit);

        if (collectData)
        {
            if (speedDiff > SPEED_LOW)
            {

                //low penalty, speeding between 5-10 over speed limit
                if (speedDiff < SPEED_MED)
                {
                    tvyourSpeed.setTextColor(getResources().getColor(R.color.alert, null));
                    score -= PEN_SMALL;
                    tvyourScore.setText(String.valueOf(score));
                }

                if (speedDiff > SPEED_MED)
                {
                    if (speedDiff < SPEED_HI)
                    {
                        tvyourSpeed.setTextColor(getResources().getColor(R.color.alert, null));
                        score -= PEN_MED;
                        tvyourScore.setText(String.valueOf(score));
                    }
                }

                if (speedDiff > SPEED_HI)
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

    }

    private  void createLocationMarker() {
        LatLng myLoc = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());

        // Step 1: Get Bitmap from Vector
        Bitmap originalBitmap = getBitmapFromVector(getApplicationContext(), R.drawable.img_location);

        // Step 2: Resize the Bitmap
        Bitmap resizedBitmap = resizeBitmap(originalBitmap, ICON_SIZE, ICON_SIZE); // Adjust width & height as needed

        // Step 3: Create a BitmapDescriptor
        BitmapDescriptor customIcon = BitmapDescriptorFactory.fromBitmap(resizedBitmap);
        MarkerOptions markerOptions = new MarkerOptions().position(myLoc).title("").icon(customIcon);

        marker = myMap.addMarker(markerOptions);
    }

    private void updateLocationMarker(LatLng myLoc) {

        if (marker == null) {

            Bitmap originalBitmap = getBitmapFromVector(getApplicationContext(), R.drawable.img_location);

            // Step 2: Resize the Bitmap
            Bitmap resizedBitmap = resizeBitmap(originalBitmap, ICON_SIZE, ICON_SIZE); // Adjust width & height as needed

            // Step 3: Create a BitmapDescriptor
            BitmapDescriptor customIcon = BitmapDescriptorFactory.fromBitmap(resizedBitmap);
            MarkerOptions markerOptions = new MarkerOptions().position(myLoc).title("").icon(customIcon);

            marker = myMap.addMarker(markerOptions);

        } else {
            // Update the position of the existing marker
            marker.setPosition(myLoc);
        }

        //animateStretchAndSquash(getApplicationContext(), marker, R.drawable.img_location, true);
        myMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myLoc, 18));
    }

    public static Bitmap getBitmapFromVector(Context context, int drawableId) {
        Drawable vectorDrawable = ContextCompat.getDrawable(context, drawableId);
        if (vectorDrawable == null) {
            throw new IllegalArgumentException("Drawable not found");
        }
        vectorDrawable.setBounds(0, 0, vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight());
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.draw(canvas);
        return bitmap;
    }

    public Bitmap resizeBitmap(Bitmap bitmap, int width, int height) {
        return Bitmap.createScaledBitmap(bitmap, width, height, false);
    }

    private void showDrivingPopup(Context context) {

        if (!optOut)
        {
            // Inflate the popup layout
            LayoutInflater inflater = LayoutInflater.from(context);
            View popupView = inflater.inflate(R.layout.opt_out, null);

            // Initialize the AlertDialog
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setView(popupView);
            builder.setCancelable(false);

            // Get references to UI elements
            TextView timerText = popupView.findViewById(R.id.timer_text);
            Button yesButton = popupView.findViewById(R.id.yes_button);
            Button noButton = popupView.findViewById(R.id.no_button);

            // Create the AlertDialog
            AlertDialog alertDialog = builder.create();
            alertDialog.show();

            // Start the timer
            countDownTimer = new CountDownTimer(COUNTDOWN_TIMER, 1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                    timerText.setText(millisUntilFinished / 1000 + " seconds left");
                }

                @Override
                public void onFinish() {
                    // Automatically trigger "Drive Mode" if the user doesn't press No
                    startTrip();
                    alertDialog.dismiss();
                }
            }.start();

            // Yes button: Immediately trigger "Drive Mode"
            yesButton.setOnClickListener(v -> {
                startTrip(v);
                countDownTimer.cancel();
                alertDialog.dismiss();
            });

            // No button: Cancel the action
            noButton.setOnClickListener(v -> {
                optOut = true;
                endTrip(v);
                countDownTimer.cancel();
                alertDialog.dismiss();
            });
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

    public void openCarInfo(View view){
        Intent intent = new Intent(this, Car_Activity.class);
        startActivity(intent);
    }

    public void startTrip(View view){
        collectData = true;
        fetchRoute();
    }

    public void startTrip(){
        collectData = true;
        fetchRoute();
    }
    public void endTrip(View view){
        collectData = false;
        myMap.clear();
        createLocationMarker();
    }
}