package Opmodes;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

@TeleOp
public class BasicTest extends LinearOpMode
{
    private ElapsedTime runtime = new ElapsedTime();
    private DcMotor leftMotor = null;
    private DcMotor rightMotor = null;
    private Servo arm = null;

    static double armUp = 0.64;
    static double armLeft = 0.14;
    static double armRight = 1.14;

    public boolean isAprox(double num, double to, double margin) {
        if (to < num + margin && to > num - margin) {
            return true;
        }
        return false;
    }

    public double applyDeadzone(double val, double margin) {
        if (val < margin && val > -margin) {
            return 0;
        }
        return val;
    }

    @Override
    public void runOpMode()
    {
        // Initialize the hardware variables. Note that the strings used here must correspond
        // to the names assigned during the robot configuration step on the DS or RC devices.
        // Jank wrong motor side names.
        rightMotor = hardwareMap.get(DcMotor.class, "left_motor");
        leftMotor = hardwareMap.get(DcMotor.class, "right_motor");
        arm = hardwareMap.get(Servo.class, "stabber");

        leftMotor.setDirection(DcMotorSimple.Direction.REVERSE);
        leftMotor.setDirection(DcMotorSimple.Direction.REVERSE);

        // Wait for the game to start (driver presses START)
        telemetry.addData("Status", "Init");
        telemetry.update();

        waitForStart();
        runtime.reset();

        int armPosIndex = 0;
        boolean yPressedLastUpdate = false;

        while (opModeIsActive()) {
            double randomOffset = -0.05 + Math.random() * (0.1);

            // No deadzone for y because we want precise speed on moving forward and backward,
            // but we don't want to turn randomly while moving.
            double modified_control_x = applyDeadzone(this.gamepad1.left_stick_x, 0.2);
            double modified_control_y = this.gamepad1.left_stick_y;

            // Random offset prevents the motors from coasting when the input velocity from the
            // controller is zero ... this does make the robot produce a weird rattling sound.
            leftMotor.setPower(-modified_control_y + modified_control_x + randomOffset);
            rightMotor.setPower(-modified_control_y - modified_control_x - randomOffset);

            // Arm movement logic.
            boolean yJustPressed = false;

            if (this.gamepad1.y) {
                if (!yPressedLastUpdate) {
                    yJustPressed = true;
                }

                yPressedLastUpdate = true;
            }
            else {
                yPressedLastUpdate = false;
            }

            if (yJustPressed) {
                if (armPosIndex >= 3) {
                    armPosIndex = 0;
                }
                else {
                    armPosIndex++;
                }
            }

            if (armPosIndex == 0) {
                arm.setPosition(armLeft);
            }
            if (armPosIndex == 1 || armPosIndex == 3) {
                arm.setPosition(armUp);
            }
            if (armPosIndex == 2) {
                arm.setPosition(armRight);
            }

            // for testing specific values.
            //arm.setPosition(this.gamepad1.right_stick_x);

            telemetry.addData("Status", "Moving Servo!");
            telemetry.addData("Left stick x", modified_control_x);
            telemetry.addData("Left stick y", modified_control_y);
            telemetry.addData("Arm Position Index", armPosIndex);
            telemetry.update();
        }
    }
}

