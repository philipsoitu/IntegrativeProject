package com.coolawesome.integrativeproject.utils;

import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.paint.Color;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Objects;

public class AverageColourGenerator {
    public static Color getAverageColor(String imagePath) throws FileNotFoundException {

        Image image = new Image(Objects.requireNonNull(AverageColourGenerator.class.getResourceAsStream(Constants.defaultCustomPlanetTextureFilePath)));

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
