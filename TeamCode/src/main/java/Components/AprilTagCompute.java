package Components;

import android.sax.StartElementListener;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.AxesOrder;
import org.firstinspires.ftc.robotcore.external.navigation.AxesReference;
import org.firstinspires.ftc.robotcore.external.navigation.Orientation;
import org.firstinspires.ftc.vision.apriltag.AprilTagPoseFtc;
import org.openftc.apriltag.AprilTagDetection;
import org.openftc.easyopencv.OpenCvCamera;
import org.openftc.easyopencv.OpenCvCameraFactory;
import org.openftc.easyopencv.OpenCvCameraRotation;

import java.util.ArrayList;

import Opmodes.Auto.Pipelines.AprilTagDetectionPipeline;
import Util.Vector3;

@Config
public class AprilTagCompute {
    OpenCvCamera camera;
    AprilTagDetectionPipeline pipeline;

    static final double FEET_PER_METER = 3.28084;

//    size="640 480"
//    focalLength="821.993f, 821.993f"
//    principalPoint="330.489f, 248.997f"
//    distortionCoefficients="-0.018522, 1.03979, 0, 0, -3.3171, 0, 0, 0"

    // Lens intrinsics for C920 at 640x480
    public static double fx = 580.0;    // Typical for C920 at 640x480
    public static double fy = 580.0;    // Assuming same focal length for y-axis
    public static double cx = 320.0;    // Approximate center of the 640x480 image
    public static double cy = 240.0;    // Approximate center of the 640x480 image

    // Meters
    double tagsize = 0.1016;

    int numFramesWithoutDetection = 0;

    final float DECIMATION_HIGH = 3;
    final float DECIMATION_LOW = 2;
    final float THRESHOLD_HIGH_DECIMATION_RANGE_METERS = 1.0f;
    final int THRESHOLD_NUM_FRAMES_NO_DETECTION_BEFORE_LOW_DECIMATION = 4;

    public boolean lastFoundValidAprilTag;

    Telemetry telemetry;

    public AprilTagCompute(HardwareMap hardwareMap, Telemetry current_telemetry) {
        telemetry = current_telemetry;

        int cameraMonitorViewId = hardwareMap.appContext.getResources().getIdentifier("cameraMonitorViewId", "id", hardwareMap.appContext.getPackageName());
        camera = OpenCvCameraFactory.getInstance().createWebcam(hardwareMap.get(WebcamName.class, "Webcam"), cameraMonitorViewId);
        pipeline = new AprilTagDetectionPipeline(tagsize, fx, fy, cx, cy);

        camera.setPipeline(pipeline);
        camera.openCameraDeviceAsync(new OpenCvCamera.AsyncCameraOpenListener() {
            @Override
            public void onOpened() {
                camera.startStreaming(640, 480, OpenCvCameraRotation.UPRIGHT);
            }

            @Override
            public void onError(int errorCode) {

            }
        });

        FtcDashboard.getInstance().startCameraStream(camera, 0);
    }

    public ArrayList<AprilTagDetection> getAprilTagDetections() {
        ArrayList<AprilTagDetection> detections = pipeline.getDetectionsUpdate();

        if (detections != null) {
            lastFoundValidAprilTag = true;

            telemetry.addData("FPS", camera.getFps());
            telemetry.addData("Overhead ms", camera.getOverheadTimeMs());
            telemetry.addData("Pipeline ms", camera.getPipelineTimeMs());

            if (detections.size() == 0)
            {
                numFramesWithoutDetection++;

                if (numFramesWithoutDetection >= THRESHOLD_NUM_FRAMES_NO_DETECTION_BEFORE_LOW_DECIMATION) {
                    pipeline.setDecimation(DECIMATION_LOW);
                }
            }
            else
            {
                numFramesWithoutDetection = 0;

                if (detections.get(0).pose.z < THRESHOLD_HIGH_DECIMATION_RANGE_METERS)
                {
                    pipeline.setDecimation(DECIMATION_HIGH);
                }

                for (AprilTagDetection detection : detections)
                {
                    Orientation rot = Orientation.getOrientation(detection.pose.R, AxesReference.INTRINSIC, AxesOrder.YXZ, AngleUnit.DEGREES);

                    telemetry.addLine(String.format("\nDetected tag ID=%d", detection.id));
                    telemetry.addLine(String.format("Translation X: %.2f feet", detection.pose.x*FEET_PER_METER));
                    telemetry.addLine(String.format("Translation Y: %.2f feet", detection.pose.y*FEET_PER_METER));
                    telemetry.addLine(String.format("Translation Z: %.2f feet", detection.pose.z*FEET_PER_METER));
                    telemetry.addLine(String.format("Rotation Yaw: %.2f degrees", rot.firstAngle));
                    telemetry.addLine(String.format("Rotation Pitch: %.2f degrees", rot.secondAngle));
                    telemetry.addLine(String.format("Rotation Roll: %.2f degrees", rot.thirdAngle));

                    Vector3 position = new Vector3(detection.pose.x, detection.pose.y, detection.pose.z).multiply(FEET_PER_METER);
                    position.rotate(rot.firstAngle, rot.secondAngle);
                }
            }

            telemetry.update();
        }
        else
        {
            lastFoundValidAprilTag = false;
        }

        return detections;
    }
}
