package Opmodes.Auto;

import com.acmerobotics.dashboard.FtcDashboard;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;

import org.firstinspires.ftc.robotcore.external.Telemetry;

import java.util.ArrayList;

import Components.AprilTagCompute;
import Util.Vector2;
import Util.Vector3;

@Autonomous(name = "AprilTagDetection", group="Demo")
public class AprilTagDetection extends OpMode {
    AprilTagCompute compute;

    @Override
    public void init() {
        compute = new AprilTagCompute(hardwareMap, FtcDashboard.getInstance().getTelemetry());
    }

    @Override
    public void loop() {
        ArrayList<org.openftc.apriltag.AprilTagDetection> detections = compute.getAprilTagDetections();
    }
}
