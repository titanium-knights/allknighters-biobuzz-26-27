package org.firstinspires.ftc.teamcode.utilities;


import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;



public class MagazineBelt {
    public CRServo beltServo;
    public LinearOpMode opMode;
    public double loadOneBallRotations = -1; // placeholder; The amount of rotations (or fractions of rotations) it takes for one ball to be loaded
    public final int BELT_SERVO_SPEED = 111; //rpm
    private boolean isRunning = false;
    ElapsedTime beltTimer = new ElapsedTime();
    public double timeToRotateBall = 1; //loadOneBallRotations/BELT_SERVO_SPEED
    public MagazineBelt(HardwareMap hmap, LinearOpMode opMode){
        beltServo = hmap.get(CRServo.class, "Belt Servo");
        this.opMode = opMode;
    }
    public void startCycle() {
        if (!isRunning) {
            beltServo.setPower(1.0);
            beltTimer.reset();
            isRunning = true;
        }
    }
    public void update() {
        if (isRunning && beltTimer.seconds() >= timeToRotateBall) {
            beltServo.setPower(0.0);
            isRunning = false;
        }
    }
}
