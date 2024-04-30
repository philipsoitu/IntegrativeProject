package com.coolawesome.integrativeproject.utils;

/**
 * Class that contains constants used throughout the project.
 */
public record Constants() {
    public static final String TIME_ELAPSED_PREFIX = "Time Elapsed: ";

    public static final String PLANET_COUNT_PREFIX = "Planet Count: ";

    public static final String NUMBER_OF_COLLISIONS_PREFIX = "Number of Collisions: ";

    public static final double defaultMass = 5000;

    public static final String defaultCustomPlanetTextureFilePath = "file:src/main/resources/images/planets/defaultCustomPlanet.jpg";

    public static final int CAM_FOV = 60;
}
