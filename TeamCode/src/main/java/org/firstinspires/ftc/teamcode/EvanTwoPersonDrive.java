package org.firstinspires.ftc.teamcode;

import static com.qualcomm.robotcore.hardware.Servo.Direction.REVERSE;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.Servo;

import java.util.Scanner;

@TeleOp
public class EvanTwoPersonDrive extends LinearOpMode {
    private RoboController roboController;
    private double intakeOpen = 0;
    private double intakeSlightOpen = 0.39;
    private double intakeClose = 0.75;
    private double intakeGripperMid = 0.4;
    private double outtakeOpen = 0.4;
    private double outtakeClose = 0.625;
    private double outtakeGripperMid = 0.5;
    private double outtakeClawFoward = 0.87;
    private double outtakeClawBack = 0;
    private double outtakeClawMiddle = 0.44;
    private double outtakeRotateMid = 0.5;
    private double intakeFlipDown = 0.7;
    private double intakeRotateDown = 1;
    private double intakeFlipUp = 0.15;
    private double intakeRotateUp = 0;
    private double outtakeTwistStraight1 = 0.15;
    private double outtakeTwistStraight2 = 0.9;
    public static double servoPos = 0.5;
    public static Delay outtakeFlipDelay = new Delay();
    public static Delay outtakeRotateDelay = new Delay();
    public static Delay outtakeTwistDelay = new Delay();
    public static Delay outtakeGripperDelay = new Delay();
    private static double outtakeFlipPos = 0;
    @Override
    public void runOpMode() throws InterruptedException {
        roboController = new RoboController(this);

        telemetry.addData("Status", "Initialized");
        telemetry.update();

        // Wait for the game to start (driver presses PLAY)
        waitForStart();

        // run until the end of the match (driver presses STOP)

        roboController.rightOuttakeFlip.setPosition(0);
        roboController.leftOuttakeFlip.setPosition(0);
        outtakeFlipPos = 0;
        while (opModeIsActive()) {
            // while opMode is running, allow the methods for manipulating the wheels and arms
            // to be used and print data values
            if (gamepad1.a) {
                grabSpecimenFromWall();
            } else if (gamepad1.b) {
                scoreSpecimen();
            } else if (gamepad1.x) {
                // pickup sample from afar method here, Wendy should have it
            }
            telemetry.addData("Status", "Running");
            telemetry.addData("RIP", roboController.rightIntakePusher.getPosition());
            telemetry.addData("LIP", roboController.leftIntakePusher.getPosition());
            telemetry.addData("IFS", roboController.intakeFlip.getPosition());
            telemetry.addData("Outtake Pos:", outtakeFlipPos);

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
            // roboController.leftOuttakeFlip.setDirection(REVERSE);
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

        else if (outtakePad.b && outtakeRotateDelay.delay()) {


            // outtake rotate: flips the claw itself on the thing on top of the linear slide
            // not to be confused with outtake flip

            if (outtakeRotateDelay.open) {
                roboController.outtakeRotate.setPosition(0);
            } else {
                roboController.outtakeRotate.setPosition(1);
            }
            outtakeRotateDelay.open = !outtakeRotateDelay.open;


        }

        /*else if (outtakePad.a && outtakeTwistDelay.delay()) {


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
        }*/
    }

    public void test(Gamepad outtakePad) {
        roboController.leftOuttakeFlip.setDirection(REVERSE);

        if (outtakePad.a && outtakeFlipDelay.delay()) {
            // outtake flip: flips the thing on top of the linear slide
            roboController.rightOuttakeFlip.setPosition(outtakeFlipPos);
            roboController.leftOuttakeFlip.setPosition(outtakeFlipPos);
            outtakeFlipPos += .05;
        } else if (outtakePad.b && outtakeFlipDelay.delay()) {
            // outtake flip: flips the thing on top of the linear slide
            roboController.rightOuttakeFlip.setPosition(outtakeFlipPos);
            roboController.leftOuttakeFlip.setPosition(outtakeFlipPos);
            outtakeFlipPos -= .05;
        }

    }

    public void transferSample(){

        // ** outtake first
        // move back before if it needs to
        if(roboController.rightOuttakeFlip.getPosition() <= 0.17){
            roboController.rightOuttakeFlip.setPosition(0);
            roboController.leftOuttakeFlip.setPosition(0);
        }

        // straighten outtake claw twist
        roboController.outtakeTwist.setPosition(outtakeTwistStraight1);

        // outtake claw up so it doesn't hit
        roboController.outtakeRotate.setPosition(outtakeClawFoward);

        // open outtake claw
        roboController.outtakeGripper.setPosition(outtakeOpen);

        sleep(200);

        // outtake arm forward
        roboController.rightOuttakeFlip.setPosition(.32);
        roboController.leftOuttakeFlip.setPosition(.32);

        sleep(300);

        // outtake claw to actual position
        roboController.outtakeRotate.setPosition(0.75);

        // ** then intake
        // higher
        roboController.intakeFlip.setPosition(intakeFlipUp);

        // higher
        roboController.intakeRotate.setPosition(intakeRotateUp);

        // straighten claw
        roboController.intakeTwist.setPosition(0);


        // slightly opened
        roboController.intakeGripper.setPosition(intakeSlightOpen);

        // retract intake slide
        roboController.rightIntakePusher.setPosition(1);
        roboController.leftIntakePusher.setPosition(1);

        // wait until the arm is at the back position first before loosening claw
        sleep(500);

        // close outtake claw
        roboController.outtakeGripper.setPosition(outtakeClose);

        sleep(200);

        // open intake claw
        roboController.intakeGripper.setPosition(intakeOpen);

        // macro position is correct
        // semi-parallel to floor
        roboController.intakeFlip.setPosition(0.72);

        // lower claw
        roboController.intakeRotate.setPosition(1);

        // slightly opened
        if(roboController.intakeGripper.getPosition() == intakeSlightOpen){
            // closed
            roboController.intakeGripper.setPosition(intakeClose);

        }
    }

    // TODO: divider :D

    public void scoreSpecimen(){
        // outtake arm down slightly
        roboController.rightOuttakeFlip.setPosition(0.8);
        roboController.leftOuttakeFlip.setPosition(0.8);

        // outtake claw down to score
        roboController.outtakeRotate.setPosition(0);

        sleep(200);

        // open outtake claw
        roboController.outtakeGripper.setPosition(outtakeOpen);
    }

    // TODO: MUST TEST BEFORE USING!! NOT SURE IF IT WORKS...
    public void grabSpecimenFromWall() throws InterruptedException {
        // sends outtake flip all the way back
        roboController.rightOuttakeFlip.setPosition(0);
        roboController.leftOuttakeFlip.setPosition(0);
        wait(100);

        // opens outtake claw
        roboController.outtakeGripper.setPosition(outtakeOpen);
        wait(100);

        // sends outtake rotate claw all the way back
        roboController.outtakeRotate.setPosition(outtakeClawBack);
        wait(100);

        // opens outtake claw
        roboController.outtakeGripper.setPosition(outtakeClose);
        wait(100);
    }
}