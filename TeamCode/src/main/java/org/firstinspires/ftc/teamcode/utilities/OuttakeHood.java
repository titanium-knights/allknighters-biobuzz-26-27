package org.firstinspires.ftc.teamcode.utilities;

import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

public class OuttakeHood {
    public Servo hoodServo;

    double flapNectar = -1; // Placeholder for the servo position for the Nectar
    double flapPollen = -1; // Placeholder for the servo position for the Pollen

    public OuttakeHood(HardwareMap hmap){
        hoodServo = hmap.get(Servo.class, "OuttakeHood");
    }

    public void flapNectar(){
        hoodServo.setPosition(flapNectar);
    }
    public void flapPollen(){
        hoodServo.setPosition(flapPollen);
    }
}
