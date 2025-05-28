package com.example.asfirstapp;

import com.google.android.gms.maps.model.LatLng;

public class City {
    public String name;
    public LatLng location;
    public String clue;

    public City(String name, LatLng location, String clue) {
        this.name = name;
        this.location = location;
        this.clue = clue;
    }
}
