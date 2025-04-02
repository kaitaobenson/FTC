package Opmodes.Auto.CustomLocalizer.PathSegments;

import Opmodes.Auto.CustomLocalizer.Constants;
import Util.Pose2D;
import Util.Vector2;

public class TurnSegment extends PathSegment {
    public TurnSegment(Vector2 pos, double startHeading, double endHeading) {
        super(new Pose2D(pos.x, pos.y, startHeading), new Pose2D(pos.x, pos.y, endHeading));
    }

    public void update() {
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
