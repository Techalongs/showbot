package org.firstinspires.ftc.teamcode.winch;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.util.TeamUtils;

public class Winch implements Telemetric, Operable {

    private final String name;
    private final LinearOpMode opMode;
    private final DcMotor motor;
    private double minMotorPowerLimit = 0.05;
    private double maxMotorPowerLimit = 0.5;
    private double motorPowerLimit = 0.1;

    public Winch(String name, LinearOpMode opMode, DcMotor motor) {
        this.name = name;
        this.opMode = opMode;
        this.motor = motor;
    }

    public void setPower(double power) {
        motor.setPower(power);
    }

    public void logTelemetry() {
        log(name + " Motor Power", motor.getPower());
        log(name + " Motor Speed Factor", motorPowerLimit);
        log(name + " Motor Run Mode", motor.getMode());
        log(name + " Motor Position", motor.getCurrentPosition());
    }

    public DcMotor getMotor() {
        return motor;
    }

    public String getName() {
        return name;
    }

    public double getMotorPowerLimit() {
        return motorPowerLimit;
    }

    public void setMotorPowerLimit(double motorPowerLimit) {
        this.motorPowerLimit = TeamUtils.clamp(motorPowerLimit, minMotorPowerLimit, maxMotorPowerLimit);
    }

    public LinearOpMode getOpMode() {
        return opMode;
    }

    @Override
    public Telemetry getTelemetry() {
        return opMode.telemetry;
    }

}
