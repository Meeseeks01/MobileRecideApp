package com.example.mobilerecipeapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mobilerecipeapp.model.Category;
import com.example.mobilerecipeapp.model.Meal;
import com.example.mobilerecipeapp.network.MealApiClient;
import com.example.mobilerecipeapp.ui.FavouritesActivity;
import com.example.mobilerecipeapp.ui.MealAdapter;
import com.example.mobilerecipeapp.ui.RecipeDetailActivity;
import com.example.mobilerecipeapp.util.NetworkUtils;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.android.material.textfield.TextInputEditText;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private MealAdapter mealAdapter;
    private ChipGroup categoryChipGroup;
    private CircularProgressIndicator progressBar;
    private LinearLayout messageLayout;
    private TextView messageText;
    private MaterialButton retryButton;
    private TextInputEditText searchEditText;
    private String lastCategory = "Seafood";

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

        categoryChipGroup = findViewById(R.id.categoryChipGroup);
        progressBar = findViewById(R.id.progressBar);
        messageLayout = findViewById(R.id.messageLayout);
        messageText = findViewById(R.id.messageText);
        retryButton = findViewById(R.id.retryButton);
        searchEditText = findViewById(R.id.searchEditText);

        RecyclerView recyclerView = findViewById(R.id.mealRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        mealAdapter = new MealAdapter(meal -> {
            Intent intent = new Intent(this, RecipeDetailActivity.class);
            intent.putExtra(RecipeDetailActivity.EXTRA_MEAL_ID, meal.getId());
            startActivity(intent);
        });

        recyclerView.setAdapter(mealAdapter);

        retryButton.setOnClickListener(v -> loadCategories());

        searchEditText.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                String query = searchEditText.getText() == null
                        ? ""
                        : searchEditText.getText().toString().trim();

                if (!query.isEmpty()) {
                    searchMeals(query);
                }

                return true;
            }
            return false;
        });

        loadCategories();
    }

    private void loadCategories() {
        if (!NetworkUtils.isOnline(this)) {
            showMessage("You are offline. Connect to the internet and try again.", true);
            return;
        }

        showLoading();

        MealApiClient.getCategories(this, new MealApiClient.CategoriesCallback() {
            @Override
            public void onSuccess(List<Category> categories) {
                categoryChipGroup.removeAllViews();

                for (Category category : categories) {
                    if (category.getName().equalsIgnoreCase("Dessert")) {
                        continue;
                    }

                    addCategoryChip(category.getName());
                }

                loadMealsByCategory(lastCategory);
            }

            @Override
            public void onError() {
                showMessage("Could not load meal categories. Please try again.", true);
            }
        });
    }

    private void addCategoryChip(String categoryName) {
        Chip chip = new Chip(this);
        chip.setText(getCategoryEmoji(categoryName) + " " + categoryName);
        chip.setCheckable(true);

        if (categoryName.equals(lastCategory)) {
            chip.setChecked(true);
        }

        chip.setOnClickListener(v -> {
            lastCategory = categoryName;
            loadMealsByCategory(categoryName);
        });

        categoryChipGroup.addView(chip);
    }

    private String getCategoryEmoji(String categoryName) {
        switch (categoryName) {
            case "Beef":
                return "🥩";
            case "Chicken":
                return "🍗";
            case "Lamb":
                return "🍖";
            case "Seafood":
                return "🦐";
            case "Vegetarian":
                return "🥗";
            case "Pasta":
                return "🍝";
            case "Pork":
                return "🥓";
            case "Breakfast":
                return "🍳";
            default:
                return "🍽";
        }
    }

    private void loadMealsByCategory(String category) {
        if (!NetworkUtils.isOnline(this)) {
            showMessage("You are offline. Connect to the internet and try again.", true);
            return;
        }

        showLoading();

        MealApiClient.getMealsByCategory(this, category, new MealApiClient.MealsCallback() {
            @Override
            public void onSuccess(List<Meal> meals) {
                showMeals(meals);
            }

            @Override
            public void onError() {
                showMessage("Could not load meals. Please try again.", true);
            }
        });
    }

    private void searchMeals(String query) {
        if (!NetworkUtils.isOnline(this)) {
            showMessage("You are offline. Connect to the internet and try again.", true);
            return;
        }

        showLoading();

        MealApiClient.searchMeals(this, query, new MealApiClient.MealsCallback() {
            @Override
            public void onSuccess(List<Meal> meals) {
                if (meals.isEmpty()) {
                    showMessage("No meals found for \"" + query + "\".", false);
                } else {
                    showMeals(meals);
                }
            }

            @Override
            public void onError() {
                showMessage("Could not search meals. Please try again.", true);
            }
        });
    }

    private void showLoading() {
        progressBar.setVisibility(View.VISIBLE);
        messageLayout.setVisibility(View.GONE);
        findViewById(R.id.mealRecyclerView).setVisibility(View.GONE);
    }

    private void showMeals(List<Meal> meals) {
        progressBar.setVisibility(View.GONE);
        messageLayout.setVisibility(View.GONE);
        findViewById(R.id.mealRecyclerView).setVisibility(View.VISIBLE);
        mealAdapter.submitList(meals);
    }

    private void showMessage(String message, boolean showRetry) {
        progressBar.setVisibility(View.GONE);
        findViewById(R.id.mealRecyclerView).setVisibility(View.GONE);
        messageLayout.setVisibility(View.VISIBLE);
        messageText.setText(message);
        retryButton.setVisibility(showRetry ? View.VISIBLE : View.GONE);
    }
}