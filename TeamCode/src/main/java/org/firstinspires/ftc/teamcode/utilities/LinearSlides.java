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
    final int upPos = 1150;        // TODO: tune toward 29 in
    final int flowerOuttake = 860; // TODO: tune. Midpoint between intake and shoot
    public static double SLIDE_POWER = 0.9;
    public static double BUSY_CHECK_DELAY = 0.1; // seconds before trusting isBusy() after a command
    public static int START_TILT_TICKS = 0; // magazine goes to intake the moment the lift starts
    public static double UP_POWER = 0.6;       // TODO: tune. Speed limit going up (both sides)
    public static double DOWN_POWER = 0.35;    // speed limit going down (right/master)
    public static double SLOW_POWER = 0.12;    // TODO: tune. Creep speed for the final approach
    public static int SLOW_ZONE_TICKS = 400;   // TODO: tune. Start creeping this far before the expected stop
    public static int intakeTuneOffset = 5;  // ticks ABOVE intake to stop at (keeps intake off the ground). Tune.
    public static int INTAKE_SEEK_TICKS = 0;  // never aim below intake
    public static int TRIM_TICKS = 20; // per gamepad press when trimming the intake height
    public static double HOMING_POWER = 0.3;   // TODO: tune. Slow downward power while searching for a switch
    public static double HOMING_TIMEOUT = 6.0; // seconds before giving up
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
    private boolean movingDown = false;
    private boolean slowed = false;
    private int slowRefRaw = 0; // right-motor position where the move should really stop (slow zone measured from here)
    private boolean homing = false;
    private boolean homingFailed = false;
    private final ElapsedTime homingTimer = new ElapsedTime();

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
        int pos = (index == 0) ? gameStartPosReal + intakeTuneOffset : levelPositions[index];
        driveTo(pos);
    }
    public void levelUp() {
        if (homing) return;
        if (levelIndex == -1 && startStep == 0) { startInitialManeuver(); return; } // first press: tilt + lift
        if (startStep == 0) goToLevel(levelIndex + 1);
    }
    public void levelDown() { if (!homing && startStep == 0) goToLevel(levelIndex - 1); }
    /** Manual trim at intake (positive = up). Kept for later arrivals at intake. */
    public void trimIntake(int ticks) {
        if (levelIndex != 0 || moving || homing || startStep != 0) return;
        intakeTuneOffset += ticks;
        setTargetsRaw(slideMotorRight.getTargetPosition() - ticks); // right counts negative going up
    }

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
    /** Recovery: slides at an unknown height. Find a limit switch and continue from there. */
    public void startRecoveryHoming() {
        homingFailed = false;
        if (isStartLimitSwitchHit()) { startInitialManeuver(); return; } // at the very bottom: normal deploy
        if (isIntakeSwitchHit()) { setIntakeReferenceAndHold(); return; } // already at intake
        levelIndex = -1;
        slideMotorRight.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        slideMotorRight.setPower(HOMING_POWER); // right counts toward 0 going down
        slideMotorLeft.setTargetPosition(-slideMotorRight.getCurrentPosition());
        slideMotorLeft.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        slideMotorLeft.setPower(SLIDE_POWER);
        homing = true;
        homingTimer.reset();
    }
    public boolean isHoming() { return homing; }
    public boolean didHomingFail() { return homingFailed; }

    /** Treat the current spot (on IntakeLS) as the intake height and hold here. */
    private void setIntakeReferenceAndHold() {
        int curR = slideMotorRight.getCurrentPosition();
        offsetR = curR + gameStartPosReal;
        levelIndex = 0;
        setTargetsRaw(curR - intakeTuneOffset);
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
        // Recovery homing: slow descent until a limit switch is found
        if (homing) {
            slideMotorLeft.setTargetPosition(-slideMotorRight.getCurrentPosition()); // left follows right
            if (isIntakeSwitchHit()) {
                homing = false;
                setIntakeReferenceAndHold();
            } else if (isStartLimitSwitchHit()) {
                homing = false;
                startInitialManeuver(); // went below intake: do the normal deploy
            } else if (homingTimer.seconds() > HOMING_TIMEOUT) {
                homing = false;
                homingFailed = true;
                setTargetsRaw(slideMotorRight.getCurrentPosition()); // stop and hold where it is
            }
        }
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

        // Controlled descent: left follows right; right creeps once near the expected stop
        if (moving && movingDown) {
            slideMotorLeft.setTargetPosition(-slideMotorRight.getCurrentPosition());
            if (!slowed) {
                int remaining = slowRefRaw - slideMotorRight.getCurrentPosition(); // ticks left until the expected stop
                if (remaining <= SLOW_ZONE_TICKS) {
                    slideMotorRight.setPower(SLOW_POWER);
                    slowed = true;
                }
            }
        }

        // End of move: left mirrors right, then full power to hold firmly
        if (moving && moveTimer.seconds() > BUSY_CHECK_DELAY && !slideMotorRight.isBusy()) {
            mirrorLeftToRight();
            slideMotorRight.setPower(SLIDE_POWER);
            slideMotorLeft.setPower(SLIDE_POWER);
            moving = false;
            movingDown = false;
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
        t.addData("Homing", homing ? "searching..." : (homingFailed ? "FAILED (no switch found)" : "done"));
        t.addData("Intake trim (ticks)", intakeTuneOffset);
    }

    // ================= PRIVATE HELPERS (all motor commands go through here) =================
    /** pos = slide height in ticks (positive = up). Going down: slow creep near the expected stop. */
    private void driveTo(int pos) {
        boolean down = (-pos + offsetR) > slideMotorRight.getCurrentPosition(); // right counts toward 0 going down
        slowRefRaw = -pos + offsetR;                            // the real stop height
        if (down && levelIndex == 0) pos -= INTAKE_SEEK_TICKS;  // aim a little below; IntakeLS stops it
        int raw = -pos + offsetR;
        if (down) setTargetsRaw(raw, DOWN_POWER, SLIDE_POWER);  // right limits speed, left follows
        else      setTargetsRaw(raw, UP_POWER, UP_POWER);       // going up: both sides same speed
        movingDown = down;
        if (down) slideMotorLeft.setTargetPosition(-slideMotorRight.getCurrentPosition());
    }

    /** Sets right to rawRightTarget, left to its mirror, and runs both. */
    /** Sets right to rawRightTarget, left to its mirror, and runs both at full power. */
    private void setTargetsRaw(int rawRightTarget) {
        setTargetsRaw(rawRightTarget, SLIDE_POWER, SLIDE_POWER);
    }

    /** Sets right to rawRightTarget, left to its mirror, with separate power limits per side. */
    private void setTargetsRaw(int rawRightTarget, double rightPower, double leftPower) {
        slideMotorRight.setTargetPosition(rawRightTarget);
        slideMotorLeft.setTargetPosition(-rawRightTarget);
        slideMotorRight.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        slideMotorLeft.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        slideMotorRight.setPower(rightPower);
        slideMotorLeft.setPower(leftPower);
        moving = true;
        movingDown = false;
        slowed = false;
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