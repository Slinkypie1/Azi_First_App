package com.example.asfirstapp;

// Class representing a single region (country) in the "Find the Country" puzzle
public class Region {
    private String countryName;       // The name of the country
    private String outlineDrawableId; // The drawable resource name for the country's outline
    private boolean isCorrect;        // Flag indicating if this is the correct answer

    // Constructor to create a Region object
    public Region(String countryName, String outlineDrawableId, boolean isCorrect) {
        this.countryName = countryName;             // Store country name
        this.outlineDrawableId = outlineDrawableId; // Store drawable resource name
        this.isCorrect = isCorrect;                 // Store whether this is the correct choice
    }

    // Getter for country name
    public String getCountryName() {
        return countryName;
    }

    // Getter for outline drawable ID
    public String getOutlineDrawableId() {
        return outlineDrawableId;
    }

    // Getter for isCorrect flag
    public boolean isCorrect() {
        return isCorrect;
    }
}
