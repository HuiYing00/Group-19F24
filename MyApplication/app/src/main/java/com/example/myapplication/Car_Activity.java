package com.example.myapplication;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;


import java.util.ArrayList;
import java.util.List;
import android.view.View;

public class Car_Activity extends AppCompatActivity {

    private BluetoothAdapter bluetoothAdapter;
    private final int PERM_BLUETOOTH = 1;
    private final int PRQ_BLUETOOTH= 2;

    private RecyclerView recyclerView;
    private VehicleAdapter vehicleAdapter;
    private List<Vehicle> vehicleList;
    private HandlerThread handlerThread;
    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_car);

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        if (bluetoothAdapter == null) {
            Toast.makeText(this, "Bluetooth not supported", Toast.LENGTH_SHORT).show();
            return;
        }

        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        vehicleList = loadVehicleData(); // Load vehicle data
        vehicleAdapter = new VehicleAdapter(vehicleList, this::connectToVehicle);
        recyclerView.setAdapter(vehicleAdapter);

        // Create a background thread
        handlerThread = new HandlerThread("BluetoothHandlerThread");
        handlerThread.start();
        handler = new Handler(handlerThread.getLooper());
    }

    private List<Vehicle> loadVehicleData() {
        // Replace this with real data, e.g., from a database or API
        List<Vehicle> vehicles = new ArrayList<>();
        vehicles.add(new Vehicle("Toyota", "Camry", 2020, "1HGCM82633A123456", "12345-67890"));
        vehicles.add(new Vehicle("Honda", "Civic", 2021, "2HGEJ6679WH512345", "98765-43210"));
        return vehicles;
    }

    private void connectToVehicle(Vehicle vehicle) {
        if (!bluetoothAdapter.isEnabled()) {
            Toast.makeText(this, "Enable Bluetooth to connect to the vehicle", Toast.LENGTH_SHORT).show();
            return;
        }

        else {
            enableBluetooth();
        }
        // Handle the Bluetooth connection logic here
        Log.d("BluetoothConnection", "Connecting to vehicle: " + vehicle.getMake() + " " + vehicle.getModel());
        Toast.makeText(this, "Connecting to " + vehicle.getMake() + " " + vehicle.getModel(), Toast.LENGTH_SHORT).show();
    }

    private void enableBluetooth() {
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        if (bluetoothAdapter == null) {
            Toast.makeText(this, "Bluetooth is not supported on this device", Toast.LENGTH_SHORT).show();
            return;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            // Check Bluetooth Connect permission for Android 12+
            if (checkSelfPermission(Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.BLUETOOTH_CONNECT}, PRQ_BLUETOOTH);
                return;
            }
        }

        // Request to enable Bluetooth
        if (!bluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, PERM_BLUETOOTH);
        } else {
            startDeviceDiscovery();
            //Toast.makeText(this, "Bluetooth is already enabled", Toast.LENGTH_SHORT).show();
        }
    }

    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S &&
                        checkSelfPermission(Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                    return; // Skip if permission is not granted
                }

                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if (device != null) {
                    // Offload processing to the background thread
                    handler.post(() -> {
                        String deviceName = (device.getName() != null) ? device.getName() : "Unknown Device";
                        String deviceInfo = "Found device: " + deviceName + " [" + device.getAddress() + "]";

                        // Log device info
                        Log.d("BluetoothDevice", deviceInfo);

                        // Optionally update UI on the main thread
                        runOnUiThread(() -> {
                            // Update UI components here
                        });
                    });

                    //Log.d("BluetoothDevice", "Found device: " + device.getName() + " [" + device.getAddress() + "]");
                }
            }
        }
    };

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PRQ_BLUETOOTH) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                enableBluetooth();
            } else {
                Toast.makeText(this, "Bluetooth permission is required to enable Bluetooth", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PERM_BLUETOOTH) {
            if (resultCode == RESULT_OK) {
                Toast.makeText(this, "Bluetooth enabled successfully", Toast.LENGTH_SHORT).show();
                startDeviceDiscovery();
            } else {
                Toast.makeText(this, "Bluetooth enabling failed", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void startDeviceDiscovery() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S &&
                checkSelfPermission(Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
            checkBluetoothPermissions(); // Request permissions if not already granted
            return;
        }

        if (bluetoothAdapter.isDiscovering()) {
            bluetoothAdapter.cancelDiscovery();
        }

        // Start discovery and register the receiver
        bluetoothAdapter.startDiscovery();

        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        filter.addAction(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        registerReceiver(receiver, filter);
    }

    private void checkBluetoothPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            // Android 12+ requires BLUETOOTH_SCAN and BLUETOOTH_CONNECT
            if (checkSelfPermission(Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED ||
                    checkSelfPermission(Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(
                        new String[]{
                                Manifest.permission.BLUETOOTH_SCAN,
                                Manifest.permission.BLUETOOTH_CONNECT
                        },
                        PRQ_BLUETOOTH
                );
            } else {
                startDeviceDiscovery(); // Permissions are already granted
            }
        } else if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) {
            // Pre-Android 12 requires location permission for Bluetooth scanning
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        PRQ_BLUETOOTH
                );
            } else {
                startDeviceDiscovery(); // Permissions are already granted
            }
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (receiver != null) {
            unregisterReceiver(receiver);
        }
        if (handlerThread != null) {
            handlerThread.quit();
        }
    }

    public void openProfile(View view){
        Intent intent = new Intent(this, Profile.class);
        startActivity(intent);
    }

    public void openMap(View view){
        Intent intent = new Intent(this, Map.class);
        startActivity(intent);
    }

    public void toHome(View view){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}
