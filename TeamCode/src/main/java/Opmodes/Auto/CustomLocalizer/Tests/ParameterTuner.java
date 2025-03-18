package Opmodes.Auto.CustomLocalizer.Tests;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import Opmodes.Auto.CustomLocalizer.Constants;
import Opmodes.Auto.CustomLocalizer.CustomPathFollower;
import Util.Vector2;

@Autonomous(name = "ParameterTuner", group = "Auto")
public class ParameterTuner extends LinearOpMode {
    CustomPathFollower pathFollower;

    Vector2 previousPosition;
    double previousVelocity;
    double previousTime;

    double customkV = 0;
    double customkA = 0;

    boolean successVel = false;
    boolean successAccel = false;

    @Override
    public void runOpMode() {
        pathFollower = new CustomPathFollower(hardwareMap, new Vector2(0, 0), 0);

        waitForStart();

        while (!isStopRequested() && !successVel) {
            double deltaTime = Math.abs(time - previousTime);

            Vector2 current_position = pathFollower.getLocalizer().getPos();
            double deltaMovement = Math.abs(current_position.y - previousPosition.y);
            double velocity = deltaMovement * deltaTime; // in/s

            if (Math.abs(velocity - Constants.testMaxVel) < 0.02) {
                successVel = true;
                continue;
            }

            if (velocity < Constants.testMaxVel) {
                customkV += 0.001;
            }
            if (velocity > Constants.testMaxVel) {
                customkV -= 0.001;
            }

            previousPosition = current_position;
            previousTime = time;
            pathFollower.updateFeedforwardOnly(new Vector2(0, Constants.testMaxVel), new Vector2(0, 0), 0, 0, customkV, customkA);

            telemetry.addData("Status", "Tuning kV");
            telemetry.addData("Current kV", customkV);
        }

        previousVelocity = 0;
        previousPosition = new Vector2(0, 0);

        while (!isStopRequested() && !successAccel) {
            double deltaTime = Math.abs(time - previousTime);

            Vector2 current_position = pathFollower.getLocalizer().getPos();
            double deltaMovement = Math.abs(current_position.y - previousPosition.y);
            double current_velocity = deltaMovement * deltaTime; // in/s
            double acceleration = Math.abs(current_velocity - previousVelocity) * deltaTime;

            if (Math.abs(acceleration - Constants.testMaxAccel) < 0.02) {
                successAccel = true;
                continue;
            }
            if (current_velocity > Constants.testMaxVel) {
                telemetry.addLine("Reached max velocity while tuning max acceleration. Retune likely necessary.");
                successAccel = true;
                continue;
            }

            if (acceleration < Constants.testMaxAccel) {
                customkA += 0.001;
            }
            if (acceleration < Constants.testMaxAccel) {
                customkA -= 0.001;
            }

            previousPosition = current_position;
            previousVelocity = current_velocity;
            previousTime = time;
            pathFollower.updateFeedforwardOnly(new Vector2(0, current_velocity), new Vector2(0, Constants.testMaxAccel), 0, 0, customkV, customkA);

            telemetry.addData("Status", "Tuning kA");
            telemetry.addData("Current kA", customkA);
        }
    }
}
