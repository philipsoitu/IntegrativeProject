package com.coolawesome.integrativeproject.utils;

import com.coolawesome.integrativeproject.Planet;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import javafx.scene.PointLight;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;

import java.io.FileWriter;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Map;

public class JsonPlanetManager {

    private static Gson gson;

    public JsonPlanetManager() {
        this.gson = new GsonBuilder()
                .registerTypeAdapter(Planet.class, new PlanetTypeAdapter())
                .create();
    }

    public static void saveToJson(Map<String, Planet> planetMap, String filePath) {
        try (FileWriter writer = new FileWriter(filePath)) {
            gson.toJson(planetMap, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Map<String, Planet> loadFromJson(String filePath) {
        try (FileReader reader = new FileReader(filePath)) {
            Type type = new TypeToken<Map<String, Planet>>(){}.getType();
            return gson.fromJson(reader, type);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
