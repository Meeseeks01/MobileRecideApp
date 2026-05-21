package com.example.mobilerecipeapp.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mobilerecipeapp.R;
import com.example.mobilerecipeapp.model.Meal;
import com.example.mobilerecipeapp.network.ImageLoader;

import java.util.ArrayList;
import java.util.List;

public class MealAdapter extends RecyclerView.Adapter<MealAdapter.MealViewHolder> {

    public interface OnMealClickListener {
        void onMealClick(Meal meal);
    }

    private final List<Meal> meals = new ArrayList<>();
    private final OnMealClickListener listener;

    public MealAdapter(OnMealClickListener listener) {
        this.listener = listener;
    }

    public void submitList(List<Meal> newMeals) {
        meals.clear();
        meals.addAll(newMeals);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MealViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_meal, parent, false);
        return new MealViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MealViewHolder holder, int position) {
        Meal meal = meals.get(position);

        holder.mealName.setText(meal.getName());
        ImageLoader.load(holder.itemView.getContext(), meal.getImageUrl(), holder.mealImage);

        holder.itemView.setOnClickListener(v -> listener.onMealClick(meal));
    }

    @Override
    public int getItemCount() {
        return meals.size();
    }

    static class MealViewHolder extends RecyclerView.ViewHolder {
        ImageView mealImage;
        TextView mealName;

        MealViewHolder(@NonNull View itemView) {
            super(itemView);
            mealImage = itemView.findViewById(R.id.mealImage);
            mealName = itemView.findViewById(R.id.mealName);
        }
    }
}