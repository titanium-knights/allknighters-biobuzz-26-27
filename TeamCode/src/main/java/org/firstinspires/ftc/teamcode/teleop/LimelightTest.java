package org.firstinspires.ftc.teamcode.teleop;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.utilities.Limelight;


@TeleOp (name = "Limelight Test")
public class LimelightTest extends OpMode {

    Limelight limelight;

    @Override
    public void init(){
        limelight = new Limelight(hardwareMap, telemetry);

    }

    @Override
    public void loop(){
        limelight.update();
        telemetry.addData("Has Target", limelight.hasTarget());
        limelight.addToTele();




        telemetry.update();
    }
}
