package org.firstinspires.ftc.teamcode.utilities;
import com.qualcomm.hardware.limelightvision.LLResult;
import com.qualcomm.hardware.limelightvision.LLResultTypes;
import com.qualcomm.hardware.limelightvision.LLStatus;
import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.teleop.Teleop;

public class Limelight {
    public Limelight3A limelight3A;
    private LLResult result;
    private enum limeLightPipeline {};
    private Telemetry telemetry;
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
    public void addToTele(){
        telemetry.addData("Target x", result.getTx());
        telemetry.addData("Target y", result.getTy());
        telemetry.addData("Target Area", result.getTa());
    }


}
