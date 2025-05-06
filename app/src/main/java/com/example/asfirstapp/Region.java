package com.example.asfirstapp;

public class Region {
    private String countryName;  // Country name
    private String outlineDrawableId; // Drawable reference for the outline
    private boolean isCorrect;

    public Region(String countryName, String outlineDrawableId, boolean isCorrect) {
        this.countryName = countryName;
        this.outlineDrawableId = outlineDrawableId;
        this.isCorrect = isCorrect;
    }

    public String getCountryName() {
        return countryName;
    }

    public String getOutlineDrawableId() {
        return outlineDrawableId;
    }

    public boolean isCorrect() {
        return isCorrect;
    }
}
