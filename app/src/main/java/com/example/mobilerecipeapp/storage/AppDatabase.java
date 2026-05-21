package com.example.mobilerecipeapp.storage;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {FavouriteRecipeEntity.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {

    public abstract FavouriteDao favouriteDao();

    private static AppDatabase instance;

    public static synchronized AppDatabase getInstance(Context context) {

        if (instance == null) {
            instance = Room.databaseBuilder(
                    context.getApplicationContext(),
                    AppDatabase.class,
                    "recipes_db"
            ).allowMainThreadQueries().build();
        }

        return instance;
    }
}