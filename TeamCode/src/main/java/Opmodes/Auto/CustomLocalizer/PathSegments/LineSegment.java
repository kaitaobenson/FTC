package Opmodes.Auto.CustomLocalizer.PathSegments;

import org.firstinspires.ftc.robotcore.external.Telemetry;

import Opmodes.Auto.CustomLocalizer.Constants;
import Util.Pose2D;

public class LineSegment extends LineSegmentConstantHeading {
    public LineSegment(Pose2D startPos, Pose2D endPos, Telemetry telemetry) {
        super(startPos, endPos, telemetry);
    }

    public void update() {
        super.update();

        currentPos.heading += currentVelocity.heading;

        double difference = endPos.heading - currentPos.heading;
        double distance = Math.abs(difference);
        if (difference < 0) {
            difference += 360;
        }
        double direction;
        if (difference > 180) {
            direction = 1;
        }
        else {
            direction = -1;
        }

        double currentDecelerationDistance = Math.pow(currentVelocity.heading / Constants.maxAngAccel, 2);
        if (distance < currentDecelerationDistance) {
            if (currentVelocity.heading < Constants.maxAngAccel) {
                currentAcceleration.heading = -currentVelocity.heading;
                currentVelocity.heading = 0;
                isFinished = true;
            }
            else {
                currentVelocity.heading -= Constants.maxAngAccel * direction;
                currentAcceleration.heading = Constants.maxAngAccel * -direction;
            }
        }
        else if (currentVelocity.heading < Constants.maxAngVel) {
            if (Constants.maxAngVel - currentVelocity.heading < Constants.maxAngAccel) {
                currentAcceleration.heading = Constants.maxAngVel - currentVelocity.heading;
                currentVelocity.heading = Constants.maxAngVel;
            }
            else {
                currentVelocity.heading += Constants.maxAngVel * direction;
                currentAcceleration.heading = Constants.maxAngAccel * direction;
            }
        }
    }
}
