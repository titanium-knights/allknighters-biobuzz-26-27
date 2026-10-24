package org.firstinspires.ftc.teamcode.utilities;

import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.CRServo;

public class MagazineServos {
    public CRServo magServoL;
    public CRServo magServoR;
    public static double POWER = 0.9;

    public MagazineServos(HardwareMap hmap){
        magServoL = hmap.get(CRServo.class, "ServoL");
        magServoR = hmap.get(CRServo.class, "ServoR");
    }

    public void run()        { set(POWER); }
    public void outtake()    { set(-POWER); }
    public void stopIntake() { set(0); }

    /** Servos face each other, so they run opposite to move game pieces the same way. */
    private void set(double p) {
        magServoL.setPower(-p);
        magServoR.setPower(p);
    }
}