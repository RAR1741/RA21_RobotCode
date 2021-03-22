package frc.robot;

import com.kauailabs.navx.frc.AHRS;

public class SwerveDrive {

    private final double TurningSpeedFactor = -1.0;
    private double length, width, diameter;
    private double temp;
    private double a,b,c,d;
    private double ws1,ws2,ws3,ws4;
    private double wa1,wa2,wa3,wa4;
    private double max;
    private int movecount;

    private SwerveModule FR;
    private SwerveModule FL;
    private SwerveModule BR;
    private SwerveModule BL;

    public SwerveDrive(SwerveModule FR, SwerveModule FL, SwerveModule BR, SwerveModule BL){
        this.FR = FR;
        this.FL = FL;
        this.BR = BR;
        this.BL = BL;

        length = 1;
        width = 1;
        diameter = Math.sqrt(Math.pow(length,2)+Math.pow(width,2));
        temp = 0.0;
        a = 0.0;b = 0.0;c = 0.0;d = 0.0;
        ws1 = 0.0;ws2 = 0.0;ws3 = 0.0;ws4 = 0.0;
        wa1 = 0.0;wa2 = 0.0;wa3 = 0.0;wa4 = 0.0;
        max = 0.0;
        movecount = 20;

        FR.initMagEncoder(1237.0);
        FL.initMagEncoder(2556.0);
        BR.initMagEncoder(3024.0);
        BL.initMagEncoder(1986.0);
    }

    // Test Code
    public void manualControl(double driveSpeed, double angleSpeed){
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

    public void driverSwerve(double x, double y, double z, double gyro, boolean fieldOrient)
    {
        if((x!=0 || y!=0) || z!=0)
        {
            swerve(x, y, z, gyro, fieldOrient);
            movecount = 30;
        }
        else
        {
            movecount--;
            if(movecount < 0)
            {
                swerve(0, 0, 0, 0, false);
            }
            else
            {
                FR.setDrive(0);
                FL.setDrive(0);
                BR.setDrive(0);
                BL.setDrive(0);
            }
        }
    }
    
    public void swerve(double x, double y, double z, double gyro, boolean fieldOrient)
    {
        gyro *= Math.PI/180.0f;
        z *= TurningSpeedFactor;
        if(fieldOrient)
        {
            temp = y * Math.cos(gyro) + x * Math.sin(gyro);
            x = -y * Math.sin(gyro) + x * Math.cos(gyro);
            y = temp;
        }

        a = x + z * (length/diameter);
        b = x - z * (length/diameter);
        c = y - z * (width/diameter);
        d = y + z * (width/diameter);

        ws1 = Math.sqrt(Math.pow(b,2) + Math.pow(c,2));
        ws2 = Math.sqrt(Math.pow(b,2) + Math.pow(d,2));
        ws3 = Math.sqrt(Math.pow(a,2) + Math.pow(d,2));
        ws4 = Math.sqrt(Math.pow(a,2) + Math.pow(c,2));
        max = 0;
        if(ws1 > max){max = ws1;}
        if(ws2 > max){max = ws2;}
        if(ws3 > max){max = ws3;}
        if(ws4 > max){max = ws4;}
        if(max > 1){ws1 /= max;ws2 /= max;ws3 /= max;ws4 /= max;}

        wa1 = Math.atan2(b,c) * 180.0f/Math.PI;
        wa2 = Math.atan2(b,d) * 180.0f/Math.PI;
        wa3 = Math.atan2(a,d) * 180.0f/Math.PI;
        wa4 = Math.atan2(a,c) * 180.0f/Math.PI;
        
        if(wa1 < 0){wa1 += 360;}//wa1 = FL
        if(wa2 < 0){wa2 += 360;}//wa2 = FR
        if(wa3 < 0){wa3 += 360;}//wa3 = BR
        if(wa4 < 0){wa4 += 360;}//wa4 = BL

        wa1 = 360 - wa1;
        wa2 = 360 - wa2;
        wa3 = 360 - wa3;
        wa4 = 360 - wa4;

        SwerveTarget tmp;
        // if((tmp = closestAngle(FR.getAngleEncoder()/FR.getEncPerDeg(), wa2)) != wa2 && ((tmp + 360)%360 != wa2 || (tmp - 360)%360 != wa2))
        // {
        //     System.out.println("FLIPPED");
        //     wa2 = tmp;
        //     ws2 *= -1;
        // }

        tmp = closestAngle(FL.getAngle(), wa1);
        wa1 = tmp.getTarget();
        ws1 *= tmp.getMotorScale();

        double joystickA = wa2;
        // System.out.println(String.format("A: %3.2f", wa2));
        tmp = closestAngle(FR.getAngle(), wa2);
        wa2 = tmp.getTarget();
        ws2 *= tmp.getMotorScale();
        // System.out.println(tmp.getMotorScale() == -1 ? "FLIPPED!" : "");
        // System.out.println(String.format("J: %3.2f \t tA: %3.2f \t cA: %3.2f \t E: %3.2f", joystickA, wa2, FR.getAngle()%360, FR.getAngleEncoder() ));


        tmp = closestAngle(BR.getAngle(), wa3);
        wa3 = tmp.getTarget();
        ws3 *= tmp.getMotorScale();
        

        tmp = closestAngle(BL.getAngle(), wa4);
        wa4 = tmp.getTarget();
        ws4 *= tmp.getMotorScale();
        // if((tmp = closestAngle(FL.getAngleEncoder()/FL.getEncPerDeg(), wa1)) != wa1)
        // {
        //     wa1 = tmp;
        //     ws1 *= -1;
        // }
        // if((tmp = closestAngle(BR.getAngleEncoder()/BR.getEncPerDeg(), wa3)) != wa3)
        // {
        //     wa3 = tmp;
        //     ws3 *= -1;
        // }
        // if((tmp = closestAngle(BL.getAngleEncoder()/BL.getEncPerDeg(), wa4)) != wa4)
        // {
        //     wa4 = tmp;
        //     ws4 *= -1;
        // }


        // System.out.println(String.format("J: %3.2f \tA: %3.2f \t P: %3.2f", joystickA, wa2, currentAngle));
        // System.out.println("-------------");

        
        double maxPower = Math.max(Math.abs(ws1),Math.max(Math.abs(ws2), Math.max(Math.abs(ws3), Math.abs(ws4))));
        if(maxPower > 1.0)
        {
            ws1 /= maxPower;
            ws2 /= maxPower;
            ws3 /= maxPower;
            ws4 /= maxPower;
        }

        FR.setDrive(ws2);
        FL.setDrive(-ws1);
        BR.setDrive(ws3);
        BL.setDrive(-ws4);

        // System.out.println("J: " + String.valueOf(wa2));
        // System.out.println("A: " + String.valueOf(wa2));
        // System.out.println("S: " + String.valueOf(ws2));
        // System.out.println("");

        FR.setAngle(wa2);
        FL.setAngle(wa1);
        BR.setAngle(wa3);
        BL.setAngle(wa4);

        // System.out.println(String.format("FR: %3.2f \t BR: %3.2f", FR.getAngleCurrent(), BR.getAngleCurrent()));
    }
    
    public void setBrake()
    {
        FR.setBrake();
        FL.setBrake();
        BR.setBrake();
        BL.setBrake();
    }
    
    public void setCoast()
    {
        FR.setCoast();
        FL.setCoast();
        BR.setCoast();
        BL.setCoast();
    }
    
    public void angleToZero()
    {
        FR.setDrive(0);
        FL.setDrive(0);
        BR.setDrive(0);
        BL.setDrive(0);
        FR.setAngle(0);
        FL.setAngle(0);
        BR.setAngle(0);
        BL.setAngle(0);
    }
    
    public void tankDrive(double left, double right)
    {
        angleToZero();
        FR.setDriveSpeed(right);
        BR.setDriveSpeed(right);
        FL.setDriveSpeed(-left);
        BL.setDriveSpeed(-left);
    }

    // public static double closestAngle(double p, double t)
    // {
    //     // return t;
    //     System.out.println("Position, Target:" + String.valueOf(p) + ":" + String.valueOf(t));

    //     p %= 360;
    //     double t1 = t % 360;
    //     double t2 = (t1+180)%360;

    //     double d1 = Math.abs(p - t1);
    //     if(d1 > 180) d1 = 360 - d1;

    //     double d2 = Math.abs(p -t2);
    //     if(d2 > 180) d2 = 360 - d2;

    //     return (d1 < d2 ? t1 : t2);
    // }

    public static SwerveTarget closestAngle(double p, double t)
    {
        // return new SwerveTarget(t, 1);

        double pTemp = p;
        
        p %= 360;
        if(p < 0) p += 360;
        
        double t1 = t % 360;
        
        double d1 = t1 - p;
        if(d1 > 180) d1 = d1 - 360;
        if(d1 < -180) d1 = d1 + 360;

        double d2 = (d1 > 0 ? d1 - 180 : 180 + d1);
        double df = (Math.abs(d1) <= Math.abs(d2) ? d1 : d2);
        double motorScale = (Math.abs(d1) <= Math.abs(d2) ? 1 : -1);

        double target = pTemp + df;

        return new SwerveTarget(target, motorScale);
    }
}

