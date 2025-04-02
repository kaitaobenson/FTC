package Opmodes.Auto.CustomLocalizer.PathSegments;

import Util.Pose2D;

public class WaitSegment extends PathSegment {
    private final double startTime;
    private final double time;

    // Time in seconds
    public WaitSegment(Pose2D pos, double time) {
        super(pos, pos);
        this.startTime = System.currentTimeMillis();
        this.time = time;
    }

    public void update() {
        if (System.currentTimeMillis() - startTime >= time * 1000) {
            isFinished = true;
        }
    }
}
