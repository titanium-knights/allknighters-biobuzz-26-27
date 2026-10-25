package org.firstinspires.ftc.teamcode.auton;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.utilities.LinearSlides;

@TeleOp(name="Do Nothing", group = "Autonomous")
public class DoesNothing extends LinearOpMode {
    @Override
    public void runOpMode(){
        waitForStart();
    }
}
