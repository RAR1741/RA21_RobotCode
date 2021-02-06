package frc.robot;

import com.revrobotics.CANSparkMax;
import com.revrobotics.ControlType;
import com.ctre.phoenix.motorcontrol.TalonFXControlMode;
import com.ctre.phoenix.motorcontrol.can.TalonFX;

public class SwerveModule {

    private double SteerSpeed,SteerTolerance,SteerEncMax,SteerEncMin;
    private double SteerOffset;
    private double MaxRPM;

    private TalonFX drive;
    private CANSparkMax angle;
    private PIDController PIDc;
    private FakePIDSource encFake;

    public SwerveModule(TalonFX d, CANSparkMax a){
        drive = d;
        angle = a;

        drive.getPIDController().setP(1);
        drive.getPIDController().setI(0.0);
        drive.getPIDController().setD(0.0);
        drive.getPIDController().setFeedbackDevice(drive.getSelectedSensorPosition());
        drive.setNeutralMode(NeutralMode.Brake);

        angle.getPIDController().setP(2);
        angle.getPIDController().setI(0.0);
        angle.getPIDController().setD(0.0);
        angle.getPIDController().setFeedbackDevice(angle.getEncoder());
        angle.setNeutralMode(NeutralMode.Coast);

        encFake = new FakePIDSource(SteerOffset,SteerEncMin,SteerEncMax);
	
        PIDc = new PIDController(SteerP,SteerI,SteerD,encFake,angle);
        PIDc.disable();
        PIDc.setContinuous(true);
        PIDc.setInputRange(SteerEncMin,SteerEncMax);
        PIDc.setOutputRange(-SteerSpeed,SteerSpeed);
        PIDc.setPercentTolerance(SteerTolerance);
        PIDc.setSetpoint(2.4);
        PIDc.enable();
    }

    public void setAngleDrive(double speed, double angle)
	{
		if(Math.abs(encFake.pidGet()/(SteerEncMax/360.0f) - angle) > 90)
		{
			angle = (angle + 180)%360;
			speed = -speed;
		}
		
		setDrive(speed);
		setAngle(angle);
	}
	
	public double getTurnSpeed()
	{
		return encFake.getSpeed();
	}
	
	public void setDrive(double speed)
	{
		drive.set(TalonFXControlMode.PercentOutput, speed);
	}
	
	public void setDriveSpeed(double speed)
	{
		drive.set(TalonFXControlMode.Velocity, speed * 2048.0 / 600.0);
	}
	
	public double pidGet()
	{
		return encFake.pidGet();
	}
	
	public void setEncMax(double max)
	{
		encFake.setMinMax(0, max);
		PIDc.setInputRange(0, max);
	}
	
	public double getEncMax()
	{
		return SteerEncMax;
	}
	
	public void PIDSet()
	{
		encFake.pidSet(encoder.pidGet());
	}
	
	public void setAngle(double angle)
	{
		PIDc.setSetpoint(angle*((SteerEncMax-SteerEncMin)/360.0f));
    }

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
