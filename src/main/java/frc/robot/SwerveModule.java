package frc.robot;

import com.revrobotics.CANSparkMax;
import com.revrobotics.ControlType;
import com.revrobotics.CANSparkMax.IdleMode;

import edu.wpi.first.wpilibj.controller.PIDController;

import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.TalonFXControlMode;
import com.ctre.phoenix.motorcontrol.can.TalonFX;

public class SwerveModule {

    private static final double EncPerDeg = 0 / 360.0f; //TODO: Determine encoder counts for complete rotation
	private TalonFX drive;
    private CANSparkMax angle;
    private PIDController PIDc;

    public SwerveModule(TalonFX d, CANSparkMax a){
        drive = d;
        angle = a;

        drive.setNeutralMode(NeutralMode.Brake);

        angle.getPIDController().setP(2);
        angle.getPIDController().setI(0.0);
        angle.getPIDController().setD(0.0);
        angle.getPIDController().setFeedbackDevice(angle.getEncoder());
		angle.getPIDController().setOutputRange(-1, 1);
        angle.setIdleMode(IdleMode.kBrake);
    }

    public void setAngleDrive(double speed, double angle)
	{
		if(Math.abs(getAngleEncoder()/EncPerDeg - angle) > 90)
		{
			angle = (angle + 180)%360;
			speed = -speed;
		}
	}
	
	public double getTurnSpeed()
	{
		return angle.getEncoder().getVelocity();
	}

	// public void setAngleSpeed(double speed)
	// {
	// 	angle.set(speed);
	// }

	public void setDrive(double speed)
	{
		drive.set(TalonFXControlMode.PercentOutput, speed);
	}

	public void setDriveSpeed(double speed)
	{
		drive.set(TalonFXControlMode.Velocity, speed * 2048.0 / 600.0);
	}
	
	public double getAngleEncoder()
	{
		return angle.getEncoder().getPosition();
	}

	public void setAngle(double goal)
	{
		angle.getPIDController().setReference(goal*EncPerDeg, ControlType.kPosition);//setSetpoint(goal*((SteerEncMax-SteerEncMin)/360.0f));
    }

	public double getEncPerDeg()
	{
		return EncPerDeg;
	}

	//TODO: Calibrate Angle

    public double getDriveEnc()
	{
		return drive.getSelectedSensorPosition();
	}

	public void setBrake() 
	{
        drive.setNeutralMode(NeutralMode.Brake);
    }
	
	public void setCoast()
	{
        drive.setNeutralMode(NeutralMode.Coast);	
    }
	
}
