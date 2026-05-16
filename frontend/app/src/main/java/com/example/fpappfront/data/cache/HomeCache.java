package com.example.fpappfront.data.cache;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.fpappfront.data.model.Ingredient;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

public class HomeCache {

    private static final String PREF = "home_cache";

    private static final String KEY_FAMILIES = "families";
    private static final String KEY_INGREDIENTS = "ingredients";

    private static final String KEY_FAMILIES_TIME = "families_time";
    private static final String KEY_INGREDIENTS_TIME = "ingredients_time";
    private static final long TTL = 24 * 60 * 60 * 1000;

    public static void saveFamilies(Context context, List<String> families) {
        SharedPreferences prefs = context.getSharedPreferences(PREF, Context.MODE_PRIVATE);

        String json = new Gson().toJson(families);

        prefs.edit()
                .putString(KEY_FAMILIES, json)
                .putLong(KEY_FAMILIES_TIME, System.currentTimeMillis())
                .apply();
    }

    public static List<String> getFamilies(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREF, Context.MODE_PRIVATE);

        long savedTime = prefs.getLong(KEY_FAMILIES_TIME, 0);
        long now = System.currentTimeMillis();

        if (now - savedTime > TTL) {
            return null;
        }

        String json = prefs.getString(KEY_FAMILIES, null);
        if (json == null) return null;

        Type type = new TypeToken<List<String>>() {}.getType();
        return new Gson().fromJson(json, type);
    }

    public static void saveIngredients(Context context, List<Ingredient> list) {
        SharedPreferences prefs = context.getSharedPreferences(PREF, Context.MODE_PRIVATE);

        String json = new Gson().toJson(list);

        prefs.edit()
                .putString(KEY_INGREDIENTS, json)
                .putLong(KEY_INGREDIENTS_TIME, System.currentTimeMillis())
                .apply();
    }
    public static List<Ingredient> getIngredients(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREF, Context.MODE_PRIVATE);

        long savedTime = prefs.getLong(KEY_INGREDIENTS_TIME, 0);
        long now = System.currentTimeMillis();

        if (now - savedTime > TTL) {
            return null;
        }

        String json = prefs.getString(KEY_INGREDIENTS, null);
        if (json == null) return null;

        Type type = new TypeToken<List<Ingredient>>() {}.getType();
        return new Gson().fromJson(json, type);
    }

    public static void clear(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREF, Context.MODE_PRIVATE);
        prefs.edit().clear().apply();
    }
}