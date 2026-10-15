package org.firstinspires.ftc.teamcode.utilities;

import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import com.qualcomm.robotcore.hardware.NormalizedColorSensor;
import com.qualcomm.robotcore.hardware.NormalizedRGBA;

import java.util.ArrayList;
import java.util.LinkedList;

//its called that cuz ColorSensor is a class apparently or something
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

        float redF, blueF, greenF, alphaF;
        redF = colorsF.red;
        blueF = colorsF.blue;
        greenF = colorsF.green;
        alphaF = colorsF.alpha;

        float redB, blueB, greenB, alphaB;
        redB = colorsB.red;
        blueB = colorsB.blue;
        greenB = colorsB.green;
        alphaB = colorsB.alpha;

        float normRedF, normBlueF, normGreenF;
        normRedF = 10 * redF / alphaF;
        normBlueF = 10 * blueF / alphaF;
        normGreenF = 10 * greenF / alphaF;

        float normRedB, normBlueB, normGreenB;
        normRedB = 10 * redB / alphaB;
        normBlueB = 10 * blueB / alphaB;
        normGreenB = 10 * greenB / alphaB;

        tele.addData("Front Color RGB", normRedF +""+ normGreenF +""+ normBlueF);
        tele.addData("Back Color RGB", normRedB + ""+ normGreenB+""+normBlueB);

        return color.EMPTY;//placeholder
    }
}
