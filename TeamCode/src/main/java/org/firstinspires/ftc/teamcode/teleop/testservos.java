package org.firstinspires.ftc.teamcode.teleop;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.teamcode.utilities.LinearSlides;
import org.firstinspires.ftc.teamcode.utilities.MagazineAngle;
import org.firstinspires.ftc.teamcode.utilities.MagazineBelt;
import org.firstinspires.ftc.teamcode.utilities.SubsystemManager;
@TeleOp(name = "TestServos")
public class testservos extends OpMode {
    //private SubsystemManager subsystemManager;
    MagazineBelt magazineBelt;
    private double modifier;
    private CRServo beltServo;
    private LinearSlides linearSlides;
    private MagazineAngle magazineAngle;

    private Servo outtakeHood;

    private boolean lastRB = false;
    private boolean lastLB = false;

    private boolean lastUp = false;
    private boolean lastDown = false;

    @Override
    public void init(){
        magazineBelt = new MagazineBelt(hardwareMap);
        //subsystemManager = new SubsystemManager(hardwareMap, telemetry);
        beltServo = hardwareMap.get(CRServo.class, "Belt Servo");
        magazineAngle = new MagazineAngle(hardwareMap);
        linearSlides = new LinearSlides(hardwareMap, telemetry, magazineAngle);
    }

    @Override
    public void loop(){
        if(gamepad1.dpad_right) {
            magazineBelt.startCycle();
        }
        magazineBelt.update();
        boolean rb = gamepad1.right_bumper;
        boolean lb = gamepad1.left_bumper;
        if (rb && !lastRB) linearSlides.levelUp();
        if (lb && !lastLB) linearSlides.levelDown();
        lastRB = rb;
        lastLB = lb;
        linearSlides.update();
        telemetry.addData("Slide R power", linearSlides.slideMotorRight.getPower());
        telemetry.addData("Slide L power", linearSlides.slideMotorLeft.getPower());
        telemetry.addData("Slide level", linearSlides.levelIndex);
        telemetry.addData("rightMotor Position", linearSlides.slideMotorRight.getCurrentPosition());
        telemetry.addData("Right motor target", linearSlides.slideMotorRight.getTargetPosition());
        telemetry.addData("Start LS pressed", linearSlides.isStartLimitSwitchHit());
        telemetry.addData("Intake LS pressed", linearSlides.isIntakeSwitchHit());
        // Measure magazine positions: dpad up/down nudges the servo
        if (gamepad1.dpad_up && !lastUp) magazineAngle.nudge(MagazineAngle.NUDGE_STEP);
        if (gamepad1.dpad_down && !lastDown) magazineAngle.nudge(-MagazineAngle.NUDGE_STEP);
        lastUp = gamepad1.dpad_up;
        lastDown = gamepad1.dpad_down;
        telemetry.addData("Mag pos", magazineAngle.getCommandedPos());
        telemetry.update();
    }


    @Override
    public void stop() {
        //if (subsystemManager != null) {
          //  subsystemManager.drive.stop();
        //}
    }
}
