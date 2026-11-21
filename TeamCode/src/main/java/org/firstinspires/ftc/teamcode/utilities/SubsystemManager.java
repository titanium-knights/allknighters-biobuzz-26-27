package org.firstinspires.ftc.teamcode.utilities;

import com.qualcomm.robotcore.hardware.HardwareMap;
import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.utilities.unused.LinearSlides;
import org.firstinspires.ftc.teamcode.utilities.unused.MagazineAngle;
import org.firstinspires.ftc.teamcode.utilities.unused.MagazineBelt;
import org.firstinspires.ftc.teamcode.utilities.unused.MagazineServos;
import org.firstinspires.ftc.teamcode.utilities.unused.OuttakeHood;


/**
 * This class is used to encapsulate all utility classes for subsystems.
 * Any OpMode (teleop or auton) can initialize this class and access any utility class
 * The instance of this object should be passed to any TeleopState to provide access to utility classes
 * This prevents the need to pass references to several utility classes to each TeleopState
 */

public class SubsystemManager {
    public final SimpleMecanumDrive drive;
    private final HardwareMap hardwareMap;
    public final Telemetry telemetry;
    public Limelight limeLight;
    public Intake intake;
    public OurColorSensor colorSensors;
    public ShootingLogic shootingLogic;

    public SubsystemManager(HardwareMap hmap, Telemetry telemetry) {
        drive = new SimpleMecanumDrive(hmap);
        hardwareMap = hmap;
        this.telemetry = telemetry;
        limeLight = new Limelight(hmap, telemetry);
        intake = new Intake(hmap);
        colorSensors = new OurColorSensor(hmap, telemetry);
        shootingLogic = new ShootingLogic(hmap, telemetry, colorSensors);
    }
}
