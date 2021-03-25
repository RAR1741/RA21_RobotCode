// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import com.ctre.phoenix.motorcontrol.can.TalonFX;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import com.kauailabs.navx.frc.AHRS;

import edu.wpi.first.wpilibj.GenericHID.Hand;
import edu.wpi.first.wpilibj.SPI;
import edu.wpi.first.wpilibj.TimedRobot;
import frc.robot.logging.LoggableController;
import frc.robot.logging.LoggableTimer;
import frc.robot.logging.Logger;

/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the TimedRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the build.gradle file in the
 * project.
 */
public class Robot extends TimedRobot {

  AHRS gyro;
  SwerveDrive swerve;
  SwerveModule FR;
  SwerveModule FL;
  SwerveModule BR;
  SwerveModule BL;
  LoggableController driver;
  LoggableController operator;
  Logger logger;
  LoggableTimer timer;


  /**
   * This function is run when the robot is first started up and should be used
   * for any initialization code.
   */
  @Override
  public void robotInit() {

    // Drive Train:
    // Back left drive - 3
    // Back left angle - 6
    // ---------------------
    // Front right drive - 5
    // Front right angle - 4
    // ---------------------
    // Front left drive - 7
    // Front left angle - 8
    // ---------------------
    // Back right drive - 9
    // Back right angle - 10

    gyro = new AHRS(SPI.Port.kMXP);
    gyro.enableLogging(false);

    BL = new SwerveModule(new TalonFX(3), new WPI_TalonSRX(6));
    FR = new SwerveModule(new TalonFX(5), new WPI_TalonSRX(4));
    FL = new SwerveModule(new TalonFX(7), new WPI_TalonSRX(8));
    BR = new SwerveModule(new TalonFX(9), new WPI_TalonSRX(10));
    swerve = new SwerveDrive(FR, FL, BR, BL);

    driver = new LoggableController("Driver", 0);
    operator = new LoggableController("Operator", 1);

    logger = new Logger();
    timer = new LoggableTimer();

    logger.addLoggable(timer);
    logger.addLoggable(driver);
    logger.addLoggable(operator);
  }

  @Override
  public void robotPeriodic() {
    logger.log();
  }

  @Override
  public void autonomousInit() {
    logger.open();
    logger.setup();

    timer.reset();
    timer.start();
  }

  @Override
  public void autonomousPeriodic() {
    logger.writeLine();
  }

  @Override
  public void teleopInit() {
    logger.open();
    logger.setup();

    timer.reset();
    timer.start();
  }

  @Override
  public void teleopPeriodic() {
    swerve.driverSwerve(
      driver.getX(Hand.kLeft),
      -driver.getY(Hand.kLeft),
      driver.getX(Hand.kRight),
      gyro.getAngle(),
      true
    );

    logger.writeLine();
  }

  @Override
  public void disabledInit() {
    logger.close();

    timer.stop();
  }

  @Override
  public void disabledPeriodic() {
  }

  @Override
  public void testInit() {
  }

  @Override
  public void testPeriodic() {
  }
}
