package com.example.asfirstapp;

/**
 * Region class
 * -------------
 * Represents a single selectable country/region in the "Find the Country" puzzle.
 * Each region holds display data (name + image) and whether it is the correct answer.
 */
public class Region {

    // Name of the country/region shown to the user (if needed for UI or logic)
    private String countryName;

    // Name of the drawable resource that represents the country outline image
    private String outlineDrawableId;

    // Indicates whether this region is the correct answer in the puzzle
    private boolean isCorrect;

    /**
     * Constructor for creating a Region object.
     *
     * @param countryName       Name of the country
     * @param outlineDrawableId Drawable resource name (e.g., "france_outline")
     * @param isCorrect         Whether this region is the correct answer
     */
    public Region(String countryName, String outlineDrawableId, boolean isCorrect) {
        this.countryName = countryName;
        this.outlineDrawableId = outlineDrawableId;
        this.isCorrect = isCorrect;
    }

    /**
     * Returns the country name.
     */
    public String getCountryName() {
        return countryName;
    }

    /**
     * Returns the drawable resource name for this region.
     * This is used to dynamically load the correct image from resources.
     */
    public String getOutlineDrawableId() {
        return outlineDrawableId;
    }

    /**
     * Returns whether this region is the correct answer.
     */
    public boolean isCorrect() {
        return isCorrect;
    }
}