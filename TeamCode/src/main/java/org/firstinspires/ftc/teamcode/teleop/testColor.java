package org.firstinspires.ftc.teamcode.teleop;

import static org.firstinspires.ftc.robotcore.external.BlocksOpModeCompanion.hardwareMap;
import static org.firstinspires.ftc.robotcore.external.BlocksOpModeCompanion.telemetry;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.utilities.Limelight;
import org.firstinspires.ftc.teamcode.utilities.LinearSlides;
import org.firstinspires.ftc.teamcode.utilities.MagazineAngle;
import org.firstinspires.ftc.teamcode.utilities.OurColorSensor;
import org.firstinspires.ftc.teamcode.utilities.SubsystemManager;

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
        ourColorSensor.rgbToColor(telemetry);
    }

    @Override
    public void stop() {

    }
}
