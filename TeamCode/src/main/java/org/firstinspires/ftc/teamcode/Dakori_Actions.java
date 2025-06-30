package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

@TeleOp
public class Dakori_Actions extends LinearOpMode {
    private Dakori_Parts Dakori_Parts;

    @Override
    public void runOpMode() {
        Dakori_Parts =  new Dakori_Parts(this);

        waitForStart();

        while (opModeIsActive()) {
            wheelMovement();
            armMovement();
        }
    }

    public void wheelMovement(){
        double driveStrength = 0;
        double strafeStrength = 0;
        double turnStrength = 0;

        driveStrength = gamepad1.left_stick_y;
        strafeStrength = -gamepad1.left_stick_x;
        turnStrength = -gamepad1.right_stick_x;

        Dakori_Parts.FLW.setPower(driveStrength + turnStrength + strafeStrength);
        Dakori_Parts.FRW.setPower(driveStrength - turnStrength - strafeStrength);
        Dakori_Parts.BLW.setPower(driveStrength + turnStrength - strafeStrength);
        Dakori_Parts.BRW.setPower(driveStrength - turnStrength + strafeStrength);
    }

    public void armMovement(){
        if(gamepad1.dpad_up == true){
            Dakori_Parts.intakeRotate.setPosition(1);
        }
        if(gamepad1.dpad_down == true){
            Dakori_Parts.intakeRotate.setPosition(0.5);
        }
    }
}
