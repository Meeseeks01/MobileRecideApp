package com.example.mobilerecipeapp.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mobilerecipeapp.R;
import com.example.mobilerecipeapp.storage.FavouriteRecipeEntity;

import java.util.ArrayList;
import java.util.List;

public class FavouriteAdapter extends RecyclerView.Adapter<FavouriteAdapter.FavouriteViewHolder> {

    private final List<FavouriteRecipeEntity> favourites = new ArrayList<>();

    public void submitList(List<FavouriteRecipeEntity> newFavourites) {
        favourites.clear();
        favourites.addAll(newFavourites);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public FavouriteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_favourite, parent, false);

        return new FavouriteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FavouriteViewHolder holder, int position) {

        FavouriteRecipeEntity recipe = favourites.get(position);

        holder.favName.setText(recipe.name);
        holder.favMeta.setText(recipe.category + " • " + recipe.area);
    }

    @Override
    public int getItemCount() {
        return favourites.size();
    }

    static class FavouriteViewHolder extends RecyclerView.ViewHolder {

        ImageView favImage;
        TextView favName;
        TextView favMeta;

        FavouriteViewHolder(@NonNull View itemView) {
            super(itemView);

            favImage = itemView.findViewById(R.id.favImage);
            favName = itemView.findViewById(R.id.favName);
            favMeta = itemView.findViewById(R.id.favMeta);
        }
    }
}