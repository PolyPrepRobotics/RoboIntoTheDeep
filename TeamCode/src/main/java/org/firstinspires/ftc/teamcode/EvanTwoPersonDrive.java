package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.Servo;

import java.util.Scanner;

@TeleOp
public class EvanTwoPersonDrive extends LinearOpMode {
    private RoboController roboController;
    public static double servoPos = 0.5;
    public static Delay outtakeFlipDelay;
    public static Delay outtakeRotateDelay;
    public static Delay outtakeTwistDelay;
    public static Delay outtakeGripperDelay;

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


    public void moveOuttake(Gamepad outtakePad) {
            /*
            TODO: PLEASE CHANGE THE GAMEPAD BUTTON CONTROLS! HAS NOT BEEN FINISHED YET!!!
            TODO: ALSO CHANGE VALUES! THESE ARE SUBSITUTE VALUES
            */
        if (outtakePad.a && outtakeFlipDelay.delay()) {


            // outtake flip: flips the thing on top of the linear slide
            if (outtakeFlipDelay.open) {
                roboController.outtakeFlip.setPosition(0);
            } else {
                roboController.outtakeFlip.setPosition(1);
            }
            outtakeFlipDelay.open = !outtakeFlipDelay.open;


        } else if (outtakePad.a && outtakeRotateDelay.delay()) {


            // outtake rotate: flips the claw itself on the thing on top of the linear slide
            // not to be confused with outtake flip

            if (outtakeRotateDelay.open) {
                roboController.outtakeRotate.setPosition(0);
            } else {
                roboController.outtakeRotate.setPosition(1);
            }
            outtakeRotateDelay.open = !outtakeRotateDelay.open;


        } else if (outtakePad.a && outtakeTwistDelay.delay()) {


            // outtake twist: rotates the little platform on the claw
            if (outtakeTwistDelay.open) {
                roboController.outtakeTwist.setPosition(0);
            } else {
                roboController.outtakeTwist.setPosition(1);
            }
            outtakeTwistDelay.open = !outtakeTwistDelay.open;


        } else if (outtakePad.a && outtakeGripperDelay.delay()) {


            // outtake gripper: the gripping things on the claw
            if (outtakeGripperDelay.open) {
                roboController.outtakeGripper.setPosition(0);
            } else {
                roboController.outtakeGripper.setPosition(1);
            }


            outtakeGripperDelay.open = !outtakeGripperDelay.open;
        }
    }
}