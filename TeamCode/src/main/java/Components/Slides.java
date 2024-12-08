package Components;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.HardwareMap;

public class Slides {

    public static final int minSlideTicks = 0;
    public static final int maxSlideTicks = 100;

    public DcMotor slideMotor = null;

    public Slides(DcMotor slideMotor) {
        this.slideMotor = slideMotor;
    }

    public void moveSlides(double power) {
        slideMotor.setPower(power);
        if ((slideMotor.getCurrentPosition() < minSlideTicks && power < 0) ||
                (slideMotor.getCurrentPosition() > maxSlideTicks && power > 0)) {
            slideMotor.setPower(power);
        }
    }

    public boolean isAtTop() {
        return slideMotor.getCurrentPosition() > maxSlideTicks;
    }

    public boolean isAtBottom() {
        return slideMotor.getCurrentPosition() < minSlideTicks;
    }

    public boolean isAboveHighBar() {
        return false;
    }

    public boolean isAboveHighBasket() {
        return false;
    }

    public void moveTowardHighBar() {

    }

    public void moveTowardHighBasket() {

    }
}
