package org.firstinspires.ftc.teamcode.teleop;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.utilities.Flywheel;
import org.firstinspires.ftc.teamcode.utilities.Intake;
import org.firstinspires.ftc.teamcode.utilities.Limelight;
import org.firstinspires.ftc.teamcode.utilities.OurColorSensor;
import org.firstinspires.ftc.teamcode.utilities.ShootingLogic;
import org.firstinspires.ftc.teamcode.utilities.SubsystemManager;

//Wifi Password: FTCTitaniumA15887!

@TeleOp(name = "TeleOp")
public class Teleop extends OpMode {
    private SubsystemManager subsystemManager;
    private Telemetry telemetry;
    private double modifier;
    private OurColorSensor ourColorSensor;
    private Intake intake;
    private ShootingLogic shootingLogic;
    private Flywheel flywheel;
    private Limelight limelight;
    private boolean lastLeftBumper;
    private boolean lastRightBumper;
    private boolean lastFlapButton;
    private boolean pollenFlapSelected;


    @Override
    public void init() {
        //archived: DNE subsystemManager = new SubsystemManager(hardwareMap, telemetry);
        modifier = 1.0;
        //ourColorSensor = subsystemManager.colorSensors;
        intake = subsystemManager.intake;
        shootingLogic = subsystemManager.shootingLogic;
        limelight = subsystemManager.limeLight;
        telemetry.addData("Drive", "Ready");
        telemetry.update();
    }

    @Override
    public void loop() {
        modifier = (gamepad1.b) ? -0.8 : 1.0;
        //OurColorSensor.color detectedColor = ourColorSensor.detectColor(telemetry);
        //ourColorSensor.magazineTelemetry(telemetry);

        //update limelight


        // chassis
        if (!gamepad1.a) {
            subsystemManager.drive.move(
                    gamepad1.left_stick_x * modifier,
                    -gamepad1.left_stick_y * modifier, // negated bc y value is opposite on the stick
                    gamepad1.right_stick_x);
        } else {
            double xDpadDir = 0;
            if (gamepad1.dpad_left) { xDpadDir--; }
            if (gamepad1.dpad_right) { xDpadDir++; }
            xDpadDir *= modifier;

            double yDpadDir = 0;
            if (gamepad1.dpad_up) { yDpadDir++; }
            if (gamepad1.dpad_down) { yDpadDir--; }
            yDpadDir *= modifier;

            subsystemManager.drive.move(
                    xDpadDir, yDpadDir, gamepad1.right_stick_x * 0.8
            );
        }



        // magazine servos and intake

        if (gamepad1.right_trigger > 0.5) {
            intake.run();
        } else if (gamepad1.left_trigger > 0.5) {
            intake.outtake();
        } else {
            intake.stopIntake();
        }

        //Shooting logic
        if (gamepad2.b && !shootingLogic.isShooting){
            shootingLogic.shootBall();
        }



        telemetry.update();
    }

    @Override
    public void stop() {
        if (subsystemManager != null) {
            subsystemManager.drive.stop();
            subsystemManager.intake.stopIntake();
            flywheel.setShooterOn(false);
        }
    }
}
