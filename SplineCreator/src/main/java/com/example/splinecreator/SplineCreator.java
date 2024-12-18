package com.example.splinecreator;

import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.acmerobotics.roadrunner.geometry.Vector2d;

import org.rowlandhall.meepmeep.MeepMeep;
import org.rowlandhall.meepmeep.roadrunner.DefaultBotBuilder;
import org.rowlandhall.meepmeep.roadrunner.entity.RoadRunnerBotEntity;

public class SplineCreator {
    public static void main(String[] args) {
        MeepMeep meepMeep = new MeepMeep(800);

        RoadRunnerBotEntity myBot = new DefaultBotBuilder(meepMeep)
                // Set bot constraints: maxVel, maxAccel, maxAngVel, maxAngAccel, track width
                .setConstraints(30, 20, 22, 3, 11)
                .followTrajectorySequence(drive -> drive.trajectorySequenceBuilder(new Pose2d(0, -60, 1.5708))
                        .forward(27)
                        .waitSeconds(1)
                        .forward(1)
                        .waitSeconds(2)
                        .back(1)
                        .strafeRight(1)
                        .splineTo(new Vector2d(48, -40), 0)
                        .waitSeconds(1)
                        .turn(1.5708)
                        .forward(1)
                        .splineTo(new Vector2d(-56, -54), -2.35619449019)
                        .waitSeconds(3)
                        .build()
                );


        meepMeep.setBackground(MeepMeep.Background.FIELD_INTOTHEDEEP_JUICE_DARK)
                .setDarkMode(true)
                .setBackgroundAlpha(0.95f)
                .addEntity(myBot)
                .start();
    }
}