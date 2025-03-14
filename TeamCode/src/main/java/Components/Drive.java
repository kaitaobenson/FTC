package Components;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.IMU;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.AxesOrder;
import org.firstinspires.ftc.robotcore.external.navigation.AxesReference;

import java.util.ArrayList;

import Util.Vector2;

public class Drive {
    public DcMotor frontLeftMotor = null;
    public DcMotor frontRightMotor = null;
    public DcMotor backLeftMotor = null;
    public DcMotor backRightMotor = null;
    public boolean useFieldDirections = true;

    private boolean lockRotation = false;
    private double startRotation = 0;
    private IMU imu;

    public enum DRIVE_ROT_STATE {
        BASKET,
        SUBMERSIBLE,
    }

    public DRIVE_ROT_STATE driveRotState = DRIVE_ROT_STATE.SUBMERSIBLE;
    private boolean manualOverride = true;

    public Drive(DcMotor frontLeftMotor, DcMotor frontRightMotor, DcMotor backLeftMotor, DcMotor backRightMotor, IMU imu) {
        this.frontLeftMotor = frontLeftMotor;
        this.frontRightMotor = frontRightMotor;
        this.backLeftMotor = backLeftMotor;
        this.backRightMotor = backRightMotor;
        this.imu = imu;

        frontLeftMotor.setDirection(DcMotorSimple.Direction.FORWARD);
        backLeftMotor.setDirection(DcMotorSimple.Direction.REVERSE);

        frontRightMotor.setDirection(DcMotorSimple.Direction.REVERSE);
        backRightMotor.setDirection(DcMotorSimple.Direction.REVERSE);
    }

    public double getCurrentRotation() {
        return imu.getRobotOrientation(AxesReference.EXTRINSIC, AxesOrder.XYZ, AngleUnit.RADIANS).thirdAngle;
    }

    public double getCurrentRotationRadians() {
        return imu.getRobotOrientation(AxesReference.EXTRINSIC, AxesOrder.XYZ, AngleUnit.DEGREES).thirdAngle;
    }

    public void stopMovement() {
        frontLeftMotor.setPower(0);
        backLeftMotor.setPower(0);
        frontRightMotor.setPower(0);
        backRightMotor.setPower(0);
    }

    public void lockRotation() {
        lockRotation = true;
        startRotation = getCurrentRotationRadians();
    }

    public void unlockRotation() {
        lockRotation = false;
    }

    public void zeroRotation() {
        imu.resetYaw();
    }

    // frontLeftPower, backLeftPower, frontRightPower, backRightMotor
    public static double[] directionToMotorPower(Vector2 direction, float rotation) {
        float rx = rotation;

        double power = Math.hypot(direction.x, direction.y);
        double inputAngle = Math.atan2(direction.y, direction.x);

        double cos = Math.cos(inputAngle - Math.PI / 4);
        double sin = Math.sin(inputAngle - Math.PI / 4);

        double frontLeftPower = cos * power + rx;
        double backLeftPower = sin * power - rx;
        double frontRightPower = sin * -power - rx;
        double backRightPower = cos * power - rx;

        // Normalize motor powers
        double maxPower = Math.max(Math.abs(frontLeftPower),
                Math.max(Math.abs(backLeftPower),
                        Math.max(Math.abs(frontRightPower), Math.abs(backRightPower))));

        if (maxPower > 1.0) {
            frontLeftPower /= maxPower;
            backLeftPower /= maxPower;
            frontRightPower /= maxPower;
            backRightPower /= maxPower;
        }

        return new double[]{frontLeftPower, backLeftPower, frontRightPower, backRightPower};
    }

    // speed is from 0 to 1
    public void moveInDirection(Vector2 direction, float rotation, float speed) {
        float rx = rotation * speed;

        double fieldRotation = imu.getRobotYawPitchRollAngles().getPitch(AngleUnit.RADIANS);

        if (rotation == 0 && !manualOverride) {
            double targetRot = 0;
            if (driveRotState == DRIVE_ROT_STATE.SUBMERSIBLE) {
                targetRot = 0.0f;
            }
            if (driveRotState == DRIVE_ROT_STATE.BASKET) {
                targetRot = (2.35619449019 + Math.PI / 2.0f);
            }
            if (fieldRotation < targetRot) {
                rx = 0.1f;
            }
            if (fieldRotation > targetRot) {
                rx = -0.1f;
            }
            if (Math.abs(fieldRotation - rotation) < 0.05) {
                rx = 0;
                manualOverride = true;
            }
        }

        double power = Math.hypot(direction.x, direction.y);
        double inputAngle = Math.atan2(direction.y, direction.x) - (useFieldDirections ? fieldRotation : 0);

        double cos = Math.cos(inputAngle - Math.PI / 4);
        double sin = Math.sin(inputAngle - Math.PI / 4);

        double frontLeftPower = cos * power * speed + rx;
        double backLeftPower = sin * power * speed - rx;
        double frontRightPower = sin * -power * speed - rx;
        double backRightPower = cos * power * speed - rx;

        // Normalize motor powers
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

    public void moveInDirection(Vector2 direction, float rotation, float speed, long milliseconds) {
        moveInDirection(direction, rotation, speed);

        float startTime = System.currentTimeMillis();
        while (System.currentTimeMillis() - startTime < milliseconds) {
            //do nothing
        }
        stopMovement();
    }
}
