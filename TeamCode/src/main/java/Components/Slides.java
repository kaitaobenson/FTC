package Components;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.HardwareMap;

public class Slides {

    public static final int minSlideTicks = 0;
    public static final int maxSlideTicks = 100;

    public DcMotor slideMotor = null;

    public Slides(HardwareMap hardwareMap) {
        slideMotor = hardwareMap.dcMotor.get("slideMotor");
    }

    public void moveSlides(double power) {
        if ((slideMotor.getCurrentPosition() < minSlideTicks && power < 0) ||
                (slideMotor.getCurrentPosition() > maxSlideTicks && power > 0)) {
            slideMotor.setPower(power);
        }
    }
}
