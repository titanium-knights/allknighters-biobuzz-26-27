package org.firstinspires.ftc.teamcode.utilities;
import com.qualcomm.hardware.limelightvision.LLResult;
import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.Telemetry;

public class Limelight {
    public Limelight3A limelight3A;
    private LLResult result;
    private enum limeLightPipeline {}
    private final Telemetry telemetry;
    final private double dLLtG = 6.7; //distance from LimeLight to Ground; Inches
    final private double aLLfG = 0; //angle from LimeLight from Ground; Degrees
    public Limelight(HardwareMap hmap, Telemetry telemetry){
        limelight3A.setPollRateHz(100);
        limelight3A.start();
        this.telemetry = telemetry;
    }
    public void update(){
        result = limelight3A.getLatestResult();
    }
    public double getLimeLightInfoSpecific(String whichData){
        switch(whichData){
            case "tx": return result.getTx();
            case "ty": return result.getTy();
            case "ta": return result.getTa();
        }
        return -1;
    }

    public LLResult getLimeLightInfoAll(){
        return result;
    }

    public double calculateGoalHeight(double distanceToGoal){
        double goalHeight;
        double bonusAngle = result.getTy();
        double totalAngle = bonusAngle + aLLfG;
        double totalAngleRadians = (totalAngle * Math.PI)/180;
        goalHeight = Math.tan(totalAngleRadians)*distanceToGoal;
        return goalHeight + dLLtG;
    }
    public boolean goalUp(double distanceToGoal){
        return calculateGoalHeight(distanceToGoal)>-1; //replace -1 with up goal height
    }

    public double calculateDistanceToGoalHypotenuse(double goalHeight){
        double distanceToGoal;
        double bonusAngle = result.getTy();
        double totalAngle = bonusAngle + aLLfG;
        double totalAngleRadians = (totalAngle * Math.PI)/180;
        distanceToGoal = (goalHeight+dLLtG) / Math.sin(totalAngleRadians);
        return distanceToGoal;
    }

    public void addToTele(){
        telemetry.addData("Target x", result.getTx());
        telemetry.addData("Target y", result.getTy());
        telemetry.addData("Target Area", result.getTa());
    }


}
