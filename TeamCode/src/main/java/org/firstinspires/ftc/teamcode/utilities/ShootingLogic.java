package org.firstinspires.ftc.teamcode.utilities;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.utilities.unused.MagazineBelt;
import org.firstinspires.ftc.teamcode.utilities.unused.OuttakeHood;

public class ShootingLogic {
    final public DcMotor shootMotor;
    private final OurColorSensor colorSensor;
    private int shotBallCount = 0;
    public boolean isShooting = false;
    public boolean flywheelSpinning;
    private final Telemetry tele;
    boolean isRunning;
    public ShootingLogic(HardwareMap hMap, Telemetry tele, OurColorSensor colorSensor){
        shootMotor = hMap.get(DcMotor.class, "ShootMotor");
        this.tele = tele;
        this.colorSensor = colorSensor;
    }
    public void shootBall(){
        if (shootMotor.getPower()>0) {
            isShooting = true;
            shotBallCount = 0;
            colorSensor.RegisterBallShot();
        } else {tele.addData("SHOOTER NOT ON!!!", "SHOOTER NOT ON");}
    }
    public void updateDuringShoot(){
        if (!isRunning && shotBallCount<4 && isShooting){
            shotBallCount++;
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
