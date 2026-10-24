package org.firstinspires.ftc.teamcode.utilities;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DigitalChannel;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.Telemetry;

public class LinearSlides {
    // ================= HARDWARE =================
    public DcMotor slideMotorRight; // MASTER: all positions are based on this motor
    public DcMotor slideMotorLeft;  // FOLLOWER: always mirrors the right motor
    public DigitalChannel startLimitSwitch;
    public DigitalChannel intakeLimitSwitch;
    private Telemetry tele;
    private MagazineAngle magazineAngle;

    // ================= POSITIONS (ticks, positive = up) =================
    final int gameStartPos = -1; // Placeholder (legacy)
    final int gameStartPosReal = 215; // Encoder ticks StartLS -> IntakeLS. Measured with SlideMeasure (R side).
    public static int intakeTuneOffset = 0; // TODO: tune. Ticks added to intake height
    final int flowerOuttake = 1060; // TODO: tune. Arbitrary midpoint between intake and shoot
    final int upPos = 1964; // Shoot position
    public static double SLIDE_POWER = 0.9;
    public static double BUSY_CHECK_DELAY = 0.1; // seconds before trusting isBusy() after a command
    public static int START_TILT_TICKS = 0; // magazine goes to intake the moment the lift starts

    // Slide positions, lowest to highest
    public int[] levelPositions = {gameStartPosReal + intakeTuneOffset, flowerOuttake, upPos}; // intake, flower, shoot
    public int levelIndex = -1; // -1 = at start (StartLS), not part of the list

    private int startStep = 0; // 0 = idle, 1 = tilting magazine, 2 = moving to intake

    // ================= STATE =================
    private boolean moving = false;
    private final ElapsedTime moveTimer = new ElapsedTime();
    private boolean lastStartHit = false;
    private boolean lastIntakeHit = false;
    private int offsetR = 0; // shifts the right motor's frame after IntakeLS correction

    // Legacy state (old TeleOp)
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

    // ================= PUBLIC API =================
    public void goToLevel(int index) {
        if (index < 0 || index >= levelPositions.length) return;
        levelIndex = index;
        driveTo(levelPositions[index]);
    }
    public void levelUp() {
        if (levelIndex == -1 && startStep == 0) { startInitialManeuver(); return; } // first press: tilt + lift
        if (startStep == 0) goToLevel(levelIndex + 1);
    }
    public void levelDown() { if (startStep == 0) goToLevel(levelIndex - 1); }

    /** Call once at Start. Returns false (and does nothing) if slides are not on StartLS. */
    /** Call once at Start. Returns false (and does nothing) if slides are not on StartLS. */
    public boolean startInitialManeuver() {
        if (!isStartLimitSwitchHit()) return false;
        // Zero here (slides are on StartLS), then tilt and move at the same time
        slideMotorRight.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        slideMotorLeft.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        offsetR = 0;
        goToLevel(0);
        startStep = 1;
        return true;
    }
    public boolean isStartManeuverRunning() { return startStep != 0; }
    public int getLevel()   { return levelIndex; }
    public boolean isMoving() { return moving; }

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

    /** Call once per loop. Handles zeroing, IntakeLS correction, and left-mirrors-right. */
    public void update() {
        // At start: StartLS defines true zero
        boolean startHit = isStartLimitSwitchHit();
        if (levelIndex == -1 && startHit && !lastStartHit) {
            slideMotorRight.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
            slideMotorLeft.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
            offsetR = 0;
        }
        lastStartHit = startHit;
        // Start maneuver: tilt magazine, then move to intake
        magazineAngle.update();
        if (startStep == 1 && -slideMotorRight.getCurrentPosition() >= START_TILT_TICKS) {
            magazineAngle.toIntake();
            startStep = 2;
        } else if (startStep == 2 && !magazineAngle.isTilting() && !moving) {
            startStep = 0; // both tilt and slide move finished
        }

        // At intake: IntakeLS defines intake height (based on right motor), stop and hold here
        boolean intakeHit = isIntakeSwitchHit();
        if (levelIndex == 0 && intakeHit && !lastIntakeHit) {
            int curR = slideMotorRight.getCurrentPosition();
            offsetR = curR + gameStartPosReal;
            setTargetsRaw(curR - intakeTuneOffset);
        }
        lastIntakeHit = intakeHit;

        // End of move: left adjusts to mirror where right actually ended up
        if (moving && moveTimer.seconds() > BUSY_CHECK_DELAY && !slideMotorRight.isBusy()) {
            mirrorLeftToRight();
            moving = false;
        }
    }

    public void addTelemetry(Telemetry t) {
        t.addData("Slide level", levelIndex);
        t.addData("Slide moving", moving);
        t.addData("R pos / target", slideMotorRight.getCurrentPosition() + " / " + slideMotorRight.getTargetPosition());
        t.addData("L pos / target", slideMotorLeft.getCurrentPosition() + " / " + slideMotorLeft.getTargetPosition());
        t.addData("Slide offsetR", offsetR);
        t.addData("Start LS pressed", isStartLimitSwitchHit());
        t.addData("Intake LS pressed", isIntakeSwitchHit());
        t.addData("Start maneuver running", isStartManeuverRunning());
        t.addData("Mag tilting", magazineAngle.isTilting());
    }

    // ================= PRIVATE HELPERS (all motor commands go through here) =================
    /** pos = slide height in ticks (positive = up). */
    private void driveTo(int pos) {
        setTargetsRaw(-pos + offsetR);
    }

    /** Sets right to rawRightTarget, left to its mirror, and runs both. */
    private void setTargetsRaw(int rawRightTarget) {
        slideMotorRight.setTargetPosition(rawRightTarget);
        slideMotorLeft.setTargetPosition(-rawRightTarget);
        slideMotorRight.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        slideMotorLeft.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        slideMotorRight.setPower(SLIDE_POWER);
        slideMotorLeft.setPower(SLIDE_POWER);
        moving = true;
        moveTimer.reset();
    }

    /** Left target = mirror of right's actual position. */
    private void mirrorLeftToRight() {
        slideMotorLeft.setTargetPosition(-slideMotorRight.getCurrentPosition());
    }

    // ================= LEGACY (used by old TeleOp, remove once TeleOp uses levels) =================
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