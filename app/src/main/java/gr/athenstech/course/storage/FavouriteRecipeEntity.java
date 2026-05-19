package gr.athenstech.course.storage;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "favourite_recipes")
public class FavouriteRecipeEntity {
    @PrimaryKey
    @NonNull
    public String id;
    public String name;
    public String imageUrl;
    public String category;
    public String area;
    public String sourceUrl;

    public FavouriteRecipeEntity(@NonNull String id, String name, String imageUrl, String category, String area, String sourceUrl) {
        this.id = id;
        this.name = name;
        this.imageUrl = imageUrl;
        this.category = category;
        this.area = area;
        this.sourceUrl = sourceUrl;
    }
}
