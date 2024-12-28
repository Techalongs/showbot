package org.firstinspires.ftc.teamcode.winch;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.slf4j.event.Level;

import java.util.function.Supplier;

@TeleOp(name = "Winch")
public class WinchBot extends LinearOpMode implements Telemetric {

    /** Sets how much data is displayed in telemetry. */
    private Level telemetryLevel = Level.TRACE;

    private Winch lowWinch;
    private Winch highWinch;

    private final double initialWinchSpeedFactor = 0.1;
    private final double winchPowerAdjustment = 0.1;

    // Winch Controls
    private final Gamepad winchGamepad = gamepad2;

    /** Supplier provides the preference for which control to use for low winch power. **/
    private final Supplier<Float> lowWinchPowerSupplier = () -> winchGamepad.right_stick_y;

    /** Supplier provides the preference for which control to use for high winch power. **/
    private final Supplier<Float> highWinchPowerSupplier = () -> -winchGamepad.left_stick_y;

    /**
     * Run linear operation mode.
     */
    @Override
    public void runOpMode() {
        this.init(hardwareMap);
        this.log("Status", "Initialized", true);
        this.waitForStart();
        this.log("Status", "Started", true);

        while (opModeIsActive()) {
            this.handleSettingsChange();
            this.handleWinchControls();
            this.updateTelemetry();
        }
    }

    private void handleWinchControls() {
        if (lowWinch != null) {
            double lowWinchPower = lowWinchPowerSupplier.get();
            lowWinch.setPower(lowWinchPower);
        }
        if (highWinch != null) {
            double highWinchPower = highWinchPowerSupplier.get();
            highWinch.setPower(highWinchPower);
        }
    }

    /**
     * Trying out doing settings changes while in operation.
     * In this case, I set up button combos to change the motor speeds.
     * Also, it speaks.
     */
    private void handleSettingsChange() {
        // A + Left Bumper = Low Winch Speed Factor Down
        // A + Right Bumper = Low Winch Speed Factor Up
        // B + Left Bumper = High Winch Speed Factor Down
        // B + Right Bumper = High Winch Speed Factor Up
        // Low Winch
        if (winchGamepad.a) {
            if (winchGamepad.left_bumper) {
                telemetry.speak("Low Winch Speed Down");
                lowWinch.setMotorPowerLimit(lowWinch.getMotorPowerLimit() - winchPowerAdjustment);
            } else if (winchGamepad.right_bumper) {
                telemetry.speak("Low Winch Speed Up");
                lowWinch.setMotorPowerLimit(lowWinch.getMotorPowerLimit() + winchPowerAdjustment);
            }
        }
        // High Winch
        if (winchGamepad.b) {
            if (winchGamepad.left_bumper) {
                highWinch.setMotorPowerLimit(highWinch.getMotorPowerLimit() - winchPowerAdjustment);
                telemetry.speak("High Winch Speed " + Math.round(highWinch.getMotorPowerLimit() * 100.0) + " percent.");
            } else if (winchGamepad.right_bumper) {
                highWinch.setMotorPowerLimit(highWinch.getMotorPowerLimit() + winchPowerAdjustment);
                telemetry.speak("High Winch Speed " + Math.round(highWinch.getMotorPowerLimit() * 100.0) + " percent.");
            }
        }
    }

    /**
     * Update the display on the driver station.
     */
    private void updateTelemetry() {
        log("Status", opModeIsActive() ? "Active" : "Inactive");
        if (lowWinch != null) {
            lowWinch.logTelemetry();
        }
        if (highWinch != null) {
            highWinch.logTelemetry();
        }
        log("Winch Gamepad", winchGamepad.getUser());
        telemetry.update();
    }

    /**
     * Initialize the bot.
     */
    private void init(final HardwareMap hardware) {

        // NOTE:
        // The winches are possibly nullable because who knows if David has plugged them both in.
        // If a winch is not findable, we'll put an error in telemetry but otherwise proceed.

        // TODO: TeamMotor is a new custom motor wrapper but I haven't tried it out yet.
        /*
        TeamMotor.getOptionalTeamMotor(this.hardwareMap, "LowWinchMotor").ifPresent(motor ->
            lowWinch = new Winch("Low Winch", this, motor)
        );
        if (lowWinch == null) {
            log(Level.WARN, "Low Winch Warning", "Low winch not found");
        }
        TeamMotor.getOptionalTeamMotor(this.hardwareMap, "HighWinchMotor").ifPresent(motor -> {
            motor.setDirection(DcMotorSimple.Direction.REVERSE);
            highWinch = new Winch("High Winch", this, motor);
        });
        if (highWinch == null) {
            log(Level.WARN, "High Winch Warning", "High winch not found");
        }
        */

        try {
            DcMotorEx lowWinchMotor = hardware.get(DcMotorEx.class, "LowWinchMotor");
            lowWinchMotor.setDirection(DcMotorSimple.Direction.FORWARD);
            lowWinchMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
            lowWinchMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
            lowWinchMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            lowWinch = new Winch("Low Winch", this, lowWinchMotor);
            lowWinch.setMotorPowerLimit(initialWinchSpeedFactor);
        } catch (IllegalArgumentException e) {
            telemetry.addData("Low Winch Warning", "LowWinchMotor was not found");
        } catch (Throwable t) {
            telemetry.addData("Low Winch Error", "Fatal: " + t.getMessage());
        }

        try {
            DcMotorEx highWinchMotor = hardware.get(DcMotorEx.class, "HighWinchMotor");
            highWinchMotor.setDirection(DcMotorSimple.Direction.REVERSE);
            highWinchMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
            highWinchMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
            highWinchMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            highWinch = new Winch("High Winch", this, highWinchMotor);
            highWinch.setMotorPowerLimit(initialWinchSpeedFactor);
        } catch (IllegalArgumentException e) {
            telemetry.addData("High Winch Warning", "HighWinchMotor was not found");
        } catch (Throwable t) {
            telemetry.addData("High Winch Error", "Fatal: " + t.getMessage());
        }
    }

    @Override
    public Telemetry getTelemetry() {
        return this.telemetry;
    }

    @Override
    public Level getTelemetryLevel() {
        return telemetryLevel;
    }

}
