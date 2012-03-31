package student;

import java.util.Set;

import model.State;

import sensor.GyroDynamics;
import sensor.Observation;
import visual.Keyboard.Key;
import control.Action;
import control.Controller;

/**
 * The simulated version of the HTWayJ controller.
 */
public class HTWayController extends Controller
{
	/**
	 * My code here
	 */
	
	public double[] t_array = new double[60*1000/10];
	public double[] u_dot_array = new double[60*1000/10];
	public double[] u_array = new double[60*1000/10];
	public double[] y_array = new double[60*1000/10];
	int index =0;
	double t =0;
	
	
    /**
     * Main balancing constants.
     */
    public static double KGYROANGLE = 7.5;
    public static double KGYROSPEED = 1.15;
    public static double KPOS = 0.07;
    public static double KSPEED = 0.1;

    /**
     * Power differential used for steering based on difference of target 
     * steering and actual motor difference.
     */
    static final double KSTEER = 0.25;

    /**
     * This constant is in degrees/second for maximum speed. Note that
     * position and speed are measured as the sum of the two motors, in
     * other words, 600 would actually be 300 degrees/second for each
     * motor.
     */
    static final double CONTROL_SPEED = 600.0;

    //--------------------------------------------------------------------------

    public HTWayController(boolean isNullState,
                        GyroDynamics.Parameters gyroParams)
    {
        super(isNullState, gyroParams);
        t_array[0]=0;
	    u_dot_array[0]=0;
	    y_array[0]=0;
    }

    //--------------------------------------------------------------------------

    @Override
    public void reset()
    {
        super.reset();
        lPower = 0.0;
        rPower = 0.0;
        mrcSumPrev = 0;
        gyroAngle = 0.0;
        motorPosition = 0.0;
        motorControlDrive = 0.0;
        motorControlSteer = 0.0;
        motorControlAccel = 0.0;
        motorDiffTarget = 0.0;
    }

    @Override
    public Action computeControl(Observation obs, State state)
    {
        double dt = dtSec();
        double gyroValue = obs.gyroValue() - gyroParameters().offset();
        t+=dt;
		if(index <60*1000/10-1){
            index++;
            t_array[index]=t;
            u_dot_array[index]=state.getDotPsiDegSec();
            u_array[index]=state.getPsiDeg();
            y_array[index]=gyroValue;
            System.out.println(index);
            }else{
            	System.out.println("enough");
            }
        gyroAngle += gyroValue * dt;
        int mrcLeft = obs.lWheelRotCtr();
        int mrcRight = obs.rWheelRotCtr();
        
        //-----------------------
        
//        double gyroValue = state.getDotPsiDegSec();
//        gyroAngle = state.getPsiDeg();
//        int mrcLeft = (int)Math.round(state.getThetaLDeg());
//        int mrcRight = (int)Math.round(state.getThetaRDeg());
        
        // ---------------------
        int mrcSum = mrcLeft + mrcRight;
        int motorDiff = mrcLeft - mrcRight;
        int mrcDelta = mrcSum - mrcSumPrev;
        
        motorPosition += mrcDelta;
        motorPosition -= motorControlDrive * dt;
        
        // motorSpeed is based on the average of the last four delta's.
        double motorSpeed = 
            (mrcDelta + mrcDeltaP1 + mrcDeltaP2 + mrcDeltaP3) / (4.0 * dt);
        
        mrcDeltaP3 = mrcDeltaP2;
        mrcDeltaP2 = mrcDeltaP1;
        mrcDeltaP1 = mrcDelta;
        mrcSumPrev = mrcSum;
        
        double power = KGYROSPEED * gyroValue
                     + KGYROANGLE * gyroAngle
                     + KPOS * motorPosition
                     + KSPEED * motorSpeed;
        
        // steering
        motorDiffTarget += motorControlSteer * dt;
        double powerSteer = KSTEER * (motorDiffTarget - motorDiff);
        lPower = power + powerSteer;
        rPower = power - powerSteer;
        
        double real2sim = 0.5; // scaling between real and simulation
//        double real2sim = 0.7; // our own version
        
        lPower *= real2sim;
        rPower *= real2sim;
        
        sleep(10); // next control in 10 msec
        return new Action(lPower, rPower);
    }

    @Override
    public void keyControl(Set<Key> keys)
    {
        super.keyControl(keys);
        
        boolean controlAccelChanged = false;
        boolean controlSteerChanged = false;
        for (Key key : keys)
        {
            switch (key)
            {
                case UP :
                    controlAccelChanged = true;
                    chgMotorControlDrive(+0.25);
                    break;
                case DOWN :
                    controlAccelChanged = true;
                    chgMotorControlDrive(-0.25);
                    break;
                case LEFT :
                    controlSteerChanged = true;
                    chgMotorControlSteer(-0.1);
                    break;
                case RIGHT :
                    controlSteerChanged = true;
                    chgMotorControlSteer(+0.1);
                    break;
            }
        }
        
        if (!controlAccelChanged)
        {
            double s = Math.signum(motorControlAccel);
            double r = Math.min(Math.abs(motorControlAccel), 0.1) / 4;
            chgMotorControlDrive(-s * r);
        }
        
        if (!controlSteerChanged)
        {
            double s = Math.signum(motorControlSteer);
            double r = Math.min(Math.abs(motorControlSteer), 0.5) / 4;
            chgMotorControlSteer(-s * r);
        }
    }

    //--------------------------------------------------------------------------

    private synchronized void chgMotorControlDrive(double chg)
    {
        motorControlAccel += chg;
        motorControlAccel = Math.min(+1.0, Math.max(-1.0, motorControlAccel));
        motorControlDrive = motorControlAccel * CONTROL_SPEED;
    }

    private synchronized void chgMotorControlSteer(double chg)
    {
        motorControlTurn += chg;
        motorControlTurn = Math.min(+1.0, Math.max(-1.0, motorControlTurn));
        motorControlSteer = motorControlTurn * CONTROL_SPEED;
    }

    //--------------------------------------------------------------------------

    private int mrcSumPrev;
    private int mrcDeltaP1, mrcDeltaP2, mrcDeltaP3;

    private double motorControlDrive;
    private double motorControlSteer;
    private double motorDiffTarget;
    private double motorControlAccel;
    private double motorControlTurn;

    private double motorPosition;
    private double gyroAngle;
    private double lPower, rPower;
}
