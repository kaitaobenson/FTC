package Opmodes;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.IMU;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;

import Components.Drive;
import Components.Intake;
import Components.Slides;
import Util.Vector2;

import java.util.HashMap;

@TeleOp
public class Main extends LinearOpMode {

    Drive drive = new Drive(hardwareMap);
    Intake intake = new Intake(hardwareMap);
    Slides slides = new Slides(hardwareMap);

    @Override
    public void runOpMode() throws InterruptedException {

        IMU imu = hardwareMap.get(IMU.class, "imu");

        IMU.Parameters parameters = new IMU.Parameters(new RevHubOrientationOnRobot(
                RevHubOrientationOnRobot.LogoFacingDirection.UP,
                RevHubOrientationOnRobot.UsbFacingDirection.FORWARD));

        imu.initialize(parameters);

        waitForStart();

        // Program loop
        while (opModeIsActive() && !isStopRequested()) {

            // Drive
            Vector2 driveDirection = new Vector2(gamepad1.left_stick_x, -gamepad1.left_stick_y);
            float driveRotation = gamepad1.right_stick_x;
            drive.moveInDirection(driveDirection, driveRotation, 1, imu);

            // Slides
            float slidesPower = -gamepad2.left_stick_y;
            slides.moveSlides(slidesPower);

            // Intake
            float intakePower = 0.0f;
            intakePower += gamepad2.a ? 0.5 : 0;
            intakePower -= gamepad2.b ? 0.5 : 0;
            intake.moveIntake(intakePower);

            // Print to console
            telemetry.addData(":} ;[ HAHAHA: silly: ", 10);
            telemetry.update();
        }
    }
}