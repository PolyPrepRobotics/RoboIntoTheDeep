package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.Gamepad;

@TeleOp
public class TwoPersonDrive extends LinearOpMode {
    private RoboController roboController;
    public static double servoPos = 0.5;

    private boolean buttonPressedDpadDown = false;
    private boolean buttonPressedDpadUp = false;
    private boolean buttonPressedCircle = false;
    private boolean buttonPressedSquare = false;
    private boolean buttonPressedA = false;
    private boolean pickupPos = false;

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
            telemetry.addData("IRS", roboController.intakeRotate.getPosition());
            telemetry.addData("IGS", roboController.intakeGripper.getPosition());

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


        // circle toggles intake arm positions
        if(armpad.circle && !buttonPressedCircle){
            if(roboController.intakeFlip.getPosition() < 0.5){
                // macro position is correct
                // semi-parallel to floor
                roboController.intakeFlip.setPosition(0.8);

                // claw is semi-parallel too
                roboController.intakeRotate.setPosition(0.5);

                pickupPos = true;
            } else {
                // find actual macro positions for outtake arm transfer
                // higher
                roboController.intakeFlip.setPosition(0.3);

                // higher
                roboController.intakeRotate.setPosition(0.2);

                pickupPos = false;
            }
        }

        buttonPressedCircle = armpad.circle;


        // x/a controls opening and closing intake claw
        if(armpad.a && !buttonPressedA){
            // initially closed
            if (roboController.intakeGripper.getPosition() <= 0.5) {
                // opening
                roboController.intakeGripper.setPosition(1);
            } else {
                // closing
                roboController.intakeGripper.setPosition(0);
            }
        }

        buttonPressedA = armpad.a;

        // hold dpad down to lower claw and pick up sample
        if(pickupPos){
            if(armpad.dpad_down && !buttonPressedDpadDown){
                // semi-parallel to floor
                roboController.intakeFlip.setPosition(0.83);

                // lower claw
                roboController.intakeRotate.setPosition(1);
            } else {
                // semi-parallel to floor
                roboController.intakeFlip.setPosition(0.8);

                // claw is semi-parallel too
                roboController.intakeRotate.setPosition(0.5);
            }
        }

        // square controls adjustment of wrist position
        if (armpad.square && !buttonPressedSquare) {
            if (roboController.intakeTwist.getPosition() < 0.25) {
                roboController.intakeTwist.setPosition(0.5);
            } else {
                roboController.intakeTwist.setPosition(0);
            }
        }

        buttonPressedSquare = armpad.square;

        /*
        if(armpad.dpad_up && !buttonPressedDpadUp){
            if(roboController.intakeRotate.getPosition() < 0.5){
                // macro position is correct
                // lower
                roboController.intakeRotate.setPosition(1);

                roboController.intakeGripper.setPosition(0);
            } else {
                // macro position is (somewhat) correct
                // higher
                roboController.intakeRotate.setPosition(0);

                roboController.intakeGripper.setPosition(1);
            }
        }

        buttonPressedDpadUp = armpad.dpad_up;

         */
    }
}