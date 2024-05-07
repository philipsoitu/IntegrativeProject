package com.coolawesome.integrativeproject.utils;

import com.coolawesome.integrativeproject.Planet;
import com.google.gson.*;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

import java.lang.reflect.Type;

/**
 * Class that represents a custom type adapter for the Planet class.
 */
public class PlanetTypeAdapter implements JsonSerializer<Planet>, JsonDeserializer<Planet> {

    /**
     * Constructor for the PlanetTypeAdapter class.
     */
    public PlanetTypeAdapter() {
    }

    /**
     * Serializes a Planet object to a JSON object.
     *
     * @param src       The Planet object to serialize.
     * @param typeOfSrc The type of the source object.
     * @param context   The JSON serialization context.
     * @return The serialized JSON object.
     */
    @Override
    public JsonElement serialize(Planet src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("id", src.name);
        jsonObject.add("position", context.serialize(src.position));
        jsonObject.add("velocity", context.serialize(src.velocity));
        jsonObject.addProperty("radius", src.radius);
        jsonObject.addProperty("mass", src.mass);
        jsonObject.addProperty("isSun", src.isSun);
        jsonObject.addProperty("color", src.color.toString());
        // Additional properties can be added here if needed
        return jsonObject;
    }

    /**
     * Deserializes a JSON object to a Planet object.
     *
     * @param json    The JSON object to deserialize.
     * @param typeOfT The type of the target object.
     * @param context The JSON deserialization context.
     * @return The deserialized Planet object.
     * @throws JsonParseException If the JSON object cannot be deserialized.
     */
    @Override
    public Planet deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();
        String id = jsonObject.get("id").getAsString();
        Vector3D position = context.deserialize(jsonObject.get("position"), Vector3D.class);
        Vector3D velocity = context.deserialize(jsonObject.get("velocity"), Vector3D.class);
        double radius = jsonObject.get("radius").getAsDouble();
        double mass = jsonObject.get("mass").getAsDouble();
        boolean isSun = jsonObject.get("isSun").getAsBoolean();
        Color color = Color.valueOf(jsonObject.get("color").getAsString());

        return new Planet(id, position, velocity, radius, mass, isSun, new Image(Constants.defaultCustomPlanetTextureFilePath), color);
    }
}

