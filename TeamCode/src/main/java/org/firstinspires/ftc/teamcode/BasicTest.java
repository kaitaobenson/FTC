package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

@TeleOp
public class BasicTest extends LinearOpMode
{
    private ElapsedTime runtime = new ElapsedTime();
    private DcMotor leftMotor = null;
    private DcMotor rightMotor = null;
    private Servo arm = null;

    static double arm_up = 0.64;

    @Override
    public void runOpMode()
    {
        // Initialize the hardware variables. Note that the strings used here must correspond
        // to the names assigned during the robot configuration step on the DS or RC devices.
        // Jank wrong motor side names.
        rightMotor = hardwareMap.get(DcMotor.class, "left_motor");
        leftMotor = hardwareMap.get(DcMotor.class, "right_motor");
        arm = hardwareMap.get(Servo.class, "stabber");

        leftMotor.setDirection(DcMotor.Direction.REVERSE);
        rightMotor.setDirection(DcMotor.Direction.REVERSE);

        // Wait for the game to start (driver presses START)
        telemetry.addData("Status", "Init");
        telemetry.update();

        waitForStart();
        runtime.reset();

        while (opModeIsActive()) {
            // This is inverted from what I'd expect, but the values are definetely correct.
            leftMotor.setPower(this.gamepad1.left_stick_x - this.gamepad1.left_stick_y);
            rightMotor.setPower(this.gamepad1.left_stick_x + this.gamepad1.left_stick_y);

            if (this.gamepad1.y) {
                arm.setPosition(arm_up);
            }

            // for testing specific values.
            //arm.setPosition(this.gamepad1.right_stick_x);

            telemetry.addData("Status", "Moving Servo!");
            telemetry.addData("Left stick x", this.gamepad1.left_stick_x);
            telemetry.addData("Left stick y", this.gamepad1.left_stick_y);
            telemetry.addData("Right Trigger", this.gamepad1.right_trigger);
            telemetry.update();
        }
    }
}

