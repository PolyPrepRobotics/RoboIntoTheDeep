package org.firstinspires.ftc.teamcode;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

public class Dakori_Parts {
    private LinearOpMode opType;
    public DcMotor FLW;
    public DcMotor BLW;
    public DcMotor FRW;
    public DcMotor BRW;
    public Servo intakeRotate;

    public Dakori_Parts(LinearOpMode opMode){
        this.opType = opMode;
        HardwareMap hardwareMap = opMode.hardwareMap;

        FLW = hardwareMap.get(DcMotor.class,"FLW");
        BLW = hardwareMap.get(DcMotor.class,"BLW");
        FRW = hardwareMap.get(DcMotor.class,"FRW");
        BRW = hardwareMap.get(DcMotor.class,"BRW");

        intakeRotate = hardwareMap.get(Servo.class,"IRS");


        FRW.setDirection(DcMotorSimple.Direction.REVERSE);
        BRW.setDirection(DcMotorSimple.Direction.REVERSE);
    }
}