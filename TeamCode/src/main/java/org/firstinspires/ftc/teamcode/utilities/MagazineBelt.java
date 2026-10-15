package org.firstinspires.ftc.teamcode.utilities;

import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;


public class MagazineBelt {
    public Servo beltServo;
    double loadOneBallRotations = -1; // placeholder; The amount of rotations (or fractions of rotations) it takes for one ball to be loaded
    public MagazineBelt(HardwareMap hmap){
        beltServo = hmap.get(Servo.class, "Belt Servo");
    }
}
