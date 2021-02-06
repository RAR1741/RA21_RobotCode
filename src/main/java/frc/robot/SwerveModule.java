package frc.robot;

import com.revrobotics.CANSparkMax;
import com.revrobotics.ControlType;
import com.ctre.phoenix.motorcontrol.TalonFXControlMode;
import com.ctre.phoenix.motorcontrol.can.TalonFX;

public class SwerveModule {
    
    private TalonFX drive;
    private CANSparkMax angle;

    public SwerveModule(TalonFX d, CANSparkMax a){
        drive = d;
        angle = a;

        drive.getPIDController().setP(1);
        drive.getPIDController().setI(0.0);
        drive.getPIDController().setD(0.0);
        drive.getPIDController().setFeedbackDevice(drive.getSelectedSensorPosition());

        angle.getPIDController().setP(2);
        angle.getPIDController().setI(0.0);
        angle.getPIDController().setD(0.0);
        angleMotor.getPIDController().setFeedbackDevice(angleMotor.getEncoder());
    }
}
