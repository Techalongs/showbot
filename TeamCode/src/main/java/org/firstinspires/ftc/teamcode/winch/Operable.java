package org.firstinspires.ftc.teamcode.winch;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

public interface Operable {

    LinearOpMode getOpMode();

    default boolean isOpActive() {
        return getOpMode().opModeIsActive();
    }

    default boolean isOpStarted() {
        return getOpMode().isStarted();
    }

    default void waitForOpStart() {
        getOpMode().waitForStart();
    }
}
