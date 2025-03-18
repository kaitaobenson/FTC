package Opmodes.Auto.CustomLocalizer;

import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.IMU;

import Components.Drive;
import Roadrunner.util.Encoder;
import Util.Vector2;
import Util.Pose2D;

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

    PIDAxis xPID;
    PIDAxis yPID;
    PIDAxis headingPID;

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

        xPID = new PIDAxis(Constants.P, Constants.I, Constants.D);
        yPID = new PIDAxis(Constants.P, Constants.I, Constants.D);
        headingPID = new PIDAxis(Constants.headingP, Constants.headingI, Constants.headingD);
    }

    public void updatePIDF(Vector2 pos, Vector2 vel, Vector2 accel, double headingPos, double headingVel, double headingAccel, double customkA, double customkV) {
        Vector2 realPos = localizer.getPos();
        double realHeading = localizer.getAngle();
        Pose2D feedforward = calculateFeedforward(vel, accel, headingVel, headingAccel, customkA, customkV);
        Pose2D totalOffset = new Pose2D(
                xPID.calculate(pos.x, realPos.x) + feedforward.x,
                yPID.calculate(pos.y, realPos.y) + feedforward.y,
                headingPID.calculate(headingPos, realHeading) + feedforward.heading
        );
        setMotorPowers(Drive.poseToMotorPower(totalOffset));
    }

    public void updateFeedforwardOnly(Vector2 vel, Vector2 accel, double headingVel, double headingAccel, double customkA, double customkV) {
        setMotorPowers(Drive.poseToMotorPower(calculateFeedforward(vel, accel, headingVel, headingAccel, customkA, customkV)));
    }

    public void updateFeedforwardOnly(Vector2 vel, Vector2 accel, double headingVel, double headingAccel) {
        updateFeedforwardOnly(vel, accel, headingVel, headingAccel, Constants.kA, Constants.kV);
    }

    public Pose2D calculateFeedforward(Vector2 vel, Vector2 accel, double headingVel, double headingAccel, double customkA, double customkV) {
        double xFF = calculateFeedforwardAxis(vel.x, accel.x, customkA, customkV);
        double yFF = calculateFeedforwardAxis(vel.y, accel.x, customkA, customkV);
        double headingFF = calculateFeedforwardAxis(headingVel, headingAccel, customkA, customkV);
        return new Pose2D(xFF, yFF, headingFF);
    }

    private double calculateFeedforwardAxis(double vel, double accel, double kV, double kA) {
        return vel * kV + accel * kA;
    }

    private void setMotorPowers(double[] powers) {
        leftFrontMotor.setPower(powers[0]);
        leftRearMotor.setPower(powers[1]);
        rightFrontMotor.setPower(powers[2]);
        rightRearMotor.setPower(powers[3]);
    }

    public Localizer getLocalizer() { return localizer; }
}
