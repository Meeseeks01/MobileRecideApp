package com.example.mobilerecipeapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mobilerecipeapp.model.Meal;
import com.example.mobilerecipeapp.network.MealApiClient;
import com.example.mobilerecipeapp.ui.FavouritesActivity;
import com.example.mobilerecipeapp.ui.MealAdapter;
import com.example.mobilerecipeapp.ui.RecipeDetailActivity;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private MealAdapter mealAdapter;
    private RecyclerView mealsRecyclerView;
    private View progressBar;
    private View errorText;
    private MaterialButton retryButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MaterialToolbar toolbar = findViewById(R.id.topAppBar);
        toolbar.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.action_favourites) {
                startActivity(new Intent(this, FavouritesActivity.class));
                return true;
            }
            return false;
        });

        SearchView searchView = findViewById(R.id.searchView);
        mealsRecyclerView = findViewById(R.id.mealsRecyclerView);
        progressBar = findViewById(R.id.progressBar);
        errorText = findViewById(R.id.errorText);
        retryButton = findViewById(R.id.retryButton);

        mealAdapter = new MealAdapter(meal -> {
            Intent intent = new Intent(this, RecipeDetailActivity.class);
            intent.putExtra(RecipeDetailActivity.EXTRA_MEAL_ID, meal.getId());
            startActivity(intent);
        });

        mealsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mealsRecyclerView.setAdapter(mealAdapter);

        retryButton.setOnClickListener(v -> loadMeals("Chicken"));

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

        loadMeals("Chicken");
    }

    private void loadMeals(String query) {
        showLoading();

        MealApiClient.searchMeals(this, query, new MealApiClient.MealsCallback() {
            @Override
            public void onSuccess(List<Meal> meals) {
                showMeals(meals);
            }

            @Override
            public void onError() {
                showError();
            }
        });
    }

    private void showLoading() {
        progressBar.setVisibility(View.VISIBLE);
        errorText.setVisibility(View.GONE);
        retryButton.setVisibility(View.GONE);
        mealsRecyclerView.setVisibility(View.GONE);
    }

    private void showMeals(List<Meal> meals) {
        progressBar.setVisibility(View.GONE);
        errorText.setVisibility(View.GONE);
        retryButton.setVisibility(View.GONE);
        mealsRecyclerView.setVisibility(View.VISIBLE);
        mealAdapter.submitList(meals);
    }

    private void showError() {
        progressBar.setVisibility(View.GONE);
        errorText.setVisibility(View.VISIBLE);
        retryButton.setVisibility(View.VISIBLE);
        mealsRecyclerView.setVisibility(View.GONE);
    }
}