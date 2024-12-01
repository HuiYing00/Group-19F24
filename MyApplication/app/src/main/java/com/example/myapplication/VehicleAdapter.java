package com.example.myapplication;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class VehicleAdapter extends RecyclerView.Adapter<VehicleAdapter.VehicleViewHolder> {

    private final List<Vehicle> vehicleList;
    private final OnConnectClickListener connectClickListener;

    // Interface for handling button clicks
    public interface OnConnectClickListener {
        void onConnectClick(Vehicle vehicle);
    }

    // Constructor
    public VehicleAdapter(List<Vehicle> vehicleList, OnConnectClickListener listener) {
        this.vehicleList = vehicleList;
        this.connectClickListener = listener;
    }

    @NonNull
    @Override
    public VehicleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_vehicle, parent, false);
        return new VehicleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VehicleViewHolder holder, int position) {
        Vehicle vehicle = vehicleList.get(position);

        // Populate the fields with vehicle data
        holder.makeTextView.setText("Make: " + vehicle.getMake());
        holder.modelTextView.setText("Model: " + vehicle.getModel());
        holder.yearTextView.setText("Year: " + vehicle.getYear());
        holder.vinTextView.setText("VIN: " + vehicle.getVin());
        holder.policyTextView.setText("Policy: " + vehicle.getPolicyNumber());

        // Handle button click for connecting to the vehicle
        holder.connectButton.setOnClickListener(v -> connectClickListener.onConnectClick(vehicle));
    }

    @Override
    public int getItemCount() {
        return vehicleList.size();
    }

    // ViewHolder for the layout
    static class VehicleViewHolder extends RecyclerView.ViewHolder {
        TextView makeTextView, modelTextView, yearTextView, vinTextView, policyTextView;
        ImageButton connectButton;

        public VehicleViewHolder(@NonNull View itemView) {
            super(itemView);
            makeTextView = itemView.findViewById(R.id.tv_make);
            modelTextView = itemView.findViewById(R.id.tv_model);
            yearTextView = itemView.findViewById(R.id.tv_year);
            vinTextView = itemView.findViewById(R.id.tv_vin);
            policyTextView = itemView.findViewById(R.id.tv_policy_number);
            connectButton = itemView.findViewById(R.id.btn_bluetooth);
        }
    }
}
