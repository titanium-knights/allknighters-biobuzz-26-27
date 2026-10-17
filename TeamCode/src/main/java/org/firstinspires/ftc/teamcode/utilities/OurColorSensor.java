package org.firstinspires.ftc.teamcode.utilities;

import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import com.qualcomm.robotcore.hardware.NormalizedColorSensor;
import com.qualcomm.robotcore.hardware.NormalizedRGBA;

import java.util.ArrayList;
import java.util.LinkedList;

//it's called that cuz ColorSensor is a class apparently or something
public class OurColorSensor {
    public NormalizedColorSensor colorSensorF;
    public NormalizedColorSensor colorSensorB;
    public color lastColor;
    public color currentColor;
    public Telemetry tele;
    public enum color{
        BLUE,
        RED,
        YELLOW,
        EMPTY
    }

    public LinkedList<color> loadedBalls = new LinkedList<>(); //
    public OurColorSensor(HardwareMap hmap, Telemetry tele){
        colorSensorF = hmap.get(NormalizedColorSensor.class, "ColorSensorFront");
        colorSensorB = hmap.get(NormalizedColorSensor.class, "ColorSensorBack");
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
        NormalizedRGBA colorsF = colorSensorF.getNormalizedColors();
        NormalizedRGBA colorsB = colorSensorB.getNormalizedColors();

        float redR, blueR, greenR, alphaR;
        redR = colorsF.red;
        blueR = colorsF.blue;
        greenR = colorsF.green;
        alphaR = colorsF.alpha;

        float redL, blueL, greenL, alphaL;
        redL = colorsB.red;
        blueL = colorsB.blue;
        greenL = colorsB.green;
        alphaL = colorsB.alpha;

        float normRedF, normBlueF, normGreenF;
        normRedF = 10 * redR / alphaR;
        normBlueF = 10 * blueR / alphaR;
        normGreenF = 10 * greenR / alphaR;

        float normRedB, normBlueB, normGreenB;
        normRedB = 10 * redL / alphaL;
        normBlueB = 10 * blueL / alphaL;
        normGreenB = 10 * greenL / alphaL;

        tele.addData("Front Color RGB", normRedF +""+ normGreenF +""+ normBlueF);
        tele.addData("Back Color RGB", normRedB + ""+ normGreenB+""+normBlueB);

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
        if ((normRedF < highboundRedG && normRedF > lowboundRedG && normGreenF > lowboundGreenG && normGreenF < highboundGreenG && normBlue1 > lowboundBlueG && normBlue1 < highboundBlueG) || (normRed2 < highboundRedG && normRed2 > lowboundRedG && normGreen2 > lowboundGreenG && normGreen2 < highboundGreenG && normBlue2 > lowboundBlueG && normBlue2 < highboundBlueG)) {
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
