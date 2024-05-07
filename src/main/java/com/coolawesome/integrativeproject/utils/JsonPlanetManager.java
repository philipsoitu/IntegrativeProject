package com.coolawesome.integrativeproject.utils;

import com.coolawesome.integrativeproject.Planet;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Map;

/**
 * Class that manages the saving and loading of planets to and from JSON files.
 */
public class JsonPlanetManager {

    private static Gson gson;

    /**
     * Constructor for the JsonPlanetManager class.
     */
    public JsonPlanetManager() {
        gson = new GsonBuilder().registerTypeAdapter(Planet.class, new PlanetTypeAdapter()).create();
    }

    /**
     * Saves a map of planets to a JSON file.
     *
     * @param planetMap The map of planets to save.
     * @param filePath  The file path to save the planets to.
     */
    public void saveToJson(Map<String, Planet> planetMap, String filePath) {
        try (FileWriter writer = new FileWriter(filePath)) {
            gson.toJson(planetMap, writer);
        } catch (IOException e) {
            System.out.println("Error saving to file: " + e.getMessage());
        }
    }

    /**
     * Loads a map of planets from a JSON file.
     *
     * @param filePath The file path to load the planets from.
     * @return The map of planets loaded from the file.
     */
    public Map<String, Planet> loadFromJson(String filePath) {
        try (FileReader reader = new FileReader(filePath)) {
            Type type = new TypeToken<Map<String, Planet>>() {
            }.getType();
            return gson.fromJson(reader, type);
        } catch (IOException e) {
            System.out.println("Error loading from file: " + e.getMessage());
            return null;
        }
    }
}
