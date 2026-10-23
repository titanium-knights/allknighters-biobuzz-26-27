package org.firstinspires.ftc.teamcode.teleop;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.teamcode.utilities.MagazineBelt;
import org.firstinspires.ftc.teamcode.utilities.SubsystemManager;
@TeleOp(name = "TestServos")
public class testservos extends OpMode {
    //private SubsystemManager subsystemManager;
    MagazineBelt magazineBelt;
    private double modifier;
    private CRServo beltServo;

    private Servo outtakeHood;

    @Override
    public void init(){
        magazineBelt = new MagazineBelt(hardwareMap);
        //subsystemManager = new SubsystemManager(hardwareMap, telemetry);
        beltServo = hardwareMap.get(CRServo.class, "Belt Servo");
    }

    @Override
    public void loop(){
        if(gamepad1.dpad_right) {
            magazineBelt.startCycle();

        }

        magazineBelt.update();
    }

    @Override
    public void stop() {
        //if (subsystemManager != null) {
          //  subsystemManager.drive.stop();
        //}
    }
}
