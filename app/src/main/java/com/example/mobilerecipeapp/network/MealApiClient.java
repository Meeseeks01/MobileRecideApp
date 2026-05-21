package com.example.mobilerecipeapp.network;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.mobilerecipeapp.model.Category;
import com.example.mobilerecipeapp.model.Meal;
import com.example.mobilerecipeapp.model.RecipeDetail;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MealApiClient {

    private static final String BASE_URL = "https://www.themealdb.com/api/json/v1/1/";

    public interface CategoriesCallback {
        void onSuccess(List<Category> categories);
        void onError();
    }

    public interface MealsCallback {
        void onSuccess(List<Meal> meals);
        void onError();
    }

    public static void getCategories(Context context, CategoriesCallback callback) {
        String url = BASE_URL + "categories.php";

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.GET,
                url,
                null,
                response -> {
                    try {
                        List<Category> categories = new ArrayList<>();
                        JSONArray array = response.getJSONArray("categories");

                        for (int i = 0; i < array.length(); i++) {
                            JSONObject item = array.getJSONObject(i);
                            categories.add(new Category(item.getString("strCategory")));
                        }

                        callback.onSuccess(categories);
                    } catch (Exception e) {
                        callback.onError();
                    }
                },
                error -> callback.onError()
        );

        Volley.newRequestQueue(context).add(request);
    }

    public static void getMealsByCategory(Context context, String category, MealsCallback callback) {
        String url = BASE_URL + "filter.php?c=" + category;

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.GET,
                url,
                null,
                response -> parseMeals(response, callback),
                error -> callback.onError()
        );

        Volley.newRequestQueue(context).add(request);
    }

    public static void searchMeals(Context context, String query, MealsCallback callback) {
        String url = BASE_URL + "search.php?s=" + query;

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.GET,
                url,
                null,
                response -> parseMeals(response, callback),
                error -> callback.onError()
        );

        Volley.newRequestQueue(context).add(request);
    }

    private static void parseMeals(JSONObject response, MealsCallback callback) {
        try {
            List<Meal> meals = new ArrayList<>();

            if (response.isNull("meals")) {
                callback.onSuccess(meals);
                return;
            }

            JSONArray array = response.getJSONArray("meals");

            for (int i = 0; i < array.length(); i++) {
                JSONObject item = array.getJSONObject(i);

                meals.add(new Meal(
                        item.getString("idMeal"),
                        item.getString("strMeal"),
                        item.optString("strMealThumb", "")
                ));
            }

            callback.onSuccess(meals);
        } catch (Exception e) {
            callback.onError();
        }
    }

    public interface DetailCallback {
        void onSuccess(RecipeDetail recipe);
        void onError();
    }

    public static void getMealDetail(Context context, String mealId, DetailCallback callback) {
        String url = BASE_URL + "lookup.php?i=" + mealId;

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.GET,
                url,
                null,
                response -> {
                    try {
                        JSONArray meals = response.getJSONArray("meals");
                        JSONObject item = meals.getJSONObject(0);

                        RecipeDetail recipe = new RecipeDetail();
                        recipe.id = item.getString("idMeal");
                        recipe.name = item.getString("strMeal");
                        recipe.category = item.optString("strCategory", "");
                        recipe.area = item.optString("strArea", "");
                        recipe.instructions = item.optString("strInstructions", "");
                        recipe.imageUrl = item.optString("strMealThumb", "");
                        recipe.sourceUrl = item.optString("strSource", "");

                        StringBuilder ingredients = new StringBuilder();
                        for (int i = 1; i <= 20; i++) {
                            String ingredient = item.optString("strIngredient" + i, "");
                            String measure = item.optString("strMeasure" + i, "");

                            if (!ingredient.trim().isEmpty()) {
                                ingredients.append("• ")
                                        .append(measure)
                                        .append(" ")
                                        .append(ingredient)
                                        .append("\n");
                            }
                        }

                        recipe.ingredients = ingredients.toString();

                        callback.onSuccess(recipe);

                    } catch (Exception e) {
                        callback.onError();
                    }
                },
                error -> callback.onError()
        );

        Volley.newRequestQueue(context).add(request);
    }
}

