package com.example.asfirstapp; // מגדיר את החבילה אליה שייכת המחלקה הזו

import com.google.android.gms.maps.model.LatLng; // מייבא את המחלקה LatLng כדי לאחסן ערכי קווי רוחב וגובה

// מחלקת מודל נתונים פשוטה המייצגת עיר במשחק "Unlock City"
public class City {

    public String name;       // שם העיר (למשל, "פריז")
    public LatLng location;   // קואורדינטות גיאוגרפיות (קו רוחב + קו אורך)
    public String clue;       // רמז המשמש לזיהוי העיר

    // בנאי: משמש ליצירת אובייקט City עם כל המידע הנדרש
    public City(String name, LatLng location, String clue) {

        this.name = name;         // שמירת שם העיר שסופק לתוך האובייקט הזה
        this.location = location; // שמירת הקואורדינטות שסופקו לתוך האובייקט הזה
        this.clue = clue;         // שמירת הרמז שסופק לתוך האובייקט הזה
    }
}