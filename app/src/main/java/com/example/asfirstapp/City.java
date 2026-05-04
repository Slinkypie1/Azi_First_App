package com.example.asfirstapp; // Defines the package this class belongs to

import com.google.android.gms.maps.model.LatLng; // Imports LatLng to store latitude and longitude values

// Simple data model class representing a city in the Unlock City game
public class City {

    public String name;       // Name of the city (e.g., "Paris")
    public LatLng location;   // Geographic coordinates (latitude + longitude)
    public String clue;       // Hint used for guessing the city

    // Constructor: used to create a City object with all required information
    public City(String name, LatLng location, String clue) {

        this.name = name;         // Save the provided city name into this object
        this.location = location; // Save the provided coordinates into this object
        this.clue = clue;         // Save the provided clue into this object
    }
}