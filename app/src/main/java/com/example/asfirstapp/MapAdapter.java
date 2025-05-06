package com.example.asfirstapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class MapAdapter extends RecyclerView.Adapter<MapAdapter.ViewHolder> {

    public interface OnRegionClickListener {
        void onRegionClick(boolean isCorrect);
    }

    private final List<Region> items;
    private final OnRegionClickListener listener;

    public MapAdapter(Context ctx, List<Region> items, OnRegionClickListener listener) {
        this.items = items;
        this.listener = listener;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.regionOutline);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.map_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Region region = items.get(position);
        Context context = holder.itemView.getContext();

        int drawableId = context.getResources().getIdentifier(region.getOutlineDrawableId(), "drawable", context.getPackageName());


        holder.imageView.setImageResource(drawableId);


        holder.imageView.setOnClickListener(v -> listener.onRegionClick(region.isCorrect()));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }
}
