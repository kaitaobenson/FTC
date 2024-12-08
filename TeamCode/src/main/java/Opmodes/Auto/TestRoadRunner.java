package Opmodes.Auto;

import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.acmerobotics.roadrunner.trajectory.Trajectory;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import Roadrunner.drive.SampleMecanumDrive;
import Roadrunner.trajectorysequence.TrajectorySequence;

@Autonomous(name = "TestRoadRunner", group = "Auto")
public class TestRoadRunner extends LinearOpMode {
    @Override
    public void runOpMode() {
        SampleMecanumDrive drive = new SampleMecanumDrive(hardwareMap);
        drive.setPoseEstimate(new Pose2d(-60, 0));

        TrajectorySequence myTrajectory = drive.trajectorySequenceBuilder(new Pose2d(-60, 0))
                .forward(5)
                .build();

        waitForStart();

        if(isStopRequested()) return;

        drive.followTrajectorySequence(myTrajectory);
    }
}