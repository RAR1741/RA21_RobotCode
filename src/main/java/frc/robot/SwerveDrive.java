package frc.robot;

public class SwerveDrive {
    private final double turningSpeedFactor = -0.3;
    private double length, width, diameter;
    private double a,b,c,d;
    private double wsFL,wsFR,wsBR,wsBL;
    private double waFL,waFR,waBR,waBL;
    private double max;
    private int moveCount;

    private SwerveModule FR;
    private SwerveModule FL;
    private SwerveModule BR;
    private SwerveModule BL;

    public SwerveDrive(SwerveModule FR, SwerveModule FL, SwerveModule BR, SwerveModule BL) {
        this.FR = FR;
        this.FL = FL;
        this.BR = BR;
        this.BL = BL;

        length = 1;
        width = 1;
        diameter = Math.sqrt(Math.pow(length,2)+Math.pow(width,2));
        a = 0.0;b = 0.0;c = 0.0;d = 0.0;
        wsFL = 0.0;wsFR = 0.0;wsBR = 0.0;wsBL = 0.0;
        waFL = 0.0;waFR = 0.0;waBR = 0.0;waBL = 0.0;
        max = 0.0;
        moveCount = 20;

        FR.initMagEncoder(1237.0);
        FL.initMagEncoder(545.0);
        BR.initMagEncoder(3024.0);
        BL.initMagEncoder(1986.0);
    }

    // Test Code
    public void manualControl(double driveSpeed, double angleSpeed) { //TODO: Also never used
        setDriveSpeed(driveSpeed);
        setAngleSpeed(angleSpeed);
    }

    public void setDriveSpeed(double speed){
        FR.setDrive(speed);
        FL.setDrive(speed);
        BR.setDrive(speed);
        BL.setDrive(speed);
    }

    public void setAngleSpeed(double speed){
        FR.setAngleSpeed(speed);
        FL.setAngleSpeed(speed);
        BR.setAngleSpeed(speed);
        BL.setAngleSpeed(speed);
    }

    public void driverSwerve(double x, double y, double z, double gyro, boolean fieldOrient) {
        if((x!=0 || y!=0) || z!=0) {
            swerve(x, y, z, gyro, fieldOrient);
            moveCount = 30;
        } else {
            moveCount--; //TODO: Why do we need moveCount? Just kill the motors? We confused; ask Cory
            if(moveCount < 0) {
                swerve(0, 0, 0, 0, false);
            } else {
                FR.setDrive(0);
                FL.setDrive(0);
                BR.setDrive(0);
                BL.setDrive(0);
            }
        }
    }

    public void swerve(double x, double y, double z, double gyro, boolean fieldOrient) {
        gyro *= Math.PI/180.0f;
        z *= turningSpeedFactor;
        if(fieldOrient) {
            double temp = y * Math.cos(gyro) + x * Math.sin(gyro);
            x = -y * Math.sin(gyro) + x * Math.cos(gyro);
            y = temp;
        }

        a = x + z * (length/diameter); //cos
        b = x - z * (length/diameter); //sec
        c = y - z * (width/diameter); //csc
        d = y + z * (width/diameter); //sin

        wsFL = Math.sqrt(Math.pow(b,2) + Math.pow(c,2));
        wsFR = Math.sqrt(Math.pow(b,2) + Math.pow(d,2));
        wsBR = Math.sqrt(Math.pow(a,2) + Math.pow(d,2));
        wsBL = Math.sqrt(Math.pow(a,2) + Math.pow(c,2)); 

        max = 0; 
        max = Math.max(max, wsFL);
        max = Math.max(max, wsFR);
        max = Math.max(max, wsBR);
        max = Math.max(max, wsBL);
        
        if(max > 1) {
            wsFL /= max; 
            wsFR /= max;
            wsBR /= max;
            wsBL /= max;
        }

        waFL = Math.atan2(b,c) * 180.0f/Math.PI; 
        waFR = Math.atan2(b,d) * 180.0f/Math.PI;
        waBR = Math.atan2(a,d) * 180.0f/Math.PI;
        waBL = Math.atan2(a,c) * 180.0f/Math.PI;

        if(waFL < 0) { 
            waFL += 360;
        }
        if(waFR < 0) {
            waFR += 360;
        }
        if(waBR < 0) {
            waBR += 360;
        }
        if(waBL < 0) {
            waBL += 360;
        }

        waFL = 360 - waFL;
        waFR = 360 - waFR;
        waBR = 360 - waBR;
        waBL = 360 - waBL;

        waFL = closestAngle(FL.getAngle(), waFL).getTarget();
        wsFL *= closestAngle(FL.getAngle(), waFL).getMotorScale();

        waFR = closestAngle(FR.getAngle(), waFR).getTarget();
        wsFR *= closestAngle(FR.getAngle(), waFR).getMotorScale();

        waBR = closestAngle(BR.getAngle(), waBR).getTarget();
        wsBR *= closestAngle(BR.getAngle(), waBR).getMotorScale();

        waBL = closestAngle(BL.getAngle(), waBL).getTarget();
        wsBL *= closestAngle(BL.getAngle(), waBL).getMotorScale();

        double maxPower = Math.max(Math.abs(wsFL),Math.max(Math.abs(wsFR), Math.max(Math.abs(wsBR), Math.abs(wsBL))));
        if(maxPower > 1.0) {
            wsFL /= maxPower;
            wsFR /= maxPower;
            wsBR /= maxPower;
            wsBL /= maxPower;
        }

        double scaleSpeed = 0.5; //TODO: Should probably be some config value to change this

        FR.setDrive(wsFR * scaleSpeed);
        FL.setDrive(-wsFL * scaleSpeed);
        BR.setDrive(wsBR * scaleSpeed);
        BL.setDrive(-wsBL * scaleSpeed);

        FR.setAngle(waFR);
        FL.setAngle(waFL);
        BR.setAngle(waBR);
        BL.setAngle(waBL);
    }

    public void setBrake() {
        FR.setBrake();
        FL.setBrake();
        BR.setBrake();
        BL.setBrake();
    }

    public void setCoast() {
        FR.setCoast();
        FL.setCoast();
        BR.setCoast();
        BL.setCoast();
    }

    public void angleToZero() {
        FR.reZero();
        FL.reZero();
        BR.reZero();
        BL.reZero();
    }

    public void tankDrive(double left, double right) {
        angleToZero();
        FR.setDrive(-right);
        BR.setDrive(-right);
        FL.setDrive(left);
        BL.setDrive(left);
    }

    public static SwerveTarget closestAngle(double p, double t) { //TODO: Still going to ask Cory
        //TODO: AHHH THESE VARIABLE NAMES!!!!!!!!!!!
        double pTemp = p; //clone p variable (we think p is current angle)

        p %= 360; //set p to the same amount of degrees but limit to 1 [unit] circle revolution
        if(p < 0) p += 360; //if p is clockwise, convert to counterclockwise

        double t1 = t % 360; //create variable set to t (we believe is target angle) in degrees limited to 1 [unit] circle revolution

        double d1 = t1 - p; //create variable to return arc length
        if(d1 > 180) d1 = d1 - 360; //invert direction if over 180 degrees (will return the same angular position but in opposite direction)
        if(d1 < -180) d1 = d1 + 360; //does the same as the line before but if below -180

        double d2 = (d1 > 0 ? d1 - 180 : 180 + d1); //creates a variable of d1's reflection (runs prior code with 0 as bounds instead of [-]180)
        double df = (Math.abs(d1) <= Math.abs(d2) ? d1 : d2); //picks the lesser value between the absolute values of d1 and d2 (for some reason; idk why)
        double motorScale = (Math.abs(d1) <= Math.abs(d2) ? 1 : -1); //determines motor direction based on whether the absolute value of d1 is less than that of d2's

        double target = pTemp + df; //adds the value of df to the original p value

        return new SwerveTarget(target, motorScale);
    }
}
