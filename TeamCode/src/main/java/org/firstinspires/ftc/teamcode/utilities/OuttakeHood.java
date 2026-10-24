package org.firstinspires.ftc.teamcode.utilities;

import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

public class OuttakeHood {
    public Servo hoodServo;
    public static double INIT_POS = 0.625;      // TODO: tune. Hood goes here at Init
    public static double BELT_SAFE_MIN = 0.55; // belt may only run when hood is at or above this
    public static double POLLEN_POS = -1;      // TODO: measure. -1 = not set, flapPollen() does nothing
    public static double NECTAR_POS = -1;      // TODO: measure. -1 = not set, flapNectar() does nothing
    private double pos;

    public OuttakeHood(HardwareMap hmap) { this(hmap, true); }

    /** moveAtInit = false: send nothing at Init (match TeleOp). Call goToInit() after Start instead. */
    public OuttakeHood(HardwareMap hmap, boolean moveAtInit){
        hoodServo = hmap.get(Servo.class, "OuttakeHood");
        if (moveAtInit) goToInit();
    }

    public void goToInit() { setPosition(INIT_POS); }

    public void setPosition(double p) {
        pos = Math.max(0.0, Math.min(1.0, p));
        hoodServo.setPosition(pos);
    }
    public void nudge(double d) { setPosition(pos + d); }
    public double getPosition() { return pos; }
    public boolean isSafeForBelt() { return pos >= BELT_SAFE_MIN; }

    public void flapPollen(){ if (POLLEN_POS >= 0 && POLLEN_POS <= 1) setPosition(POLLEN_POS); }
    public void flapNectar(){ if (NECTAR_POS >= 0 && NECTAR_POS <= 1) setPosition(NECTAR_POS); }
}