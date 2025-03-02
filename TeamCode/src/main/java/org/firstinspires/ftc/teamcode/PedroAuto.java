package org.firstinspires.ftc.teamcode;

import com.pedropathing.follower.Follower;
import com.pedropathing.localization.Pose;
import com.pedropathing.pathgen.BezierLine;
import com.pedropathing.pathgen.Path;
import com.pedropathing.pathgen.PathChain;
import com.pedropathing.pathgen.Point;
import com.pedropathing.util.Constants;
import com.pedropathing.util.Timer;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

import pedroPathing.constants.FConstants;
import pedroPathing.constants.LConstants;

@Autonomous(name = "Pedro Pathing Autonomous")

public class PedroAuto extends OpMode {
    private Follower follower;
    private Timer pathTimer, actionTimer, opmodeTimer;

    //store state of auto
    private int pathState;

    //private RoboController roboController;
    private double intakeOpen = 0;
    private double intakeSlightOpen = 0.39;
    private double intakeClose = 0.75;
    private double intakeGripperMid = 0.4;
    private double outtakeOpen = 0.4;
    private double outtakeClose = 0.62;
    private double outtakeGripperMid = 0.5;
    private double outtakeClawFoward = 0.87;
    //private double outtakeClawFoward = 0.87;
    private double outtakeClawBack = 0;
    private double outtakeClawMiddle = 0.44;
    private double outtakeTwistStraight1 = 0.15;
    private double outtakeTwistStraight2 = 0.9;
    private double outtakeRotateMid = 0.5;
    private double intakeFlipDown = 0.7;
    private double intakeRotateDown = 1;
    private double intakeFlipUp = 0.15;
    private double intakeRotateUp = 0;

    private final Pose startPose = new Pose(9, 60, Math.toRadians(0));  // Starting position
    // Scoring positions
    private final Pose score0Pose = new Pose(39, 65, Math.toRadians(0));
    private final Pose score1Pose = new Pose(39, 67, Math.toRadians(0));
    private final Pose score2Pose = new Pose(39, 69, Math.toRadians(0));
    private final Pose score3Pose = new Pose(39, 71, Math.toRadians(0));

    private final Pose pickup1Pose = new Pose(22, 24, Math.toRadians(0));
    private final Pose pickup2Pose = new Pose(22, 14, Math.toRadians(0));
    private final Pose pickupSpeciPose = new Pose(15.2, 36, Math.toRadians(0));
    private final Pose pickupSpeciPose2 = new Pose(15.8, 36, Math.toRadians(0));


    private final Pose parkPose = new Pose(60, 98, Math.toRadians(90));    // Parking position
    private final Pose parkControlPose = new Pose(60, 98, Math.toRadians(90)); // Control point for curved path

    private Path scorePreload, park;
    private PathChain pickDrop1, pickDrop2, pickUpSpeci1, scoreSpeci1, pickUpSpeci2, scoreSpeci2, pickUpSpeci3, scoreSpeci3;

    private Servo rightIntakePusher;
    private Servo leftIntakePusher;
    private Servo intakeFlip;
    private Servo intakeRotate;
    private Servo intakeTwist;
    private Servo intakeGripper;
    private DcMotor leftVerticalSlide;
    private DcMotor rightVerticalSlide;

    private Servo leftOuttakeFlip;
    private Servo rightOuttakeFlip;
    private Servo outtakeRotate;
    private Servo outtakeTwist;
    private Servo outtakeGripper;


    public void buildPaths() {
        // Path for scoring preload
        scorePreload = new Path(new BezierLine(new Point(startPose), new Point(score0Pose)));
        scorePreload.setConstantHeadingInterpolation(startPose.getHeading());

        // Path chains for picking up and scoring samples
        pickDrop1 = follower.pathBuilder()
                .addPath(new BezierLine(new Point(score0Pose), new Point(pickup1Pose)))
                .setLinearHeadingInterpolation(0,0)
                .build();

        pickDrop2 = follower.pathBuilder()
                .addPath(new BezierLine(new Point(pickup1Pose), new Point(pickup2Pose)))
                .setLinearHeadingInterpolation(0,0)
                .build();

        pickUpSpeci1 = follower.pathBuilder()
                .addPath(new BezierLine(new Point(pickup2Pose), new Point(pickupSpeciPose)))
                .setLinearHeadingInterpolation(0, 0)
                .build();

        scoreSpeci1 = follower.pathBuilder()
                .addPath(new BezierLine(new Point(pickupSpeciPose), new Point(score1Pose)))
                .setLinearHeadingInterpolation(0, 0)
                .build();

        pickUpSpeci2 = follower.pathBuilder()
                .addPath(new BezierLine(new Point(score1Pose), new Point(pickupSpeciPose2)))
                .setLinearHeadingInterpolation(0,0)
                .build();

        scoreSpeci2 = follower.pathBuilder()
                .addPath(new BezierLine(new Point(pickupSpeciPose), new Point(score2Pose)))
                .setLinearHeadingInterpolation(0,0)
                .build();

        pickUpSpeci3 = follower.pathBuilder()
                .addPath(new BezierLine(new Point(score2Pose), new Point(pickupSpeciPose)))
                .setLinearHeadingInterpolation(0,0)
                .build();

        scoreSpeci3 = follower.pathBuilder()
                .addPath(new BezierLine(new Point(pickupSpeciPose), new Point(score3Pose)))
                .setLinearHeadingInterpolation(0,0)
                .build();

        // Curved path for parking
        //park = new Path(new BezierCurve(new Point(scorePose), new Point(parkControlPose), new Point(parkPose)));
        //park.setLinearHeadingInterpolation(scorePose.getHeading(), parkPose.getHeading());

    }

    public void autonomousPathUpdate() throws InterruptedException {
        switch (pathState) {
            case 0: //Move from start to scoring position, aim and score
//                outtakeGripper.setPosition(outtakeClose);
                //pause(100);

                // retract
                rightIntakePusher.setPosition(1);
                leftIntakePusher.setPosition(1);

                setSpecimen();
                follower.followPath(scorePreload);

                //pause(100);

                setPathState(1);
                break;

            case 1: //move to between the first block and obs. zone, pick and drop the block into obs. zone
                if (!follower.isBusy()) {
                    scoreSpecimen();

                    // higher
                    intakeFlip.setPosition(intakeFlipUp);

                    // higher
                    intakeRotate.setPosition(intakeRotateUp);

                    follower.followPath(pickDrop1, true);
//                    pickDrop();
//                    //wait(500);
//
//                    pause(500);

                    setPathState(2);
                }
                break;

            case 2: //move to between the second block and obs. zone, pick and drop the block into obs. zone
                if (!follower.isBusy()) {
                    pickDrop();
                    //wait(500);

                    //pause(500);

                    follower.followPath(pickDrop2, true);
//                    pickDrop();
//
//                    pause(500);
                    setPathState(3);
                }
                break;


            case 3: //move backwards to grab specimen from the wall
                if (!follower.isBusy()) {
                    pickDrop();

                    //pause(500);
                    follower.followPath(pickUpSpeci1, true);
//                    grabSpecimen();
                    setPathState(4);
                }
                break;

            case 4: //score speci1
                if (!follower.isBusy()) {
                    grabSpecimen();
                    setSpecimen();
                    follower.followPath(scoreSpeci1, true);
//                    pause(1000);
//                    scoreSpecimen();
                    setPathState(5);
                }
                break;

            case 5: //move near obs. zone and grab speci2 from the wall
                if (!follower.isBusy()) {
                    //pause(1000);
                    scoreSpecimen();
                    follower.followPath(pickUpSpeci2, true);
                    dropSample();
                    //grabSpecimen();
                    setPathState(6);
                }
                break;

            case 6: //score speci2
                if (!follower.isBusy()) {
                    grabSpecimen();
                    setSpecimen();
                    follower.followPath(scoreSpeci2, true);
//                    pause(1000);
//                    scoreSpecimen();
                    setPathState(7);
                }
                break;

            case 7: //move near obs. zone and grab speci3(human player's preload) from the wall
                if (!follower.isBusy()) {
                    //pause(1000);
                    scoreSpecimen();
                    follower.followPath(pickUpSpeci3, true);
//                    grabSpecimen();
                    setPathState(8);
                }
                break;

            case 8: //score speci3
                if (!follower.isBusy()) {
                    grabSpecimen();
                    setSpecimen();
                    follower.followPath(scoreSpeci3, true);
//                    pause(1000);
//                    scoreSpecimen();
                    setPathState(9);
                }
                break;

            case 9://End the autonomous routine, or we can add park here
                if (!follower.isBusy()) {
                    //pause(1000);
                    scoreSpecimen();
                    setPathState(-1);
                }
                break;
        }
    }
    public void pickDrop() throws InterruptedException {
        // opens intake claw
        intakeGripper.setPosition(intakeOpen);

        pause(100);

        //extend
        rightIntakePusher.setPosition(.75);
        leftIntakePusher.setPosition(.75);

        pause(600);

        // lower to floor
        intakeFlip.setPosition(0.8);

        // slightly higher claw
        intakeRotate.setPosition(0.92);

        pause(400);

        // closes intake claw
        intakeGripper.setPosition(intakeClose);
        //wait(100);

        pause(100);

        // semi-parallel to floor
        //intakeFlip.setPosition(intakeFlipDown);

        // lower claw
        //intakeRotate.setPosition(intakeRotateDown);

        //pause(100);

        // retract
        //rightIntakePusher.setPosition(1);
        //leftIntakePusher.setPosition(1);
        //wait(100);

        //pause(100);

        transferSample();
        //wait(100);

        //pause(100);
    }

    public void transferSample() throws InterruptedException {
        // ** outtake first
        // move back before if it needs to
        if(rightOuttakeFlip.getPosition() <= 0.17){
            rightOuttakeFlip.setPosition(0);
            leftOuttakeFlip.setPosition(0);
        }

        // straighten outtake claw twist
        outtakeTwist.setPosition(outtakeTwistStraight1);

        // outtake claw up so it doesn't hit
        outtakeRotate.setPosition(outtakeClawFoward);

        // open outtake claw
        outtakeGripper.setPosition(outtakeOpen);

        //wait(200);

        pause(100);

        // outtake arm forward
        rightOuttakeFlip.setPosition(.32);
        leftOuttakeFlip.setPosition(.32);

        //wait(300);

        pause(300);

        // outtake claw to actual position
        outtakeRotate.setPosition(0.75);

        // ** then intake
        // higher
        intakeFlip.setPosition(intakeFlipUp);

        // higher
        intakeRotate.setPosition(intakeRotateUp);

        // straighten claw
        intakeTwist.setPosition(0);

        // slightly opened
        intakeGripper.setPosition(intakeSlightOpen);

        pause(200);

        // retract intake slide
        rightIntakePusher.setPosition(1);
        leftIntakePusher.setPosition(1);

        // wait until the arm is at the back position first before loosening claw
        //wait(500);

        pause(600);

        // close outtake claw
        outtakeGripper.setPosition(outtakeClose);

        //wait(200);

        pause(100);

        // open intake claw
        intakeGripper.setPosition(intakeOpen);

        pause(100);

        // macro position is correct
        // semi-parallel to floor
//        intakeFlip.setPosition(0.72);
//
//        // lower claw
//        intakeRotate.setPosition(1);
//
//        // slightly opened
//        if(intakeGripper.getPosition() == intakeSlightOpen){
//            // closed
//            intakeGripper.setPosition(intakeClose);
//        }

          dropSample();
    }

    public void dropSample(){
        // semi-parallel to floor
        intakeFlip.setPosition(intakeFlipDown);

        // lower claw
        intakeRotate.setPosition(intakeRotateDown);

        //pickupPos = true;

        // resets the tracker for outtake position
        //scorePos = 0;

        // outtake claw up so it doesn't hit
        outtakeRotate.setPosition(outtakeClawFoward);

        // straighten outtake claw twist
        outtakeTwist.setPosition(outtakeTwistStraight1);

        pause(100);

        // move outtake arm back
        rightOuttakeFlip.setPosition(0);
        leftOuttakeFlip.setPosition(0);

        pause(400);

        // outtake claw back
        outtakeRotate.setPosition(0.12);

        pause(200);

        // open outtake claw
        outtakeGripper.setPosition(outtakeOpen);
    }

    public void setPathState(int pState) {
        pathState = pState;
        pathTimer.resetTimer();
    }

    public void setSpecimen(){
        // semi-parallel to floor
        intakeFlip.setPosition(intakeFlipDown);

        // lower claw
        intakeRotate.setPosition(intakeRotateDown);

        pause(300);

        //pickupPos = true;

        // outtake arm forward
        rightOuttakeFlip.setPosition(1);
        leftOuttakeFlip.setPosition(1);

        pause(400);

        // higher
        //intakeFlip.setPosition(intakeFlipUp);

        // higher
        //intakeRotate.setPosition(intakeRotateUp);

        //wait(300);

        //pause(300);

        // rotate wrist 180 degrees
        outtakeTwist.setPosition(outtakeTwistStraight2);

        // outtake claw middle
        outtakeRotate.setPosition(outtakeClawMiddle);
    }

    public void scoreSpecimen() throws InterruptedException {
        // outtake arm down slightly
        rightOuttakeFlip.setPosition(0.8);
        leftOuttakeFlip.setPosition(0.8);

        // outtake claw down to score
        outtakeRotate.setPosition(0);

        //wait(200);

        pause(200);

        // open outtake claw
        outtakeGripper.setPosition(outtakeOpen);

//        // outtake claw up so it doesn't hit
//        outtakeRotate.setPosition(outtakeClawFoward);
//
//        // open outtake claw
//        outtakeGripper.setPosition(outtakeOpen);
//
//        // straighten outtake claw twist
//        outtakeTwist.setPosition(outtakeTwistStraight1);
//
//        pause(100);
//
//        // move outtake arm back
//        rightOuttakeFlip.setPosition(0);
//        leftOuttakeFlip.setPosition(0);
//
//        pause(300);
//
//        // outtake claw back
//        outtakeRotate.setPosition(outtakeClawBack);
    }

    public void grabSpecimen() throws InterruptedException {
//        // sends outtake flip all the way back
//        rightOuttakeFlip.setPosition(0);
//        leftOuttakeFlip.setPosition(0);
//        //wait(100);
//
//        pause(100);
//
//        // opens outtake claw
//        outtakeGripper.setPosition(outtakeOpen);
//        //wait(100);
//
//        pause(100);
//
//        // sends outtake rotate claw all the way back
//        outtakeRotate.setPosition(outtakeClawBack);
//        //wait(100);
//
//        // wait for human player
//        pause(3000);
//
//        // closes outtake claw
//        outtakeGripper.setPosition(outtakeClose);
//        //wait(100);
//
//        pause(100);

        // ***

        // semi-parallel to floor
        intakeFlip.setPosition(intakeFlipDown);

        // lower claw
        intakeRotate.setPosition(intakeRotateDown);

        //pause(100);

        //pickupPos = true;

        // resets the tracker for outtake position
        //scorePos = 0;

//        // outtake claw up so it doesn't hit
//        outtakeRotate.setPosition(outtakeClawFoward);
//
//        // open outtake claw
//        outtakeGripper.setPosition(outtakeOpen);
//
//        // straighten outtake claw twist
//        outtakeTwist.setPosition(outtakeTwistStraight1);
//
//        pause(100);
//
//        // move outtake arm back
//        rightOuttakeFlip.setPosition(0);
//        leftOuttakeFlip.setPosition(0);
//
//        pause(300);
//
//        // outtake claw back
//        outtakeRotate.setPosition(outtakeClawBack);

        //pause(300);

        //wait(100);

        // wait for human player
        pause(750);

        // closes outtake claw
        outtakeGripper.setPosition(outtakeClose);
        //wait(100);

        pause(100);

        // sends outtake rotate claw all the way back
        outtakeRotate.setPosition(outtakeClawBack);
    }

    /** This is the main loop of the OpMode, it will run repeatedly after clicking "Play". **/
    @Override
    public void loop() {

        // These loop the movements of the robot
        follower.update();
        try {
            autonomousPathUpdate();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        // Feedback to Driver Hub
        telemetry.addData("path state", pathState);
        telemetry.addData("x", follower.getPose().getX());
        telemetry.addData("y", follower.getPose().getY());
        telemetry.addData("heading", follower.getPose().getHeading());

        telemetry.addData("LOFS", leftOuttakeFlip.getPosition());
        telemetry.addData("ROFS", rightOuttakeFlip.getPosition());

        telemetry.addData("ORS", outtakeRotate.getPosition());
        telemetry.addData("OTS", outtakeTwist.getPosition());

        telemetry.update();
    }

    /** This method is called once at the init of the OpMode. **/
    @Override
    public void init() {
        pathTimer = new Timer();
        opmodeTimer = new Timer();
        opmodeTimer.resetTimer();

        Constants.setConstants(FConstants.class, LConstants.class);
        follower = new Follower(hardwareMap);
        follower.setStartingPose(startPose);

        // config parts
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

        // close outtake claw onto specimen
        outtakeGripper.setPosition(outtakeClose);

        // reverse half of the needed servos
        leftOuttakeFlip.setDirection(Servo.Direction.REVERSE);
        leftIntakePusher.setDirection(Servo.Direction.REVERSE);

        buildPaths();
    }

    /** This method is called once at the start of the OpMode.
     * It runs all the setup actions, including building paths and starting the path system **/
    @Override
    public void start() {
        opmodeTimer.resetTimer();
        setPathState(0);
    }
    public void pause(double time) {
        ElapsedTime myTimer = new ElapsedTime();
        myTimer.reset();
        while (myTimer.milliseconds() < time) {
            telemetry.addData("Elapsed Time", myTimer.milliseconds());
            telemetry.update();
           //follower.holdPoint(follower.getPose());
        }
    }
}