package com.example.asfirstapp;

import android.content.Context; // Needed to access resources like drawable images
import android.view.LayoutInflater; // Used to inflate XML layouts into View objects
import android.view.View; // Base class for all UI components
import android.view.ViewGroup; // Represents a container for other views (used by RecyclerView)
import android.widget.ImageView; // UI element to display country outlines

import androidx.annotation.NonNull; // Ensures method parameters are not null
import androidx.recyclerview.widget.RecyclerView; // RecyclerView base class

import java.util.List; // To store the list of Region objects

// RecyclerView Adapter class to populate a grid of country options
public class MapAdapter extends RecyclerView.Adapter<MapAdapter.ViewHolder> {

    // Interface to handle clicks on country outlines
    public interface OnRegionClickListener {
        void onRegionClick(boolean isCorrect); // Sends true if the clicked country is correct
    }

    private final List<Region> items; // The list of Region objects to display in RecyclerView
    private final OnRegionClickListener listener; // Listener for click events

    // Adapter constructor
    public MapAdapter(Context ctx, List<Region> items, OnRegionClickListener listener) {
        this.items = items; // Store the list of countries
        this.listener = listener; // Store the click listener
    }

    // ViewHolder class holds references to views for each RecyclerView item
    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView; // ImageView for displaying the country outline

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            // Connect ImageView reference to XML layout element
            imageView = itemView.findViewById(R.id.regionOutline);
        }
    }

    // Inflate the layout for each item in RecyclerView
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.map_item, parent, false); // Inflate map_item.xml
        return new ViewHolder(v); // Return a new ViewHolder for the item
    }

    // Bind data to each ViewHolder
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Region region = items.get(position); // Get the Region object at this position
        Context context = holder.itemView.getContext(); // Get context to access resources

        // Dynamically get the drawable resource ID using the name stored in Region
        int drawableId = context.getResources()
                .getIdentifier(region.getOutlineDrawableId(), "drawable", context.getPackageName());

        // Set the ImageView resource to display the country's outline
        holder.imageView.setImageResource(drawableId);

        // Set a click listener for the ImageView
        holder.imageView.setOnClickListener(v -> {
            // Call the listener callback with true/false based on whether the country is correct
            listener.onRegionClick(region.isCorrect());
        });
    }

    @Override
    public int getItemCount() {
        return items.size(); // Return total number of countries in the list
    }
}
