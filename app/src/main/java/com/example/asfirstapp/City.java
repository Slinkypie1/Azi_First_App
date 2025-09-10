package com.example.asfirstapp; // Package declaration

import com.google.android.gms.maps.model.LatLng; // Import LatLng to store geographic coordinates

// Class representing a city in the UnlockCityActivity
public class City {
    public String name;       // Name of the city
    public LatLng location;   // Geographic coordinates (latitude & longitude)
    public String clue;       // Clue text for the player to guess the city

    // Constructor to initialize all fields
    public City(String name, LatLng location, String clue) {
        this.name = name;       // Set city name
        this.location = location; // Set coordinates
        this.clue = clue;       // Set clue for this city
    }
}
