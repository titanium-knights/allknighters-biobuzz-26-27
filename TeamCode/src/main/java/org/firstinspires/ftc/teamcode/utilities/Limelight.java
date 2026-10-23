package org.firstinspires.ftc.teamcode.utilities;

import com.qualcomm.hardware.limelightvision.LLResult;
import com.qualcomm.hardware.limelightvision.LLStatus;
import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.Telemetry;

public class Limelight {
    private static final double CAMERA_HEIGHT_INCHES = 6.7;
    private static final double CAMERA_PITCH_DEGREES = 0.0;

    private final Limelight3A limelight3A;
    private LLResult result;
    private final Telemetry telemetry;

    public Limelight(HardwareMap hmap, Telemetry telemetry) {
        limelight3A = hmap.get(Limelight3A.class, CONFIG.LIMELIGHT);
        limelight3A.setPollRateHz(100);
        limelight3A.start();
        this.telemetry = telemetry;
    }

    public void update() {
        result = limelight3A.getLatestResult();
    }

    public boolean hasTarget() {
        return result != null && result.isValid();
    }

    public double getLimeLightInfoSpecific(String whichData) {
        if (!hasTarget()) {
            return Double.NaN;
        }

        switch (whichData) {
            case "tx":
                return result.getTx();
            case "ty":
                return result.getTy();
            case "ta":
                return result.getTa();
            default:
                throw new IllegalArgumentException("Unknown Limelight data: " + whichData);
        }
    }

    public LLResult getLimeLightInfoAll() {
        return result;
    }

    public double calculateGoalHeight(double distanceToGoal) {
        if (!hasTarget()) {
            return Double.NaN;
        }

        double totalAngleRadians = Math.toRadians(result.getTy() + CAMERA_PITCH_DEGREES);
        return CAMERA_HEIGHT_INCHES + Math.tan(totalAngleRadians) * distanceToGoal;
    }

    public boolean goalUp(double distanceToGoal, double upGoalHeightInches) {
        return hasTarget() && calculateGoalHeight(distanceToGoal) >= upGoalHeightInches;
    }

    public double calculateDistanceToGoalHypotenuse(double goalHeight) {
        if (!hasTarget()) {
            return Double.NaN;
        }

        double totalAngleRadians = Math.toRadians(result.getTy() + CAMERA_PITCH_DEGREES);
        double sine = Math.sin(totalAngleRadians);
        if (Math.abs(sine) < 1e-6) {
            return Double.NaN;
        }

        double distanceToGoal = (goalHeight - CAMERA_HEIGHT_INCHES) / sine;
        return distanceToGoal >= 0 ? distanceToGoal : Double.NaN;
    }

    public void addToTele() {
        LLStatus status = limelight3A.getStatus();
        if (status != null) {
            telemetry.addData("Pipeline", "%d (%s)", status.getPipelineIndex(), status.getPipelineType());
            telemetry.addData("FPS", status.getFps());
        }

        if (!hasTarget()) {
            telemetry.addData("Target", "No valid target");
            return;
        }

        telemetry.addData("Target x", result.getTx());
        telemetry.addData("Target y", result.getTy());
        telemetry.addData("Target Area", result.getTa());
    }

    public void stop() {
        limelight3A.stop();
        result = null;
    }
}
