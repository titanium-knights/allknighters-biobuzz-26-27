package org.firstinspires.ftc.teamcode.utilities;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DigitalChannel;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.Telemetry;

public class LinearSlides {
    public DcMotor slideMotorRight; //Baseline. in the case of unevenness, left motor adjusts to right motor pos
    public DcMotor slideMotorLeft;
    public DigitalChannel startLimitSwitch;
    public DigitalChannel intakeLimitSwitch;
    private Telemetry tele;
    private MagazineAngle magazineAngle;
    final int gameStartPos = -1; // Placeholder for the servo position of the slide when the game starts
    final int gameStartPosReal = 156; // Placeholder for the servo position of the slide after starting but like actually
    final int flowerOuttake = -1; //Placeholder for the position when outtaking to the flower
    final int upPos = 1964; //Placeholder for the position when shooting;
    public int targetLevel = 0; //0 is start, 1 is intake, 2 is shoot
    public boolean hitLimitSwitch = false;
    public boolean slidesRunning = false;


    public LinearSlides(HardwareMap hmap, Telemetry tele, MagazineAngle magazineAngle){
        slideMotorRight = hmap.get(DcMotor.class, "Slide Motor 1");
        slideMotorLeft = hmap.get(DcMotor.class, "Slide Motor 2");
        startLimitSwitch = hmap.get(DigitalChannel.class, "StartLS");
        intakeLimitSwitch = hmap.get(DigitalChannel.class, "IntakeLS");
        this.tele = tele;
        this.magazineAngle = magazineAngle;
        slideMotorRight.setPower(0);
        slideMotorLeft.setPower(0);
        slideMotorLeft.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        slideMotorRight.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
    }

    public void checkEquivalenceAndAdjust(){
        int difference = slideMotorRight.getCurrentPosition() - slideMotorLeft.getCurrentPosition();
        if (difference>=1){
            slideMotorLeft.setTargetPosition(slideMotorRight.getCurrentPosition());
        }
    }

    public int getTargetLevel(){
        return targetLevel;
    }


    public boolean checkStartPosIsRight(){
        boolean out = hitLimitSwitch;
        tele.addData("Slides are at correct Positions", out);
        return out;
    }
    public boolean isIntakeSwitchHit(){
        boolean out = !intakeLimitSwitch.getState();
        if (out){hitLimitSwitch = true;}
        return out;
    }
    public boolean isStartLimitSwitchHit(){
        boolean out = !startLimitSwitch.getState();
        if(out){hitLimitSwitch = true;}
        return out;
    }

    public void toShoot(){
        slideMotorRight.setTargetPosition(-upPos);
        slideMotorLeft.setTargetPosition(upPos);
        slideMotorLeft.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        slideMotorRight.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        slideMotorRight.setPower(0.9);
        slideMotorLeft.setPower(0.9);
        targetLevel = 2;
        slidesRunning = true;
    }
    public void isLimitSwitchHitAndStopIfSo(){
        if (isIntakeSwitchHit() && slidesRunning && (targetLevel == 1)){
            slideMotorRight.setPower(0);
            slideMotorLeft.setPower(0);
            checkEquivalenceAndAdjust();
            magazineAngle.magAngleBackward();
            slidesRunning = false;
        }
    }
    public boolean isAtCorrectPositionToShoot(){
        if (slideMotorRight.getCurrentPosition() == slideMotorRight.getTargetPosition() && targetLevel == 2 && slidesRunning){
            slideMotorRight.setPower(0);
            slideMotorLeft.setPower(0);

            checkEquivalenceAndAdjust();
            slidesRunning = false;
            magazineAngle.magAngleForward();
            return true;
        }
        return false;
    }

    public boolean checkIfAtShoot(){
        if (slideMotorRight.getCurrentPosition() == slideMotorRight.getTargetPosition() && targetLevel == 2){
            slideMotorRight.setPower(0);
            slideMotorLeft.setPower(0);
            checkEquivalenceAndAdjust();
            slidesRunning = false;
            magazineAngle.magAngleForward();
            return true;
        }
        return slideMotorRight.getCurrentPosition() == slideMotorRight.getTargetPosition();
    }

    public boolean isSlidesRunning(){return slidesRunning;}

    public void toFlowerOuttake(){
        slideMotorRight.setTargetPosition(flowerOuttake);
        slideMotorLeft.setTargetPosition(-flowerOuttake);
        slidesRunning = true;
    }
    public void toIntake(){
        slideMotorRight.setTargetPosition(-gameStartPosReal);
        slideMotorLeft.setTargetPosition(gameStartPosReal);
        slideMotorLeft.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        slideMotorRight.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        slideMotorRight.setPower(0.9);
        slideMotorLeft.setPower(0.9);
        targetLevel = 1;
        slidesRunning= true;
    }
}