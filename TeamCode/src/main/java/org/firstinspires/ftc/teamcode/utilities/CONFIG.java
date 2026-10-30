package org.firstinspires.ftc.teamcode.utilities;

import com.qualcomm.hardware.limelightvision.LLFieldMap;
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

  //Apriltag IDS. The position is based on looking at the field if the red is on the left
  public static final int backRedLeftTagID = 37;
    public static final int backRedRightTagID = 34;
    public static final int backBlueLeftTagID = 45;
    public static final int backBlueRightTagID = 42;
    public static final int FrontRedLeftTagID = 33;
    public static final int FrontRedRightTagID = 30;
    public static final int FrontBlueLeftTagID = 41;
    public static final int FrontBlueRightTagID = 38;
}
