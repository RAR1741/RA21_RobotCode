package frc.robot;

import com.ctre.phoenix.motorcontrol.can.TalonFX;
import com.revrobotics.CANSparkMax;

public class Shooter {
    TalonFX shooter;
    CANSparkMax feeder;
    CANSparkMax angle;

    Shooter(TalonFX shooter, CANSparkMax feeder, CANSparkMax angle){
        this.shooter = shooter;
        this.feeder = feeder;
        this.angle = angle;
    }
}
