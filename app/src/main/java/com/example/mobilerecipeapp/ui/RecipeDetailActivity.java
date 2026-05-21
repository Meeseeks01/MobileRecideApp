package com.example.mobilerecipeapp.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.mobilerecipeapp.R;
import com.example.mobilerecipeapp.model.RecipeDetail;
import com.example.mobilerecipeapp.network.ImageLoader;
import com.example.mobilerecipeapp.network.MealApiClient;
import com.example.mobilerecipeapp.storage.AppDatabase;
import com.example.mobilerecipeapp.storage.FavouriteRecipeEntity;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;
import com.google.android.material.snackbar.Snackbar;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RecipeDetailActivity extends AppCompatActivity {

    public static final String EXTRA_MEAL_ID = "meal_id";

    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    private RecipeDetail currentRecipe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_detail);

        MaterialToolbar toolbar = findViewById(R.id.detailToolbar);
        toolbar.setNavigationOnClickListener(v -> finish());

        MaterialButton saveButton = findViewById(R.id.saveButton);
        MaterialButton shareButton = findViewById(R.id.shareButton);

        saveButton.setOnClickListener(v -> saveFavourite());
        shareButton.setOnClickListener(v -> shareRecipe());

        String mealId = getIntent().getStringExtra(EXTRA_MEAL_ID);
        loadRecipe(mealId);
    }

    private void loadRecipe(String mealId) {
        MealApiClient.getMealDetail(this, mealId, new MealApiClient.DetailCallback() {
            @Override
            public void onSuccess(RecipeDetail recipe) {
                showRecipe(recipe);
            }

            @Override
            public void onError() {
                Snackbar.make(
                        findViewById(R.id.detailName),
                        "Could not load recipe",
                        Snackbar.LENGTH_SHORT
                ).show();
            }
        });
    }

    private void showRecipe(RecipeDetail recipe) {
        currentRecipe = recipe;

        ((TextView) findViewById(R.id.detailName)).setText(recipe.name);
        ((Chip) findViewById(R.id.categoryChip)).setText(recipe.category);
        ((Chip) findViewById(R.id.areaChip)).setText(recipe.area);
        ((TextView) findViewById(R.id.ingredientsText)).setText(recipe.ingredients);
        ((TextView) findViewById(R.id.instructionsText)).setText(recipe.instructions);

        ImageView imageView = findViewById(R.id.detailImage);
        ImageLoader.load(this, recipe.imageUrl, imageView);
    }

    private void saveFavourite() {
        if (currentRecipe == null) return;

        FavouriteRecipeEntity favourite = new FavouriteRecipeEntity(
                currentRecipe.id,
                currentRecipe.name,
                currentRecipe.category,
                currentRecipe.area,
                currentRecipe.imageUrl,
                currentRecipe.sourceUrl
        );

        executorService.execute(() -> {
            AppDatabase.getInstance(this).favouriteDao().insert(favourite);

            runOnUiThread(() ->
                    Snackbar.make(
                            findViewById(R.id.detailName),
                            "Saved to favourites",
                            Snackbar.LENGTH_SHORT
                    ).show()
            );
        });
    }

    private void shareRecipe() {
        if (currentRecipe == null) return;

        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(
                Intent.EXTRA_TEXT,
                "Check out this recipe: " + currentRecipe.name + "\n\n" + currentRecipe.sourceUrl
        );

        startActivity(Intent.createChooser(shareIntent, "Share recipe"));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        executorService.shutdown();
    }
}