package org.firstinspires.ftc.teamcode;

import android.annotation.SuppressLint;

import androidx.annotation.NonNull;

import com.qualcomm.robotcore.hardware.DcMotor;

import org.firstinspires.ftc.robotcore.external.Telemetry;

public class Winch {

    private final String name;
    private final DcMotor motor;
    private double motorPowerLimit = 1.0;

    public Winch(String name, DcMotor motor) {
        this.name = name;
        this.motor = motor;
    }

    public void setPower(double power) {
        if (power > motorPowerLimit) {
            power = motorPowerLimit;
        } else if (power < -motorPowerLimit) {
            power = -motorPowerLimit;
        }
        motor.setPower(power);
    }

    public double getPower() {
        return motor.getPower();
    }

    public void updateTelemetry(Telemetry telemetry) {
        telemetry.addData(name + " Motor Power", this.getPower());
        telemetry.addData(name + " Motor Limit", this.getMotorPowerLimit());
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
        this.motorPowerLimit = motorPowerLimit;
    }

    @SuppressLint("DefaultLocale")
    @NonNull
    @Override
    public String toString() {
        return String.format("Winch(name=%s, power=%.2f, powerLimit=%.2f)", name, this.getPower(), this.getMotorPowerLimit());
    }

}
