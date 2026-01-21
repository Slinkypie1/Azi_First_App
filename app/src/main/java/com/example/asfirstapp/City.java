package com.example.asfirstapp; // Defines the package this class belongs to

import com.google.android.gms.maps.model.LatLng; // Imports LatLng to store latitude and longitude values

// Class that represents a single city in the Unlock City game
public class City {

    public String name;       // Stores the name of the city
    public LatLng location;   // Stores the city's geographic coordinates
    public String clue;       // Stores the clue used to guess the city

    // Constructor used to create a City object with all required data
    public City(String name, LatLng location, String clue) {
        this.name = name;         // Assigns the provided name to this city
        this.location = location; // Assigns the provided coordinates to this city
        this.clue = clue;         // Assigns the provided clue text
    }
}

