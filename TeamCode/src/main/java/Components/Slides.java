package Components;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.HardwareMap;

public class Slides {

    public static final int minSlideTicks = 0;
    public static final int maxSlideTicks = -2000;
    public static final int topBarHeight = -1300;

    public DcMotor slideMotor = null;

    public Slides(DcMotor slideMotor) {
        this.slideMotor = slideMotor;
    }

    public void moveSlides(double power) {
        slideMotor.setPower(power);
        if (!((slideMotor.getCurrentPosition() < minSlideTicks && power < 0) ||
                (slideMotor.getCurrentPosition() > maxSlideTicks && power > 0))) {
            slideMotor.setPower(power);
        }
    }

    public boolean isAtTop() {
        return slideMotor.getCurrentPosition() < maxSlideTicks;
    }

    public boolean isAtBottom() {
        return slideMotor.getCurrentPosition() > minSlideTicks;
    }

    public boolean isAboveHighBar() {
        return slideMotor.getCurrentPosition() < topBarHeight;
    }

    public boolean isAboveHighBasket() {
        return false;
    }

    public void moveTowardHighBar() {
        if (!isAboveHighBar()) {
            moveSlides(-1);
        }
    }

    public void moveTowardHighBasket() {

    }
}
