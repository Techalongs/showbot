package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import java.util.function.Supplier;

@TeleOp(name = "Winching Ferdinand")
public class WinchingFerdinand extends LinearOpMode {

    @Override
    public void runOpMode() {
        final double POSITION_ANGLE = 0.6; // Increase = more angled (less steep)

        // WARNING: I don't know if winch right-vs-left is actually right.
        //          It may be reversed due to the motor orientation.
        //          But we haven't tested yet.
        Supplier<Float> winchControl = () -> gamepad1.right_trigger - gamepad1.left_trigger;
        Supplier<Boolean> droneControl = () -> gamepad1.dpad_up && gamepad1.y;
        Supplier<Boolean> fastDriveToggle = () -> gamepad1.right_bumper;
        Supplier<Boolean> slowDriveToggle = () -> gamepad1.left_bumper;

        Robot ferdinand = new Robot(hardwareMap, telemetry, this, 0.9);
        ferdinand.setWinchPowerSupplier(winchControl);
        ferdinand.init(POSITION_ANGLE);

        telemetry.addData("Status", "Initialized");
        telemetry.update();
        waitForStart();

        while (opModeIsActive()) {
            if (opModeIsActive()) {
                // Drivetrain controls
                if (fastDriveToggle.get()) {
                    ferdinand.drive(0.5, gamepad1); // Standard - 0.84
                } else if (slowDriveToggle.get()) {
                    ferdinand.drive(0.1, gamepad1); // Standard - 0.4
                } else {
                    ferdinand.drive(0.3, gamepad1);
                }

                // Winch controls
                ferdinand.updateWinch();

                // Drone controls
                if (droneControl.get()) {
                    ferdinand.launchDrone(POSITION_ANGLE);
                }

                ferdinand.displayData();
            }
        }
    }

}
