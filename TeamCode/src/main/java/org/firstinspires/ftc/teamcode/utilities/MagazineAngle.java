package org.firstinspires.ftc.teamcode.utilities;

import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

public class MagazineAngle {
    public Servo angleServo;

    public MagazineAngle(HardwareMap hmap){
        angleServo = hmap.get(Servo.class, "Magazine Angle Servo");
    }
    public void setAngle(double angle){
        angleServo.setPosition(angle);
    }
}
