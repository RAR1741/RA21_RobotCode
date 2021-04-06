// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import com.ctre.phoenix.motorcontrol.can.TalonFX;
import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;

import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.GenericHID.Hand;

/**
 * The VM is configured to automatically run this class, and to call the functions corresponding to
 * each mode, as described in the TimedRobot documentation. If you change the name of this class or
 * the package after creating this project, you must also update the build.gradle file in the
 * project.
 */
public class Robot extends TimedRobot {
  /**
   * This function is run when the robot is first started up and should be used for any
   * initialization code.
   */

  Shooter shooter;
  XboxController driver = null;
  XboxController operator = null;
  boolean shooterRunning;

  @Override
  public void robotInit() {

    shooter = new Shooter(new TalonFX(0), new CANSparkMax(11, MotorType.kBrushless), new CANSparkMax(13, MotorType.kBrushless));

    driver = new XboxController(0);
    operator = new XboxController(1);
    shooterRunning = false;
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
    if(driver.getBButtonPressed()) //For some reason the controller thinks this is A
      shooterRunning = !shooterRunning;
    double shootPower = 0.50;
    shooter.setPower(driver.getYButton() ? shootPower: (shooterRunning ? shootPower : 0));
    // shooter.setPower(shooterRunning ? shootPower : 0);
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
