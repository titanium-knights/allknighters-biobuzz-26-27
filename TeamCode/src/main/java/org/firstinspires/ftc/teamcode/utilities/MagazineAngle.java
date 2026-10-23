package org.firstinspires.ftc.teamcode.utilities;

import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

public class MagazineAngle {
    public Servo angleServo;
    final public double startAngle = -1;
    final public double shootAngle = -1;

    public MagazineAngle(HardwareMap hmap){
        angleServo = hmap.get(Servo.class, "Magazine Angle Servo");
    }
    public void setAngle(double angle){
        if (angle >= 0 && angle <= 1) angleServo.setPosition(angle);
    }
    public void setStartAngle(){setAngle(startAngle);}
    public void setShootAngle(){setAngle(shootAngle);}
}
