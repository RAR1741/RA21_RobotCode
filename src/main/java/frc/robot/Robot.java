// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import com.ctre.phoenix.motorcontrol.can.*;
import com.revrobotics.CANSparkMax;
import com.revrobotics.EncoderType;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;

import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.SPI;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.GenericHID.Hand;
import frc.robot.SwerveDrive;
import frc.robot.SwerveModule;

import com.kauailabs.navx.frc.AHRS;

/**
 * The VM is configured to automatically run this class, and to call the functions corresponding to
 * each mode, as described in the TimedRobot documentation. If you change the name of this class or
 * the package after creating this project, you must also update the build.gradle file in the
 * project.
 */
public class Robot extends TimedRobot {

  AHRS gyro;
  SwerveDrive swerve;
  SwerveModule FR;
  SwerveModule FL;
  SwerveModule BR;
  SwerveModule BL;
  XboxController driver = null;
  XboxController operator = null;
  /**
   * This function is run when the robot is first started up and should be used for any
   * initialization code.
   */
  @Override
  public void robotInit() {
    /**
     * Drive Train:
     * Front right drive - 3
     * Front right angle - 4
     * Front left drive - 7
     * Front left angle - 8
     * Back right drive - 9
     * Back right angle - 10
     * Back left drive - 5
     * Back left angle - 6
     */

    gyro = new AHRS(SPI.Port.kMXP);
    gyro.enableLogging(false);

    FR = new SwerveModule(new TalonFX(3), new WPI_TalonSRX(4));
    FL = new SwerveModule(new TalonFX(7), new WPI_TalonSRX(8));
    BR = new SwerveModule(new TalonFX(9), new WPI_TalonSRX(10));
    BL = new SwerveModule(new TalonFX(5), new WPI_TalonSRX(6));
    swerve = new SwerveDrive(FR, FL, BR, BL);
    FR.initMagEncoder(0); //TODO: Config center position
    FL.initMagEncoder(0);
    BR.initMagEncoder(0);
    BL.initMagEncoder(0);

    driver = new XboxController(0);
    operator = new XboxController(1);
  }

  @Override
  public void robotPeriodic() {}

  @Override
  public void autonomousInit() {}

  @Override
  public void autonomousPeriodic() {}

  @Override
  public void teleopInit() {}

  @Override
  public void teleopPeriodic() {
    // if (driver.getAButtonPressed()){FR.setAngle(90);}
    // if (driver.getAButtonPressed()){FR.setAngle((FR.getAngleEncoder() / FR.EncPerDeg) + 90);}
		// System.out.println("FR Current Angle: " + String.valueOf(FR.getAngleEncoder() / FR.EncPerDeg));
    // System.out.println(FR.angle.getEncoder().getCountsPerRevolution());
    // System.out.println(FR.angle.getEncoder(EncoderType.kHallSensor, 1).getPosition());
    // if (FR.angle.getOutputCurrent()>1)
    //   System.out.println(FR.angle.getOutputCurrent());

    swerve.driverSwerve(driver.getX(Hand.kLeft), -driver.getY(Hand.kLeft), driver.getX(Hand.kRight), gyro.getAngle(), false);
    // swerve.manualControl(0, driver.getX(Hand.kRight));
    // BR.setDrive(driver.getY(Hand.kLeft));
    // BL.setDrive(driver.getY(Hand.kLeft));

  }

  @Override
  public void disabledInit() {}

  @Override
  public void disabledPeriodic() {}

  @Override
  public void testInit() {}

  @Override
  public void testPeriodic() {}
}
