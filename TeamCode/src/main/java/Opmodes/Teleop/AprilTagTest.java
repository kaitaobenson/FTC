package Opmodes.Teleop;

import android.util.Size;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.robotcore.external.hardware.camera.CameraName;
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.vision.VisionPortal;
import org.firstinspires.ftc.vision.apriltag.AprilTagDetection;
import org.firstinspires.ftc.vision.apriltag.AprilTagProcessor;

@TeleOp(name = "AprilTags", group = "Testing")
public class AprilTagTest extends LinearOpMode {

    @Override
    public void runOpMode() {
        CameraName camera = hardwareMap.get(WebcamName.class, "Webcam 1");

        AprilTagProcessor tagProcessor = new AprilTagProcessor.Builder()
                .setDrawAxes(true)
                .setDrawCubeProjection(true)
                .setDrawTagID(true)
                .setDrawTagOutline(true)
                .build();

        VisionPortal visionPortal = new VisionPortal.Builder()
                .addProcessor(tagProcessor)
                .setCamera(camera)
                .setCameraResolution(new Size(640, 480))
                .build();

        waitForStart();

        while (opModeIsActive() && !isStopRequested()) {
            int i = 0;
            for (AprilTagDetection tag : tagProcessor.getDetections()) {
                telemetry.addData("x" + i, tag.ftcPose.x);
                telemetry.addData("y" + i, tag.ftcPose.y);
                telemetry.addData("z" + i, tag.ftcPose.z);
                telemetry.addData("roll" + i, tag.ftcPose.roll);
                telemetry.addData("pitch" + i, tag.ftcPose.pitch);
                telemetry.addData("yaw" + i, tag.ftcPose.yaw);
            }
        }

        telemetry.update();
    }

}
