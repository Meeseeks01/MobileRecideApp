package com.example.mobilerecipeapp.ui;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mobilerecipeapp.R;
import com.example.mobilerecipeapp.storage.AppDatabase;
import com.example.mobilerecipeapp.storage.FavouriteRecipeEntity;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.snackbar.Snackbar;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class FavouritesActivity extends AppCompatActivity {

    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    private FavouriteAdapter adapter;
    private RecyclerView recyclerView;
    private View emptyLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favourites);

        MaterialToolbar toolbar = findViewById(R.id.favToolbar);
        toolbar.setNavigationOnClickListener(v -> finish());

        recyclerView = findViewById(R.id.favouritesRecyclerView);
        emptyLayout = findViewById(R.id.emptyLayout);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new FavouriteAdapter();
        recyclerView.setAdapter(adapter);

        adapter.setOnRemoveClickListener(this::removeFavourite);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadFavourites();
    }

    private void loadFavourites() {
        executorService.execute(() -> {
            List<FavouriteRecipeEntity> favourites =
                    AppDatabase.getInstance(this).favouriteDao().getAllFavourites();

            runOnUiThread(() -> {
                adapter.submitList(favourites);

                boolean isEmpty = favourites.isEmpty();
                emptyLayout.setVisibility(isEmpty ? View.VISIBLE : View.GONE);
                recyclerView.setVisibility(isEmpty ? View.GONE : View.VISIBLE);
            });
        });
    }

    private void removeFavourite(FavouriteRecipeEntity recipe) {
        executorService.execute(() -> {
            AppDatabase.getInstance(this).favouriteDao().delete(recipe);

            runOnUiThread(() -> {
                Snackbar.make(recyclerView, "Removed from favourites", Snackbar.LENGTH_SHORT).show();
                loadFavourites();
            });
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        executorService.shutdown();
    }
}