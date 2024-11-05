package Opmodes.Auto;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.IMU;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;

import java.util.ArrayList;
import java.util.Objects;
import java.util.Vector;

import Components.AprilTagCompute;
import Components.Drive;
import Util.Vector2;

@Config
@Autonomous(name = "AprilTagDetection", group="Demo")
public class AprilTagDetection extends OpMode {
    AprilTagCompute compute;
    IMU imu;
    Drive drive;
    double timesLostAprilTag = 0;

    public static float minDistConsideredReached = 0.1f;
    public static float fullMovementMultiplier = 1;

    public boolean targetReached = false;
    public Vector2 targetPosition = new Vector2(0, 0.5);

    private Vector2 movementDirection = new Vector2(0, 0);

    @Override
    public void init() {
        telemetry = FtcDashboard.getInstance().getTelemetry();

        imu = hardwareMap.get(IMU.class, "imu");

        IMU.Parameters parameters = new IMU.Parameters(new RevHubOrientationOnRobot(
                RevHubOrientationOnRobot.LogoFacingDirection.UP,
                RevHubOrientationOnRobot.UsbFacingDirection.FORWARD));

        imu.initialize(parameters);

        DcMotor frontLeftMotor = hardwareMap.dcMotor.get("frontLeftMotor");
        DcMotor frontRightMotor = hardwareMap.dcMotor.get("frontRightMotor");
        DcMotor backLeftMotor = hardwareMap.dcMotor.get("backLeftMotor");
        DcMotor backRightMotor = hardwareMap.dcMotor.get("backRightMotor");

        drive = new Drive(frontLeftMotor, frontRightMotor, backLeftMotor, backRightMotor, imu);
        drive.useFieldDirections = true;

        imu.resetYaw();

        compute = new AprilTagCompute(hardwareMap, telemetry);
    }

    @Override
    public void loop() {
        ArrayList<org.openftc.apriltag.AprilTagDetection> detections = compute.getAprilTagDetections();

        telemetry.addData("Times lost april tag", timesLostAprilTag);

        if (detections == null || detections.isEmpty()) {
            timesLostAprilTag++;

            // The April tag has probably gone out of view.
            if (timesLostAprilTag > 200) {
                telemetry.addData("Last Valid April Tag Direction", compute.lastValidAprilTagDirection);
                drive.moveInDirection(new Vector2(0, 0), compute.lastValidAprilTagDirection * 0.3f, 0.3f);
            }

            return;
        }

        timesLostAprilTag = 0;

        Vector2 robotPosition = compute.getRobotPosition(detections);

        // Example code
        double distToTargetPosition = robotPosition.distanceTo(targetPosition);

        telemetry.addData("Distance to target position", distToTargetPosition);
        telemetry.addData("IMU Angle", imu.getRobotYawPitchRollAngles().getYaw(AngleUnit.RADIANS));

        if (distToTargetPosition < minDistConsideredReached) {
            targetReached = true;

            if (targetPosition.equals(new Vector2(0, 0.5))) {
                targetPosition = new Vector2(0.25, 0.8);
            }
            else {
                targetPosition = new Vector2(0, 0.5);
            }

            return;
        }

        Vector2 targetMovementDirection = targetPosition.subtract(robotPosition).normalized();

        movementDirection = movementDirection.add(targetMovementDirection.subtract(movementDirection).multiply(0.25));

        double speedMultiplier = Math.min(Math.max(fullMovementMultiplier * distToTargetPosition * 2, 0.2f), 1f);
        drive.moveInDirection(new Vector2(-movementDirection.y, -movementDirection.x), 0, (float)speedMultiplier);

        telemetry.addData("Movement Direction", Vector2.toString(movementDirection));
        telemetry.addData("Target Position", Vector2.toString(targetPosition));
    }
}
