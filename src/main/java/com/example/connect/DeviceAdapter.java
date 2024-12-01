package com.example.connect;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class DeviceAdapter extends RecyclerView.Adapter<DeviceAdapter.DeviceViewHolder> {

    private final List<BluetoothDevice> devices;
    private final OnDeviceClickListener listener;
    private BluetoothAdapter bluetoothAdapter;

    private final Context context;
    public interface OnDeviceClickListener {
        void onDeviceClick(BluetoothDevice device);
    }

    public DeviceAdapter(Context context, List<BluetoothDevice> devices, OnDeviceClickListener listener) {
        this.devices = devices;
        this.listener = listener;
        this.context = context;
    }

    @NonNull
    @Override
    public DeviceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_device, parent, false);
        return new DeviceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DeviceViewHolder holder, int position) {


        BluetoothDevice device = devices.get(position);

        // Get the device name (check permissions if required)
        String deviceName = "Unknown Device";
        String deviceAddress = "Unknown Address";

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S || // No need for permission below Android 12
                ContextCompat.checkSelfPermission(context, android.Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED) {

            deviceName = (device.getName() != null) ? device.getName() : "Unknown Device";
            deviceAddress = (device.getAddress() != null) ? device.getAddress() : "Unknown Address";

        } else {
            Toast.makeText(context, "Bluetooth permission required to get device name", Toast.LENGTH_SHORT).show();
        }

        holder.deviceName.setText(deviceName);
        holder.deviceAddress.setText(deviceAddress);

        holder.itemView.setOnClickListener(v -> listener.onDeviceClick(device));
    }

    @Override
    public int getItemCount() {
        return devices.size();
    }

    static class DeviceViewHolder extends RecyclerView.ViewHolder {
        TextView deviceName, deviceAddress;

        public DeviceViewHolder(@NonNull View itemView) {
            super(itemView);
            deviceName = itemView.findViewById(R.id.tv_dname);
            deviceAddress = itemView.findViewById(R.id.tv_daddress);
        }
    }

}
