package frc.robot;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.TalonFX;
import com.revrobotics.CANSparkMax;
import com.revrobotics.ControlType;
import com.revrobotics.CANSparkMax.IdleMode;

public class Shooter {
    TalonFX shooter;
    CANSparkMax feeder;
    CANSparkMax angle;

    Shooter(TalonFX shooter, CANSparkMax feeder, CANSparkMax angle){
        this.shooter = shooter;
        this.feeder = feeder;
        this.angle = angle;

        angle.getPIDController().setP(0.1);
        angle.getPIDController().setI(0.0);
        angle.getPIDController().setD(0.0);
        angle.getPIDController().setFeedbackDevice(angle.getEncoder());
        angle.setIdleMode(IdleMode.kBrake);
    }

    public void setPower(double power){
        shooter.set(ControlMode.PercentOutput, power*0.5);
    }

    public void setFeed(boolean feed){
        feeder.set(feed ? 0.15 : 0);
    }

    public void setAnglePower(double power){
        angle.set(power*0.05);
    }

    public void setAngle(double goal){
        angle.getPIDController().setReference(goal, ControlType.kPosition);
    }
}
