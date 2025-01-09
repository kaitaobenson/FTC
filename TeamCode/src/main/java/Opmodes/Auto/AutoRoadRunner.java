package Opmodes.Auto;

import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.acmerobotics.roadrunner.geometry.Vector2d;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;

import Components.Arm;
import Components.Drive;
import Components.Hang;
import Components.Slides;
import Roadrunner.drive.SampleMecanumDrive;
import Roadrunner.trajectorysequence.TrajectorySequence;

@Autonomous(name = "AutoRoadRunner", group = "Auto")
public class AutoRoadRunner extends LinearOpMode {
    static Vector2d one = new Vector2d(0, -5);
    static Vector2d two = new Vector2d(-5, 10);

    private enum STATE_ENUM {
        MOVING,
        RAISING_SLIDES_TO_BAR,
        ATTACHING_SPECIMEN,
        LOWERING_SLIDES,
        LOWERING_ARM_TO_SPECIMEN,
        GRABBING_SPECIMEN,
        RAISING_ARM_WITH_SPECIMEN,
        RAISING_SLIDES_TO_BASKET,
        LOWERING_ARM,
        DROPPING_SPECIMEN,
    }

    private STATE_ENUM state = STATE_ENUM.MOVING;
    private Slides slides;
    private Arm arm;

    @Override
    public void runOpMode() {

        // Slides config
        DcMotor slideMotor = hardwareMap.dcMotor.get("slidesMotor");

        slides = new Slides(slideMotor);

        Servo armServo = hardwareMap.servo.get("armServo");
        Servo clawServo = hardwareMap.servo.get("clawServo");

        arm = new Arm(armServo, clawServo);

        SampleMecanumDrive drive = new SampleMecanumDrive(hardwareMap);
        drive.setPoseEstimate(new Pose2d(0, -60, 1.5708));
        TrajectorySequence myTrajectory = drive.trajectorySequenceBuilder(new Pose2d(0, -60, 1.5708))
                .addDisplacementMarker(() -> {state = STATE_ENUM.RAISING_SLIDES_TO_BAR;})
                .forward(30)
                .waitSeconds(1)
                .addDisplacementMarker(() -> {state = STATE_ENUM.ATTACHING_SPECIMEN;})
                .back(2)
                .strafeLeft(1)
                .splineTo(new Vector2d(-49, -39), 3.14)
                .addDisplacementMarker(() -> {state = STATE_ENUM.LOWERING_ARM_TO_SPECIMEN;})
                .forward(0.1)
                .waitSeconds(0.6)
                .back(0.1)
                .addDisplacementMarker(() -> {state = STATE_ENUM.GRABBING_SPECIMEN;})
                .forward(0.1)
                .waitSeconds(0.4)
                .back(0.13)
                .addDisplacementMarker(() -> {state = STATE_ENUM.RAISING_ARM_WITH_SPECIMEN;})
                .turn(1.5708)
                .forward(1)
                .splineTo(new Vector2d(-57, -53), -2.35619449019)
                .addTemporalMarker(15, () -> {state = STATE_ENUM.RAISING_SLIDES_TO_BASKET;})
                .addTemporalMarker(18.5, () -> {state = STATE_ENUM.DROPPING_SPECIMEN;})
                .waitSeconds(3)
                .build();


        waitForStart();

        drive.followTrajectorySequenceAsync(myTrajectory);
        arm.closeClaw();
        arm.moveArmUp();

        while (!isStopRequested()) {
            drive.update();

            if (state == STATE_ENUM.RAISING_SLIDES_TO_BAR) {
                telemetry.addData("AUTO STATE", "Raising slides to bar");
                telemetry.addData("above high bar", slides.isAboveHighBar());
                if (!slides.isAboveHighBar()) {
                    slides.moveTowardHighBar();
                }
                else {
                    slides.moveSlides(0);
                }

            }
            if (state == STATE_ENUM.ATTACHING_SPECIMEN) {
                telemetry.addData("AUTO STATE", "Attaching specimen");
                // Maybe move the arm forward if necessary.
                slides.moveSlides(0.6);
            }
            if (state == STATE_ENUM.LOWERING_SLIDES) {
                telemetry.addData("AUTO STATE", "Lowering slides");
                if (!slides.isAtBottom()) {
                    slides.moveSlides(1);
                }
            }
            if (state == STATE_ENUM.LOWERING_ARM_TO_SPECIMEN) {
                telemetry.addData("AUTO STATE", "Lowering arm to specimen");
                arm.openClaw();
                arm.moveArmDown();
            }
            if (state == STATE_ENUM.GRABBING_SPECIMEN) {
                telemetry.addData("AUTO STATE", "Grabbing specimen");
                arm.closeClaw();
            }
            if (state == STATE_ENUM.RAISING_ARM_WITH_SPECIMEN) {
                telemetry.addData("AUTO STATE", "Raising arm with specimen");
                arm.moveArmUp();
            }
            if (state == STATE_ENUM.RAISING_SLIDES_TO_BASKET) {
                telemetry.addData("AUTO STATE", "Raising slides to basket");
                if (slides.isAtTop()) {
                    state = STATE_ENUM.LOWERING_ARM;
                }
                else {
                    slides.moveSlides(-1);
                }
            }
            if (state == STATE_ENUM.LOWERING_ARM) {
                telemetry.addData("AUTO STATE", "Lowering arm");
                arm.moveArmSlightlyOver();
            }
            if (state == STATE_ENUM.DROPPING_SPECIMEN) {
                telemetry.addData("AUTO STATE", "Dropping specimen");
                arm.openClaw();
            }

            telemetry.update();
        }
    }
}
