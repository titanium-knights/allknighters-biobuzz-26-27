package org.firstinspires.ftc.teamcode.utilities;

import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.Telemetry;

public class PollenTurret {
    CRServo rightServo;
    CRServo leftServo;
    ElapsedTime timer;
    private static final double servoRPM = -1; //placeholder
    public PollenTurret(HardwareMap hmap, Telemetry telemetry, Limelight limelight){
        rightServo = hmap.get(CRServo.class, "right servo");
        leftServo = hmap.get(CRServo.class, "left servo");
        timer = new ElapsedTime();
    }

    public void rightTurnToAngle(double targetAngle){
        double time = servoRPM * targetAngle/60;
        timer.reset();
        rightServo.setPower(1);
        while (timer.seconds() < time){}

    }
}
