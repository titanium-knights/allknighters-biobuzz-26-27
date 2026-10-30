package org.firstinspires.ftc.teamcode.utilities;

import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.util.ElapsedTime;

public class MagazineAngle {
    public CRServo angleServo;
    private double angleTime = 0.1;
    private double power = 0.3;
    private ElapsedTime runtime = new ElapsedTime();


    public MagazineAngle(HardwareMap hmap){
        angleServo = hmap.get(CRServo.class, "Magazine Angle Servo");
    }
    public void magAngleForward(){
        angleServo.setPower(power);
        runtime.reset();
        if (runtime.seconds()>=angleTime){angleServo.setPower(angleTime);}
        angleServo.setPower(0.0);
    }
    public void magAngleBackward(){
        angleServo.setPower(-1 * power);
        runtime.reset();
        if (runtime.seconds()>=angleTime){angleServo.setPower(angleTime);}
        angleServo.setPower(0.0);
    }
}
