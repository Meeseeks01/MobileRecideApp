package com.example.mobilerecipeapp;

import android.os.Bundle;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mobilerecipeapp.model.Meal;
import com.example.mobilerecipeapp.network.MealApiClient;
import com.example.mobilerecipeapp.ui.MealAdapter;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private MealAdapter mealAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RecyclerView mealsRecyclerView = findViewById(R.id.mealsRecyclerView);
        SearchView searchView = findViewById(R.id.searchView);

        mealAdapter = new MealAdapter();

        mealsRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        mealsRecyclerView.setAdapter(mealAdapter);

        loadMeals("Chicken");

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String query) {
                loadMeals(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
    }

    private void loadMeals(String query) {

        MealApiClient.searchMeals(this, query, new MealApiClient.MealsCallback() {

            @Override
            public void onSuccess(List<Meal> meals) {
                mealAdapter.submitList(meals);
            }

            @Override
            public void onError() {
                Toast.makeText(
                        MainActivity.this,
                        "Failed to load recipes",
                        Toast.LENGTH_SHORT
                ).show();
            }
        });
    }
}