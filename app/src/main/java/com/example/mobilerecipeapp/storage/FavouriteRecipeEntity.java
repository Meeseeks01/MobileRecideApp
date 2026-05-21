package com.example.mobilerecipeapp.storage;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "favourite_recipes")
public class FavouriteRecipeEntity {

    @PrimaryKey
    @NonNull
    public String id;

    public String name;
    public String category;
    public String area;
    public String imageUrl;
    public String sourceUrl;

    public FavouriteRecipeEntity(
            @NonNull String id,
            String name,
            String category,
            String area,
            String imageUrl,
            String sourceUrl
    ) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.area = area;
        this.imageUrl = imageUrl;
        this.sourceUrl = sourceUrl;
    }
}