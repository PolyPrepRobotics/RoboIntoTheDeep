package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.Servo;

@TeleOp
public class TwoPersonDrive extends LinearOpMode {
    private RoboController roboController;
    public static double servoPos = 0.5;

    private boolean buttonPressedDpadDown = false;
    private boolean buttonPressedDpadUp = false;
    private boolean buttonPressedCircle = false;
    private boolean buttonPressedSquare = false;
    private boolean buttonPressedA = false;
    private boolean buttonPressedTriangle = false;
    private boolean pickupPos = false;

    public static Delay outtakeFlipDelay = new Delay();
    public static Delay outtakeRotateDelay = new Delay();
    public static Delay outtakeTwistDelay = new Delay();
    public static Delay outtakeGripperDelay = new Delay();
    private static int outtakeFlipPos = 0;

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
            telemetry.addData("ITS", roboController.intakeTwist.getPosition());

            telemetry.addData("LOFS", roboController.leftOuttakeFlip.getPosition());
            telemetry.addData("ROFS", roboController.rightOuttakeFlip.getPosition());
            telemetry.addData("ORS", roboController.outtakeRotate.getPosition());
            telemetry.addData("OTS", roboController.outtakeTwist.getPosition());
            telemetry.addData("OGS", roboController.outtakeGripper.getPosition());

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

        // new strafing method that uses the back triggers instead
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


        // *** INTAKE ***

        // bumpers control extension of intake arm
        if (armpad.left_bumper) {
            roboController.rightIntakePusher.setPosition(1);
            roboController.leftIntakePusher.setPosition(0);
        } else if (armpad.right_bumper) {
            roboController.rightIntakePusher.setPosition(0);
            roboController.leftIntakePusher.setPosition(1);
        }


        // circle toggles intake arm positions
        if(armpad.circle && !buttonPressedCircle){
            if(roboController.intakeFlip.getPosition() < 0.5){
                // macro position is correct
                // semi-parallel to floor
                roboController.intakeFlip.setPosition(0.72);

                // lower claw
                roboController.intakeRotate.setPosition(1);

                // slightly opened
                if(roboController.intakeGripper.getPosition() == 0.39){
                    // closed
                    roboController.intakeGripper.setPosition(0.75);
                }

                pickupPos = true;
            } else {
                // find actual macro positions for outtake arm transfer
                // higher
                roboController.intakeFlip.setPosition(0.15);

                // higher
                roboController.intakeRotate.setPosition(0);

                // closed
                if(roboController.intakeGripper.getPosition() == 0.75){
                    // straighten claw
                    roboController.intakeTwist.setPosition(0);

                    // wait until the arm is at the back position first before loosening claw
                    sleep(500);

                    // slightly opened
                    roboController.intakeGripper.setPosition(0.39);
                }

                pickupPos = false;
            }
        }

        buttonPressedCircle = armpad.circle;


        // x/a controls opening and closing intake claw
        if((armpad.a && !buttonPressedA)){
            // initially closed
            if (roboController.intakeGripper.getPosition() <= 0.5) {
                // closed
                roboController.intakeGripper.setPosition(0.75);
            } else {
                // opened
                roboController.intakeGripper.setPosition(0);
            }
        }

        buttonPressedA = armpad.a;

        // hold dpad down to lower claw and pick up sample
        if(pickupPos){
            if(armpad.dpad_down && !buttonPressedDpadDown){
                // lower to floor
                roboController.intakeFlip.setPosition(0.8);

                // slightly higher claw
                roboController.intakeRotate.setPosition(0.92);
            } else {
                // semi-parallel to floor
                roboController.intakeFlip.setPosition(0.72);

                // lower claw
                roboController.intakeRotate.setPosition(1);
            }
        }

        // square controls adjustment of wrist position
        if (armpad.square && !buttonPressedSquare) {
            if (roboController.intakeTwist.getPosition() < 0.25) {
                roboController.intakeTwist.setPosition(0.52);
            } else {
                roboController.intakeTwist.setPosition(0);
            }
        }

        buttonPressedSquare = armpad.square;

        // macro position for transfer between intake and outtake
        if (armpad.triangle && !buttonPressedTriangle) {
            // higher
            roboController.intakeFlip.setPosition(0.15);

            // higher
            roboController.intakeRotate.setPosition(0);

            // straighten claw
            roboController.intakeTwist.setPosition(0);


            // slightly opened
            roboController.intakeGripper.setPosition(0.39);

            // outtake claw up so it doesn't hit
            roboController.outtakeRotate.setPosition(1);

            // open outtake claw
            roboController.outtakeGripper.setPosition(0);

            sleep(500);

            // outtake arm forward
            roboController.rightOuttakeFlip.setPosition(.37);
            roboController.leftOuttakeFlip.setPosition(.37);

            // outtake claw forward
            roboController.outtakeRotate.setPosition(0.7);

            // retract intake slide
            roboController.rightIntakePusher.setPosition(1);
            roboController.leftIntakePusher.setPosition(0);

            // wait until the arm is at the back position first before loosening claw
            sleep(1000);

            // close outtake claw
            roboController.outtakeGripper.setPosition(0.75);

            sleep(250);

            // open intake claw
            roboController.intakeGripper.setPosition(0);

            // extend intake slide slightly
            roboController.rightIntakePusher.setPosition(0.8);
            roboController.leftIntakePusher.setPosition(0.2);
        }

        buttonPressedTriangle = armpad.triangle;

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


        // *** OUTTAKE ***
        /*
            TODO: PLEASE CHANGE THE GAMEPAD BUTTON CONTROLS! HAS NOT BEEN FINISHED YET!!!
            TODO: ALSO CHANGE VALUES! THESE ARE SUBSITUTE VALUES
            */

        // triggers control extension of outtake arm
        if (armpad.right_trigger > 0.25) {
            // extend even tho it's negative
            roboController.leftVerticalSlide.setPower(-1);
            roboController.rightVerticalSlide.setPower(-1);

        } else if (armpad.left_trigger > 0.25) {
            // retract even tho it's positive
            roboController.leftVerticalSlide.setPower(1);
            roboController.rightVerticalSlide.setPower(1);

        } else {
            roboController.leftVerticalSlide.setPower(0);
            roboController.rightVerticalSlide.setPower(0);
        }

        if (armpad.dpad_left && outtakeFlipDelay.delay()) {

            // outtake flip: flips the thing on top of the linear slide
            roboController.leftOuttakeFlip.setDirection(Servo.Direction.REVERSE);
            if (outtakeFlipPos == 2) {
                outtakeFlipPos = 0;
            } else {
                outtakeFlipPos++;
            }

            if (outtakeFlipPos == 0) {
                roboController.rightOuttakeFlip.setPosition(0);
                roboController.leftOuttakeFlip.setPosition(0);
            } else if (outtakeFlipPos == 1) {
                roboController.rightOuttakeFlip.setPosition(.35);
                roboController.leftOuttakeFlip.setPosition(.35);
            } else if (outtakeFlipPos == 2) {
                roboController.rightOuttakeFlip.setPosition(1);
                roboController.leftOuttakeFlip.setPosition(1);
            }
        }

        else if (armpad.dpad_right && outtakeRotateDelay.delay()) {


            // outtake rotate: flips the claw itself on the thing on top of the linear slide
            // not to be confused with outtake flip

            if (outtakeRotateDelay.open) {
                // back
                roboController.outtakeRotate.setPosition(0);
            } else {
                // forward
                roboController.outtakeRotate.setPosition(1);
            }
            outtakeRotateDelay.open = !outtakeRotateDelay.open;


        }

        // dpad up toggles outtake claw
        if(armpad.dpad_up && !buttonPressedDpadUp){
            // initially closed
            if (roboController.outtakeGripper.getPosition() <= 0.5) {
                // closed
                roboController.outtakeGripper.setPosition(0.75);
            } else {
                // opened
                roboController.outtakeGripper.setPosition(0);
            }
        }

        buttonPressedDpadUp = armpad.dpad_up;

        /*else if (armpad.a && outtakeTwistDelay.delay()) {


            // outtake twist: rotates the little platform on the claw
            if (outtakeTwistDelay.open) {
                roboController.outtakeTwist.setPosition(0);
            } else {
                roboController.outtakeTwist.setPosition(1);
            }
            outtakeTwistDelay.open = !outtakeTwistDelay.open;


        } else if (armpad.a && outtakeGripperDelay.delay()) {


            // outtake gripper: the gripping things on the claw
            if (outtakeGripperDelay.open) {
                roboController.outtakeGripper.setPosition(0);
            } else {
                roboController.outtakeGripper.setPosition(1);
            }


            outtakeGripperDelay.open = !outtakeGripperDelay.open;
        }*/
    }
}