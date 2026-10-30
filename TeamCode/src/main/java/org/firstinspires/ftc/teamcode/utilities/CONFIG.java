package org.firstinspires.ftc.teamcode.utilities;

import com.qualcomm.robotcore.hardware.DcMotorSimple;

public class CONFIG {
  public static final String LIMELIGHT = "limelight";

  public static final String FRONT_LEFT = "fl";
  public static final DcMotorSimple.Direction FL_DIRECTION = DcMotorSimple.Direction.FORWARD;

  public static final String FRONT_RIGHT = "fr";
  public static final DcMotorSimple.Direction FR_DIRECTION = DcMotorSimple.Direction.FORWARD;

  public static final String BACK_LEFT = "bl";
  public static final DcMotorSimple.Direction BL_DIRECTION = DcMotorSimple.Direction.REVERSE;

  public static final String BACK_RIGHT = "br";
  public static final DcMotorSimple.Direction BR_DIRECTION = DcMotorSimple.Direction.FORWARD;

  // Placeholder hardware names for the two-turret robot; rename to match the robot config.
  public static final String POLLEN_FLYWHEEL = "PollenFlywheel";
  public static final String POLLEN_AIM_SERVO = "PollenAimServo";
  public static final String NECTAR_FLYWHEEL = "NectarFlywheel";
  public static final String NECTAR_AIM_SERVO = "NectarAimServo";
}
