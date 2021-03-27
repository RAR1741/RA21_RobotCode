package frc.robot;

import com.revrobotics.CANSparkMax;
import edu.wpi.first.wpilibj.DigitalInput;

public class Index {
  public CANSparkMax belt;
  public DigitalInput finalIndex;

  private double indexSpeed = 0.1;
  private int numBalls;

  public enum State {
    Idle, Loading, Loaded, Shooting, ManualControl
  }

  private State state;

  public Index(CANSparkMax b, DigitalInput f) {
    belt = b;
    finalIndex = f;

    reset();
  }

  public void update() {
    System.out.println(state);

    switch (state) {
      case Idle:
        break;
      case Loading:
        if (!getFinalIndex()) {
          setIndexSpeed(indexSpeed);
        } else {
          setIndexSpeed(0);

          numBalls++;
          state = State.Loaded;
        }
        break;
      case Loaded:
        break;
      case Shooting:
        break;
      case ManualControl:
        break;
      default:
        System.out.println("We shouldn't be here...");
        break;
    }
  }

  public void startLoading() {
    state = State.Loading;
  }

  public void indexUntilLoaded() {
    setIndexSpeed(getFinalIndex() ? 0 : indexSpeed);
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
