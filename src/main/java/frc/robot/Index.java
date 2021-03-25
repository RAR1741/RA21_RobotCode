package frc.robot;

import com.revrobotics.CANSparkMax;
import edu.wpi.first.wpilibj.DigitalInput;

public class Index {
    public CANSparkMax belt;
    public DigitalInput finalIndex;

    public Index(CANSparkMax  b, DigitalInput f) {
        belt = b;

        finalIndex = f;
    }

    public void setIndexSpeed(double speed) {
        belt.set(speed);
    }
}
