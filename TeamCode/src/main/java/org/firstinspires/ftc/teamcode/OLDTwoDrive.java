package org.firstinspires.ftc.teamcode;

import com.acmerobotics.roadrunner.Pose2d;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.Gamepad;

@TeleOp
public class OLDTwoDrive extends LinearOpMode {
    private OLDRobo roboController;
    private boolean movingBack = false;

    @Override
    public void runOpMode() {
        roboController = new OLDRobo(this);

        telemetry.addData("Status", "Initialized");
        telemetry.update();

        MecanumDrive drive = new MecanumDrive(hardwareMap, new Pose2d(20, 24, 0));

        // Wait for the game to start (driver presses PLAY)
        waitForStart();

        // run until the end of the match (driver presses STOP)
        while (opModeIsActive()) {
            // while opMode is running, allow the methods for manipulating the wheels and arms
            // to be used and print data values
            moveWheels(gamepad1);
            moveArm(gamepad2);

            telemetry.addData("Status", "Running");
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

        if(movepad.left_trigger > 0){
            strafePower = -movepad.left_trigger;
        } else if(movepad.right_trigger > 0){
            strafePower = movepad.right_trigger;
        } else {
            strafePower = 0;
        }


        // turns the robot's (wheel) motors left and right using the game pad 1 right joystick
        turnPower = movepad.right_stick_x;

        if(movepad.dpad_down){
            drivePower = -1;
            strafePower = 0;
            turnPower = 0;
        }

        // drive, turn, and strafe logic
        // https://youtu.be/jRVUHapKx4o?si=1jVJ-ts7d2rkHCdq

        if(movepad.dpad_down){
            roboController.FLW.setPower(drivePower * 0.8);
            roboController.FRW.setPower(drivePower * 0.75);
            roboController.BLW.setPower(drivePower * 0.8);
            roboController.BRW.setPower(drivePower * 0.75);
        } else {
            roboController.FLW.setPower(drivePower + turnPower + strafePower);
            roboController.FRW.setPower(drivePower - turnPower - strafePower);
            roboController.BLW.setPower(drivePower + turnPower - strafePower);
            roboController.BRW.setPower(drivePower - turnPower + strafePower);
        }


        telemetry.addData("Drive Power", drivePower);
        telemetry.addData("Strafe Power", strafePower);
        telemetry.addData("Turn Power", turnPower);

        // dpad up for toggling specimen arm
        if(movepad.dpad_up && !roboController.specimenArmLastState){
            // preset specimen arm down
            /*
            if(roboController.justStarted){
                //roboController.specimenArm.setPosition(0);
                roboController.specimenArm.setPosition(0.738);

                roboController.justStarted = false;
            } else {

             */

            if (roboController.specimenArm.getPosition() < 0.5) {
                // set specimenArm position 1
                roboController.specimenArm.setPosition(0.738);
            } else {
                // set specimenArm position 2
                roboController.specimenArm.setPosition(0.25);
            }
            //}
        }

        roboController.specimenArmLastState = movepad.dpad_up;

        /*
        // solo driver will use up and down dpad (only in 2-person drive) to control
        // hanging arm linear slide
        if(movepad.dpad_up){
            roboController.hangingArm.setPower(1);
        } else if(movepad.dpad_down) {
            roboController.hangingArm.setPower(-1);
        } else {
            roboController.hangingArm.setPower(0);
        }
         */
    }

    public void moveArm(Gamepad armpad){
        // ** arm movement **

        /*
        // preset specimen arm down
        if(roboController.justStarted){
            roboController.specimenArm.setPosition(0);

            roboController.justStarted = false;
        }
         */

        // powers the robot's (arm) motors using the game pad 2 left joystick
        // (this will make the arm's motor power vary depending on how much you're using the joystick.
        // this can be changed later if we want the motor to have a constant power no matter what
        // value the joystick is giving)
        // linear slide(s) for outtake arm
        // (provides a threshold for deactivating motor power since joystick may not be at exactly 0)

        // right = extend
        // left = retract

        // triggers control extension of intake arm
        if (armpad.right_trigger > 0.25) {
            roboController.HLS.setPower(armpad.right_trigger);
        } else if (armpad.left_trigger > 0.25) {
            roboController.HLS.setPower(-armpad.left_trigger);
        } else {
            roboController.HLS.setPower(0);
        }

        // bumpers control extension of outtake arm
        if (armpad.left_bumper) {
            roboController.VLS.setPower(-1);
        } else if (armpad.right_bumper) {
            roboController.VLS.setPower(1);
        } else {
            roboController.VLS.setPower(0);
        }

        // circle controls 3 intake arm positions
        if (armpad.circle && !roboController.inArmLastState) {
            if (roboController.inArmState == 2) {
                roboController.inArmState = 0;
            } else {
                roboController.inArmState++;
            }

            if (roboController.inArmState == 0) {
                // neutral position
                roboController.shoulder.setPosition(0.1);

            } else if (roboController.inArmState == 1) {
                // pickup position (slightly hovered)
                roboController.shoulder.setPosition(0.64);

            } else if (roboController.inArmState == 2) {
                // drop off position
                roboController.shoulder.setPosition(0.26);
            }
        }

        // hold dpad down to lower intake arm more
        if (armpad.dpad_down) {
            if (!roboController.inArmLastStateLower) {
                if (roboController.inArmState == 1) {
                    // pickup position (on block level)
                    roboController.shoulder.setPosition(0.73);
                }

                roboController.inArmLastStateLower = true;
            }
        } else {
            if (roboController.inArmState == 1) {
                // pickup position (slightly hovered)
                roboController.shoulder.setPosition(0.64);
            }

            roboController.inArmLastStateLower = false;
        }

        roboController.inArmLastState = armpad.circle;

        // 1 = open
        // 0 = closed

        // x/a controls opening and closing claw
        if (armpad.a && !roboController.inClawLastState) {
            // initially closed
            if (roboController.inClaw.getPosition() <= 0.575) {
                // opening
                roboController.inClaw.setPosition(0.65);

                // initially opened
            } else {
                // closing
                roboController.inClaw.setPosition(0.4);
            }
        }

        roboController.inClawLastState = armpad.a;

        // triangle controls the bucket position
        if (armpad.triangle && !roboController.outClawLastState) {
            if (roboController.outClaw.getPosition() < 0.5) {
                roboController.outClaw.setPosition(1);
            } else {
                roboController.outClaw.setPosition(0);
            }
        }

        roboController.outClawLastState = armpad.triangle;

        // square controls adjustment of wrist position
        if (armpad.square && !roboController.wristLastState) {
            if (roboController.wrist.getPosition() < 0.25) {
                roboController.wrist.setPosition(0.5);
            } else {
                roboController.wrist.setPosition(0);
            }
        }

        roboController.wristLastState = armpad.square;

        /*
        // dpad up for toggling specimen arm
        if(armpad.dpad_up && !roboController.specimenArmLastState){
            // preset specimen arm down
            if(roboController.justStarted){
                roboController.specimenArm.setPosition(0);

                roboController.justStarted = false;
            } else {

                if (roboController.specimenArm.getPosition() < 0.5) {
                    // set specimenArm position 1
                    roboController.specimenArm.setPosition(0.738);
                } else {
                    // set specimenArm position 2
                    roboController.specimenArm.setPosition(0.25);
                }
            }
        }

        roboController.specimenArmLastState = armpad.dpad_up;
        */
    }
}