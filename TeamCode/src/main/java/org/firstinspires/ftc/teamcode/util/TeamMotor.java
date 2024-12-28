package org.firstinspires.ftc.teamcode.util;

import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorImplEx;
import com.qualcomm.robotcore.hardware.HardwareMap;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

/**
 * TODO: I haven't tested this. Trying it out for fun.
 */
public class TeamMotor extends DcMotorImplEx {

    protected static Consumer<TeamMotor> DEFAULT_CONFIGURE_MOTOR = m -> {
        m.setMode(RunMode.STOP_AND_RESET_ENCODER);
        m.setMode(RunMode.RUN_USING_ENCODER);
        m.setZeroPowerBehavior(ZeroPowerBehavior.BRAKE);
        m.setDirection(Direction.FORWARD);
    };

    protected AtomicBoolean initialized = new AtomicBoolean(false);
    protected Consumer<TeamMotor> configureMotor;
    protected double minPower = -1.0;
    protected double maxPower = 1.0;
    protected double powerLimitFactor = 1.0;
    protected double maxPowerLimitFactor = 1.0;
    protected double minPowerLimitFactor = 0.0;

    public static TeamMotor getTeamMotor(HardwareMap hardware, String name, Consumer<TeamMotor> configure) throws DeviceNotFoundException {
        try {
            DcMotorEx dcMotorEx = DeviceUtils.getDevice(hardware, DcMotorEx.class, name);
            return getTeamMotor(dcMotorEx, configure);
        } catch (Throwable t) {
            throw new DeviceNotFoundException(name, "Error while getting device from hardware map: " + t.getMessage());
        }
    }

    public static TeamMotor getTeamMotor(HardwareMap hardware, String name) throws DeviceNotFoundException {
        return getTeamMotor(hardware, name, DEFAULT_CONFIGURE_MOTOR);
    }

    public static Optional<TeamMotor> getOptionalTeamMotor(HardwareMap hardware, String name) throws DeviceNotFoundException {
        try {
            return Optional.of(getTeamMotor(hardware, name));
        } catch (Throwable t) {
            return Optional.empty();
        }
    }

    public static TeamMotor getTeamMotor(DcMotorEx motor) {
        return getTeamMotor(motor, DEFAULT_CONFIGURE_MOTOR);
    }

    public static TeamMotor getTeamMotor(DcMotorEx motor, Consumer<TeamMotor> configure) {
        return new TeamMotor(motor, configure);
    }

    protected TeamMotor(DcMotorEx dcMotor, Consumer<TeamMotor> configure) {
        super(dcMotor.getController(), dcMotor.getPortNumber(), dcMotor.getDirection(), dcMotor.getMotorType());
        this.configureMotor = configure;
        this.init();
    }

    protected void init() {
        if (!initialized.getAndSet(true)) {
            if (configureMotor != null) {
                configureMotor.accept(this);
            }
        }
    }

    protected double limitPower(double power) {
        return TeamUtils.clamp(power * powerLimitFactor, minPower, maxPower);
    }

    @Override
    public synchronized void setPower(double power) {
        super.setPower(this.limitPower(power));
    }

    public TeamMotor withDirection(Direction direction) {
        this.setDirection(direction);
        return this;
    }

    public TeamMotor withRunMode(RunMode runMode) {
        this.setMode(runMode);
        return this;
    }

    public TeamMotor withZeroPowerBehavior(ZeroPowerBehavior behavior) {
        this.setZeroPowerBehavior(behavior);
        return this;
    }

    public TeamMotor withPowerLimitFactor(double powerLimitFactor) {
        this.powerLimitFactor = TeamUtils.clamp(powerLimitFactor, minPowerLimitFactor, maxPowerLimitFactor);
        return this;
    }

}
