package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.Gamepad;

@TeleOp
public class OLDSoloDrive extends LinearOpMode {
    private OLDRobo roboController;

    @Override
    public void runOpMode() {
        roboController = new OLDRobo(this);

        telemetry.addData("Status", "Initialized");
        telemetry.update();

        // Wait for the game to start (driver presses PLAY)
        waitForStart();

        // run until the end of the match (driver presses STOP)
        while (opModeIsActive()) {
            moveRobot(gamepad1);

            telemetry.addData("Status", "Running");
            telemetry.update();
        }
    }

    public void moveRobot(Gamepad gamepad){
        // ** wheel movement **
        double drivePower = 0;
        double strafePower = 0;
        double turnPower = 0;

        // moves the robot's (wheel) motors forward and back using the game pad 1 left joystick
        drivePower = -gamepad.left_stick_y;

        // moves the robot's (wheel) motors left and right using the game pad 1 left joystick
        strafePower = gamepad.left_stick_x;

        // turns the robot's (wheel) motors left and right using the game pad 1 right joystick
        turnPower = gamepad.right_stick_x;

        // drive, turn, and strafe logic
        // https://youtu.be/jRVUHapKx4o?si=1jVJ-ts7d2rkHCdq
        roboController.FLW.setPower(drivePower + turnPower + strafePower);
        roboController.FRW.setPower(drivePower - turnPower - strafePower);
        roboController.BLW.setPower(drivePower + turnPower - strafePower);
        roboController.BRW.setPower(drivePower - turnPower + strafePower);


        telemetry.addData("Drive Power", drivePower);
        telemetry.addData("Strafe Power", strafePower);
        telemetry.addData("Turn Power", turnPower);


        // ** arm movement **

        // powers the robot's (arm) motors using the game pad 2 left joystick
        // (this will make the arm's motor power vary depending on how much you're using the joystick.
        // this can be changed later if we want the motor to have a constant power no matter what
        // value the joystick is giving)
        // linear slide(s) for outtake arm
        // (provides a threshold for deactivating motor power since joystick may not be at exactly 0)

        // right = extend
        // left = retract

        // triggers control extension of intake arm
        if (gamepad.right_trigger > 0.25) {
            roboController.HLS.setPower(gamepad.right_trigger);
        } else if (gamepad.left_trigger > 0.25) {
            roboController.HLS.setPower(-gamepad.left_trigger);
        } else {
            roboController.HLS.setPower(0);
        }

        // bumpers control extension of outtake arm
        if (gamepad.left_bumper) {
            roboController.VLS.setPower(-1);
        } else if (gamepad.right_bumper) {
            roboController.VLS.setPower(1);
        } else {
            roboController.VLS.setPower(0);
        }

        // circle controls 3 intake arm positions
        if (gamepad.circle && !roboController.inArmLastState) {
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
        if (gamepad.dpad_down) {
            if (!roboController.inArmLastStateLower) {
                if (roboController.inArmState == 1) {
                    // pickup position (on block level)
                    roboController.shoulder.setPosition(0.75);
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

        roboController.inArmLastState = gamepad.circle;

        // 1 = open
        // 0 = closed

        // x/a controls opening and closing claw
        if (gamepad.a && !roboController.inClawLastState) {
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

        roboController.inClawLastState = gamepad.a;

        // triangle controls the bucket position
        if (gamepad.triangle && !roboController.outClawLastState) {
            if (roboController.outClaw.getPosition() < 0.5) {
                roboController.outClaw.setPosition(1);
            } else {
                roboController.outClaw.setPosition(0);
            }
        }

        roboController.outClawLastState = gamepad.triangle;

        // square controls adjustment of wrist position
        if (gamepad.square && !roboController.wristLastState) {
            if (roboController.wrist.getPosition() < 0.25) {
                roboController.wrist.setPosition(0.5);
            } else {
                roboController.wrist.setPosition(0);
            }
        }

        roboController.wristLastState = gamepad.square;

        // dpad up for toggling specimen arm
        if(gamepad.dpad_up && !roboController.specimenArmLastState){
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

        roboController.specimenArmLastState = gamepad.dpad_up;

        // solo driver will use right and left dpad (only in solo drive) to control
        // hanging arm linear slide
        if(gamepad.dpad_right){
            roboController.hangingArm.setPower(1);
        } else if(gamepad.dpad_left) {
            roboController.hangingArm.setPower(-1);
        } else {
            roboController.hangingArm.setPower(0);
        }
    }
}