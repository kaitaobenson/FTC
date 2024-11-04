package Opmodes.Auto;

import com.acmerobotics.dashboard.FtcDashboard;
import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.IMU;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.AxesOrder;
import org.firstinspires.ftc.robotcore.external.navigation.AxesReference;
import org.openftc.easyopencv.OpenCvWebcam;

import Components.Drive;
import Components.FollowBlock;
import Opmodes.Auto.Pipelines.EdgeDetectionProcessPipeline;

@Autonomous(name = "AutoFollowBlock", group = "Demo")
public class AutoFollowBlock extends OpMode {

    IMU imu;
    Drive drive;
    EdgeDetectionProcessPipeline pipeline;
    FollowBlock followBlock;
    double startRotation;

    @Override
    public void init() {
        imu = hardwareMap.get(IMU.class, "imu");

        IMU.Parameters parameters = new IMU.Parameters(new RevHubOrientationOnRobot(
                RevHubOrientationOnRobot.LogoFacingDirection.UP,
                RevHubOrientationOnRobot.UsbFacingDirection.FORWARD));

        imu.initialize(parameters);

        startRotation = imu.getRobotOrientation(AxesReference.EXTRINSIC, AxesOrder.XYZ, AngleUnit.DEGREES).thirdAngle;

        DcMotor frontLeftMotor = hardwareMap.dcMotor.get("frontLeftMotor");
        DcMotor frontRightMotor = hardwareMap.dcMotor.get("frontRightMotor");
        DcMotor backLeftMotor = hardwareMap.dcMotor.get("backLeftMotor");
        DcMotor backRightMotor = hardwareMap.dcMotor.get("backRightMotor");

        drive = new Drive(frontLeftMotor, frontRightMotor, backLeftMotor, backRightMotor);
        followBlock = new FollowBlock("Webcam", FtcDashboard.getInstance().getTelemetry(), hardwareMap);
    }

    @Override
    public void loop() {
        // We probably don't need all this debug.
        telemetry.addData("Frame Count", followBlock.webcam.getFrameCount());
        telemetry.addData("FPS", String.format("%.2f", followBlock.webcam.getFps()));
        telemetry.addData("Total frame time ms", followBlock.webcam.getTotalFrameTimeMs());
        telemetry.addData("Pipeline time ms", followBlock.webcam.getPipelineTimeMs());
        telemetry.addData("Overhead time ms", followBlock.webcam.getOverheadTimeMs());
        telemetry.addData("Theoretical max FPS", followBlock.webcam.getCurrentPipelineMaxFps());
        telemetry.update();

        float currentRotation = imu.getRobotOrientation(AxesReference.EXTRINSIC, AxesOrder.XYZ, AngleUnit.DEGREES).thirdAngle;

        float rotationCorrection = 0;
        if (currentRotation < startRotation - 0.2) {
            rotationCorrection = -0.05f;
        }
        if (currentRotation > startRotation + 0.2) {
            rotationCorrection = 0.05f;
        }

        drive.moveInDirection(followBlock.getMovementDirection(), rotationCorrection, 1f);
    }
}
