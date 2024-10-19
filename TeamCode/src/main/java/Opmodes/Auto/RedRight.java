package Opmodes.Auto;

import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.IMU;

import Components.Drive;
import Util.Vector2;

// Red alliance, on the side closest to the taped off rectangle.

@Autonomous(name = "RedRight", group = "RedAuto")
public class RedRight extends LinearOpMode {

    Drive drive = new Drive(hardwareMap);
    @Override
    public void runOpMode() throws InterruptedException {
        IMU imu = hardwareMap.get(IMU.class, "imu");

        IMU.Parameters parameters = new IMU.Parameters(new RevHubOrientationOnRobot(
                RevHubOrientationOnRobot.LogoFacingDirection.UP,
                RevHubOrientationOnRobot.UsbFacingDirection.FORWARD));

        imu.initialize(parameters);

        waitForStart();

        // Sideways
        drive.moveInDirection(new Vector2(1, 0), 0, 1, imu, 100);
    }
}
