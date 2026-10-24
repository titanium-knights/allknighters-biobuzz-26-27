package org.firstinspires.ftc.teamcode.utilities;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;

public class
SimpleMecanumDrive {

    private final DcMotor fl, fr, bl, br;

    public SimpleMecanumDrive(HardwareMap hmap) {
        fl = hmap.get(DcMotor.class, CONFIG.FRONT_LEFT);
        fr = hmap.get(DcMotor.class, CONFIG.FRONT_RIGHT);
        bl = hmap.get(DcMotor.class, CONFIG.BACK_LEFT);
        br = hmap.get(DcMotor.class, CONFIG.BACK_RIGHT);

        fl.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        fr.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        bl.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        br.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        fl.setDirection(CONFIG.FL_DIRECTION);
        fr.setDirection(CONFIG.FR_DIRECTION);
        bl.setDirection(CONFIG.BL_DIRECTION);
        br.setDirection(CONFIG.BR_DIRECTION);

        fl.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        fr.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        bl.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        br.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        stop();
    }

    public void move(double x, double y, double turn) {
        double flPower = y + x + turn;
        double frPower = y - x - turn;
        double blPower = y - x + turn;
        double brPower = y + x - turn;

        double max = Math.max(1.0, Math.max(
                Math.max(Math.abs(flPower), Math.abs(frPower)),
                Math.max(Math.abs(blPower), Math.abs(brPower))));

        fl.setPower(flPower / max);
        fr.setPower(frPower / max);
        bl.setPower(blPower / max);
        br.setPower(brPower / max);
    }

    public void stop() {
        move(0, 0, 0);
    }

    public void moveFL(double power) {
        fl.setPower(power);
    }

    public void stopFL() {
        fl.setPower(0);
    }

    public void moveFR(double power) {
        fr.setPower(power);
    }

    public void stopFR() {
        fr.setPower(0);
    }

    public void moveBL(double power) {
        bl.setPower(power);
    }

    public void stopBL() {
        bl.setPower(0);
    }

    public void moveBR(double power) {
        br.setPower(power);
    }

    public void stopBR() {
        br.setPower(0);
    }
}
