package org.firstinspires.ftc.teamcode;

import androidx.annotation.NonNull;

import com.acmerobotics.dashboard.telemetry.TelemetryPacket;
import com.acmerobotics.roadrunner.Action;
import com.acmerobotics.roadrunner.Pose2d;
import com.acmerobotics.roadrunner.SequentialAction;
import com.acmerobotics.roadrunner.TrajectoryActionBuilder;
import com.acmerobotics.roadrunner.Vector2d;
import com.acmerobotics.roadrunner.ftc.Actions;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.Servo;

@Autonomous(name = "RR Best 3 Specimen (MIDDLE)")
public class RR_BestAuto extends LinearOpMode {
    @Override
    // outdated
    public void runOpMode() throws InterruptedException {
        MecanumDrive drive = new MecanumDrive(hardwareMap, new Pose2d(0, 0, 0));
        Servo specimenServo = hardwareMap.servo.get("specimenArm");


        TrajectoryActionBuilder action2 = drive.actionBuilder(new Pose2d(0,0,0))
                // specimen scoring servo position
                .stopAndAdd(new ServoAction(specimenServo, 0.25))
                .setTangent(0)

                // 1
                // spline to submersible
                .splineToConstantHeading(new Vector2d(50, 35), Math.toRadians(0))

                // put arm down
                .stopAndAdd(new ServoAction(specimenServo, 0.738))

                // move back
                .lineToX(30)

                // move right to zone
                .setTangent(Math.PI / 2)
                .lineToY(-24)

                // wait for human player to adjust
                .waitSeconds(0.5)

                // move back and attach specimen
                .setTangent(0)
                .lineToX(3)

                // 2
                // wait for a bit
                .waitSeconds(0.75)

                // specimen scoring servo position
                .stopAndAdd(new ServoAction(specimenServo, 0.25))

                // spline to submersible
                .splineToConstantHeading(new Vector2d(30, 30), Math.toRadians(0))

                // move a bit forward
                .lineToX(52)

                // put arm down
                .stopAndAdd(new ServoAction(specimenServo, 0.738))

                // move back
                .lineToX(8)

                // move a bit to the right
                .setTangent(Math.PI / 2)
                .lineToY(-19)

                // 2.1
                // diagonal line to samples
                .strafeTo(new Vector2d(85, -52))

                // move back
                .setTangent(0)
                .lineToX(10)

                // 2.2
                // forward
                .setTangent(0)
                .lineToX(75)

                // more right
                .setTangent(Math.PI / 2)
                .lineToY(-64)

                // move back and attach specimen
                .setTangent(0)
                .lineToX(5.5)

                // wait for a bit
                .waitSeconds(0.75)

                // specimen scoring servo position
                .stopAndAdd(new ServoAction(specimenServo, 0.25))

                // spline to submersible
                .splineToConstantHeading(new Vector2d(25, 24), Math.toRadians(0))

                // move a bit forward
                .lineToX(55)

                // put arm down
                .stopAndAdd(new ServoAction(specimenServo, 0.738))

                // move back
                .setTangent(0)
                .lineToX(20)

                // specimen arm forward
                .stopAndAdd(new ServoAction(specimenServo, 0.25))

                // move right
                .setTangent(Math.PI / 2)
                .lineToY(-60);


        waitForStart();

        Actions.runBlocking(
                new SequentialAction(
                        action2.build()
                )
        );
    }

    // class for positioning the servo arm
    public class ServoAction implements Action {
        Servo servo;
        double position;

        public ServoAction(Servo initialServo, double initialPosition) {
            this.servo = initialServo;
            this.position = initialPosition;
        }

        @Override
        public boolean run(@NonNull TelemetryPacket telemetryPacket) {
            servo.setPosition(position);

            return false;
        }
    }
}