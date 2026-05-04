package com.example.asfirstapp;

// Needed to access Android resources such as drawable images
import android.content.Context;

// Used to convert XML layout files into actual View objects
import android.view.LayoutInflater;

// Base class for all UI components in Android
import android.view.View;

// Represents a parent container for multiple views (used in RecyclerView)
import android.view.ViewGroup;

// UI element used to display country outline images
import android.widget.ImageView;

import androidx.annotation.NonNull;
// Ensures method parameters and return values are not null

import androidx.recyclerview.widget.RecyclerView;
// Base class for creating lists/grids using RecyclerView

import java.util.List;
// Used to store a list of Region objects

// RecyclerView Adapter that displays selectable country outlines in a grid
public class MapAdapter extends RecyclerView.Adapter<MapAdapter.ViewHolder> {

    // Interface used to communicate click events back to the activity
    public interface OnRegionClickListener {
        void onRegionClick(boolean isCorrect);
        // Sends whether the selected country is the correct answer
    }

    private final List<Region> items;
    // List of all countries (Region objects) shown in the grid

    private final OnRegionClickListener listener;
    // Listener that handles when a user clicks on a country

    // Constructor: receives data and click listener
    public MapAdapter(Context ctx, List<Region> items, OnRegionClickListener listener) {
        this.items = items;
        // Store list of regions

        this.listener = listener;
        // Store click handler
    }

    // ViewHolder holds references to the UI elements for each grid item
    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        // Image that shows the country outline

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.regionOutline);
            // Connect ImageView from XML layout
        }
    }

    // Creates a new ViewHolder when needed
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.map_item, parent, false);
        // Inflate the XML layout for each grid item

        return new ViewHolder(v);
        // Return new ViewHolder instance
    }

    // Binds data (country images + click behavior) to each item
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        Region region = items.get(position);
        // Get the Region object for this position

        Context context = holder.itemView.getContext();
        // Get context for accessing resources

        int drawableId = context.getResources()
                .getIdentifier(region.getOutlineDrawableId(), "drawable", context.getPackageName());
        // Convert string name into drawable resource ID

        holder.imageView.setImageResource(drawableId);
        // Set the country outline image

        holder.imageView.setOnClickListener(v -> {
            listener.onRegionClick(region.isCorrect());
            // Notify activity whether the clicked country is correct
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
        // Return total number of countries in the grid
    }
}