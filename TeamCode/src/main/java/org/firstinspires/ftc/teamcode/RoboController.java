package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.IMU;
import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;
import java.lang.Math;


// overall class that stores robot configuration, presetting, and autonomous movements
public class RoboController {
    private LinearOpMode opMode;

    // wheel parts
    // (F = Front, B = Back, L = Left, R = Right, W = Wheel)
    public DcMotor FLW;
    public DcMotor FRW;
    public DcMotor BLW;
    public DcMotor BRW;

    public IMU imu;

    // ** new variables **
    // intake
    public Servo rightIntakePusher;
    public Servo leftIntakePusher;
    public Servo intakeFlip;
    public Servo intakeRotate;
    public Servo intakeTwist;
    public Servo intakeGripper;
    public DcMotor leftVerticalSlide;
    public DcMotor rightVerticalSlide;

    // outtake
    public Servo leftOuttakeFlip;
    public Servo rightOuttakeFlip;
    public Servo outtakeRotate;
    public Servo outtakeTwist;
    public Servo outtakeGripper;

    // ****

    public RoboController(LinearOpMode opMode){

        this.opMode = opMode;
        HardwareMap hardwareMap = opMode.hardwareMap;

        // wheel config
        FLW = hardwareMap.get(DcMotor.class, "FLW");
        FRW = hardwareMap.get(DcMotor.class, "FRW");
        BLW = hardwareMap.get(DcMotor.class, "BLW");
        BRW = hardwareMap.get(DcMotor.class, "BRW");

        // imu
        imu = hardwareMap.get(IMU.class, "imu");

        rightIntakePusher = hardwareMap.get(Servo.class, "RIP");
        leftIntakePusher = hardwareMap.get(Servo.class, "LIP");
        intakeFlip = hardwareMap.get(Servo.class, "IFS");
        intakeRotate = hardwareMap.get(Servo.class, "IRS");
        intakeTwist = hardwareMap.get(Servo.class, "ITS");
        intakeGripper = hardwareMap.get(Servo.class, "IGS");
        leftVerticalSlide = hardwareMap.get(DcMotor.class, "LVLS");
        rightVerticalSlide = hardwareMap.get(DcMotor.class, "RVLS");

        leftOuttakeFlip = hardwareMap.get(Servo.class, "LOFS");
        rightOuttakeFlip = hardwareMap.get(Servo.class, "ROFS");
        outtakeRotate = hardwareMap.get(Servo.class, "ORS");
        outtakeTwist = hardwareMap.get(Servo.class, "OTS");
        outtakeGripper = hardwareMap.get(Servo.class, "OGS");

        // presetting
        FRW.setDirection(DcMotorSimple.Direction.REVERSE);
        BRW.setDirection(DcMotorSimple.Direction.REVERSE);

        FLW.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        FRW.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        BLW.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        BRW.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        // Initialize IMU directly
        imu.initialize(
                new IMU.Parameters(
                        new RevHubOrientationOnRobot(
                                RevHubOrientationOnRobot.LogoFacingDirection.BACKWARD,
                                RevHubOrientationOnRobot.UsbFacingDirection.RIGHT
                        )
                )
        );
    }

    // wheel/distance conversions
    public static int inchesToCounts(double inchesToDrive) {
        double circumference = Constants.WHEEL_DIAMETER_IN_INCHES * Math.PI;
        double rotations = inchesToDrive / circumference;
        double countsToDrive = rotations * Constants.WHEEL_MOTOR_COUNTS_PER_ROTATION;
        return (int) countsToDrive;
    }

    // auto movement for left and right
    public void moveOnXAxis(double inches, double speed) {
        double ticks = inchesToCounts(inches);

        opMode.telemetry.addData("x", "");
        opMode.telemetry.update();

        // Reset encoders (so that the tracked distance of the robot always starts from 0)
        FLW.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        FRW.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        BLW.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        BRW.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        // speed of wheel
        FLW.setPower(speed);
        FRW.setPower(-speed);
        BLW.setPower(-speed);
        BRW.setPower(speed);

        // how far the wheel movement should bring the robot
        FLW.setTargetPosition(inchesToCounts(inches));
        FRW.setTargetPosition(-(inchesToCounts(inches)));
        BLW.setTargetPosition(-(inchesToCounts(inches)));
        BRW.setTargetPosition(inchesToCounts(inches));

        // powers the wheels until it has reached the specified distance
        FLW.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        FRW.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        BLW.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        BRW.setMode(DcMotor.RunMode.RUN_TO_POSITION);

        // print values while auto mode is running and wheels are moving (since the values pertain to the wheels
        // and are only needed when the wheels are doing something)
        while (opMode.opModeIsActive() && FLW.isBusy()) {
            // Loop until the motor reaches its target position.
            opMode.telemetry.addData("Front Left Encoder", FLW.getCurrentPosition());
            opMode.telemetry.addData("Front Right Encoder", FRW.getCurrentPosition());
            opMode.telemetry.addData("Rear Left Encoder", BLW.getCurrentPosition());
            opMode.telemetry.addData("Rear Right Encoder", BRW.getCurrentPosition());
            opMode.telemetry.update();
        }

        opMode.sleep(250);
    }

    // auto movement for forward and back
    public void moveOnYAxis(double inches, double speed){
        int ticks = inchesToCounts(inches);

        opMode.telemetry.addData("y", "");
        opMode.telemetry.update();

        FLW.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        FRW.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        BLW.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        BRW.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        FLW.setPower(speed);
        FRW.setPower(speed);
        BLW.setPower(speed);
        BRW.setPower(speed);

        FLW.setTargetPosition(inchesToCounts(inches));
        FRW.setTargetPosition(inchesToCounts(inches));
        BLW.setTargetPosition(inchesToCounts(inches));
        BRW.setTargetPosition(inchesToCounts(inches));

        FLW.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        FRW.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        BLW.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        BRW.setMode(DcMotor.RunMode.RUN_TO_POSITION);


        while (opMode.opModeIsActive() && FLW.isBusy()) {
            opMode.telemetry.addData("Front Left Encoder", FLW.getCurrentPosition());
            opMode.telemetry.addData("Front Right Encoder", FRW.getCurrentPosition());
            opMode.telemetry.addData("Rear Left Encoder", BLW.getCurrentPosition());
            opMode.telemetry.addData("Rear Right Encoder", BRW.getCurrentPosition());
            opMode.telemetry.update();
        }

        opMode.sleep(250);
    }

    public void Spin(double inches, double speed) {
        int ticks = inchesToCounts(inches);

        opMode.telemetry.addData("spin", "");
        opMode.telemetry.update();

        FLW.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        FRW.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        BLW.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        BRW.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        FLW.setPower(speed);
        FRW.setPower(-speed);
        BLW.setPower(speed);
        BRW.setPower(-speed);

        FLW.setTargetPosition(inchesToCounts(inches));
        FRW.setTargetPosition(-(inchesToCounts(inches)));
        BLW.setTargetPosition(inchesToCounts(inches));
        BRW.setTargetPosition(-(inchesToCounts(inches)));

        FLW.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        FRW.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        BLW.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        BRW.setMode(DcMotor.RunMode.RUN_TO_POSITION);


        while (opMode.opModeIsActive() && FLW.isBusy()) {
            opMode.telemetry.addData("Front Left Encoder", FLW.getCurrentPosition());
            opMode.telemetry.addData("Front Right Encoder", FRW.getCurrentPosition());
            opMode.telemetry.addData("Rear Left Encoder", BLW.getCurrentPosition());
            opMode.telemetry.addData("Rear Right Encoder", BRW.getCurrentPosition());
            opMode.telemetry.update();
        }

        opMode.sleep(250);
    }
}