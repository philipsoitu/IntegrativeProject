package com.coolawesome.integrativeproject.utils;

public class PIDController {
    private double kp; // Proportional gain
    private double ki; // Integral gain
    private double kd; // Derivative gain

    private double previousError;
    private double integral;

    public PIDController(double kp, double ki, double kd) {
        this.kp = kp;
        this.ki = ki;
        this.kd = kd;
    }

    public double calculate(double setpoint, double actual) {
        double error = setpoint - actual;
        integral += error;
        double derivative = error - previousError;
        previousError = error;
        return kp * error + ki * integral + kd * derivative;
    }
}
