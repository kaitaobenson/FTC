package Opmodes.Auto.CustomLocalizer.PathSegments;

import org.firstinspires.ftc.robotcore.external.Telemetry;

import Opmodes.Auto.CustomLocalizer.Constants;
import Util.Pose2D;
import Util.Vector2;

public class LineSegmentConstantHeading extends PathSegment {
    private final double maxAccelTime = Constants.maxVel / Constants.maxAccel;
    private Telemetry telemetry;
    private double lastTime;
    private boolean alreadySlowedDown = false;

    public LineSegmentConstantHeading(Pose2D startPos, Pose2D endPos, Telemetry telemetry) {
        super(startPos, endPos);
        this.currentPos = startPos;
        this.telemetry = telemetry;
        this.lastTime = System.currentTimeMillis() / 1000.0;
    }

    public void update() {
        double delta = (System.currentTimeMillis() / 1000.0) - lastTime;
        lastTime = System.currentTimeMillis() / 1000.0;

        if (currentPos == null) {
            currentPos = startPos;
        }

        currentPos.setVector(currentPos.getVector().add(currentVelocity.getVector().multiply(delta)));

        double currentSpeed = currentVelocity.getVector().length();
        Vector2 direction = endPos.getVector().subtract(currentPos.getVector()).normalized();

        double currentMaxAccelDistance = Math.pow(currentSpeed, 2) / (2 * Constants.maxAccel);
        double distance = currentPos.getVector().distanceTo(endPos.getVector());

        if (distance < 0.1) {
            currentVelocity.setVector(direction.multiply(0));
            currentAcceleration.setVector(direction.multiply(-currentSpeed * delta));
            isFinished = true;
            return;
        }

        // Slowing down
        if (distance <= currentMaxAccelDistance) {
            alreadySlowedDown = true;
            telemetry.addData("Slwoing down", 0);
            currentVelocity.setVector(direction.multiply(currentSpeed - Constants.maxAccel * delta));
            currentAcceleration.setVector(direction.multiply(-Constants.maxAccel * delta));
        }

        // Speeding up
        else if (currentSpeed < Constants.maxVel && !alreadySlowedDown) {
            telemetry.addData("Speeding up", 0);
            if (Constants.maxVel - currentSpeed < Constants.maxAccel * delta) {
                currentVelocity.setVector(direction.multiply(Constants.maxVel));
                currentAcceleration.setVector(direction.multiply(Constants.maxVel - currentSpeed));
            }
            else {
                currentVelocity.setVector(direction.multiply(currentSpeed + Constants.maxAccel * delta));
                currentAcceleration.setVector(direction.multiply(Constants.maxAccel * delta));
            }
        }

        else {
            telemetry.addData("straight", 0);
        }

        telemetry.addData("Distance", currentPos.getVector().x);
        telemetry.addData("distance y", currentPos.getVector().y);
        telemetry.addData("current max accel distance", currentMaxAccelDistance);
        telemetry.addData("delta", delta);
        telemetry.addData("velocity y", currentVelocity.y);
        telemetry.addData("velocity x", currentVelocity.x);
        telemetry.addData("accel y", currentAcceleration.y);
        telemetry.addData("accel x", currentAcceleration.x);
    }
}
