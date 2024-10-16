package Components;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;

public class Intake {
    public enum IntakeStatus {
        IN, OUT, STOPPED
    }
    public DcMotor intakeMotor = null;

    public Intake(HardwareMap hardwareMap) {
        intakeMotor = hardwareMap.dcMotor.get("intakeMotor");
    }

    public void moveIntake(float power) {
        intakeMotor.setPower(power);
    }

    public IntakeStatus getIntakeStatus() {
        IntakeStatus intakeStatus = IntakeStatus.STOPPED;
        if (intakeMotor.getPower() < 0) {
            intakeStatus = IntakeStatus.IN;
        } else if (intakeMotor.getPower() > 0) {
            intakeStatus = IntakeStatus.OUT;
        }
        return intakeStatus;
    }
}