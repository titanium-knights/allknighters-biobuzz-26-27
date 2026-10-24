package org.firstinspires.ftc.teamcode.utilities;

import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

public class MagazineBelt {
    public CRServo beltServo;
    public LinearOpMode opMode;
    public boolean isRunning = false;
    ElapsedTime beltTimer = new ElapsedTime();
    public double timeToRotateBall = 1.5;  // TODO: set to LOOP_TIME / PADDLES once paddle count is known
    public static double BELT_POWER = 1.0;
    public static double LOOP_TIME = 8.0;  // measured: one full belt loop at BELT_POWER
    public static int PADDLES = 1;         // 1 = return to exact start spot; set to real paddle count to return to nearest paddle

    private double runTime = 0;
    private double totalRun = 0; // seconds of forward running since paddles were last aligned

    public MagazineBelt(HardwareMap hmap){
        beltServo = hmap.get(CRServo.class, "Belt Servo");
    }

    /** Move one paddle (one ball). Ignored if the belt is already running. */
    public void startCycle() {
        if (!isRunning) runFor(timeToRotateBall);
    }

    /** Run the belt for a set time. Replaces whatever run is in progress. */
    public void runFor(double seconds) {
        if (isRunning) totalRun += beltTimer.seconds(); // count the run being replaced
        beltServo.setPower(BELT_POWER);
        beltTimer.reset();
        runTime = seconds;
        isRunning = true;
    }

    public void stop() {
        if (isRunning) totalRun += beltTimer.seconds(); // count how long it actually ran
        beltServo.setPower(0);
        isRunning = false;
    }

    /** Call every loop. Stops the belt when its time is up. */
    public void update() {
        if (isRunning && beltTimer.seconds() >= runTime) stop();
    }

    public double timeLeft() {
        return isRunning ? Math.max(0, runTime - beltTimer.seconds()) : 0;
    }

    // ===== Alignment =====
    /** Seconds of running needed to get back to an aligned spot. */
    public double timeToRealign() {
        double spacing = LOOP_TIME / Math.max(1, PADDLES);
        double r = totalRun % spacing;
        return (r < 0.02 || spacing - r < 0.02) ? 0 : spacing - r;
    }

    /** Run the belt forward to the next aligned spot. */
    public void realign() {
        double t = timeToRealign();
        if (t > 0) runFor(t);
    }

    /** Call when paddles are known to be in the reference spot. */
    public void markAligned() { totalRun = 0; }

    /** Set how far into the loop the belt is (seconds), e.g. for a preloaded magazine. */
    public void setTotalRun(double seconds) { totalRun = seconds; }
}