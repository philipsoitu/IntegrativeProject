package com.coolawesome.integrativeproject.utils;

import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.paint.Color;

/**
 * Class that represents a utility for generating the average color of an image.
 */
public class AverageColourGenerator {

    /**
     * Constructor for the AverageColourGenerator class.
     */
    public AverageColourGenerator() {
    }

    /**
     * Calculates the average color of an image
     *
     * @param image The image for which to calculate the average color
     * @return The average color of the image
     */
    public static Color getAverageColor(Image image) {

        PixelReader pixelReader = image.getPixelReader();

        int width = (int) image.getWidth();
        int height = (int) image.getHeight();

        double totalRed = 0;
        double totalGreen = 0;
        double totalBlue = 0;

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                Color color = pixelReader.getColor(x, y);
                totalRed += color.getRed();
                totalGreen += color.getGreen();
                totalBlue += color.getBlue();
            }
        }

        int totalPixels = width * height;
        double avgRed = totalRed / totalPixels;
        double avgGreen = totalGreen / totalPixels;
        double avgBlue = totalBlue / totalPixels;

        return Color.color(avgRed, avgGreen, avgBlue);

    }


}
