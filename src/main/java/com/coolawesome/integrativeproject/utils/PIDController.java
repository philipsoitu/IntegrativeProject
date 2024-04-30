package com.coolawesome.integrativeproject.utils;

/**
 * Class that represents a PID controller.
 */
public class PIDController {
    private final double kp; // Proportional gain
    private final double ki; // Integral gain
    private final double kd; // Derivative gain

    private double previousError;
    private double integral;

    /**
     * Constructor for the PIDController class.
     *
     * @param kp Proportional gain
     * @param ki Integral gain
     * @param kd Derivative gain
     */
    public PIDController(double kp, double ki, double kd) {
        this.kp = kp;
        this.ki = ki;
        this.kd = kd;
    }

    /**
     * Calculates the output of the PID controller.
     *
     * @param setpoint The setpoint for the controller.
     * @param actual   The actual value of the system.
     * @return The output of the PID controller.
     */
    public double calculate(double setpoint, double actual) {
        double error = setpoint - actual;
        integral += error;
        double derivative = error - previousError;
        previousError = error;
        return kp * error + ki * integral + kd * derivative;
    }
}
