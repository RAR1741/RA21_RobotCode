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
    private final static double TICKS_PER_ROTATION = 1; //TODO: Get values
    private final static double TICKS_PER_INCH = 1;
    private JsonElement auto;
    private List<AutoInstruction> instructions;
    private int step;
    private Timer timer;
    private double start;
    private double navxStart;
    private AHRS gyro;
    private SwerveDrive swerve;

    private FileReader fr;
    private JsonReader jr;
    private JsonParser jp;


    public static class AutoInstruction
    {
        public String type;
        public Unit unit;
        public double amount;
        public List<Double> args;

        
    
        public enum Unit { Seconds, Milliseconds, EncoderTicks, Rotations, Inches, Feet, Degrees, Invalid }

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
            fr = new FileReader(file);
            jr = new JsonReader(fr);
            auto = JsonParser.parseReader(jr);
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
                    AutoInstruction.Unit unit = unitString != null ? parseUnit(unitString) : null;

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

    public static AutoInstruction.Unit parseUnit(String in)
    {
        return AutoInstruction.Unit.valueOf(in);
    }

    @Override
    public void run(){
    }

    private boolean driveTime(double x, double y, double z, double t, boolean fieldOrient)
    {
        if(timer.get() < t)
        {
            swerve.swerve(x, y, z, getAngle() + navxStart, fieldOrient);
        }
        else
        {
            return true;
        }
        return false;
    }

    private boolean driveDistance(double x, double y, double z, double d, boolean fieldOrient)
    {
        if(Math.abs(swerve.getEncoder()-start) < d)
        {
            swerve.swerve(x, y, z, getAngle() - navxStart, fieldOrient);
        }
        else
        {
            return true;
        }
        return false;
    }

    private boolean rotateDegrees(double speed, double deg, boolean fieldOrient)
    {
        if(Math.abs(getAngle()-navxStart-deg) < 0.5) { return true; }
        swerve.swerve(0, 0, speed, getAngle(), fieldOrient);
        return false;
    }

    public void wait(AutoInstruction ai)
    {
        if(timer.get() >= ai.args.get(0))
        {
            reset();
        }
    }

    public void turnDegrees(AutoInstruction ai, boolean fieldOrient)
    {
        if(rotateDegrees(ai.args.get(0), ai.amount, fieldOrient))
        {
            swerve.swerve(0, 0, 0, getAngle()-navxStart, false);
            reset();
        }
    }

    public void drive(AutoInstruction ai, boolean fieldOrient)
    {
        AutoInstruction.Unit u = ai.unit;
        if(u.equals(AutoInstruction.Unit.Seconds) || u.equals(AutoInstruction.Unit.Milliseconds))
        {
            if(driveTime(ai.args.get(0), ai.args.get(1), ai.args.get(2), (u.equals(AutoInstruction.Unit.Seconds) ? ai.amount : ai.amount/1000.0), fieldOrient))
            {
                reset();
            }
        }
        else if(u.equals(AutoInstruction.Unit.EncoderTicks) || u.equals(AutoInstruction.Unit.Rotations))
        {
            if(driveDistance(ai.args.get(0), ai.args.get(1), ai.args.get(2), (u.equals(AutoInstruction.Unit.EncoderTicks) ? ai.amount : ai.amount*TICKS_PER_ROTATION), fieldOrient))
            {
                reset();
            }
        }
        else if(u.equals(AutoInstruction.Unit.Feet) || u.equals(AutoInstruction.Unit.Inches))
        {	
            if(driveDistance(ai.args.get(0),ai.args.get(1), ai.args.get(2), (u.equals(AutoInstruction.Unit.Inches) ? ai.amount*TICKS_PER_INCH : (ai.amount*TICKS_PER_INCH))*12.0, fieldOrient))
            {
                reset();
            }
        }
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
        start = swerve.getEncoder();
        navxStart = getAngle();
    }

}
