package frc.robot.logging;

public interface Loggable
{
	public abstract void setupLogging(Logger logger);
	public abstract void log(Logger logger);
}
