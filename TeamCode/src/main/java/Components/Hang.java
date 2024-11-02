package Components;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Servo;

import Util.Vector2;

public class Hang {

    public DcMotor leftMotor = null;
    public DcMotor rightMotor = null;
    public Servo leftServo = null;
    public Servo rightServo = null;

    public Hang(DcMotor leftMotor, DcMotor rightMotor, Servo leftServo, Servo rightServo) {
        this.leftMotor = leftMotor;
        this.rightMotor = rightMotor;
        this.leftServo = leftServo;
        this.rightServo = rightServo;

        leftMotor.setDirection(DcMotorSimple.Direction.FORWARD);
        rightMotor.setDirection(DcMotorSimple.Direction.FORWARD);

    }

    public void armsUp() {
        this.leftServo.setPosition(1);
        this.rightServo.setPosition(0.4);
    }

    public void goUp(double power) {
        setBothMotors(-power);
    }

    public void goDown(double power) {
        setBothMotors(power);
    }

    public void stop() {
        setBothMotors(0);
    }

    private void setBothMotors(double power) {
        this.leftMotor.setPower(power);
        this.rightMotor.setPower(power);
    }

    public void setBothServos(double angle) {
        this.leftServo.setPosition(angle);
        this.rightServo.setPosition(angle);
    }



}
