package org.firstinspires.ftc.teamcode.utilities;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.VoltageSensor;

import org.firstinspires.ftc.robotcore.external.Telemetry;

public class ShootingLogic {
    final public DcMotor shootMotor;
    private final MagazineBelt magazineBelt;
    private final OurColorSensor colorSensor;
    private final Flywheel flywheel;
    private final VoltageSensor voltageSensor;
    private int shotBallCount = 0;
    public boolean isShooting = false;
    public boolean flywheelSpinning;
    private final Telemetry tele;
    public ShootingLogic(HardwareMap hMap, Telemetry tele, OurColorSensor colorSensor, MagazineBelt magazineBelt){
        shootMotor = hMap.get(DcMotor.class, "ShootMotor");
        voltageSensor = hMap.get(VoltageSensor.class, "VoltageSensor");
        this.magazineBelt = magazineBelt;
        this.colorSensor = colorSensor;
        flywheel = new Flywheel(shootMotor, tele, voltageSensor);
        this.tele = tele;
    }
    public void shootBall(){
        if (flywheelSpinning) {
            isShooting = true;
            shotBallCount = 0;
            colorSensor.RegisterBallShot();
            magazineBelt.startCycle();
        } else {tele.addData("SHOOTER NOT ON!!!", "SHOOTER NOT ON");}
    }
    public void updateDuringShoot(){
        if (flywheel.isAtSpeed() && !magazineBelt.isRunning && shotBallCount<4){
            shotBallCount++;
            magazineBelt.startCycle();
        } else if (shotBallCount>=4){
            isShooting = false;
        }
    }
    public double setTargetRPM(double targetRPM){
        double oldRPM = flywheel.getTargetRPM();
        flywheel.setTargetRPM(targetRPM);
        return oldRPM;
    }
    public void update(){
        flywheel.update();
        tele.addData("Flywheel CURRENT Speed", flywheel.getCurrentRPM());
        tele.addData("Flywheel TARGET Speed", flywheel.getTargetRPM());
    }
    public double getTargetRPM(double targetRPM){
        return flywheel.getTargetRPM();
    }

}
