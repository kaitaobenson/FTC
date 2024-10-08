package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.IMU;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.MotionDetection;

@TeleOp
public class MecanumDrive extends LinearOpMode {
    @Override
    public void runOpMode() throws InterruptedException {
        DcMotor frontLeftMotor = hardwareMap.dcMotor.get("frontLeftMotor");
        DcMotor backLeftMotor = hardwareMap.dcMotor.get("backLeftMotor");
        DcMotor frontRightMotor = hardwareMap.dcMotor.get("frontRightMotor");
        DcMotor backRightMotor = hardwareMap.dcMotor.get("backRightMotor");

        frontRightMotor.setDirection(DcMotorSimple.Direction.REVERSE);
        backRightMotor.setDirection(DcMotorSimple.Direction.REVERSE);

        IMU imu = hardwareMap.get(IMU.class, "imu");

        IMU.Parameters parameters = new IMU.Parameters(new RevHubOrientationOnRobot(
                RevHubOrientationOnRobot.LogoFacingDirection.UP,
                RevHubOrientationOnRobot.UsbFacingDirection.FORWARD));

        imu.initialize(parameters);

        waitForStart();

        if (isStopRequested()) return;

        while (opModeIsActive()) {
            // Movement stick
            double x = gamepad1.left_stick_x;
            double y = -gamepad1.left_stick_y;
            // Rotation stick
            double rx = gamepad1.right_stick_x;

            // Reset rotations
            if (gamepad1.options) {
                imu.resetYaw();
            }

            double turn = rx * 0.5;

            double power = Math.hypot(x, y);

            double inputAngle = Math.atan2(y, x);
            double botAngle = imu.getRobotYawPitchRollAngles().getYaw(AngleUnit.RADIANS);

            double movementAngle = inputAngle - botAngle;

            double cos = Math.cos(movementAngle - Math.PI / 4);
            double sin = Math.sin(movementAngle - Math.PI / 4);

            // frontLeft and backRight are both cos because they are opposite corners (and have the same movement vector)
            double frontLeftPower = cos * power + turn;
            double backRightPower = cos * power - turn;

            // frontRight and backLeft are both sin because they are opposite corners (and have the same movement vector)
            double frontRightPower = sin * power - turn;
            double backLeftPower = sin * power + turn;

            // Normalize the motor powers to ensure the highest power is 1 or -1
            double maxPower = Math.max(Math.abs(frontLeftPower),
                    Math.max(Math.abs(backLeftPower),
                            Math.max(Math.abs(frontRightPower), Math.abs(backRightPower))));

            if (maxPower > 1.0) {
                frontLeftPower /= maxPower;
                backLeftPower /= maxPower;
                frontRightPower /= maxPower;
                backRightPower /= maxPower;
            }

            // Set motor powers
            frontLeftMotor.setPower(frontLeftPower);
            backLeftMotor.setPower(backLeftPower);
            frontRightMotor.setPower(frontRightPower);
            backRightMotor.setPower(backRightPower);

            // Telemetry for debugging
            telemetry.addData("Front Left Power", frontLeftPower);
            telemetry.addData("Back Left Power", backLeftPower);
            telemetry.addData("Front Right Power", frontRightPower);
            telemetry.addData("Back Right Power", backRightPower);

            Vector2 fieldPosition = getFieldPosition(frontLeftMotor, backLeftMotor, frontRightMotor, backRightMotor);
            telemetry.addData("Field X: ", fieldPosition.x);
            telemetry.addData("Field Y: ", fieldPosition.y);

            telemetry.update();
        }
    }

    private Vector2 getFieldPosition(DcMotor frontLeftMotor, DcMotor backLeftMotor, DcMotor frontRightMotor, DcMotor backRightMotor) {
        // Get the encoder positions (current motor ticks)
        int frontLeftPos = frontLeftMotor.getCurrentPosition();
        int backLeftPos = backLeftMotor.getCurrentPosition();
        int frontRightPos = frontRightMotor.getCurrentPosition();
        int backRightPos = backRightMotor.getCurrentPosition();

        // Convert encoder ticks to distance moved (this conversion factor will depend on your specific motors and wheels)
        double TICKS_PER_REV = 1120; // Example value, depends on your motor
        double WHEEL_DIAMETER = 4.0; // Example value in inches
        double CIRCUMFERENCE = Math.PI * WHEEL_DIAMETER;
        double TICKS_PER_INCH = TICKS_PER_REV / CIRCUMFERENCE;

        // Calculate how much each motor has moved in inches
        double frontLeftInches = frontLeftPos / TICKS_PER_INCH;
        double backLeftInches = backLeftPos / TICKS_PER_INCH;
        double frontRightInches = frontRightPos / TICKS_PER_INCH;
        double backRightInches = backRightPos / TICKS_PER_INCH;

        // Forward (Y) and Strafe (X) contributions from the motors
        double forwardMovement = (frontLeftInches + backLeftInches + frontRightInches + backRightInches) / 4.0;
        double strafeMovement = (-frontLeftInches + backLeftInches + frontRightInches - backRightInches) / 4.0;

        // You can adjust these values based on calibration to ensure accurate movement tracking
        return new Vector2(strafeMovement, forwardMovement);
    }
}