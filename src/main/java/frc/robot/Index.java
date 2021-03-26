package frc.robot;

import com.revrobotics.CANSparkMax;
import edu.wpi.first.wpilibj.DigitalInput;

public class Index {
  public CANSparkMax belt;
  public DigitalInput finalIndex;

  public int numBalls;

  public enum State {
    Idle, Loading, Loaded, Shooting, ManualControl
  }

  private State state;

  public Index(CANSparkMax b, DigitalInput f) {
    belt = b;
    finalIndex = f;

    reset();
  }

  public void overrideState(State s) {
    state = s;
  }

  public void update() {
    switch (state) {
      case Idle:
        break;
      case Loading:
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

  public void setIndexSpeed(double speed) {
    belt.set(speed);
  }

  public void indexUntilLoaded() {
    setIndexSpeed(getFinalIndex() ? 0.1 : 0);
  }

  public boolean getFinalIndex() {
    return finalIndex.get();
  }

  private void reset() {
    numBalls = 0;
    state = State.Idle;
  }
}
