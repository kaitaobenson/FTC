package Opmodes.Auto;

import com.acmerobotics.dashboard.FtcDashboard;
import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.IMU;

import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.openftc.easyopencv.OpenCvCamera;
import org.openftc.easyopencv.OpenCvCameraFactory;
import org.openftc.easyopencv.OpenCvCameraRotation;
import org.openftc.easyopencv.OpenCvWebcam;

import Components.Drive;
import Util.Vector2;

@Autonomous(name = "AutoFollowBlock", group = "Auto")
public class AutoFollowBlock extends OpMode {

    IMU imu;
    Drive drive;
    OpenCvWebcam webcam;
    EdgeDetectionProcessPipeline pipeline;

    @Override
    public void init() {
        IMU imu = hardwareMap.get(IMU.class, "imu");

        IMU.Parameters parameters = new IMU.Parameters(new RevHubOrientationOnRobot(
                RevHubOrientationOnRobot.LogoFacingDirection.UP,
                RevHubOrientationOnRobot.UsbFacingDirection.FORWARD));

        imu.initialize(parameters);

        DcMotor frontLeftMotor = hardwareMap.dcMotor.get("frontLeftMotor");
        DcMotor frontRightMotor = hardwareMap.dcMotor.get("frontRightMotor");
        DcMotor backLeftMotor = hardwareMap.dcMotor.get("backLeftMotor");
        DcMotor backRightMotor = hardwareMap.dcMotor.get("backRightMotor");

        drive = new Drive(frontLeftMotor, frontRightMotor, backLeftMotor, backRightMotor);

        // Webcam tests
        String packageName = hardwareMap.appContext.getPackageName();
        int cameraMonitorViewId = hardwareMap.appContext.getResources().getIdentifier("cameraMonitorViewId", "id", packageName);

        pipeline = new EdgeDetectionProcessPipeline();

        webcam = OpenCvCameraFactory.getInstance().createWebcam(hardwareMap.get(WebcamName.class, "Webcam"), cameraMonitorViewId);
        webcam.setPipeline(pipeline);
        webcam.setMillisecondsPermissionTimeout(5000);
        webcam.openCameraDeviceAsync(new OpenCvCamera.AsyncCameraOpenListener() {
            @Override
            public void onOpened() {
                webcam.startStreaming(320, 240, OpenCvCameraRotation.UPRIGHT);
            }

            @Override
            public void onError(int errorCode) {
                telemetry.addLine("Error: " + errorCode);
            }
        });

        FtcDashboard.getInstance().startCameraStream(webcam, 0);

        telemetry.addLine("Waiting for start");
        telemetry.update();
    }

    @Override
    public void loop() {
        // We probably don't need all this debug.
        telemetry.addData("Frame Count", webcam.getFrameCount());
        telemetry.addData("FPS", String.format("%.2f", webcam.getFps()));
        telemetry.addData("Total frame time ms", webcam.getTotalFrameTimeMs());
        telemetry.addData("Pipeline time ms", webcam.getPipelineTimeMs());
        telemetry.addData("Overhead time ms", webcam.getOverheadTimeMs());
        telemetry.addData("Theoretical max FPS", webcam.getCurrentPipelineMaxFps());
        telemetry.addData("Center Offset", pipeline.center);
        telemetry.update();

        if (pipeline.center < -5) {
            drive.moveInDirection(new Vector2(-1, 0), 0, 0.4f);
        }
        else if (pipeline.center > 5) {
            drive.moveInDirection(new Vector2(1, 0), 0, 0.4f);
        }
        else {
            drive.moveInDirection(new Vector2(0, 0), 0, 0.4f);
        }
    }
}
