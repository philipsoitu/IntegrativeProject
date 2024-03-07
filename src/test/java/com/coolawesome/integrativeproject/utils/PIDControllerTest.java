package com.coolawesome.integrativeproject.utils;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class PIDControllerTest {

    @Test
    public void pidControllerReducesPositiveError() {
        PIDController pidController = new PIDController(1, 0, 0);
        double output = pidController.calculate(10, 0);
        assertEquals(10, output);
    }

    @Test
    public void pidControllerReducesNegativeError() {
        PIDController pidController = new PIDController(1, 0, 0);
        double output = pidController.calculate(0, 10);
        assertEquals(-10, output);
    }

    @Test
    public void pidControllerDampensOscillations() {
        PIDController pidController = new PIDController(0, 0, 1);
        pidController.calculate(10, 0);
        double output = pidController.calculate(10, 20);
        assertEquals(-20, output);
    }

    @Test
    public void pidControllerHandlesZeroError() {
        PIDController pidController = new PIDController(1, 1, 1);
        double output = pidController.calculate(0, 0);
        assertEquals(0, output);
    }
}