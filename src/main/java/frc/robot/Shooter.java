package frc.robot;

import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.TalonFXControlMode;
import com.ctre.phoenix.motorcontrol.can.TalonFX;
import com.ctre.phoenix.motorcontrol.can.TalonFXConfiguration;
import com.revrobotics.CANDigitalInput.LimitSwitchPolarity;
import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMax.IdleMode;
import com.revrobotics.ControlType;

public class Shooter {

  // TODO: Determine values
  static final double EncPerDeg = 1.0f * 360.0f;
  private static final double HOMING_SPEED_DOWN = -0.3; // Speed at which we seek downward during
                                                        // homing
  private static final double HOME_POSITION = 0.0; // Angle at lower limit switch
  private static final double POSITION_TOLERANCE = 0.05; // Limit of being "close enough" on the
                                                         // angle

  TalonFX shooter;
  CANSparkMax angle;
  TalonFXConfiguration shooterConfig;

  private double targetAngle;

  public enum State {
    HomingDown, Idle, MovingToAngle, ManualControl
  }

  private State state;

  Shooter(TalonFX shooter, CANSparkMax angle) {
    this.shooter = shooter;
    this.angle = angle;

    // Configure the shooter motor
    shooterConfig = new TalonFXConfiguration();
    shooterConfig.peakOutputForward = 1.0;
    shooterConfig.peakOutputReverse = 0.0; // Don't let the motor go backwards

    shooterConfig.slot0.kP = 0.27;
    shooterConfig.slot0.kI = 0.00011;
    // shooterConfig.slot0.kD = 0.0;
    shooterConfig.slot0.kD = 15.0;
    shooterConfig.slot0.kF = 0.0;

    shooter.configAllSettings(shooterConfig);
    shooter.setNeutralMode(NeutralMode.Coast);

    // Configure the angle motor
    angle.getPIDController().setP(0.1);
    angle.getPIDController().setI(0.0);
    angle.getPIDController().setD(0.0);
    angle.getPIDController().setFeedbackDevice(angle.getEncoder());
    angle.setIdleMode(IdleMode.kBrake);

    state = State.Idle;
  }

  /**
   * Sets motor power.
   *
   * @param power the power at which the shooter spins.
   */
  public void manualControl(double power, double angleMotorPower) {
    state = State.ManualControl;
    setShooterPower(power * 0.5);
    angle.set(angleMotorPower * 0.05);
  }

  public void setShooterPower(double power) {
    shooter.set(TalonFXControlMode.PercentOutput, power);
  }

  public void setShooterSpeed(double rpm) {
    shooter.set(TalonFXControlMode.Velocity, rpm);
    System.out.println(rpm);
  }

  public double getShooterSpeed() {
    return shooter.getSelectedSensorVelocity();
  }

  public boolean isAtTargetSpeed() {
    return getShooterSpeed() >= shooter.getClosedLoopTarget();
  }

  public double getTargetAngle() {
    return targetAngle;
  }

  public boolean onTarget() {
    double error = angle.getEncoder().getPosition() - targetAngle;
    return Math.abs(error) < POSITION_TOLERANCE;
  }

  /**
   * Run main state machine for semi-autonomous control of the robot.
   */
  public void update() {
    switch (state) {
      case HomingDown:
        angle.set(HOMING_SPEED_DOWN);

        if (angle.getReverseLimitSwitch(LimitSwitchPolarity.kNormallyOpen).get()) {
          // We've reached the lower limit of the screw assembly, we're now at a
          // known position. Set the absolute position to the encoder so we can deal
          // with easier units.
          angle.set(0);
          angle.getEncoder().setPosition(HOME_POSITION);
          state = State.Idle;
        }
        break;
      case Idle:
        // Idle!
        break;
      case MovingToAngle:
        if (onTarget()) {
          state = State.Idle;
        }
        break;
      case ManualControl:
        // Nothing
        break;
    }
  }

  public void reHome() {
    state = State.HomingDown;
  }

  public void setAnglePower(double power) {
    angle.set(power);
  }

  /**
   * Sets angle motor to a specified angle
   *
   * @param degrees degrees to which the angle motor will be turned.
   */
  public void setAngle(double degrees) {
    state = State.MovingToAngle;
    targetAngle = degrees;
    angle.getPIDController().setReference(targetAngle, ControlType.kPosition);
  }

  public double getAngle() {
    return angle.getEncoder().getPosition() / EncPerDeg;
  }

  public boolean getReverseLimit() {
    return angle.getReverseLimitSwitch(LimitSwitchPolarity.kNormallyOpen).get();
  }

  public boolean getForwardLimit() {
    return angle.getForwardLimitSwitch(LimitSwitchPolarity.kNormallyOpen).get();
  }

  public State getState() {
    return state;
  }
}
