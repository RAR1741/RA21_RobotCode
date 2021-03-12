package frc.robot;

import com.kauailabs.navx.frc.AHRS;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonParser;
import com.google.gson.JsonElement;
import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.google.gson.stream.JsonReader;
import com.kauailabs.navx.frc.AHRS;

import edu.wpi.first.wpilibj.Timer;

public class JsonAutonomous extends Autonomous{
    private JsonElement auto;
	private List<AutoInstruction> instructions;
	private int step;
	private Timer timer;
	private double navxStart;
	private AHRS gyro;
	private SwerveDrive swerve;

    private FileReader fr;
    private JsonReader jr;
    private JsonParser jp;

    private enum Unit { Seconds, Milliseconds, EncoderTicks, Rotations, Inches, Feet, Degrees, Invalid };


    private static class AutoInstruction
	{
		public String type;
		public Unit unit;
		public double amount;
		public List<Double> args;

        
    
        private enum Unit { Seconds, Milliseconds, EncoderTicks, Rotations, Inches, Feet, Degrees, Invalid };

		public AutoInstruction(String type, List<Double> args)
		{
			this.type = type;
			this.args = args;
		}

		public AutoInstruction(String type, Unit unit, double amount, List<Double> args)
		{
			this.type = type;
			this.unit = unit;
			this.amount = amount;
			this.args = args;
		}
    }

    /**
	 * Creates a JsonAutonomous from the specified file
	 * @param file The location of the file to parse
	 */
	public JsonAutonomous(String file, AHRS gyro, SwerveDrive drive) {
		this.swerve = drive;
		this.gyro = gyro;

		parseFile(file);
    }

    public void parseFile(String file)
	{
		step = -1;
		timer = new Timer();
		instructions = new ArrayList<AutoInstruction>();
		try
		{
			fr = new FileReader(new File(file));
			jr = new JsonReader(fr);
			jp = new JsonParser();
			auto = jp.parse(jr);
			JsonElement inner = auto.getAsJsonObject().get("auto");
			if(inner.isJsonArray())
			{
				for(JsonElement e : inner.getAsJsonArray())
				{
					JsonObject o = e.getAsJsonObject();

					List<Double> extraArgs = new ArrayList<Double>();
					for(JsonElement e2 : o.get("args").getAsJsonArray())
					{
						extraArgs.add(e2.getAsDouble());
					}

					String type = o.get("type").getAsString();

					String unitString = o.has("unit") ? o.get("unit").getAsString() : null;
					Unit unit = unitString != null ? parseUnit(unitString) : null;

					double amount = o.has("amount") ? o.get("amount").getAsDouble() : null;

					AutoInstruction ai = unit == null ? new AutoInstruction(type, extraArgs) : new AutoInstruction(type, unit, amount, extraArgs);
					instructions.add(ai);
				}
			}
		}
		catch (JsonIOException | JsonSyntaxException | FileNotFoundException e)
		{
			e.printStackTrace();
		}
	}

    public static Unit parseUnit(String in)
	{
		return Unit.valueOf(in);
	}

    @Override
	public void run(){
    }

    private double getAngle(){
		return gyro.getAngle();
	}

	private void reset()
	{
		step++;
		swerve.swerve(0.0, 0.0, 0.0, gyro.getAngle(), false);
		timer.reset();
		timer.start();
		gyro.reset();
		navxStart = getAngle();
	}

}
