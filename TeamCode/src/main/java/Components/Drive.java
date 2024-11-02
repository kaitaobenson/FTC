package Components;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.IMU;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;

import Util.Vector2;

public class Drive {

    public DcMotor frontLeftMotor = null;
    public DcMotor frontRightMotor = null;
    public DcMotor backLeftMotor = null;
    public DcMotor backRightMotor = null;

    public Drive(DcMotor frontLeftMotor, DcMotor frontRightMotor, DcMotor backLeftMotor, DcMotor backRightMotor) {
        this.frontLeftMotor = frontLeftMotor;
        this.frontRightMotor = frontRightMotor;
        this.backLeftMotor = backLeftMotor;
        this.backRightMotor = backRightMotor;

        frontLeftMotor.setDirection(DcMotorSimple.Direction.FORWARD);
        backLeftMotor.setDirection(DcMotorSimple.Direction.FORWARD);

        frontRightMotor.setDirection(DcMotorSimple.Direction.REVERSE);
        backRightMotor.setDirection(DcMotorSimple.Direction.REVERSE);
    }

    public void stopMovement() {
        frontLeftMotor.setPower(0);
        backLeftMotor.setPower(0);
        frontRightMotor.setPower(0);
        backRightMotor.setPower(0);
    }

    // speed is from 0 to 1
    public void moveInDirection(Vector2 direction, float rotation, float speed) {
        float rx = rotation * speed;

        double power = Math.hypot(direction.x, direction.y);
        double inputAngle = Math.atan2(direction.y, direction.x);

        double cos = Math.cos(inputAngle - Math.PI / 4);
        double sin = Math.sin(inputAngle - Math.PI / 4);

        double frontLeftPower = cos * power + rx;
        double backLeftPower = sin * power + rx;
        double frontRightPower = sin * power - rx;
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

        // Set motor powers
        frontLeftMotor.setPower(frontLeftPower * speed);
        backLeftMotor.setPower(backLeftPower * speed);
        frontRightMotor.setPower(frontRightPower * speed);
        backRightMotor.setPower(backRightPower * speed);
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
