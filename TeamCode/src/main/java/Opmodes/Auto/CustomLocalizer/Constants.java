package Opmodes.Auto.CustomLocalizer;

import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;

@Config
public class Constants {
    public static RevHubOrientationOnRobot.LogoFacingDirection LOGO_FACING_DIR =
            RevHubOrientationOnRobot.LogoFacingDirection.BACKWARD;
    public static RevHubOrientationOnRobot.UsbFacingDirection USB_FACING_DIR =
            RevHubOrientationOnRobot.UsbFacingDirection.UP;

    public static double TICKS_PER_REV = 2000;
    public static double WHEEL_RADIUS = 0.629921; // in
    public static double WHEEL_CIRCUMFRENCE = 2 * Math.PI * WHEEL_RADIUS;
    public static double TICKS_PER_INCH = WHEEL_CIRCUMFRENCE / TICKS_PER_REV;
    public static double X_MULTIPLIER = 1;
    public static double Y_MULTIPLIER = 1;

    // Inches per second
    public static double maxVel = 25;
    // Inches per second squared
    public static double maxAccel = 10;

    // Degrees per second
    public static double maxAngVel = 15;
    // Degrees per second squared
    public static double maxAngAccel = 7;


    public static double kV = 0.02;
    public static double kA = 0.002;

    public static double P = 0;
    public static double I = 0;
    public static double D = 0;

    public static double headingP = 4;
    public static double headingI = 0;
    public static double headingD = 0;

    public static double testMaxVel = 20;
    public static double testMaxAccel = 10;
}
