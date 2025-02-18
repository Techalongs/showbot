package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.robotcore.external.Telemetry;

import java.util.HashMap;
import java.util.function.Supplier;

public class Robot implements MecanumDrivetrain {
    private final DcMotor frontLeft;
    private final DcMotor frontRight;
    private final DcMotor backLeft;
    private final DcMotor backRight;
    private final Servo dronePosition;
    private final Servo droneLaunch;
    private final Telemetry telemetry;
    private final LinearOpMode opMode;
    private final HashMap<String, String> extraData = new HashMap<>();
    private double speed;

    private boolean useWinch = true;
    private Winch winch;
    private double winchPowerLimit = 0.5;
    /** Supplier provides the preference for which control to use for winch power. **/
    private Supplier<Float> winchPowerSupplier;

    public Robot(HardwareMap hardwareMap, Telemetry telemetry, LinearOpMode opMode, double speed) {
        this.opMode = opMode;
        this.telemetry = telemetry;
        this.speed = speed;

        frontLeft = hardwareMap.get(DcMotor.class, "CH1");
        frontRight = hardwareMap.get(DcMotor.class, "CH0");
        backLeft = hardwareMap.get(DcMotor.class, "CH3");
        backRight = hardwareMap.get(DcMotor.class, "CH2");
        dronePosition = hardwareMap.get(Servo.class, "dronePosition");
        droneLaunch = hardwareMap.get(Servo.class, "droneLaunch");

        winchPowerSupplier = () -> this.opMode.gamepad1.right_trigger - this.opMode.gamepad1.left_trigger;

        try {
            DcMotorEx winchMotor = hardwareMap.get(DcMotorEx.class, "WinchMotor");
            winchMotor.setDirection(DcMotorSimple.Direction.FORWARD);
            winchMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
            winchMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
            winchMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            winch = new Winch("Winch", winchMotor);
            winch.setMotorPowerLimit(winchPowerLimit);
            useWinch = true;
        } catch (IllegalArgumentException e) {
            this.updateTelemetry("Winch Warning", "WinchMotor was not found");
            useWinch = false;
        } catch (Throwable t) {
            this.updateTelemetry("Winch Error", "Fatal: " + t.getMessage());
            useWinch = false;
        }
    }

    public Robot(HardwareMap hardwareMap, Telemetry telemetry, LinearOpMode opMode) {
        this(hardwareMap, telemetry, opMode, 0.5);
    }

    public void init(double positionAngle) {
        frontRight.setDirection(DcMotorSimple.Direction.REVERSE);
        backRight.setDirection(DcMotorSimple.Direction.REVERSE);

        frontLeft.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        frontRight.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        backLeft.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        backRight.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        frontLeft.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        frontRight.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        backLeft.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        backRight.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        frontLeft.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        frontRight.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        backLeft.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        backRight.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        dronePosition.setPosition(positionAngle);
        droneLaunch.setPosition(0);
    }

    @Override
    public void drive(double limiter, Gamepad gamepad) {
        float FLPower = (-gamepad.left_stick_y + gamepad.right_stick_x) + gamepad.left_stick_x;
        float FRPower = (-gamepad.left_stick_y - gamepad.right_stick_x) - gamepad.left_stick_x;
        float BLPower = (-gamepad.left_stick_y + gamepad.right_stick_x) - gamepad.left_stick_x;
        float BRPower = (-gamepad.left_stick_y - gamepad.right_stick_x) + gamepad.left_stick_x;

        frontLeft.setPower(FLPower * limiter);
        frontRight.setPower(FRPower * limiter);
        backLeft.setPower(BLPower * limiter);
        backRight.setPower(BRPower * limiter);
    }

    private void move(int tick1, int tick2, int tick3, int tick4) {
        frontLeft.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        frontRight.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        backLeft.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        backRight.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        frontLeft.setTargetPosition(tick1);
        frontRight.setTargetPosition(tick2);
        backLeft.setTargetPosition(tick3);
        backRight.setTargetPosition(tick4);

        frontLeft.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        frontRight.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        backLeft.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        backRight.setMode(DcMotor.RunMode.RUN_TO_POSITION);

        frontLeft.setPower(speed);
        frontRight.setPower(speed);
        backLeft.setPower(speed);
        backRight.setPower(speed);

        while (frontLeft.isBusy() && frontRight.isBusy() && backLeft.isBusy() && backRight.isBusy() && opMode.opModeIsActive()) {
            displayData();
        }

        frontLeft.setPower(0);
        frontRight.setPower(0);
        backLeft.setPower(0);
        backRight.setPower(0);
    }

    @Override
    public void moveForward(int ticks) {
        move(ticks, ticks, ticks, ticks);
    }

    @Override
    public void moveBackward(int ticks) {
        moveForward(-ticks);
    }

    @Override
    public void turnLeft(int ticks) {
        move(-ticks, ticks, -ticks, ticks);
    }

    @Override
    public void turnRight(int ticks) {
        turnLeft(-ticks);
    }

    @Override
    public void strafeLeft(int ticks) {
        move(-ticks, ticks, ticks, -ticks);
    }

    @Override
    public void strafeRight(int ticks) {
        strafeLeft(-ticks);
    }

    public void launchDrone(double positionAngle) {
        dronePosition.setPosition(positionAngle);
        droneLaunch.setPosition(1);
    }

    public void setWinchPowerSupplier(Supplier<Float> supplier) {
        this.winchPowerSupplier = supplier;
    }

    public void updateWinch() {
        if (useWinch) {
            winch.setPower(winchPowerSupplier.get());
        }
    }

    public double getFLMotorPower() {
        return frontLeft.getPower();
    }

    public double getFRMotorPower() {
        return frontRight.getPower();
    }

    public double getBLMotorPower() {
        return backLeft.getPower();
    }

    public double getBRMotorPower() {
        return backRight.getPower();
    }

    public double getFLMotorPosition() {
        return frontLeft.getCurrentPosition();
    }

    public double getFRMotorPosition() {
        return frontRight.getCurrentPosition();
    }

    public double getBLMotorPosition() {
        return backLeft.getCurrentPosition();
    }

    public double getBRMotorPosition() {
        return backRight.getCurrentPosition();
    }

    public void addData(String caption, double value) {
        addData(caption, String.valueOf(value));
    }

    public void changeSpeed(double speed) {
        this.speed = speed;
    }

    public void addData(String caption, String value) {
        extraData.put(caption, value);
    }

    public void displayData() {
        telemetry.addData("Status", "Running");
        telemetry.addData("Winch Power", getFLMotorPower());
        telemetry.addData("Front Left Power", getFLMotorPower());
        telemetry.addData("Front Right Power", getFRMotorPower());
        telemetry.addData("Back Left Power", getBLMotorPower());
        telemetry.addData("Back Right Power", getBRMotorPower());
        telemetry.addData("Front Left Position", getFLMotorPosition());
        telemetry.addData("Front Right Position", getFRMotorPosition());
        telemetry.addData("Back Left Position", getBLMotorPosition());
        telemetry.addData("Back Right Position", getBRMotorPosition());
        if (useWinch) {
            winch.updateTelemetry(telemetry);
        }

        for (String caption : extraData.keySet()) {
            telemetry.addData(caption, extraData.get(caption));
        }

        telemetry.update();
    }

    public void updateTelemetry(String caption, Object value) {
        telemetry.addData(caption, value);
        telemetry.update();
    }

}
