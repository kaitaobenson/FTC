package Components;

import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.robotcore.external.Telemetry;

public class Arm {
    Servo armServo;
    Servo clawServo;

    public Arm(Servo armServo, Servo clawServo) {
        this.armServo = armServo;
        this.clawServo = clawServo;
    }

    public void debugPosition(Telemetry telemetry) {
        telemetry.addData("Debug arm position", armServo.getPosition());
        telemetry.addData("Debug claw position", clawServo.getPosition());
    }

    public void moveArmUp() {

    }

    public void moveArmDown() {

    }

    public void moveArmSlightlyOver() {

    }

    public void openClaw() {
        clawServo.setPosition(1);
    }

    public void closeClaw() {
        clawServo.setPosition(0.58);
    }

    public void setArmPosition(double angle) {
        armServo.setPosition(angle);
    }
}
