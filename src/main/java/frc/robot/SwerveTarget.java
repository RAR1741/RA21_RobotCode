package frc.robot;

public class SwerveTarget {
    private double target;
    private double motorScale;

    public SwerveTarget(double target, double motorScale) {
        this.target = target;
        this.motorScale = motorScale;
    }

    public double getTarget() {
        return this.target;
    }

    public double getMotorScale() {
        return this.motorScale;
    }
}
