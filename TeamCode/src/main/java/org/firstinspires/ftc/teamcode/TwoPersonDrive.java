package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.Servo;

@TeleOp
public class TwoPersonDrive extends LinearOpMode {
    private RoboController roboController;
    private double intakeOpen = 0;
    private double intakeSlightOpen = 0.39;
    private double intakeClose = 0.75;
    private double intakeGripperMid = 0.4;
    private double outtakeOpen = 0.4;
    private double outtakeClose = 0.62;
    private double outtakeGripperMid = 0.5;
    private double outtakeClawForward = 0.87;
    //private double outtakeClawForward = 0.87;
    private double outtakeClawBack = 0;
    private double outtakeClawMiddle = 0.44;
    private double outtakeRotateMid = 0.5;
    private double intakeFlipDown = 0.7;
    private double intakeRotateDown = 1;
    private double intakeFlipUp = 0.15;
    private double intakeRotateUp = 0;

    private boolean buttonPressedDpadDown = false;
    private boolean buttonPressedDpadUp1 = false;
    private boolean buttonPressedDpadUp2 = false;
    private boolean buttonPressedDpadLeft = false;
    private boolean buttonPressedDpadRight = false;
    private boolean buttonPressedBumperLeft = false;
    private boolean buttonPressedBumperRight = false;

    private boolean buttonPressedCircle = false;
    private boolean buttonPressedSquare1 = false;
    private boolean buttonPressedSquare2 = false;
    private boolean buttonPressedA = false;
    private boolean buttonPressedTriangle1 = false;
    private boolean buttonPressedTriangle2 = false;
    private double outtakeTwistStraight1 = 0.15;
    private double outtakeTwistStraight2 = 0.9;
    private boolean pickupPos = false;
    private int scorePos = 0;
    private int intakeToggle = 0;
    private int toggleOuttakeWrist = 0;

    private boolean lastBumperPressed = false;

    public static Delay outtakeFlipDelay = new Delay();
    public static Delay outtakeRotateDelay = new Delay();
    public static Delay outtakeTwistDelay = new Delay();
    public static Delay outtakeGripperDelay = new Delay();
    private static int outtakeFlipPos = -1;

    @Override
    public void runOpMode() {
        roboController = new RoboController(this);

        telemetry.addData("Status", "Initialized");
        telemetry.update();

        // straightened arm
        // **
        //roboController.intakeFlip.setPosition(0.3);

        // higher
        //roboController.intakeRotate.setPosition(intakeRotateUp);

        // Wait for the game to start (driver presses PLAY)
        waitForStart();

        // preset robot positions
        // semi-parallel to floor
        roboController.intakeFlip.setPosition(intakeFlipDown);

        // lower claw
        roboController.intakeRotate.setPosition(intakeRotateDown);

        pickupPos = true;

        // straightened arm
        // **
//        roboController.intakeFlip.setPosition(0.3);
//
//        // higher
//        roboController.intakeRotate.setPosition(intakeRotateUp);

        //sleep(100);

        // retract arm
        roboController.rightIntakePusher.setPosition(1);
        roboController.leftIntakePusher.setPosition(1);


        // preset outtake arm positions
        // resets the tracker for outtake position
        scorePos = 0;

        // outtake claw up so it doesn't hit
        roboController.outtakeRotate.setPosition(outtakeClawForward);

        // straighten outtake claw twist
        roboController.outtakeTwist.setPosition(outtakeTwistStraight2);

        sleep(100);

        // move outtake arm back
        roboController.rightOuttakeFlip.setPosition(0);
        roboController.leftOuttakeFlip.setPosition(0);

        sleep(200);

        // outtake claw back
        roboController.outtakeRotate.setPosition(0.12);

        sleep(100);

        // open outtake claw
        // maybe change value
        roboController.outtakeGripper.setPosition(outtakeOpen);


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
            telemetry.addData("Intake Toggle", intakeToggle);

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

        strafePower = movepad.left_stick_x;

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

        // square/x controls adjustment of wrist position
        if (movepad.square && !buttonPressedSquare1) {
            if (roboController.outtakeTwist.getPosition() < 0.5) {
                roboController.outtakeTwist.setPosition(outtakeTwistStraight2);
            } else {
                roboController.outtakeTwist.setPosition(outtakeTwistStraight1);
            }
        }

        buttonPressedSquare1 = movepad.square;


        // *** SPECIMEN ARM (outtake) ***
        if(movepad.right_bumper){
            dropSample();
        }

        if(movepad.triangle && !buttonPressedTriangle1){
            // close outtake claw
            roboController.outtakeGripper.setPosition(outtakeClose);

            sleep(500);

            if(roboController.outtakeRotate.getPosition() <= 0.2){
                // tilt outtake claw up
                roboController.outtakeRotate.setPosition(0);

                sleep(500);
            }
        }

        buttonPressedTriangle1 = movepad.triangle;


        if(movepad.left_bumper){
            if(scorePos == 0){
                setSpecimen();
            } else if(scorePos == 1){
                scoreSpecimen();
            }

            scorePos++;

            if(scorePos > 1){
                scorePos = 0;
            }
        }

        // dpad up toggles outtake claw
        if(movepad.dpad_up && !buttonPressedDpadUp1){
            // initially closed
            if (roboController.outtakeGripper.getPosition() <= outtakeGripperMid) {
                // closed
                roboController.outtakeGripper.setPosition(outtakeClose);
            } else {
                // opened
                roboController.outtakeGripper.setPosition(outtakeOpen);
            }

            // straighten outtake claw twist
            roboController.outtakeTwist.setPosition(0.17);
        }

        buttonPressedDpadUp1 = movepad.dpad_up;
    }
    public void moveArm(Gamepad armpad){
        // ** arm movement **

        // right = exten
        // left = retract


        // *** INTAKE ***

        // bumpers control extension of intake arm
        if (armpad.left_bumper && !buttonPressedBumperLeft) {
            if(intakeToggle == 0){
                // retract
                roboController.rightIntakePusher.setPosition(1);
                roboController.leftIntakePusher.setPosition(1);
            } else if(intakeToggle == 1){
                // middle
                roboController.rightIntakePusher.setPosition(0.75);
                roboController.leftIntakePusher.setPosition(0.75);
            }

            intakeToggle++;

            if(intakeToggle > 1){
                intakeToggle = 0;
            }
        } else if (armpad.right_bumper && !buttonPressedBumperRight) {
            if(intakeToggle == 0){
                // extend
                roboController.rightIntakePusher.setPosition(0);
                roboController.leftIntakePusher.setPosition(0);
            } else if(intakeToggle == 1){
                // middle
                roboController.rightIntakePusher.setPosition(0.75);
                roboController.leftIntakePusher.setPosition(0.75);
            }

            intakeToggle++;

            if(intakeToggle > 1){
                intakeToggle = 0;
            }
        }

        buttonPressedBumperLeft = armpad.left_bumper;
        buttonPressedBumperRight = armpad.right_bumper;



        // circle/b toggles intake arm positions
        if(armpad.circle && !buttonPressedCircle){
            if(roboController.intakeFlip.getPosition() < 0.5){
                // semi-parallel to floor
                roboController.intakeFlip.setPosition(0.7);

                // lower claw
                roboController.intakeRotate.setPosition(1);

                // slightly opened
                if(roboController.intakeGripper.getPosition() == intakeSlightOpen){
                    // closed
                    roboController.intakeGripper.setPosition(intakeClose);
                }

                pickupPos = true;
            } else {
                // higher to transfer
                roboController.intakeFlip.setPosition(intakeFlipUp);

                // higher to transfer
                roboController.intakeRotate.setPosition(intakeRotateUp);

                // closed
                if(roboController.intakeGripper.getPosition() == 0.75){
                    // straighten claw
                    roboController.intakeTwist.setPosition(0);

                    // wait until the arm is at the back position first before loosening claw
                    sleep(500);

                    // slightly opened
                    roboController.intakeGripper.setPosition(intakeSlightOpen);
                }

                pickupPos = false;
            }
        }

        buttonPressedCircle = armpad.circle;


        // x/a controls opening and closing intake claw
        if((armpad.a && !buttonPressedA)){
            // initially closed
            if (roboController.intakeGripper.getPosition() <= intakeGripperMid) {
                // closed
                roboController.intakeGripper.setPosition(intakeClose);
            } else {
                // opened
                roboController.intakeGripper.setPosition(intakeOpen);
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
                roboController.intakeFlip.setPosition(intakeFlipDown);

                // lower claw
                roboController.intakeRotate.setPosition(intakeRotateDown);
            }
        }

        // square/x controls adjustment of wrist position
        if (armpad.square && !buttonPressedSquare2) {
            if (roboController.intakeTwist.getPosition() < 0.25) {
                roboController.intakeTwist.setPosition(0.52);
            } else {
                roboController.intakeTwist.setPosition(0);
            }
        }

        buttonPressedSquare2 = armpad.square;

        // macro position for transfer between intake and outtake
        if (armpad.triangle && !buttonPressedTriangle2) {
            transferSample();
        }

        buttonPressedTriangle2 = armpad.triangle;

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

        // *** ONLY FOR TESTING
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

        if (armpad.dpad_left && !buttonPressedDpadLeft) {

            // outtake flip: flips the thing on top of the linear slide
            // roboController.leftOuttakeFlip.setDirection(Servo.Direction.REVERSE);
            if (outtakeFlipPos == 2) {
                outtakeFlipPos = 0;
            } else {
                outtakeFlipPos++;
            }

            if (outtakeFlipPos == 0) {
                roboController.rightOuttakeFlip.setPosition(0);
                roboController.leftOuttakeFlip.setPosition(0);
            } else if (outtakeFlipPos == 1) {
                roboController.rightOuttakeFlip.setPosition(.65);
                roboController.leftOuttakeFlip.setPosition(.65);
            } else if (outtakeFlipPos == 2) {
                roboController.rightOuttakeFlip.setPosition(1);
                roboController.leftOuttakeFlip.setPosition(1);
            }
        }

        buttonPressedDpadLeft = armpad.dpad_left;

        if (armpad.dpad_right && !buttonPressedDpadRight) {
            // outtake rotate: flips the claw itself on the thing on top of the linear slide
            // not to be confused with outtake flip

            if(toggleOuttakeWrist == 0){
                // back
                roboController.outtakeRotate.setPosition(outtakeClawBack);
            } else if(toggleOuttakeWrist == 1){
                // middle
                roboController.outtakeRotate.setPosition(outtakeClawMiddle);
            } else if(toggleOuttakeWrist == 2){
                // forward
                roboController.outtakeRotate.setPosition(outtakeClawForward);
            }

            toggleOuttakeWrist++;

            if(toggleOuttakeWrist > 2){
                toggleOuttakeWrist = 0;
            }

            /*
            if (roboController.outtakeRotate.getPosition() >= outtakeRotateMid) {
                // back
                roboController.outtakeRotate.setPosition(outtakeClawBack);
            } else {
                // forward
                roboController.outtakeRotate.setPosition(outtakeClawForward);
            }

             */
        }

        buttonPressedDpadRight = armpad.dpad_right;


        // dpad up toggles outtake claw
        if(armpad.dpad_up && !buttonPressedDpadUp2){
            // initially closed
            if (roboController.outtakeGripper.getPosition() <= outtakeGripperMid) {
                // closed
                roboController.outtakeGripper.setPosition(outtakeClose);
            } else {
                // opened
                roboController.outtakeGripper.setPosition(outtakeOpen);
            }
        }

        buttonPressedDpadUp2 = armpad.dpad_up;

        /*
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
                roboController.outtakeRotate.setPosition(0.9);
            }
            outtakeRotateDelay.open = !outtakeRotateDelay.open;


        }

         */

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

    public void transferSample(){
        // resets the tracker for outtake position
        scorePos = 0;

        // ** outtake first
        // move back before if it needs to
        if(roboController.rightOuttakeFlip.getPosition() <= 0.17){
            roboController.rightOuttakeFlip.setPosition(0);
            roboController.leftOuttakeFlip.setPosition(0);
        }

        // straighten outtake claw twist
        roboController.outtakeTwist.setPosition(outtakeTwistStraight2);

        // outtake claw up so it doesn't hit
        roboController.outtakeRotate.setPosition(outtakeClawForward);

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

        pickupPos = true;
    }

    public void dropSample(){
        // semi-parallel to floor
        roboController.intakeFlip.setPosition(intakeFlipDown);

        // lower claw
        roboController.intakeRotate.setPosition(intakeRotateDown);

        pickupPos = true;

        // resets the tracker for outtake position
        scorePos = 0;

        // outtake claw up so it doesn't hit
        roboController.outtakeRotate.setPosition(outtakeClawForward);

        // straighten outtake claw twist
        roboController.outtakeTwist.setPosition(outtakeTwistStraight1);

        sleep(100);

        // move outtake arm back
        roboController.rightOuttakeFlip.setPosition(0);
        roboController.leftOuttakeFlip.setPosition(0);

        sleep(300);

        // outtake claw back
        roboController.outtakeRotate.setPosition(0.12);

        sleep(300);

        // open outtake claw
        roboController.outtakeGripper.setPosition(outtakeOpen);
    }

    public void setSpecimen(){
        // semi-parallel to floor
        roboController.intakeFlip.setPosition(intakeFlipDown);

        // lower claw
        roboController.intakeRotate.setPosition(intakeRotateDown);

        pickupPos = true;

        // outtake arm forward
        roboController.rightOuttakeFlip.setPosition(1);
        roboController.leftOuttakeFlip.setPosition(1);

        sleep(300);

        // rotate wrist 180 degrees
        roboController.outtakeTwist.setPosition(outtakeTwistStraight2);

        // outtake claw middle
        roboController.outtakeRotate.setPosition(outtakeClawMiddle);
    }

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