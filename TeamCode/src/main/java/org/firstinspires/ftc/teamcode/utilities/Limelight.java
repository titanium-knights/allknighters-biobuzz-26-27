package org.firstinspires.ftc.teamcode.utilities;

import com.qualcomm.hardware.limelightvision.LLResult;
import com.qualcomm.hardware.limelightvision.LLResultTypes;
import com.qualcomm.hardware.limelightvision.LLStatus;
import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.navigation.Pose3D;

public class Limelight {
    private static final double CAMERA_HEIGHT_INCHES = 6.7;
    private static final double CAMERA_PITCH_DEGREES = 0.0;
    private static final double rightTurretx = -1; //placeholder
    private static final double rightTurrety = -1; //placeholder

    private static final double leftTurretx = -1;
    private static final double leftTurrety = -1;



    public double horizontalDistance;
    private final Limelight3A limelight3A;
    private LLResult result;
    private Pose3D cordResult;
    private LLResultTypes.FiducialResult currentApriltag;
    private final Telemetry telemetry;

    //
    /*Math Plan for turrents:
    1. Get horizontal distance to location
    2. Get angle to limelight
    3. Mark the coordinate of the Apriltag
    4. Use the coordinate of the shooter to find the slope of the line from the apriltag to the shooter
    find the
     */

    public Limelight(HardwareMap hmap, Telemetry telemetry) {
        limelight3A = hmap.get(Limelight3A.class, CONFIG.LIMELIGHT);
        limelight3A.setPollRateHz(100);
        limelight3A.start();
        this.telemetry = telemetry;
    }

    public void update() {
        result = limelight3A.getLatestResult();
        cordResult = result.getBotpose_MT2();
        currentApriltag = getFiducial();
    }
    public LLResultTypes.FiducialResult getFiducial(){
        return result.getFiducialResults().get(0);
    }

    public double alignRight(){
        double x = currentApriltag.getTargetPoseRobotSpace().getPosition().x;
        double y = currentApriltag.getTargetPoseRobotSpace().getPosition().y;
        double horizontalDistance =  Math.sqrt(Math.pow(x,2) + Math.pow(y, 2));
        double distanceTurretToTag = Math.sqrt(Math.pow(x-rightTurretx, 2) + Math.pow(y-rightTurrety, 2));
        return Math.asin((Math.sin(result.getTx()) * horizontalDistance)/distanceTurretToTag);
    }

    public double alignLeft(){
        double x = currentApriltag.getTargetPoseRobotSpace().getPosition().x;
        double y = currentApriltag.getTargetPoseRobotSpace().getPosition().y;
        double horizontalDistance = Math.sqrt(Math.pow(x,2) + Math.pow(y,2));
        double distanceTurretToTag = Math.sqrt(Math.pow(x-leftTurretx, 2) + Math.pow(y-rightTurrety, 2));
        return Math.asin(Math.sin(result.getTx()) * horizontalDistance)/distanceTurretToTag;
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

    public long getStaleness(){return result.getStaleness();}


    public void stop() {
        limelight3A.stop();
        result = null;
    }
}
