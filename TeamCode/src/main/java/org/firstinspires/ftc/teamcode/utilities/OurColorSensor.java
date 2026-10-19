package org.firstinspires.ftc.teamcode.utilities;

import android.icu.number.NumberFormatter;

import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import com.qualcomm.robotcore.hardware.NormalizedColorSensor;
import com.qualcomm.robotcore.hardware.NormalizedRGBA;
import com.qualcomm.robotcore.hardware.CRServo;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.LinkedList;

//it's called that cuz ColorSensor is a class apparently or something
public class OurColorSensor {
    public NormalizedColorSensor colorSensorR;
    public NormalizedColorSensor colorSensorL;
    public color lastColor;
    public color currentColor;
    public Telemetry tele;

    public CRServo insideServoR;
    public CRServo insideServoL;

    private double power = 0.9;

    private NumberFormat format = new DecimalFormat("#0.0000");
    public enum color{
        BLUE,
        RED,
        YELLOW,
        EMPTY
    }

    public LinkedList<color> loadedBalls = new LinkedList<>(); //
    public OurColorSensor(HardwareMap hmap, Telemetry tele){
        colorSensorR = hmap.get(NormalizedColorSensor.class, "CSRight");
        colorSensorL = hmap.get(NormalizedColorSensor.class, "CSLeft");
        insideServoR = hmap.get(CRServo.class, "ServoR");
        insideServoL = hmap.get(CRServo.class, "ServoL");
        this.tele = tele;
    }
    public void magazineTelemetry(Telemetry tele) {
        tele.addData("Magazine", loadedBalls);
    }
    public void startIntake(){
        insideServoL.setPower(-power);
        insideServoR.setPower(-power);
    }
    public void stopIntake(){
        insideServoL.setPower(0);
        insideServoR.setPower(0);
    }
    public void RegisterBallIntake(color intakedBallColor){
        loadedBalls.pop();
        loadedBalls.add(intakedBallColor);
    }
    public void RegisterBallShot(){
        loadedBalls.pop();
        loadedBalls.add(color.EMPTY);
    }
    public color detectColor(Telemetry tele){
        NormalizedRGBA colorsR = colorSensorR.getNormalizedColors();
        NormalizedRGBA colorsL = colorSensorL.getNormalizedColors();

        float redR, blueR, greenR, alphaR;
        redR = colorsR.red;
        blueR = colorsR.blue;
        greenR = colorsR.green;
        alphaR = colorsR.alpha;

        float redL, blueL, greenL, alphaL;
        redL = colorsL.red;
        blueL = colorsL.blue;
        greenL = colorsL.green;
        alphaL = colorsL.alpha;

        float normRedR, normBlueR, normGreenR;
        normRedR = 10 * redR / alphaR;
        normBlueR = 10 * blueR / alphaR;
        normGreenR = 10 * greenR / alphaR;

        float normRedL, normBlueL, normGreenL;
        normRedL = 10 * redL / alphaL;
        normBlueL = 10 * blueL / alphaL;
        normGreenL = 10 * greenL / alphaL;

        tele.addData("Right Color RGB", format.format(normRedR)  +", "+ format.format(normBlueR) +", "+ format.format(normBlueR));
        tele.addData("Left Color RGB", format.format(normRedL) + ", "+ format.format(normGreenL)+", "+format.format(normBlueL));

        // Purple telemetry: yet to be done
        double lowboundRedR = -1;
        double lowboundGreenR = -1;
        double lowboundBlueR = -1;

        double highboundRedR = -1;
        double highboundGreenR = -1;
        double highboundBlueR = -1;

        // Green telemetry: yet to be done
        double lowboundRedB = -1;
        double lowboundGreenB = -1;
        double lowboundBlueB = -1;

        double highboundRedB = -1;
        double highboundGreenB = -1;
        double highboundBlueB = -1;

        // Yellow telemetry
        double lowboundRedY = -1;
        double lowboundGreenY = -1;
        double lowboundBlueY = -1;

        double highboundRedY = -1;
        double highboundGreenY = -1;
        double highboundBlueY = -1;

        // Empty excluded by: green too low for Green range, blue too low for Purple range

        if ((normRedR < highboundRedR && normRedR > lowboundRedR && normGreenR > lowboundGreenR && normGreenR < highboundGreenR && normBlueL > lowboundBlueR && normBlueL < highboundBlueR) || (normRedL < highboundRedR && normRedL > lowboundRedR && normGreenL > lowboundGreenR && normGreenL < highboundGreenR && normBlueL > lowboundBlueR && normBlueL < highboundBlueR)) {
            tele.addData("Detected Color", "Red");
            return color.RED;
        }

        else if ((normRedR < highboundRedB && normRedR > lowboundRedB && normGreenR > lowboundGreenB && normGreenR < highboundGreenB && normBlueR < highboundBlueB && normBlueR > lowboundBlueB) || (normRedL < highboundRedB && normRedL > lowboundRedB && normGreenL > lowboundGreenB && normGreenL < highboundGreenB && normBlueL < highboundBlueB && normBlueL > lowboundBlueB)) {
            tele.addData("Detected Color", "Blue");
            return color.BLUE;
        }

        else if ((normRedR < highboundRedY && normRedR > lowboundRedY && normGreenR > lowboundGreenY && normGreenR < highboundGreenY && normBlueR < highboundBlueY && normBlueR > lowboundBlueY) || (normRedL < highboundRedY && normRedL > lowboundRedY && normGreenL > lowboundGreenY && normGreenL < highboundGreenY && normBlueL < highboundBlueY && normBlueL > lowboundBlueY)) {
            tele.addData("Detected Color", "Yellow");
            return color.YELLOW;
        }

        else {
            tele.addData("Detected Color", "Empty");
            return color.EMPTY;
        }
    }
}
