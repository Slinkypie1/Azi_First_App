package com.example.asfirstapp;

import android.content.Context; // Needed for accessing resources
import android.view.LayoutInflater; // To inflate layout XML files
import android.view.View; // Base class for UI components
import android.view.ViewGroup; // For RecyclerView container
import android.widget.ImageView; // To display country outlines

import androidx.annotation.NonNull; // Ensures parameters are non-null
import androidx.recyclerview.widget.RecyclerView; // RecyclerView base class

import java.util.List; // List of Region objects

// Adapter class to populate RecyclerView with country options
public class MapAdapter extends RecyclerView.Adapter<MapAdapter.ViewHolder> {

    // Interface to handle click events on regions
    public interface OnRegionClickListener {
        void onRegionClick(boolean isCorrect); // Sends whether the clicked region is correct
    }

    private final List<Region> items; // List of Region objects to display
    private final OnRegionClickListener listener; // Listener for click events

    // Adapter constructor
    public MapAdapter(Context ctx, List<Region> items, OnRegionClickListener listener) {
        this.items = items; // Store list of regions
        this.listener = listener; // Store the click listener
    }

    // ViewHolder class holds references to the UI components for each item
    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView; // ImageView to show country outline

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.regionOutline); // Connect to layout ImageView
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the layout for each RecyclerView item
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.map_item, parent, false);
        return new ViewHolder(v); // Return new ViewHolder instance
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Region region = items.get(position); // Get the region object for this position
        Context context = holder.itemView.getContext(); // Get context for resource lookup

        // Get drawable resource ID by name dynamically
        int drawableId = context.getResources()
                .getIdentifier(region.getOutlineDrawableId(), "drawable", context.getPackageName());

        // Set the ImageView resource to the country's outline
        holder.imageView.setImageResource(drawableId);

        // Set click listener for the ImageView
        holder.imageView.setOnClickListener(v -> {
            // Call the listener callback and pass whether this region is correct
            listener.onRegionClick(region.isCorrect());
        });
    }

    @Override
    public int getItemCount() {
        return items.size(); // Return total number of items in the list
    }
}
