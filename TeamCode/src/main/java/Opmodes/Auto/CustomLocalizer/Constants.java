package Opmodes.Auto.CustomLocalizer;

import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;

public class Constants {
    public static RevHubOrientationOnRobot.LogoFacingDirection LOGO_FACING_DIR =
            RevHubOrientationOnRobot.LogoFacingDirection.BACKWARD;
    public static RevHubOrientationOnRobot.UsbFacingDirection USB_FACING_DIR =
            RevHubOrientationOnRobot.UsbFacingDirection.UP;

    public static double TICKS_PER_REV = 2000;
    public static double WHEEL_RADIUS = 0.629921; // in
    public static double WHEEL_CIRCUMFRENCE = 2 * Math.PI * WHEEL_RADIUS;
    public static double TICKS_PER_INCH = WHEEL_CIRCUMFRENCE / TICKS_PER_REV;

    public static double maxVel = 40;
    public static double maxAccel = 35;

    public static double kV = 0;
    public static double kA = 0;

    public static double testMaxVel = 20;
    public static double testMaxAccel = 10;
}
