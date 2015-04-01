package fr.leward.graphdesigner.math;

import javafx.geometry.Point2D;

/**
 * Created by Paul-Julien on 22/02/2015.
 */
public class StraightLine {

    private double xA;
    private double yA;
    private double xB;
    private double yB;

    // y = ax + b
    private double a;
    private double b;

    public StraightLine(double xA, double yA, double xB, double yB) {
        this.xA = xA;
        this.yA = yA;
        this.xB = xB;
        this.yB = yB;

        // y = ax + b
        a = (yB - yA) / (xB - xA);
        b = yA - (a * xA);
    }

    public StraightLine(double xA, double yA, double a) {
        this.xA = xA;
        this.yA = yA;
        this.a = a;

        // y = ax + b
        // b = -(ax - y)
        this.b = -1 * (a * xA - yA);
    }

    public double calculateY(double x) {
        return a * x + b;
    }

    @Override
    public String toString() {
        return "y = ax + b = " + a + "x + " + b;
    }

    public double getxA() {
        return xA;
    }

    public double getyA() {
        return yA;
    }

    public double getxB() {
        return xB;
    }

    public double getyB() {
        return yB;
    }

    public double getA() {
        return a;
    }

    public double getB() {
        return b;
    }
}
