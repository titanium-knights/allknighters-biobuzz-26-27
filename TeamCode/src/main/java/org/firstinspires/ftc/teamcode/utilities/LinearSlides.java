package org.firstinspires.ftc.teamcode.utilities;

import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.HardwareMap;

public class LinearSlides {
    public Servo slideServo;

    double upPos = -1; // Placeholder for the servo position of the slide when up
    double downPos = -1; // Placeholder for the servo position of the slide when down

    public LinearSlides(HardwareMap hmap){
        slideServo = hmap.get(Servo.class, "Slide Servo");
    }
    public void up(){
        slideServo.setPosition(upPos);
    }
    public void down(){
        slideServo.setPosition(downPos);
    }
}
