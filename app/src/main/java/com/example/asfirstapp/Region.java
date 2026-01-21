package com.example.asfirstapp;

/**
 * Region class
 * -------------
 * Represents a single selectable country/region in the "Find the Country" puzzle.
 * Each region has a name, a drawable representing its outline, and a flag
 * indicating if it is the correct answer.
 */
public class Region {

    private String countryName;       // Name of the country/region
    private String outlineDrawableId; // Drawable resource name for the country outline
    private boolean isCorrect;        // True if this region is the correct answer

    /**
     * Constructor to create a Region object.
     *
     * @param countryName       Name of the country
     * @param outlineDrawableId Name of drawable resource (e.g., "france_outline")
     * @param isCorrect         True if this is the correct answer
     */
    public Region(String countryName, String outlineDrawableId, boolean isCorrect) {
        this.countryName = countryName;               // Store the country name
        this.outlineDrawableId = outlineDrawableId;   // Store the drawable ID
        this.isCorrect = isCorrect;                   // Store whether it is correct
    }

    /**
     * Get the country name.
     *
     * @return Name of the country
     */
    public String getCountryName() {
        return countryName;
    }

    /**
     * Get the drawable resource name for the country outline.
     *
     * @return Drawable resource ID as a string
     */
    public String getOutlineDrawableId() {
        return outlineDrawableId;
    }

    /**
     * Check if this region is the correct answer.
     *
     * @return True if correct, false otherwise
     */
    public boolean isCorrect() {
        return isCorrect;
    }
}
