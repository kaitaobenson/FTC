package Util;

public class Vector2 {
    public double x;
    public double y;

    public Vector2(double x, double y) {
        this.x = x;
        this.y = y;
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
}
