package frc.robot;

import com.revrobotics.CANSparkMax;

public class Collector {
    
    CANSparkMax collector;

    Collector(CANSparkMax collector){
        this.collector = collector;
    }

    public void setPower(double power){
        collector.set(power);
    }
}
