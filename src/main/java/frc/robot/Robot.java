// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import com.ctre.phoenix.motorcontrol.can.TalonFX;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import com.kauailabs.navx.frc.AHRS;
import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;
import edu.wpi.first.wpilibj.DigitalInput;
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

  Boolean enableDrivetrain = true;
  Boolean enableShooter = true;
  Boolean enableIndex = true;

  AHRS gyro;

  Shooter shooter;

  Index index;

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

    if (enableShooter) {
      shooter = new Shooter(new TalonFX(14), new CANSparkMax(13, MotorType.kBrushless));
    }

    if (enableDrivetrain) {
      BL = new SwerveModule(new TalonFX(3), new WPI_TalonSRX(6));
      FR = new SwerveModule(new TalonFX(5), new WPI_TalonSRX(4));
      FL = new SwerveModule(new TalonFX(7), new WPI_TalonSRX(8));
      BR = new SwerveModule(new TalonFX(9), new WPI_TalonSRX(10));
      swerve = new SwerveDrive(FR, FL, BR, BL);
    }

    if (enableIndex) {
      index = new Index(new CANSparkMax(11, MotorType.kBrushless), new DigitalInput(0),
          new DigitalInput(1), new DigitalInput(2));
    }

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

    if (enableShooter) {
      // System.out.println("---------------");
      // System.out.println(shooter.getShooterSpeed());

      double shooterTargetSpeed = 0;
      double shooterTargetAngle = 0;

      if (driver.getAButton()) {
        shooterTargetSpeed = 10200.0;
        // shooterTargetAngle = 10;
      }
      // else if (driver.getBButton()) {
      //   shooterTargetSpeed = 15000.0;
      //   shooterTargetAngle = 15;
      // } else if (driver.getYButton()) {
      //   shooterTargetSpeed = 14800.0;
      //   shooterTargetAngle = 20;
      // } else if (driver.getXButton()) {
      //   shooterTargetSpeed = 16000.0;
      //   shooterTargetAngle = 30;
      // }
      // shooter.setShooterSpeed(shooterTargetSpeed);
      // shooter.setAngle(shooterTargetAngle);
      System.out.println(shooter.getAngle());

      shooter.setAnglePower(operator.getY(Hand.kLeft) * 0.1);

      if (operator.getXButtonPressed()) {
        shooter.reHome();
      }

      shooter.update();
      // System.out.println(shooter.getForwardLimit());
    }

    if (enableDrivetrain) {
      swerve.driverSwerve(driver.getX(Hand.kLeft), -driver.getY(Hand.kLeft),
          driver.getX(Hand.kRight), gyro.getAngle(), true);
    }

    if (enableIndex) {
      // Include if the shooter is up to speed in this calculation as well
      boolean firing = driver.getAButton() && shooter.isAtTargetSpeed();

      boolean ejecting = operator.getBButton();

      index.update(firing, ejecting);
    }

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
