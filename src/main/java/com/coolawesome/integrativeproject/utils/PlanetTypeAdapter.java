package com.coolawesome.integrativeproject.utils;

import com.google.gson.*;
import com.coolawesome.integrativeproject.*;
import javafx.scene.paint.Color;

import java.lang.reflect.Type;

public class PlanetTypeAdapter implements JsonSerializer<Planet>, JsonDeserializer<Planet> {

    @Override
    public JsonElement serialize(Planet src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("id", src.id);
        jsonObject.add("position", context.serialize(src.position));
        jsonObject.add("velocity", context.serialize(src.velocity));
        jsonObject.addProperty("radius", src.radius);
        jsonObject.addProperty("mass", src.mass);
        jsonObject.addProperty("isSun", src.isSun);
        jsonObject.addProperty("color", src.color.toString());
        // Additional properties can be added here if needed
        return jsonObject;
    }

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

        return new Planet(id, position, velocity, radius, mass, isSun, color);
    }
}

