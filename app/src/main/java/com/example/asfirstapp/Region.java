package com.example.asfirstapp; // החבילה אליה שייכת המחלקה הזו

/**
 * מחלקת Region
 * -------------
 * מייצגת מדינה/אזור בודד הניתן לבחירה בפאזל "מצא את המדינה".
 * כל אזור מחזיק נתוני תצוגה (שם + תמונה) והאם הוא התשובה הנכונה.
 */
public class Region {

    // שם המדינה/האזור המוצג למשתמש (אם נדרש עבור ממשק המשתמש או הלוגיקה)
    private String countryName;

    // שם משאב התמונה (drawable) המייצג את קווי המתאר של המדינה
    private String outlineDrawableId;

    // מציין האם אזור זה הוא התשובה הנכונה בפאזל
    private boolean isCorrect;

    /**
     * בנאי ליצירת אובייקט Region.
     *
     * @param countryName       שם המדינה
     * @param outlineDrawableId שם משאב התמונה (למשל, "france_outline")
     * @param isCorrect         האם אזור זה הוא התשובה הנכונה
     */
    public Region(String countryName, String outlineDrawableId, boolean isCorrect) {
        this.countryName = countryName;
        this.outlineDrawableId = outlineDrawableId;
        this.isCorrect = isCorrect;
    }

    /**
     * מחזיר את שם המדינה.
     */
    public String getCountryName() {
        return countryName;
    }

    /**
     * מחזיר את שם משאב התמונה עבור אזור זה.
     * משמש לטעינה דינמית של התמונה הנכונה מהמשאבים.
     */
    public String getOutlineDrawableId() {
        return outlineDrawableId;
    }

    /**
     * מחזיר האם אזור זה הוא התשובה הנכונה.
     */
    public boolean isCorrect() {
        return isCorrect;
    }
}