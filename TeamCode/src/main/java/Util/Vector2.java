package Util;

public class Vector2 {
    public double x;
    public double y;

    public Vector2(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public boolean equals(Vector2 a) {
        return this.x == a.x && this.y == a.y;
    }

    public double toAngle() {
        return Math.atan2(this.y, this.x);
    }
    public static Vector2 fromAngle(double angle) {
        Vector2 output = new Vector2(Math.cos(angle), Math.sin(angle));
        return output;
    }
    public double distanceTo(Vector2 to) {
        return Math.sqrt(Math.pow(this.y - to.y, 2) + Math.pow(this.x - to.x, 2));
    }

    public Vector2 add(Vector2 a) {
        return new Vector2(this.x + a.x, this.y + a.y);
    }

    public Vector2 add(double a) {
        return new Vector2(this.x + a, this.y + a);
    }

    public Vector2 multiply(Vector2 a) {
        return new Vector2(this.x * a.x, this.y * a.y);
    }

    public Vector2 multiply(double a) {
        return new Vector2(this.x * a, this.y * a);
    }

    public Vector2 subtract(Vector2 a) {
        return new Vector2(this.x - a.x, this.y - a.y);
    }

    public Vector2 subtract(double a) {
        return new Vector2(this.x - a, this.y - a);
    }

    public Vector2 divide(Vector2 a) {
        return new Vector2(this.x / a.x, this.y / a.y);
    }

    public Vector2 divide(double a) {
        return new Vector2(this.x / a, this.y / a);
    }

    public void rotate(double angle) {
        // Calculate the new x and y values
        double cosAngle = Math.cos(angle);
        double sinAngle = Math.sin(angle);

        this.x = x * cosAngle - y * sinAngle;
        this.y = x * sinAngle + y * cosAngle;
    }
}
