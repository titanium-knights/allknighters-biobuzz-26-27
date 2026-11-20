package org.firstinspires.ftc.teamcode.teleop;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.robotcore.internal.system.AppUtil;
import org.firstinspires.ftc.teamcode.utilities.OurColorSensor;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Records labeled color-sensor readings straight to a CSV on the Control Hub, so nothing is
 * copied off the Driver Station by hand. Press the gamepad button whose color matches the piece:
 *   B = Red, X = Blue, Y = Yellow, A = Nothing.
 * One press = one row. Move/rotate the piece between presses so the data covers real variation.
 * File: /sdcard/FIRST/colorlog-<time>.csv, same columns as coloredballz.csv.
 */
@TeleOp(name = "ColorLogger", group = "test")
public class ColorLogger extends LinearOpMode {
    private static final String HEADER = "Red (Left Sensor),Green (Left Sensor),Blue (Left Sensor),"
            + "Red (Right Sensor),Green (Right Sensor),Blue (Right Sensor),Color";

    @Override
    public void runOpMode() {
        OurColorSensor sensors = new OurColorSensor(hardwareMap, telemetry);
        String stamp = new SimpleDateFormat("yyyyMMdd-HHmmss", Locale.US).format(new Date());
        File file = new File(AppUtil.ROOT_FOLDER, "colorlog-" + stamp + ".csv");
        Map<String, Integer> counts = new LinkedHashMap<>();
        for (String c : new String[]{"Red", "Blue", "Yellow", "Nothing"}) counts.put(c, 0);

        telemetry.addData("File", file.getPath());
        telemetry.addLine("B=Red  X=Blue  Y=Yellow  A=Nothing");
        telemetry.update();
        waitForStart();

        try (FileWriter out = new FileWriter(file)) {
            out.write(HEADER + "\n");
            boolean wasPressed = false;
            while (opModeIsActive()) {
                double[] v = sensors.readNormalized();
                String label = gamepad1.b ? "Red" : gamepad1.x ? "Blue" : gamepad1.y ? "Yellow" : gamepad1.a ? "Nothing" : null;
                if (label != null && !wasPressed) { // one row per press, not per loop
                    StringBuilder row = new StringBuilder();
                    for (double x : v) row.append(String.format(Locale.US, "%.4f,", x));
                    out.write(row.append(label).append('\n').toString());
                    out.flush(); // a crash or stop mid-session keeps every row already logged
                    counts.put(label, counts.get(label) + 1);
                }
                wasPressed = label != null;

                telemetry.addData("Left  RGB", "%.4f, %.4f, %.4f", v[0], v[1], v[2]);
                telemetry.addData("Right RGB", "%.4f, %.4f, %.4f", v[3], v[4], v[5]);
                telemetry.addData("Logged", counts);
                telemetry.addData("File", file.getPath());
                telemetry.update();
            }
        } catch (IOException e) {
            telemetry.addData("ERROR writing log", e.getMessage());
            telemetry.update();
            while (opModeIsActive()) idle(); // keep the error on screen instead of exiting silently
        }
    }
}
