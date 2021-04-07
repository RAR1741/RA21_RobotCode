package frc.robot;

import com.revrobotics.CANSparkMax;
import edu.wpi.first.wpilibj.DigitalInput;

public class Index {
  public CANSparkMax belt;
  public DigitalInput firstIndex;
  public DigitalInput middleIndex;
  public DigitalInput finalIndex;

  private double indexSpeed = 0.2;
  // private double shootSpeed = 0.5; // Power port challenge
  private double shootSpeed = 0.2; // Accuracy challenge
  private int numBalls;

  public enum State {
    Idle, Loading, Loaded, Shooting, ManualControl
  }

  private State state;

  public Index(CANSparkMax b, DigitalInput i0, DigitalInput i1, DigitalInput i2) {
    belt = b;

    finalIndex = i0;
    middleIndex = i1;
    firstIndex = i2;

    reset();
  }

  public void update(boolean firing, boolean ejecting) {
    // System.out.println(String.format("%s \t| %s \t| %s", getFirstIndex(), getMiddleIndex(), getFinalIndex()));

    if (firing) {
      // Fire
      setIndexSpeed(shootSpeed);
    } else if (getFinalIndex()) {
      // Do nothing
      setIndexSpeed(0);
    } else if (getMiddleIndex()) {
      // Run until the first sensor is cleared
      if (getFirstIndex()) {
        setIndexSpeed(indexSpeed);
      } else {
        setIndexSpeed(0);
      }
    } else if (getFirstIndex()) {
      setIndexSpeed(indexSpeed);
    } else {
      setIndexSpeed(0);
    }

    if (ejecting) {
      setIndexSpeed(-indexSpeed * 1.5);
    }
  }

  public void startLoading() {
    state = State.Loading;
  }

  public void indexUntilLoaded() {
    setIndexSpeed(getFinalIndex() ? 0 : indexSpeed);
  }

  public boolean getFirstIndex() {
    return !firstIndex.get();
  }

  public boolean getMiddleIndex() {
    return !middleIndex.get();
  }

  public boolean getFinalIndex() {
    return !finalIndex.get();
  }

  public State getState() {
    return state;
  }

  public void setState(State s) {
    state = s;
  }

  public void setIndexSpeed(double speed) {
    belt.set(speed);
  }

  private void reset() {
    numBalls = 0;
    state = State.Idle;
  }
}
