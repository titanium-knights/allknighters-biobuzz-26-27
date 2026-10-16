package org.firstinspires.ftc.teamcode.utilities;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.Telemetry;

public class LinearSlides {
    public DcMotor slideMotorRight; //Baseline. in the case of unevenness, left motor adjusts to right motor pos
    public DcMotor slideMotorLeft;
    private Telemetry tele;

    final int gameStartPos = -1; // Placeholder for the servo position of the slide when the game starts
    final int gameStartPosReal = -1; // Placeholder for the servo position of the slide after starting but like actually
    final int flowerOuttake = -1; //Placeholder for the position when outtaking to the flower
    final int upPos = -1; //Placeholder for the position when shooting;

    public LinearSlides(HardwareMap hmap, Telemetry tele){
        slideMotorRight = hmap.get(DcMotor.class, "Slide Motor 1");
        slideMotorLeft = hmap.get(DcMotor.class, "Slide Motor 2");
        this.tele = tele;
    }

    public void checkEquivalenceAndAdjust(){
        int difference = slideMotorLeft.getCurrentPosition() - slideMotorRight.getCurrentPosition();
        if (difference>=1){
            slideMotorRight.setTargetPosition(slideMotorLeft.getCurrentPosition());
        }
    }
    public boolean checkStartPosIsRight(){
        boolean out = (slideMotorLeft.getCurrentPosition() == slideMotorRight.getCurrentPosition() && (slideMotorLeft.getCurrentPosition() == gameStartPos));
        tele.addData("Slides are at correct Positions", out);
        return out;
    }

    public void toUpPos(){
        slideMotorRight.setTargetPosition(upPos);
        slideMotorLeft.setTargetPosition(-upPos);
    }
    public void toFlowerOuttake(){
        slideMotorRight.setTargetPosition(flowerOuttake);
        slideMotorLeft.setTargetPosition(-flowerOuttake);
    }
    public void toGameStartPosReal(){
        slideMotorRight.setTargetPosition(gameStartPosReal);
        slideMotorLeft.setTargetPosition(-gameStartPosReal);
    }
}
