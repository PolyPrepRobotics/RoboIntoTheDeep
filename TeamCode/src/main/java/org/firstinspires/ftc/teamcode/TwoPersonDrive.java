package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.Gamepad;

@TeleOp
public class TwoPersonDrive extends LinearOpMode {
    private RoboController roboController;
    public static double servoPos = 0.5;

    @Override
    public void runOpMode() {
        roboController = new RoboController(this);

        telemetry.addData("Status", "Initialized");
        telemetry.update();

        // Wait for the game to start (driver presses PLAY)
        waitForStart();

        // run until the end of the match (driver presses STOP)
        while (opModeIsActive()) {
            // while opMode is running, allow the methods for manipulating the wheels and arms
            // to be used and print data values
            moveWheels(gamepad1);
            moveArm(gamepad2);

            telemetry.addData("Status", "Running");
            telemetry.addData("RIP", roboController.rightIntakePusher.getPosition());
            telemetry.addData("LIP", roboController.leftIntakePusher.getPosition());
            telemetry.addData("IFS", roboController.intakeFlip.getPosition());

            telemetry.update();
        }
    }

    public void moveWheels(Gamepad movepad){
        // ** wheel movement **
        double drivePower = 0;
        double strafePower = 0;
        double turnPower = 0;

        // moves the robot's (wheel) motors forward and back using the game pad 1 left joystick
        drivePower = -movepad.left_stick_y;

        // moves the robot's (wheel) motors left and right using the game pad 1 left joystick
        // strafePower = movepad.left_stick_x;

        // new strafing method that uses the backn triggers instead
        if(movepad.left_trigger > 0){
            strafePower = -movepad.left_trigger;
        } else if(movepad.right_trigger > 0){
            strafePower = movepad.right_trigger;
        } else {
            strafePower = 0;
        }

        // turns the robot's (wheel) motors left and right using the game pad 1 right joystick
        turnPower = movepad.right_stick_x;

        // drive, turn, and strafe logic
        // https://youtu.be/jRVUHapKx4o?si=1jVJ-ts7d2rkHCdq
        roboController.FLW.setPower(drivePower + turnPower + strafePower);
        roboController.FRW.setPower(drivePower - turnPower - strafePower);
        roboController.BLW.setPower(drivePower + turnPower - strafePower);
        roboController.BRW.setPower(drivePower - turnPower + strafePower);

        telemetry.addData("Drive Power", drivePower);
        telemetry.addData("Strafe Power", strafePower);
        telemetry.addData("Turn Power", turnPower);
    }

    public void moveArm(Gamepad armpad){
        // ** arm movement **

        // right = extend
        // left = retract

        //roboController.rightIntakePusher.setPosition(servoPos);
        //roboController.leftIntakePusher.setPosition(servoPos);

        // triggers control extension of intake arm
        if (armpad.right_trigger > 0.25) {
            roboController.rightIntakePusher.setPosition(roboController.rightIntakePusher.getPosition() + 0.001);
            roboController.leftIntakePusher.setPosition(roboController.leftIntakePusher.getPosition() - 0.001);
        } else if (armpad.left_trigger > 0.25) {
            roboController.rightIntakePusher.setPosition(roboController.rightIntakePusher.getPosition() - 0.001);
            roboController.leftIntakePusher.setPosition(roboController.leftIntakePusher.getPosition() + 0.001);
        }

        // bumpers control extension of outtake arm
        if (armpad.left_bumper) {
            roboController.leftVerticalSlide.setPower(-1);
            roboController.rightVerticalSlide.setPower(-1);
        } else if (armpad.right_bumper) {
            roboController.leftVerticalSlide.setPower(1);
            roboController.rightVerticalSlide.setPower(1);
        } else {
            roboController.leftVerticalSlide.setPower(0);
            roboController.rightVerticalSlide.setPower(0);
        }

        if(armpad.dpad_down){
            if(roboController.intakeFlip.getPosition() < 0.5){
                roboController.intakeFlip.setPosition(0.7);
            } else {
                roboController.intakeFlip.setPosition(0);
            }
        }
    }
}