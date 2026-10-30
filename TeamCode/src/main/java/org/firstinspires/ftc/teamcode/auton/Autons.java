package org.firstinspires.ftc.teamcode.auton;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

/** The eight autonomous OpModes shown in the Driver Station menu. */
public final class Autons {
    private Autons() {}

    @Autonomous(name = "Red Lower - Leave+Park", group = "Park")
    public static class RedLowerPark extends BaseAuton {
        public RedLowerPark() { super(StartConfig.RED_LOWER, false); }
    }

    @Autonomous(name = "Red Upper - Leave+Park", group = "Park")
    public static class RedUpperPark extends BaseAuton {
        public RedUpperPark() { super(StartConfig.RED_UPPER, false); }
    }

    @Autonomous(name = "Blue Lower - Leave+Park", group = "Park")
    public static class BlueLowerPark extends BaseAuton {
        public BlueLowerPark() { super(StartConfig.BLUE_LOWER, false); }
    }

    @Autonomous(name = "Blue Upper - Leave+Park", group = "Park")
    public static class BlueUpperPark extends BaseAuton {
        public BlueUpperPark() { super(StartConfig.BLUE_UPPER, false); }
    }

    @Autonomous(name = "Red Lower - Leave+Shoot+Park", group = "Shoot")
    public static class RedLowerShoot extends BaseAuton {
        public RedLowerShoot() { super(StartConfig.RED_LOWER, true); }
    }

    @Autonomous(name = "Red Upper - Leave+Shoot+Park", group = "Shoot")
    public static class RedUpperShoot extends BaseAuton {
        public RedUpperShoot() { super(StartConfig.RED_UPPER, true); }
    }

    @Autonomous(name = "Blue Lower - Leave+Shoot+Park", group = "Shoot")
    public static class BlueLowerShoot extends BaseAuton {
        public BlueLowerShoot() { super(StartConfig.BLUE_LOWER, true); }
    }

    @Autonomous(name = "Blue Upper - Leave+Shoot+Park", group = "Shoot")
    public static class BlueUpperShoot extends BaseAuton {
        public BlueUpperShoot() { super(StartConfig.BLUE_UPPER, true); }
    }
}
