package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

@Autonomous(name = "LEFT - Auto Park (MIDDLE)", group = "Concept")

// will score the preset block and park in observation zone
public class AutoParkLEFT extends LinearOpMode {

    @Override
    public void runOpMode() {
        RoboController roboController = new RoboController(this);

        /** Wait for the game to begin */
        telemetry.addData(">", "Press Play to start op mode");
        telemetry.update();
        waitForStart();

        if (opModeIsActive()) {
            // 18 inches = about 90 degree turn
            // Diagonal distance = about 33.94113

            // autonomous park

            // preset drop off position
            roboController.shoulder.setPosition(0.32);

            // move left
            roboController.moveOnXAxis(-24, 0.8);
        }
    }
}