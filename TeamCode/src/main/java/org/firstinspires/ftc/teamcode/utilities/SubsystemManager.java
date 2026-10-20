package org.firstinspires.ftc.teamcode.utilities;

import com.qualcomm.robotcore.hardware.HardwareMap;
import org.firstinspires.ftc.robotcore.external.Telemetry;



/**
 * This class is used to encapsulate all utility classes for subsystems.
 * Any OpMode (teleop or auton) can initialize this class and access any utility class
 * The instance of this object should be passed to any TeleopState to provide access to utility classes
 * This prevents the need to pass references to several utility classes to each TeleopState
 */

public class SubsystemManager {
    public final SimpleMecanumDrive drive;
    private final HardwareMap hardwareMap;
    private final Telemetry telemetry;
    private Limelight limeLight;
    public Intake intake;
    public LinearSlides linearSlides;
    public MagazineAngle magazineAngle;
    public MagazineBelt magazineBelt;
    public MagazineServos magazineServos;
    public OurColorSensor colorSensors;
    public OuttakeHood outtakeHood;
    public ShootingLogic shootingLogic;

    public SubsystemManager(HardwareMap hmap, Telemetry telemetry) {
        drive = new SimpleMecanumDrive(hmap);
        hardwareMap = hmap;
        this.telemetry = telemetry;
        limeLight = new Limelight(hmap, telemetry);
        intake = new Intake(hmap);
        linearSlides = new LinearSlides(hmap,telemetry);
        magazineAngle = new MagazineAngle(hmap);
        magazineBelt = new MagazineBelt(hmap);
        magazineServos = new MagazineServos(hmap);
        colorSensors = new OurColorSensor(hmap, telemetry);
        outtakeHood = new OuttakeHood(hmap);
        shootingLogic = new ShootingLogic(hmap, telemetry, colorSensors, magazineBelt, outtakeHood);
    }

    public Limelight getLimelight() {
        if (limeLight == null) {
            limeLight = new Limelight(hardwareMap, telemetry);
        }
        return limeLight;
    }
}
