package Opmodes.Auto.CustomLocalizer;

import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.IMU;

import org.firstinspires.ftc.robotcore.external.Const;

import java.lang.reflect.Array;
import java.util.ArrayList;

import Components.Drive;
import Roadrunner.util.Encoder;
import Util.Vector2;

public class CustomPathFollower {
    DcMotorEx leftFrontMotor;
    DcMotorEx leftRearMotor;
    DcMotorEx rightRearMotor;
    DcMotorEx rightFrontMotor;

    Encoder perpendicularEncoder;
    Encoder parallelEncoder;

    Drive mecanumDrive;
    Localizer localizer;

    IMU imu;

    public CustomPathFollower(HardwareMap hardwareMap, Vector2 initialPos, double initialHeading) {
        leftFrontMotor = hardwareMap.get(DcMotorEx.class, "leftFront");
        leftRearMotor = hardwareMap.get(DcMotorEx.class, "leftRear");
        rightRearMotor = hardwareMap.get(DcMotorEx.class, "rightRear");
        rightFrontMotor = hardwareMap.get(DcMotorEx.class, "rightFront");

        leftFrontMotor.setDirection(DcMotorSimple.Direction.REVERSE);
        leftRearMotor.setDirection(DcMotorSimple.Direction.FORWARD);
        rightRearMotor.setDirection(DcMotorSimple.Direction.REVERSE);
        rightFrontMotor.setDirection(DcMotorSimple.Direction.FORWARD);

        perpendicularEncoder = new Encoder(hardwareMap.get(DcMotorEx.class, "perpendicularEncoder"));
        parallelEncoder = new Encoder(hardwareMap.get(DcMotorEx.class, "parallelEncoder"));

        imu = hardwareMap.get(IMU.class, "imu");
        IMU.Parameters parameters = new IMU.Parameters(new RevHubOrientationOnRobot(
                Constants.LOGO_FACING_DIR, Constants.USB_FACING_DIR));
        imu.initialize(parameters);

        mecanumDrive = new Drive(leftFrontMotor, rightFrontMotor, leftRearMotor, rightRearMotor, imu);
        localizer = new Localizer(perpendicularEncoder, parallelEncoder, imu, initialPos, initialHeading);
    }

    public void setDriveSignal(Vector2 vel, Vector2 accel, double customkA, double customkV) {
        double[] velocities = Drive.directionToMotorPower(vel, 0);
        double[] accelerations = Drive.directionToMotorPower(accel, 0);
        ArrayList<Double> powers = calculateFeedforward(velocities, accelerations, customkA, customkV);
        setMotorPowers(powers.get(0), powers.get(1), powers.get(2), powers.get(3));
    }

    public void setDriveSignal(Vector2 vel, Vector2 accel) {
        setDriveSignal(vel, accel, Constants.kA, Constants.kV);
    }

    private ArrayList<Double> calculateFeedforward(double[] velocities, double[] accelerations, double kA, double kV) {
        ArrayList<Double> out = new ArrayList<>();
        for (int i = 0; i < velocities.length; i++) {
            out.add(kV * velocities[i] + kA * accelerations[i]);
        }
        return out;
    }

    private void setMotorPowers(double frontLeft, double backLeft, double frontRight, double backRight) {
        leftFrontMotor.setPower(frontLeft);
        leftRearMotor.setPower(backLeft);
        rightFrontMotor.setPower(frontRight);
        rightRearMotor.setPower(backRight);
    }

    public Localizer getLocalizer() { return localizer; }
}
