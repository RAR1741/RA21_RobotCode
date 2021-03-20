package frc.robot;

import com.revrobotics.AlternateEncoderType;
import com.revrobotics.CANEncoder;
import com.revrobotics.CANSparkMax;
import com.revrobotics.ControlType;
import com.revrobotics.CANSparkMax.IdleMode;

import edu.wpi.first.wpilibj.*;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.TalonFXControlMode;
import com.ctre.phoenix.motorcontrol.can.TalonFX;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.ctre.phoenix.motorcontrol.can.TalonSRXPIDSetConfiguration;

public class SwerveModule {

    // public static final double EncPerDeg = 1.0f * (12.0f / 1.0f) * (64.0f / 12.0f) / 360.0f;
	public static final double EncPerDeg = 4096.0f / 360.0f;
	private TalonFX drive;
    // public CANSparkMax angle;
	public TalonSRX angle;
	public TalonSRXPIDSetConfiguration pid;

    public SwerveModule(TalonFX d, TalonSRX a){
        drive = d;
        angle = a;

        drive.setNeutralMode(NeutralMode.Brake);

		// angle.conficFa
		angle.selectProfileSlot(0, 0);
		angle.config_kP(0, 0.5);
		angle.config_kI(0, 0);
		angle.config_kD(0, 0);
		angle.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Absolute, 0, 50);
		//TODO: Reverse the direction the feedback sensor counts
		// angle.setInverted(true); //Maybe this will work
		// angle.configSelectedFeedbackCoefficient(-1);
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
		return angle.getSelectedSensorVelocity();
	}

	public void setAngleSpeed(double speed)
	{
		angle.set(ControlMode.PercentOutput, speed);
	}

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
		return angle.getSensorCollection().getPulseWidthPosition();
		// return angle.getSelectedSensorPosition();
	}

	public void setAngle(double goal)
	{
		angle.set(ControlMode.Position, goal*EncPerDeg);
		// angle.set(ControlMode.Position, 2048);
    }

    public double getDriveEnc()
	{
		return drive.getSelectedSensorPosition();
	}

	public double getAngle(){
		return getAngleEncoder()/EncPerDeg;
	}

	public void setBrake() 
	{
        drive.setNeutralMode(NeutralMode.Brake);
    }
	
	public void setCoast()
	{
        drive.setNeutralMode(NeutralMode.Coast);	
    }

	public void initMagEncoder(int encoderCenter){
		int pulseWidth = angle.getSensorCollection().getPulseWidthPosition();
		pulseWidth -= encoderCenter;
		pulseWidth %= 360;
		if(pulseWidth < 0) pulseWidth += 360;
		angle.getSensorCollection().setQuadraturePosition(pulseWidth, 50);
	}
	
}
