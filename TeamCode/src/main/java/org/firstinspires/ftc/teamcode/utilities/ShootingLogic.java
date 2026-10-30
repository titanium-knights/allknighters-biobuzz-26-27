package org.firstinspires.ftc.teamcode.utilities;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.VoltageSensor;

import org.firstinspires.ftc.robotcore.external.Telemetry;

public class ShootingLogic {
    final public DcMotor shootMotor;
    private final MagazineBelt magazineBelt;
    private final OurColorSensor colorSensor;
    private final OuttakeHood outtakeHood;
    private int shotBallCount = 0;
    public boolean isShooting = false;
    public boolean flywheelSpinning;
    private final Telemetry tele;
    public ShootingLogic(HardwareMap hMap, Telemetry tele, OurColorSensor colorSensor, MagazineBelt magazineBelt, OuttakeHood outtakeHood){
        shootMotor = hMap.get(DcMotor.class, "ShootMotor");
        this.magazineBelt = magazineBelt;
        this.tele = tele;
        this.outtakeHood = outtakeHood;
        this.colorSensor = colorSensor;
    }
    public void shootBall(){
        if (shootMotor.getPower()>0) {
            isShooting = true;
            shotBallCount = 0;
            colorSensor.RegisterBallShot();
            magazineBelt.startCycle();
        } else {tele.addData("SHOOTER NOT ON!!!", "SHOOTER NOT ON");}
    }
    public void updateDuringShoot(){
        if (!magazineBelt.isRunning && shotBallCount<4 && isShooting){
            outtakeHood.flapPollen();
            shotBallCount++;
            magazineBelt.startCycle();
        } else if (shotBallCount>=4){
            isShooting = false;
            shotBallCount = 0;
        }
    }
    public double setPower(int power){
        double oldPower = shootMotor.getPower();
        shootMotor.setPower(power);
        return oldPower;
    }
    public double getPower(double targetRPM){
        return shootMotor.getPower();
    }


}
