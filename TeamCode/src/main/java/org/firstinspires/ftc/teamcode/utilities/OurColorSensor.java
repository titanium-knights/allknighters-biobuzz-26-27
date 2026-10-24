package org.firstinspires.ftc.teamcode.utilities;

import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.NormalizedColorSensor;
import com.qualcomm.robotcore.hardware.NormalizedRGBA;

import org.firstinspires.ftc.robotcore.external.Telemetry;

import java.util.LinkedList;

//it's called that cuz ColorSensor is a class apparently or something
public class OurColorSensor {
    public NormalizedColorSensor colorSensorR;
    public NormalizedColorSensor colorSensorL;
    public Telemetry tele;

    public enum color {
        BLUE,   // blue nectar
        RED,    // red nectar
        YELLOW, // pollen
        EMPTY   // nothing (surgical tube)
    }

    // ===== Thresholds (share of R+G+B), from left-sensor test data. TODO: fine tune =====
    public static double BLUE_MIN_BLUE_SHARE = 0.50;
    public static double RED_MIN_RED_SHARE = 0.44;
    public static double YELLOW_MIN_GREEN_SHARE = 0.50;
    public static double YELLOW_MAX_BLUE_SHARE = 0.20;

    public static int MAGAZINE_CAPACITY = 4;
    public LinkedList<color> loadedBalls = new LinkedList<>(); // front = next ball to shoot

    public OurColorSensor(HardwareMap hmap, Telemetry tele){
        colorSensorR = hmap.get(NormalizedColorSensor.class, "CSRight");
        colorSensorL = hmap.get(NormalizedColorSensor.class, "CSLeft");
        this.tele = tele;
    }

    /** Classify one reading by channel shares. */
    public static color classify(NormalizedRGBA c) {
        double total = c.red + c.green + c.blue;
        if (total <= 1e-6) return color.EMPTY; // no reading
        double rs = c.red / total;
        double gs = c.green / total;
        double bs = c.blue / total;
        if (bs >= BLUE_MIN_BLUE_SHARE) return color.BLUE;
        if (rs >= RED_MIN_RED_SHARE) return color.RED;
        if (gs >= YELLOW_MIN_GREEN_SHARE && bs <= YELLOW_MAX_BLUE_SHARE) return color.YELLOW;
        return color.EMPTY;
    }

    /** Read the LEFT sensor and classify it, without telemetry. */
    public color read() {
        return classify(colorSensorL.getNormalizedColors());
    }

    /** Detect the ball color using the LEFT sensor. Right sensor is shown in telemetry only. */
    public color detectColor(Telemetry tele) {
        NormalizedRGBA l = colorSensorL.getNormalizedColors();
        NormalizedRGBA r = colorSensorR.getNormalizedColors();
        color detected = classify(l);

        addShares(tele, "Left shares R/G/B", l);
        addShares(tele, "Right shares R/G/B", r);
        tele.addData("Detected (left)", detected);
        tele.addData("Right would say", classify(r));
        return detected;
    }

    private static void addShares(Telemetry tele, String label, NormalizedRGBA c) {
        double total = c.red + c.green + c.blue;
        if (total <= 1e-6) { tele.addData(label, "no reading"); return; }
        tele.addData(label, "%.2f / %.2f / %.2f", c.red / total, c.green / total, c.blue / total);
    }

    // ===== Magazine model (safe: never crashes on empty or full) =====
    public void magazineTelemetry(Telemetry tele) {
        tele.addData("Magazine", loadedBalls);
    }
    public void RegisterBallIntake(color intakedBallColor){
        if (intakedBallColor != color.EMPTY && loadedBalls.size() < MAGAZINE_CAPACITY) {
            loadedBalls.addLast(intakedBallColor);
        }
    }
    public void RegisterBallShot(){
        loadedBalls.pollFirst(); // returns null instead of crashing if empty
    }
}