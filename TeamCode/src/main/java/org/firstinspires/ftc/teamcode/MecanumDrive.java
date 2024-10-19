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

        while (opModeIsActive() && !isStopRequested()) {
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

            double botAngle = imu.getRobotYawPitchRollAngles().getYaw(AngleUnit.RADIANS);

            double inputAngle = Math.atan2(y, x);
            double movementAngle = inputAngle;

            // Calculate the directional components of movement
            double cos = Math.cos(movementAngle - Math.PI / 4);
            double sin = Math.sin(movementAngle - Math.PI / 4);

            // Now calculate the motor powers
            double frontLeftPower = cos * power + turn;
            double backLeftPower = sin * power + turn;
            double frontRightPower = sin * power - turn;
            double backRightPower = cos * power - turn;

            // Normalize motor powers to prevent exceeding maximum value
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

            //Vector2 fieldPosition = getFieldPosition(frontLeftMotor, backLeftMotor, frontRightMotor, backRightMotor, imu);
            //telemetry.addData("Field X: ", fieldPosition.x);
            //telemetry.addData("Field Y: ", fieldPosition.y);

            /*
            Here's a little camel because everything is in flames

                              |~~~~~~~~~|
            -------           |`        |
            | o  o |_+_+_+_+_+|         |----|
            | d    |                          }
            -------        | |  |  \    |          _ }


             */
            DcMotor ascendMotorRight = hardwareMap.dcMotor.get("ascendMotorRight");
            DcMotor ascendMotorLeft = hardwareMap.dcMotor.get("ascendMotorLeft");
            float ascendPower = 0.0f;
            if (gamepad1.a) {
                ascendPower += 1.0f;
            } else if (gamepad1.b) {
                ascendPower -= 1.0f;
            }

            ascendMotorRight.setPower(ascendPower);
            ascendMotorLeft.setPower(ascendPower);

            telemetry.addData("Ascend Power", ascendPower);

            telemetry.update();
        }
    }

    private Vector2 getFieldPosition(DcMotor frontLeftMotor, DcMotor backLeftMotor, DcMotor frontRightMotor, DcMotor backRightMotor, IMU imu) {
        // Get the encoder positions (current motor ticks)
        int frontLeftPos = frontLeftMotor.getCurrentPosition();
        int backLeftPos = backLeftMotor.getCurrentPosition();
        int frontRightPos = frontRightMotor.getCurrentPosition();
        int backRightPos = backRightMotor.getCurrentPosition();

        // Convert encoder ticks to distance moved (this conversion factor will depend on your specific motors and wheels)
        double TICKS_PER_REV = 537.7; // Example value, depends on your motor
        double WHEEL_DIAMETER = 3.14961; // Example value in inches
        double CIRCUMFERENCE = Math.PI * WHEEL_DIAMETER;
        double TICKS_PER_INCH = TICKS_PER_REV / CIRCUMFERENCE;

        // Calculate how much each motor has moved in inches
        double frontLeftInches = frontLeftPos / TICKS_PER_INCH;
        double backLeftInches = backLeftPos / TICKS_PER_INCH;
        double frontRightInches = frontRightPos / TICKS_PER_INCH;
        double backRightInches = backRightPos / TICKS_PER_INCH;

        // Get XY position from the motors

        // Calculate forward (Y) and strafe (X) movements based on the Mecanum drive configuration
        double forwardMovement = (frontLeftInches + backLeftInches + frontRightInches + backRightInches) / 4.0;
        double strafeMovement = (-frontLeftInches + backLeftInches + frontRightInches - backRightInches) / 4.0;

        // Get the robot's current yaw (heading) from the IMU
        double botYaw = imu.getRobotYawPitchRollAngles().getYaw(AngleUnit.RADIANS);

        // Use the yaw to calculate field-centric coordinates
        double fieldX = strafeMovement * Math.cos(botYaw) - forwardMovement * Math.sin(botYaw);
        double fieldY = strafeMovement * Math.sin(botYaw) + forwardMovement * Math.cos(botYaw);

        // You can adjust these values based on calibration to ensure accurate movement tracking
        return new Vector2(strafeMovement, forwardMovement);
    }
}