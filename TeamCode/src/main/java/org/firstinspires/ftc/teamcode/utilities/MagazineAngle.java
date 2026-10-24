package org.firstinspires.ftc.teamcode.utilities;

import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

public class MagazineAngle {
    public Servo angleServo;
    public static double INTAKE_POS = 0.73; // intake angle, measured
    public static double SHOOT_POS = -1;    // TODO: measure. Shooting angle. -1 = not set, toShoot() does nothing
    public static double STOW_POS = 0.9;    // TODO: measure. PLACEHOLDER start angle for 18 in sizing
    public static double NUDGE_STEP = 0.01; // per D-pad press when measuring
    public static double MOVE_TIME = 0.5;   // seconds allowed to reach a position
    private final ElapsedTime moveTimer = new ElapsedTime();
    private boolean moving = false;
    private double commandedPos = INTAKE_POS;

    public MagazineAngle(HardwareMap hmap) { this(hmap, true); }

    /** wakeAtInit = false: send nothing at Init (match TeleOp). Call toStow() after Start instead. */
    public MagazineAngle(HardwareMap hmap, boolean wakeAtInit){
        angleServo = hmap.get(Servo.class, "Magazine Angle Servo");
        if (wakeAtInit) toStow(); // wake the Axon by holding the stow angle it already sits at
    }

    // ================= PUBLIC API =================
    public void toIntake() { moveTo(INTAKE_POS); }
    public void toStow()   { if (STOW_POS >= 0 && STOW_POS <= 1) moveTo(STOW_POS); }
    public void toShoot()  { if (SHOOT_POS >= 0 && SHOOT_POS <= 1) moveTo(SHOOT_POS); }
    public void nudge(double delta) { moveTo(commandedPos + delta); }
    public double getCommandedPos() { return commandedPos; }
    public boolean isTilting() { return moving; }

    /** Call every loop. Tracks when a move should be finished; the servo holds position on its own. */
    public void update() {
        if (moving && moveTimer.seconds() >= MOVE_TIME) moving = false;
    }

    private void moveTo(double pos) {
        pos = Math.max(0.0, Math.min(1.0, pos));
        commandedPos = pos;
        angleServo.setPosition(pos);
        moveTimer.reset();
        moving = true;
    }

    // ================= LEGACY (old TeleOp): intentionally do nothing so it cannot swing the magazine =================
    public void magAngleForward()  { }
    public void magAngleBackward() { }
}