package Components;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.IMU;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;

import Util.Vector2;

public class Drive {

    public static final float rotationFactor = 0.5f;

    public DcMotor frontLeftMotor = null;
    public DcMotor frontRightMotor = null;
    public DcMotor backLeftMotor = null;
    public DcMotor backRightMotor = null;

    public Drive(HardwareMap hardwareMap) {
        frontLeftMotor = hardwareMap.dcMotor.get("frontLeftMotor");
        frontRightMotor = hardwareMap.dcMotor.get("frontRightMotor");
        backLeftMotor = hardwareMap.dcMotor.get("backLeftMotor");
        backRightMotor = hardwareMap.dcMotor.get("backRightMotor");

        frontRightMotor.setDirection(DcMotorSimple.Direction.REVERSE);
        backRightMotor.setDirection(DcMotorSimple.Direction.REVERSE);
    }

    // Speed is from 0 to 1, imu is for bot rotation
    public void moveInDirection(Vector2 inputDirection, float inputRotation, float speed, IMU imu) {
        inputRotation *= rotationFactor;

        double inputAngle = inputDirection.toAngle();
        double power = Math.hypot(inputDirection.x, inputDirection.y) / 2;
        double botAngle = imu.getRobotYawPitchRollAngles().getYaw(AngleUnit.RADIANS);

        double movementAngle = inputAngle - botAngle;

        // Subtract 45 degrees (mecanum wheel outwards movement vector)
        Vector2 rotated_dir = Vector2.fromAngle(movementAngle - Math.PI / 4);

        // Calculate the motor powers
        double frontLeftPower = rotated_dir.x * power + inputRotation;
        double backLeftPower = rotated_dir.y * power + inputRotation;
        double frontRightPower = rotated_dir.y * power - inputRotation;
        double backRightPower = rotated_dir.x * power - inputRotation;

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
    }

    private Vector2 getFieldPosition(IMU imu) {
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
