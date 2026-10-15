package org.firstinspires.ftc.teamcode.utilities;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;

public class LinearSlides {
    public DcMotor slideMotor;

    int upPos = -1; // Placeholder for the servo position of the slide when up
    int downPos = -1; // Placeholder for the servo position of the slide when down

    public LinearSlides(HardwareMap hmap){
        slideMotor = hmap.get(DcMotor.class, "Slide Servo");
    }
    public void up(){
        slideMotor.setTargetPosition(upPos);
    }
    public void down(){
        slideMotor.setTargetPosition(downPos);
    }
}
