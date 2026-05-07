package com.example.asfirstapp; // החבילה אליה שייכת המחלקה הזו

// דרוש כדי לגשת למשאבי אנדרואיד כגון תמונות (drawables)
import android.content.Context;

// משמש להפיכת קבצי פריסה של XML לאובייקטי View בפועל
import android.view.LayoutInflater;

// מחלקת בסיס לכל רכיבי ממשק המשתמש באנדרואיד
import android.view.View;

// מייצג מיכל הורה עבור מספר תצוגות (בשימוש ב-RecyclerView)
import android.view.ViewGroup;

// רכיב ממשק משתמש המשמש להצגת תמונות של קווי מתאר של מדינות
import android.widget.ImageView;

import androidx.annotation.NonNull;
// מבטיח שפרמטרי שיטה וערכי החזרה אינם null

import androidx.recyclerview.widget.RecyclerView;
// מחלקת בסיס ליצירת רשימות/רשתות באמצעות RecyclerView

import java.util.List;
// משמש לאחסון רשימה של אובייקטי Region

// מתאם (Adapter) ל-RecyclerView המציג קווי מתאר של מדינות לבחירה ברשת
public class MapAdapter extends RecyclerView.Adapter<MapAdapter.ViewHolder> {

    // ממשק המשמש להעברת אירועי לחיצה חזרה לאקטיביטי
    public interface OnRegionClickListener {
        void onRegionClick(boolean isCorrect);
        // שולח האם המדינה שנבחרה היא התשובה הנכונה
    }

    private final List<Region> items;
    // רשימה של כל המדינות (אובייקטי Region) המוצגות ברשת

    private final OnRegionClickListener listener;
    // מאזין המטפל במקרים שבהם משתמש לוחץ על מדינה

    // בנאי: מקבל נתונים ומאזין ללחיצות
    public MapAdapter(Context ctx, List<Region> items, OnRegionClickListener listener) {
        this.items = items;
        // שמירת רשימת האזורים

        this.listener = listener;
        // שמירת המטפל בלחיצות
    }

    // ViewHolder מחזיק הפניות לרכיבי ממשק המשתמש עבור כל פריט ברשת
    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        // תמונה המציגה את קווי המתאר של המדינה

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.regionOutline);
            // קישור ה-ImageView מקובץ הפריסה של ה-XML
        }
    }

    // יוצר ViewHolder חדש בעת הצורך
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.map_item, parent, false);
        // "ניפוח" פריסת ה-XML עבור כל פריט ברשת

        return new ViewHolder(v);
        // החזרת מופע חדש של ViewHolder
    }

    // מחבר נתונים (תמונות מדינה + התנהגות לחיצה) לכל פריט
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        Region region = items.get(position);
        // קבלת אובייקט ה-Region עבור המיקום הזה

        Context context = holder.itemView.getContext();
        // קבלת הקשר (Context) לצורך גישה למשאבים

        int drawableId = context.getResources()
                .getIdentifier(region.getOutlineDrawableId(), "drawable", context.getPackageName());
        // המרת שם המחרוזת למזהה משאב תמונה (drawable)

        holder.imageView.setImageResource(drawableId);
        // הגדרת תמונת קווי המתאר של המדינה

        holder.imageView.setOnClickListener(v -> {
            listener.onRegionClick(region.isCorrect());
            // הודעה לאקטיביטי האם המדינה שנלחצה נכונה
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
        // החזרת המספר הכולל של מדינות ברשת
    }
}