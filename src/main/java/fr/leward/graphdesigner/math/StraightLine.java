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

    public StraightLine(double a, double b) {
        this.a = a;
        this.b = b;
    }

    public double calculateY(double x) {
        return a * x + b;
    }

    public StraightLine translatedLine(double l) {
        double lineAngle = Math.atan(a);
        double completeAngle = lineAngle + Math.PI / 2;
        double xA2 = Math.cos(completeAngle) * l + xA;
        double yA2 = Math.sin(completeAngle) * l + yA;
        double xB2 = Math.cos(completeAngle) * l + xB;
        double yB2 = Math.sin(completeAngle) * l + yB;
        return new StraightLine(xA2, yA2, xB2, yB2);
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
