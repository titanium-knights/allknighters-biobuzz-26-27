package org.firstinspires.ftc.teamcode.utilities;

import android.icu.number.NumberFormatter;

import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import com.qualcomm.robotcore.hardware.NormalizedColorSensor;
import com.qualcomm.robotcore.hardware.NormalizedRGBA;

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
        this.tele = tele;
    }
    public void RegisterBallIntake(color intakedBallColor){
        loadedBalls.pop();
        loadedBalls.add(intakedBallColor);
    }
    public void RegisterBallShot(){
        loadedBalls.pop();
        loadedBalls.add(color.EMPTY);
    }
    public color rgbToColor(Telemetry tele){
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

        // Purple telemetry: red=[0.284,0.2944] green=[0.3268,0.3754] blue=[0.4273,0.4636]
        double lowboundRedP = 0.25;
        double lowboundGreenP = 0.29;
        double lowboundBlueP = 0.39;

        double highboundRedP = 0.33;
        double highboundGreenP = 0.41;
        double highboundBlueP = 0.50;

        // Green telemetry: red=[0.3736,0.3913] green=[0.6127,0.6272] blue=[0.4064,0.4433]
        double lowboundRedG = 0.34;
        double lowboundGreenG = 0.58;
        double lowboundBlueG = 0.37;

        double highboundRedG = 0.42;
        double highboundGreenG = 0.66;
        double highboundBlueG = 0.48;

        // Empty telemetry: red=[0.3147,0.4539] green=[0.4431,0.5364] blue=[0.2804,0.3301]
        // Empty excluded by: green too low for Green range, blue too low for Purple range
        /*
        if ((normRedR < highboundRedG && normRedR > lowboundRedG && normGreenR > lowboundGreenG && normGreenR < highboundGreenG && normBlue1 > lowboundBlueG && normBlue1 < highboundBlueG) || (normRed2 < highboundRedG && normRed2 > lowboundRedG && normGreen2 > lowboundGreenG && normGreen2 < highboundGreenG && normBlue2 > lowboundBlueG && normBlue2 < highboundBlueG)) {
            tele.addData("Detected Color", "Green");
            return color.RED;
        }

         */
        //else if ((normRed1 < highboundRedP && normRed1 > lowboundRedP && normGreen1 > lowboundGreenP && normGreen1 < highboundGreenP && normBlue1 < highboundBlueP && normBlue1 > lowboundBlueP) || (normRed2 < highboundRedP && normRed2 > lowboundRedP && normGreen2 > lowboundGreenP && normGreen2 < highboundGreenP && normBlue2 < highboundBlueP && normBlue2 > lowboundBlueP)) {
            //tele.addData("Detected Color", "Purple");
            //return color.BLUE;
        //}
        /*
        else {
            tele.addData("Detected Color", "Empty");
            return color.EMPTY;
        }
        */
        return color.EMPTY;
    }
}
