package frc.robot;

import com.revrobotics.CANSparkMax;
import edu.wpi.first.wpilibj.DigitalInput;

public class Index {
  public CANSparkMax belt;
  public DigitalInput firstIndex;
  public DigitalInput middleIndex;
  public DigitalInput finalIndex;

  private double indexSpeed = 0.1;
  private double shootSpeed = 0.5;
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
    System.out.println(String.format("%s \t| %s \t| %s", getFirstIndex(), getMiddleIndex(), getFinalIndex()));

    // Don't load if there a ball trigging the final sensor
    // if (getFinalIndex() && state != State.Shooting) {
    //   state = State.Idle;
    // }

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

    // switch (state) {
    //   case Idle:
    //     setIndexSpeed(0);
    //     break;

    //   case Loading:
    //     if (!getFinalIndex()) {
    //       setIndexSpeed(indexSpeed);
    //     } else {
    //       setIndexSpeed(0);

    //       numBalls++;
    //       state = State.Loaded;
    //     }
    //     break;

    //   case Loaded:
    //     break;

    //   case Shooting:
    //     setIndexSpeed(shootSpeed);
    //     break;

    //   case ManualControl:
    //     break;

    //   default:
    //     System.out.println("We shouldn't be here...");
    //     break;
    // }
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
