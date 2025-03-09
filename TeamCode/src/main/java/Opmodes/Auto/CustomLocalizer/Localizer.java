package Opmodes.Auto.CustomLocalizer;

import com.qualcomm.robotcore.hardware.IMU;

import org.firstinspires.ftc.robotcore.external.Const;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;

import Roadrunner.util.Encoder;
import Util.Vector2;

public class Localizer {
    private Encoder perpEncoder;
    private Encoder parallelEncoder;
    private IMU imu;

    private Vector2 pos;
    private int prevParallelTicks;
    private int prevPerpTicks;
    private double prevTheta;

    public Localizer(Encoder perpEncoder, Encoder parallelEncoder, IMU imu, Vector2 initialPos, double initialTheta) {
        this.perpEncoder = perpEncoder;
        this.parallelEncoder = parallelEncoder;
        this.pos = initialPos;
        this.prevTheta = initialTheta;
    }

    public void update() {
        Vector2 deltaTicks = new Vector2(perpEncoder.getCurrentPosition() - prevPerpTicks, parallelEncoder.getCurrentPosition() - prevParallelTicks);
        Vector2 deltaInches = deltaTicks.multiply(Constants.TICKS_PER_INCH);

        double theta = imu.getRobotYawPitchRollAngles().getYaw(AngleUnit.RADIANS);
        double averageTheta = (prevTheta + theta) / 2.0;
        double cosTheta = Math.cos(averageTheta);
        double sinTheta = Math.sin(averageTheta);

        Vector2 deltaGlobal = new Vector2(deltaInches.y * cosTheta + deltaInches.x * sinTheta, deltaInches.y * sinTheta - deltaInches.x * cosTheta);
        pos = pos.add(deltaGlobal);

        prevPerpTicks = perpEncoder.getCurrentPosition();
        prevParallelTicks = parallelEncoder.getCurrentPosition();
        prevTheta = theta;
    }

    public Vector2 getPos() {
        return pos;
    }

    public double getAngle() {
        return prevTheta;
    }
}
