package com.example.mobilerecipeapp.storage;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface FavouriteDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(FavouriteRecipeEntity recipe);

    @Delete
    void delete(FavouriteRecipeEntity recipe);

    @Query("SELECT * FROM favourite_recipes")
    List<FavouriteRecipeEntity> getAllFavourites();
}