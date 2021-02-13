package frc.robot;

import com.kauailabs.navx.frc.AHRS;

public class SwerveDrive {
    
    private double length, width, diameter;

    private SwerveModule FR;
    private SwerveModule FL;
    private SwerveModule BR;
    private SwerveModule BL;

    private AHRS gyro;

    public SwerveDrive(SwerveModule FR, SwerveModule FL, SwerveModule BR, SwerveModule BL, AHRS gyro){
        this.FR = FR;
        this.FL = FL;
        this.BR = BR;
        this.BL = BL;

        length = 1; //tbd
        width = 1; //tbd
        diameter = Math.sqrt(Math.pow(length,2)+Math.pow(width,2));

        this.gyro = gyro;
    }

    /* // Test Code
    public void manualControl(double driveSpeed, double angleSpeed){
        setDriveSpeed(driveSpeed);
        setAngleSpeed(angleSpeed);
    }

    public void setDriveSpeed(double speed){
        FR.setDrive(speed);
        FL.setDrive(speed);
        BR.setDrive(speed);
        BL.setDrive(speed);
    }

    public void setAngleSpeed(double speed){
        FR.setAngleSpeed(speed);
        FL.setAngleSpeed(speed);
        BR.setAngleSpeed(speed);
        BL.setAngleSpeed(speed);
    } */
}
