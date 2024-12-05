package Components;

import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.robotcore.external.Telemetry;

public class Arm {
    Servo armServo;

    public Arm(Servo armServo) {
        this.armServo = armServo;
    }

    public void debugPosition(Telemetry telemetry) {
        telemetry.addData("Debug arm position", armServo.getPosition());
    }

    public void moveArmUp() {

    }
}
