package org.firstinspires.ftc.teamcode.teleop;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.teamcode.utilities.LinearSlides;
import org.firstinspires.ftc.teamcode.utilities.MagazineBelt;
import org.firstinspires.ftc.teamcode.utilities.SubsystemManager;
@TeleOp(name = "TestServos")
public class testservos extends OpMode {
    //private SubsystemManager subsystemManager;
    MagazineBelt magazineBelt;
    private double modifier;
    private CRServo beltServo;
    private LinearSlides linearSlides;

    private Servo outtakeHood;

    @Override
    public void init(){
        magazineBelt = new MagazineBelt(hardwareMap);
        //subsystemManager = new SubsystemManager(hardwareMap, telemetry);
        beltServo = hardwareMap.get(CRServo.class, "Belt Servo");
        linearSlides = new LinearSlides(hardwareMap, telemetry);
    }

    @Override
    public void loop(){
        if(gamepad1.dpad_right) {
            magazineBelt.startCycle();

        }

        magazineBelt.update();
        if (gamepad1.left_bumper){
            linearSlides.toIntake();
        }
        if (gamepad1.right_bumper){
            linearSlides.toShoot();
        }
        if(linearSlides.getTargetLevel()==1){
            linearSlides.isLimitSwitchHitAndStopIfSo();
        } else if (linearSlides.getTargetLevel()==2){
            linearSlides.isAtCorrectPositionToShoot();
        }
        telemetry.addData("rightMotor Position", linearSlides.slideMotorRight.getCurrentPosition());
        telemetry.addData("Right motor target", linearSlides.slideMotorRight.getTargetPosition());
    }


    @Override
    public void stop() {
        //if (subsystemManager != null) {
          //  subsystemManager.drive.stop();
        //}
    }
}
