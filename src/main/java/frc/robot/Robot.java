// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import edu.wpi.first.wpilibj.TimedRobot;
import frc.robot.logging.LoggableController;
import frc.robot.logging.LoggableTimer;
import frc.robot.logging.Logger;

/**
 * The VM is configured to automatically run this class, and to call the functions corresponding to
 * each mode, as described in the TimedRobot documentation. If you change the name of this class or
 * the package after creating this project, you must also update the build.gradle file in the
 * project.
 */
public class Robot extends TimedRobot {
  Logger logger;
  LoggableTimer timer;
  LoggableController driver;

  /**
   * This function is run when the robot is first started up and should be used for any
   * initialization code.
   */
  @Override
  public void robotInit() {
    logger = new Logger();
    timer = new LoggableTimer();
    driver = new LoggableController("Driver", 0);

    logger.addLoggable(timer);
    logger.addLoggable(driver);
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
    logger.writeLine();
  }

  @Override
  public void disabledInit() {
    logger.close();

    timer.stop();
  }

  @Override
  public void disabledPeriodic() {}

  @Override
  public void testInit() {}

  @Override
  public void testPeriodic() {}
}
