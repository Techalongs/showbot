package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

@TeleOp(name = "Walking Ferdinand")
public class WalkingFerdinand extends LinearOpMode {

    @Override
    public void runOpMode() {
        final double POSITION_ANGLE = 0.6; // Increase = more angled (less steep)

        Robot ferdinand = new Robot(hardwareMap, telemetry, this, 0.9);
        ferdinand.init(POSITION_ANGLE);

        telemetry.addData("Status", "Initialized");
        telemetry.update();
        waitForStart();

        while (opModeIsActive()) {
            if (opModeIsActive()) {
                // Drivetrain controls
                double y = 0;

                if (gamepad1.right_bumper) ferdinand.drive(1, gamepad1, y); // Standard - 0.84
                else if (gamepad1.left_bumper) ferdinand.drive(0.7, gamepad1, y); // Standard - 0.4
                else ferdinand.drive(1, gamepad1, y);
                // Set normal speed to 0.5 at beginning of next season - for practice

                // Drone controls
                if (gamepad1.dpad_up && gamepad1.left_trigger > 0.5) ferdinand.launchDrone(POSITION_ANGLE);

                ferdinand.displayData();
            }
        }
    }
}
