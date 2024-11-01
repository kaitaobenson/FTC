package Opmodes.Teleop;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.IMU;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;
import com.qualcomm.robotcore.hardware.Servo;

import Components.Drive;
import Components.Hang;
import Components.Intake;
import Components.Slides;
import Util.Vector2;

@TeleOp(name = "Main", group = "Working")
public class Main extends OpMode {

    IMU imu;
    Drive drive;
    Hang hang;
    Intake intake;
    Slides slides;

    @Override
    public void init() {
        // Imu config
        imu = hardwareMap.get(IMU.class, "imu");

        IMU.Parameters parameters = new IMU.Parameters(new RevHubOrientationOnRobot(
                RevHubOrientationOnRobot.LogoFacingDirection.UP,
                RevHubOrientationOnRobot.UsbFacingDirection.FORWARD));

        imu.initialize(parameters);

        // Drive config
        DcMotor frontLeftMotor = hardwareMap.dcMotor.get("frontLeftMotor");
        DcMotor frontRightMotor = hardwareMap.dcMotor.get("frontRightMotor");
        DcMotor backLeftMotor = hardwareMap.dcMotor.get("backLeftMotor");
        DcMotor backRightMotor = hardwareMap.dcMotor.get("backRightMotor");

        drive = new Drive(frontLeftMotor, frontRightMotor, backLeftMotor, backRightMotor);

        // Intake config
        DcMotor intakeMotor = hardwareMap.dcMotor.get("intakeMotor");
        Servo intakeTiltServo = hardwareMap.servo.get("intakeTiltServo");

        intake = new Intake(intakeMotor);

        // Slides config
        DcMotor slideMotor = hardwareMap.dcMotor.get("slideMotor");

        slides = new Slides(slideMotor);

        // Hang config
        DcMotor leftHangMotor = hardwareMap.dcMotor.get("leftHangMotor");
        DcMotor rightHangMotor = hardwareMap.dcMotor.get("rightHangMotor");
    }

    @Override
    public void loop() {
        // Drive
        Vector2 driveDirection = new Vector2(gamepad1.left_stick_x, -gamepad1.left_stick_y);
        float driveRotation = gamepad1.right_stick_x;
        drive.moveInDirection(driveDirection, driveRotation, 1.0f);

        Vector2 preciseDirection = new Vector2(gamepad2.left_stick_x, -gamepad2.left_stick_y);
        float preciseRotation = gamepad2.right_stick_x;
        drive.moveInDirection(preciseDirection, preciseRotation, 0.2f);

        // Slides
        float slidesPower = -gamepad2.left_stick_y;
        slides.moveSlides(slidesPower);

        // Intake
        float intakePower = 0.0f;
        intakePower += gamepad2.right_trigger > 0.8 ? 0.5 : 0;
        intakePower += gamepad2.left_trigger > 0.8 ? -0.5 : 0;
        intake.moveIntake(intakePower);

        // Hang
        float hangPower = 0.0f;



        /*
        telemetry.addData("Drive: Front Left Motor", drive.frontLeftMotor.getPower());
        telemetry.addData("Drive: Front Right Motor", drive.frontRightMotor.getPower());
        telemetry.addData("Drive: Back Left Motor", drive.backLeftMotor.getPower());
        telemetry.addData("Drive: Back Right Motor", drive.backRightMotor.getPower());
         */

        telemetry.update();
    }
}