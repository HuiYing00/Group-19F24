package com.example.myapplication;

public class Vehicle {
    private String make;
    private String model;
    private int year;
    private String vin;
    private String policyNumber;

    // Constructor
    public Vehicle(String make, String model, int year, String vin, String policyNumber) {
        this.make = make;
        this.model = model;
        this.year = year;
        this.vin = vin;
        this.policyNumber = policyNumber;
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

    public String getPolicyNumber() {
        return policyNumber;
    }
}