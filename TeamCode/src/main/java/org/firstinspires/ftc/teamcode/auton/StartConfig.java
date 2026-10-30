package org.firstinspires.ftc.teamcode.auton;

/**
 * The four field quadrants, in field coordinates (inches, origin bottom-left corner, red on the left):
 *   RED_LOWER  x<72, y<72 (contains the 0,0 corner)   RED_UPPER  x<72, y>72
 *   BLUE_LOWER x>72, y<72 (next to RED_LOWER)         BLUE_UPPER x>72, y>72 (opposite RED_LOWER)
 *
 * RED_LOWER comes from the team's RedLowerShooting path. The others are derived, not measured:
 * BLUE_UPPER is RED_LOWER rotated 180 degrees about the field center, BLUE_LOWER is RED_UPPER rotated
 * the same way, and RED_UPPER starts on the top wall at x=30 to stay clear of the top-wall FLOWER (x~48).
 * Each shoot corner is the field corner of its quadrant, aimed at the HIVE.
 * TODO: confirm the four start spots and shoot spots on the real field.
 */
public enum StartConfig {
    //        alliance        start x, y, heading     leave x, y     shoot x, y, heading
    RED_LOWER(Alliance.RED,   46.5,   8,   90,        46.5,  12,     12.3,  13.9,   45),
    RED_UPPER(Alliance.RED,   30,   136,  -90,        30,   132,     12.3, 130.1,  -45),
    BLUE_LOWER(Alliance.BLUE, 114,    8,   90,        114,   12,    131.7,  13.9,  135),
    BLUE_UPPER(Alliance.BLUE, 97.5, 136,  -90,        97.5, 132,    131.7, 130.1, -135);

    public final Alliance alliance;
    public final double[] start;
    public final double startHeadingDeg;
    public final double[] leave;
    public final double[] shoot;
    public final double shootHeadingDeg;

    StartConfig(Alliance alliance, double startX, double startY, double startHeadingDeg,
                double leaveX, double leaveY, double shootX, double shootY, double shootHeadingDeg) {
        this.alliance = alliance;
        this.start = new double[] {startX, startY};
        this.startHeadingDeg = startHeadingDeg;
        this.leave = new double[] {leaveX, leaveY};
        this.shoot = new double[] {shootX, shootY};
        this.shootHeadingDeg = shootHeadingDeg;
    }
}
