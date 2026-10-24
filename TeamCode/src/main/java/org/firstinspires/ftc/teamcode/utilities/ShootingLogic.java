package org.firstinspires.ftc.teamcode.utilities;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.Telemetry;

public class ShootingLogic {
    public static double SHOOT_POWER = 1.0;  // full power
    public static double SPINUP_TIME = 1.0;  // TODO: tune. Seconds after turning on before feeding

    final public DcMotor shootMotor;
    private final MagazineBelt magazineBelt;
    private final OuttakeHood outtakeHood;
    private final Telemetry tele;
    private final ElapsedTime spinTimer = new ElapsedTime();

    private boolean spinning = false;
    public boolean isShooting = false;
    private boolean launchStarted = false;
    private boolean lastLaunchCompleted = false;

    // colorSensor is not used here. Kept so SubsystemManager still compiles.
    public ShootingLogic(HardwareMap hMap, Telemetry tele, OurColorSensor colorSensor, MagazineBelt magazineBelt, OuttakeHood outtakeHood){
        shootMotor = hMap.get(DcMotor.class, "ShootMotor");
        shootMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        shootMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT); // coast down, no hard braking
        shootMotor.setPower(0);
        this.magazineBelt = magazineBelt;
        this.outtakeHood = outtakeHood;
        this.tele = tele;
    }

    // ================= PUBLIC API =================
    /** Flywheel on/off at full power. Turning it off cancels a launch in progress and stops the belt. */
    public void setSpinning(boolean on) {
        if (on && !spinning) spinTimer.reset();
        spinning = on;
        shootMotor.setPower(on ? SHOOT_POWER : 0);
        if (!on && isShooting) {
            magazineBelt.stop();
            isShooting = false;
            lastLaunchCompleted = false;
        }
    }

    /** True once the flywheel has been on for SPINUP_TIME. */
    public boolean isAtSpeed() { return spinning && spinTimer.seconds() >= SPINUP_TIME; }

    /** Launch everything: after spin-up, run the belt to finish its loop (first paddle back on the mark). */
    public void launch() {
        if (!spinning) { tele.addData("SHOOTER", "NOT ON"); return; }
        if (isShooting) return;
        outtakeHood.flapPollen();
        isShooting = true;
        launchStarted = false;
        lastLaunchCompleted = false;
    }

    /** True if the last launch ran all the way to the end of the loop. */
    public boolean wasLaunchCompleted() { return lastLaunchCompleted; }

    /** Call every loop. Runs belt timing and the launch. */
    public void update() {
        magazineBelt.update();
        if (!isShooting) return;

        if (!launchStarted) {
            if (!magazineBelt.isRunning && isAtSpeed() && outtakeHood.isSafeForBelt()) {
                double t = magazineBelt.timeToRealign();
                magazineBelt.runFor(t > 0 ? t : MagazineBelt.LOOP_TIME); // already on the mark: run a full loop
                launchStarted = true;
            }
        } else if (!outtakeHood.isSafeForBelt()) {
            magazineBelt.stop(); // hood dropped too low: abort
            isShooting = false;
        } else if (!magazineBelt.isRunning) {
            isShooting = false;  // loop finished
            lastLaunchCompleted = true;
        }
    }

    public void stop() { setSpinning(false); }

    public void addTelemetry(Telemetry t) {
        t.addData("Flywheel", spinning ? (isAtSpeed() ? "READY" : "spinning up") : "off");
        t.addData("Launching", isShooting ? String.format("yes (%.1f s left)", magazineBelt.timeLeft()) : "no");
    }

    // ================= LEGACY / OLDER OPMODES =================
    public void shoot() { launch(); }
    public void shoot(int balls) { launch(); }
    public void shootBall() { launch(); }
    public void updateDuringShoot() { } // handled in update()
}