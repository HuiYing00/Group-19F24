package com.example.connect;

public class Vehicle {
    private String make;
    private String model;
    private int year;
    private String vin;
    private String policyNumber;
    private String btaddress;

    // Constructor
    public Vehicle(String make, String model, int year, String vin, String policyNumber) {
        this.make = make;
        this.model = model;
        this.year = year;
        this.vin = vin;
        this.policyNumber = policyNumber;
        this.btaddress = "";
    }

    // Getters
    public String getMake() {
        return make;
    }

    public String getModel() {
        return model;
    }

    public int getYear() {
        return year;
    }

    public String getVin() {
        return vin;
    }

    public String getPolicyNumber() { return policyNumber; }

    public String getBluetoothAddress() { return btaddress; }
    public void setBluetoothAddress(String adr) { this.btaddress = adr; }
}