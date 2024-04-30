package com.coolawesome.integrativeproject.utils;

import javafx.scene.image.Image;

public final class Constants {
    //Constants for simulation information
    public static final String TIME_ELAPSED_PREFIX = "Time Elapsed: ";

    public static final String PLANET_COUNT_PREFIX = "Planet Count: ";

    public static final String AVERAGE_FORCE_PREFIX = "Average Magnitude of Force: ";

    public static final String NUMBER_OF_COLLISIONS_PREFIX = "Number of Collisions: ";

    public static final String[] ALGORITHM_CHOICES = {"Brute Force", "Barnes Hut"};

    public static final double defaultMass = 5000;

    public static final String defaultCustomPlanetTextureFilePath = "/images/planets/defaultCustomPlanet.jpg";

    //Private constructor to stop the instantiation  of this class
    private Constants() {
    }
}
