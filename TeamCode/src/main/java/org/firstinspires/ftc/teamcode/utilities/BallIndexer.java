package org.firstinspires.ftc.teamcode.utilities;

import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.Telemetry;

/** Counts pollen coming into the magazine and advances the belt one paddle per ball. */
public class BallIndexer {
    public static int CAPACITY = 3;               // balls the magazine holds
    public static int DETECT_LOOPS = 5;           // consecutive pollen readings needed to count a ball
    public static double SETTLE_TIME = 0.2;       // seconds to wait after the belt stops before reading again
    public static double RETURN_OFFSET_TIME = 0.3; // TODO: tune. Belt run after coming down from shoot

    private final OurColorSensor sensor;
    private final MagazineBelt belt;
    private final OuttakeHood hood;
    private final ElapsedTime settleTimer = new ElapsedTime();

    private int count;
    private int seenLoops = 0;
    private OurColorSensor.color lastSeen = OurColorSensor.color.EMPTY;

    public BallIndexer(OurColorSensor sensor, MagazineBelt belt, OuttakeHood hood, int startCount) {
        this.sensor = sensor;
        this.belt = belt;
        this.hood = hood;
        this.count = startCount;
    }

    /** Bring the paddles back to an aligned spot, only when the magazine is empty. */
    public void runReturnOffset() {
        if (count > 0) return;            // balls still loaded: never run the belt at intake height
        if (!hood.isSafeForBelt()) return;
        belt.realign();
        seenLoops = 0;
    }

    /** Call every loop. active = slides at intake and intake running. */
    public void update(boolean active) {
        if (belt.isRunning) { settleTimer.reset(); seenLoops = 0; return; } // wait for belt to finish
        if (!active || isFull()) { seenLoops = 0; return; }
        if (settleTimer.seconds() < SETTLE_TIME) return;                    // let things settle

        lastSeen = sensor.read();
        if (lastSeen == OurColorSensor.color.YELLOW) seenLoops++;
        else seenLoops = 0;

        if (seenLoops >= DETECT_LOOPS && hood.isSafeForBelt()) {
            belt.startCycle(); // move one paddle
            count++;
            seenLoops = 0;
        }
    }

    public int getCount() { return count; }
    public void setCount(int c) { count = Math.max(0, Math.min(CAPACITY, c)); }
    public boolean isFull() { return count >= CAPACITY; }

    public void addTelemetry(Telemetry t) {
        t.addData("Magazine", "%d / %d%s", count, CAPACITY, isFull() ? "  FULL" : "");
        t.addData("Sensor sees", lastSeen + " (" + seenLoops + "/" + DETECT_LOOPS + ")");
        t.addData("Belt realign needs", "%.2f s", belt.timeToRealign());
    }
    /** Manual override: act as if a pollen was detected. Returns true if it moved the belt. */
    public boolean forceIntake() {
        if (isFull() || belt.isRunning || !hood.isSafeForBelt()) return false;
        belt.startCycle(); // move one paddle
        count++;
        seenLoops = 0;
        return true;
    }
}