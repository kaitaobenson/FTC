package Opmodes.Auto.CustomLocalizer;

import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.Telemetry;

import java.util.ArrayList;

import Opmodes.Auto.CustomLocalizer.PathSegments.CodeSegment;
import Opmodes.Auto.CustomLocalizer.PathSegments.LineSegment;
import Opmodes.Auto.CustomLocalizer.PathSegments.LineSegmentConstantHeading;
import Opmodes.Auto.CustomLocalizer.PathSegments.PathSegment;
import Opmodes.Auto.CustomLocalizer.PathSegments.TurnSegment;
import Util.CustomCallable;
import Util.Pose2D;
import Util.Vector2;

// This path follower is dynamic meaning that it can be updated at runtime. The functionality relies
// on a path queue which empties out over time.
public class DynamicPathFollower {
    private static final double maxPathSegmentTime = 20000;
    PIDFFollower pointFollower;
    ArrayList<PathSegment> pathQueue = new ArrayList<>();
    boolean isSuspended = false;
    double timeStartedCurrentPathSegment = System.currentTimeMillis();
    Pose2D finalPos;
    private Telemetry telemetry;

    public DynamicPathFollower(HardwareMap hardwareMap, Vector2 initialPos, double initialHeading, Telemetry telemetry) {
        pointFollower = new PIDFFollower(hardwareMap, initialPos, initialHeading);
        finalPos = new Pose2D(initialPos.x, initialPos.y, initialHeading);
        this.telemetry = telemetry;
    }

    public void addSegmentToQueue(PathSegment segment) {
        pathQueue.add(segment);
    }

    public PathFollowerUpdateError update() {
        pointFollower.localizer.update();

        telemetry.addData("real x", pointFollower.localizer.getPos().x);
        telemetry.addData("real y", pointFollower.localizer.getPos().y);

        if (pathQueue.isEmpty() || isSuspended) {
            return PathFollowerUpdateError.NO_PATH;
        }

        finalPos = pathQueue.get(pathQueue.size() - 1).endPos;

        PathSegment currentSegment = pathQueue.get(0);
        currentSegment.update();

        pointFollower.updatePIDF(
                currentSegment.getPos().getVector(),
                currentSegment.getVel(),
                currentSegment.getAccel(), currentSegment.getPos().heading,
                currentSegment.getHeadingVel(),
                currentSegment.getHeadingAccel()
        );

        if (currentSegment.isFinished) {
            timeStartedCurrentPathSegment = System.currentTimeMillis();
            pathQueue.remove(0);
        }
        else if (System.currentTimeMillis() - timeStartedCurrentPathSegment > maxPathSegmentTime) {
            pathQueue.remove(0);
            return PathFollowerUpdateError.ERROR_TIMEOUT;
        }

        return PathFollowerUpdateError.SUCCESS;
    }

    private void updateFinalPos() {
        finalPos = pathQueue.get(pathQueue.size() - 1).endPos;
    }

    public void forward(double distance) {
        Vector2 endVector = finalPos.getVector().add(Vector2.fromAngle(finalPos.heading + Math.PI / 2.0).multiply(-distance));
        addPathSegment(new LineSegmentConstantHeading(finalPos, new Pose2D(endVector.x, endVector.y, finalPos.heading), telemetry));
    }

    public void back(double distance) {
        forward(-distance);
    }

    public void turn(double angle) {
        addPathSegment(new TurnSegment(finalPos.getVector(), finalPos.heading, finalPos.heading + angle));
    }

    public void function(CustomCallable call) {
        addPathSegment(new CodeSegment(finalPos, call));
    }

    public void suspendPathFollowing() {
        isSuspended = true;
    }

    public void resumePathFollowing() {
        isSuspended = false;
    }

    public void clearQueue() {
        pathQueue.clear();
    }

    public void addPathSegment(PathSegment toAdd) {
        pathQueue.add(toAdd);
        updateFinalPos();
    }

    public ArrayList<PathSegment> getQueue() {
        return pathQueue;
    }

    public void setQueue(ArrayList<PathSegment> newQueue) {
        pathQueue = newQueue;
    }
}
