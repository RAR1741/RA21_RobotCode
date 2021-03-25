package frc.robot;

import com.revrobotics.CANSparkMax;

public class Index {
    public CANSparkMax belt;
    public CANSparkMax angle;

    public Index(CANSparkMax  b, CANSparkMax a) {
        belt = b;
        angle = a;
    }

    public void setIndexSpeed(double speed) {
        belt.set(speed);
    }

    public void setAngleSpeed(double speed) {
        angle.set(speed);
    }
}
