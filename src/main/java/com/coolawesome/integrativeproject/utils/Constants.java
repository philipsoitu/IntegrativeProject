package com.coolawesome.integrativeproject.utils;

public final class Constants {

    public static final String frameRatePrefix = "Frame Rate: ";

    public static final String timeElapsedPrefix = "Time Elapsed: ";

    public static final String planetCountPrefix = "Planet Count: ";



    //shouldn't be able to create an instance of this class
    private Constants() {
        throw new AssertionError();
    }
}
