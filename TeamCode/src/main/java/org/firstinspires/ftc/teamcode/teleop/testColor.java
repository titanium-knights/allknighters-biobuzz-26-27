package org.firstinspires.ftc.teamcode.teleop;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.utilities.OurColorSensor;

@TeleOp(name = "TestColor")
public class testColor extends OpMode {

    private OurColorSensor ourColorSensor;

    @Override
    public void init() {
        telemetry.update();
        ourColorSensor = new OurColorSensor(hardwareMap, telemetry);

    }

    @Override
    public void loop() {
        ourColorSensor.detectColor(telemetry);
    }

    @Override
    public void stop() {

    }
}
