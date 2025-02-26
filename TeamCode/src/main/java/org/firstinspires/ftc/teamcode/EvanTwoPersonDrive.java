package org.firstinspires.ftc.teamcode;
import static com.qualcomm.robotcore.hardware.Servo.Direction.REVERSE;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.Gamepad;

@TeleOp
public class EvanTwoPersonDrive extends LinearOpMode {
    private RoboController roboController;
    public static double servoPos = 0.5;
    public static Delay outtakeFlipDelay = new Delay();
    public static Delay outtakeRotateDelay = new Delay();
    public static Delay outtakeTwistDelay = new Delay();
    public static Delay outtakeGripperDelay = new Delay();

    // tests
    private static double outtakeFlipPos = 0;
    private static double outtakeRotatePos = 0;

    @Override
    public void runOpMode() {
        roboController = new RoboController(this);

        telemetry.addData("Status", "Initialized");
        telemetry.update();

        // Wait for the game to start (driver presses PLAY)
        waitForStart();

        // run until the end of the match (driver presses STOP)

        roboController.outtakeFlipR.setPosition(0);
        roboController.outtakeFlipL.setPosition(0);
        outtakeFlipPos = 0;

        roboController.outtakeRotate.setPosition(0);
        outtakeRotatePos = 0;

        while (opModeIsActive()) {
            // while opMode is running, allow the methods for manipulating the wheels and arms
            // to be used and print data values
            //moveWheels(gamepad1);
            // moveArm(gamepad2);
            test(gamepad1);

            telemetry.addData("Status", "Running");
            telemetry.addData("RIP", roboController.rightIntakePusher.getPosition());
            telemetry.addData("LIP", roboController.leftIntakePusher.getPosition());
            telemetry.addData("IFS", roboController.intakeFlip.getPosition());
            telemetry.addData("Flip Outtake Pos:", outtakeFlipPos);
            telemetry.addData("Rotate Outtake Pos:", outtakeRotatePos);
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
            TODO: ALSO CHANGE VALUES! THESE ARE SUBSTITUTE VALUES
            */

        if (outtakePad.a && outtakeFlipDelay.delay()) {

            // outtake flip: flips the thing on top of the linear slide
            roboController.outtakeFlipL.setDirection(REVERSE);
            if (outtakeFlipPos == 2) {
                outtakeFlipPos = 0;
            } else {
                outtakeFlipPos++;
            }

            if (outtakeFlipPos == 0) {
                roboController.outtakeFlipR.setPosition(0);
                roboController.outtakeFlipL.setPosition(0);
            } else if (outtakeFlipPos == 1) {
                roboController.outtakeFlipR.setPosition(.35);
                roboController.outtakeFlipL.setPosition(.35);
            } else if (outtakeFlipPos == 2) {
                roboController.outtakeFlipR.setPosition(1);
                roboController.outtakeFlipL.setPosition(1);
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
        roboController.outtakeFlipL.setDirection(REVERSE);
        if (outtakePad.a && outtakeFlipDelay.delay()) {
            // outtake flip: flips the thing on top of the linear slide
            roboController.outtakeFlipR.setPosition(outtakeFlipPos);
            roboController.outtakeFlipL.setPosition(outtakeFlipPos);
            outtakeFlipPos += .05;
        } else if (outtakePad.b && outtakeFlipDelay.delay()) {
            // outtake flip: flips the thing on top of the linear slide
            roboController.outtakeFlipR.setPosition(outtakeFlipPos);
            roboController.outtakeFlipL.setPosition(outtakeFlipPos);
            outtakeFlipPos -= .05;
        } else if (outtakePad.x && outtakeRotateDelay.delay()) {
            roboController.outtakeRotate.setPosition(outtakeRotatePos);
            outtakeRotatePos += .05;
        } else if (outtakePad.y && outtakeRotateDelay.delay()) {
            roboController.outtakeRotate.setPosition(outtakeRotatePos);
            outtakeRotatePos -= .05;
        }
    }


    public void scoringMacro(Gamepad outtakePad) throws InterruptedException {
        roboController.outtakeFlipL.setDirection(REVERSE);

        //TODO: Change placeholder values

        final double flipRaisePos = Math.abs(24); //placeholder value
        final double flipSetPos = Math.sqrt(Math.pow(21,2)); //placeholder value
        final double rotatePos = Math.PI; //placeholder value
        final double rotateSetPos = Math.E; //placeholder value

        roboController.outtakeFlipR.setPosition(flipRaisePos);
        roboController.outtakeFlipL.setPosition(flipRaisePos);

        wait(200);

        roboController.outtakeRotate.setPosition(rotatePos);

        wait(2000);

        roboController.outtakeFlipR.setPosition(flipSetPos);
        roboController.outtakeFlipL.setPosition(flipSetPos);

        wait(200);

        roboController.outtakeRotate.setPosition(rotateSetPos);
    }
}