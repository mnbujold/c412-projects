package solution;

import java.awt.event.KeyEvent;

import run.Robot;
import run.ThreadLogic;
import comm.CommunicatorLogic;

import control.RobotController;

/**
 * Low-level robot controller navigated by an AutoNavPFLocPCController.
 */
public class AutoNavPFLocRobotController extends RobotController
{
    // number of samples used to measure the gyroscope bias
    static final int GYRO_BIAS_N = 200;
    
    // gyroscope sign (-1 or +1) set based on the sensor orientation
    static final int GYRO_SIGN = +1;
    
    // Main balancing constants.
    static final double WHEEL_RATIO = 1.0;
    static final double KGYROANGLE = 7.5;
    static final double KGYROSPEED = 1.15;
    static final double KPOS = 0.07;
    static final double KSPEED = 0.1;
    
    // This constant aids in drive control. When the robot starts moving 
    // because of user control, this constant helps get the robot leaning 
    // in the right direction. Similarly, it helps bring robot 
    // to a stop when stopping.
    static final double KDRIVE = -0.02;
    
    // Power differential used for steering based on difference of target
    // steering and actual motor difference.
    static final double KSTEER = 0.25;
    
    // This constant is in degrees/second for maximum speed. Note that
    // position and speed are measured as the sum of the two motors, in
    // other words, 600 would actually be 300 degrees/second for each motor.
    static final double CONTROL_SPEED = 600.0;
    
    // If robot power is saturated (over +/- 100) for over this time limit
    // then robot must have fallen. In milliseconds.
    static final int TIME_FALL_LIMIT = 500;
    
    // Target delay between two consecutive control steps (ms).
    static final int CONTROL_DELAY = 10;

    // Target delay between two consecutive observer steps (ms). 
    static final int OBSERVER_DELAY = 3;
    
    //--------------------------------------------------------------------------
    
    public AutoNavPFLocRobotController(Robot robot)
    {
        super (robot);
        commLogic = new CommunicatorLogicImpl();
        observer = new Observer();
    }
    
    @Override
    public void initialize() throws Exception
    {
        super.initialize();
        
        gyroOffset = 0.0;
        for (int i = 0; i < GYRO_BIAS_N; ++i)
        {
            gyroOffset += GYRO_SIGN * robot().readGyro();
            robot().msDelay(5);
        }
        gyroOffset /= GYRO_BIAS_N;
        gyroAngle = 0.0;
        
        mrcSumPrev = 0;
        mrcDeltaP1 = mrcDeltaP2 = mrcDeltaP3 = 0;
        
        motorPosition = 0.0;
        motorControlDrive = 0.0;
        motorControlSteer = 0.0;
        motorControlAccel = 0.0;
        motorDiffTarget = 0.0;
        
        prevTime = 0;
        tMotorPosOk = 0;
        
        robot().createCommunicator(commLogic);
    }
    
    @Override
    public void control() throws Exception
    {
        long time = robot().currentTimeMillis();
        if (prevTime == 0)
        {
            // 1st step
            prevTime = time;
            tMotorPosOk = time;
            robot().resetLeftRotationCounter();
            robot().resetRightRotationCounter();
            robot().spawn("observer", observer);
            robot().msDelay(5);
            return;
        }
        
        double dt = (time - prevTime) * MILLISEC_TO_SEC;
        prevTime = time;
        
        double gyroValue = GYRO_SIGN * robot().readGyro() - gyroOffset;
        gyroAngle += gyroValue * dt;
        
        int mrcLeft = robot().leftRotationCounter();
        int mrcRight = robot().rightRotationCounter();
        int mrcSum = mrcLeft + mrcRight;
        int motorDiff = mrcLeft - mrcRight;
        int mrcDelta = mrcSum - mrcSumPrev;
        motorPosition += mrcDelta;
        motorPosition -= motorControlDrive * dt;
                
        double motorControlDrive = 0.0;
        double motorControlSteer = 0.0;
        synchronized (commLogic)
        {
            motorControlDrive = this.motorControlDrive;
            motorControlSteer = this.motorControlSteer;
        }

        double motorSpeed =
            (mrcDelta + mrcDeltaP1 + mrcDeltaP2 + mrcDeltaP3) / (4.0 * dt);
        
        mrcDeltaP3 = mrcDeltaP2;
        mrcDeltaP2 = mrcDeltaP1;
        mrcDeltaP1 = mrcDelta;
        mrcSumPrev = mrcSum;
        
        int power = (int)( (KGYROSPEED * gyroValue
                            + KGYROANGLE * gyroAngle) / WHEEL_RATIO
                           + KPOS * motorPosition
                           + KDRIVE * motorControlDrive
                           + KSPEED * motorSpeed);
        
        if (Math.abs(power) < 100) tMotorPosOk = time;
        if (time - tMotorPosOk > TIME_FALL_LIMIT) { terminate(); return; }
        
        // steering
        motorDiffTarget += motorControlSteer * dt;
        int powerSteer = (int)(KSTEER * (motorDiffTarget - motorDiff));
        int lPower = power + powerSteer;
        int rPower = power - powerSteer;
        
        robot().controlLeftMotor(limitPower(lPower));
        robot().controlRightMotor(limitPower(rPower));
        
        int delay = CONTROL_DELAY + (int)(time - robot().currentTimeMillis());
        if (0 < delay) robot().msDelay(delay);
    }
    
    //--------------------------------------------------------------------------
    
    private class Observer extends ThreadLogic
    {
        @Override
        public void run()
        {
            pitch = 0.0;
            while (true)
            {
                update();
                msDelay(OBSERVER_DELAY);
            }
        }
        
        public synchronized double update()
        {
            pitch += 1;
            return pitch;
        }
        
        private double pitch = 0.0;
    }
    
    //--------------------------------------------------------------------------
    
    // Maximum number of key codes to be received.
    private static final byte MAX_KEY_CODES = 3;
    
    private class CommunicatorLogicImpl extends CommunicatorLogic
    {
        @Override
        public void initalize() throws Exception
        {
            super.initalize();
            channel().writeByte(MAX_KEY_CODES);
            channel().writeByte((byte)robot().readDistances().length);
            channel().flush();
            
            mrcPrevL = robot().leftRotationCounter();
            mrcPrevR = robot().rightRotationCounter();
        }
        
        @Override
        public void logic() throws Exception
        {
            byte n = channel().readByte();
            switch (n)
            {
                case -1 :
                {
                    // termination request
                    terminate();
                    break;
                }
                case -2 :
                {
                    // observation request
                    int mrcL = robot().leftRotationCounter();
                    int mrcR = robot().rightRotationCounter();
                    double pitch = observer.update();
                    int d[] = robot().readDistances();
                    
                    short deltaL = (short)(mrcL - mrcPrevL);
                    short deltaR = (short)(mrcR - mrcPrevR);
                    
                    channel().writeFloat((float)pitch);
                    channel().writeShort(deltaL);
                    channel().writeShort(deltaR);
                    for (int i = 0; i < d.length; ++i)
                        channel().writeShort((short)d[i]);
                    channel().flush();
                    
                    mrcPrevL = mrcL;
                    mrcPrevR = mrcR;
                    break;
                }
                default :
                {
                    // receiving controls
                    boolean controlAccelChanged = false;
                    boolean controlTurnChanged = false;
                    for (byte i = 0; i < n; ++i)
                        switch (channel().readShort())
                        {
                            case KeyEvent.VK_UP :
                                controlAccelChanged = true;
                                motorControlAccel += 0.25;
                                if (motorControlAccel > 1.0)
                                    motorControlAccel = 1.0;
                                break;
                            case KeyEvent.VK_DOWN :
                                controlAccelChanged = true;
                                motorControlAccel -= 0.25;
                                if (motorControlAccel < -1.0)
                                    motorControlAccel = -1.0;
                                break;
                            case KeyEvent.VK_LEFT :
                                controlTurnChanged = true;
                                motorControlTurn -= 0.1;
                                if (motorControlTurn < -1.0)
                                    motorControlTurn = -1.0;
                                break;
                            case KeyEvent.VK_RIGHT :
                                controlTurnChanged = true;
                                motorControlTurn += 0.1;
                                if (motorControlTurn > 1.0)
                                    motorControlTurn = 1.0;
                                break;
                        }
                    
                    if (!controlAccelChanged && 0.0 != motorControlAccel)
                    {
                        controlAccelChanged = true;
                        motorControlAccel -=
                            Math.signum(motorControlAccel) *
                            Math.min(Math.abs(motorControlAccel), 0.1);
                    }
                    
                    if (!controlTurnChanged && 0.0 != motorControlTurn)
                    {
                        controlTurnChanged = true;
                        motorControlTurn -=
                            Math.signum(motorControlTurn) *
                            Math.min(Math.abs(motorControlTurn), 0.5);
                    }
                    
                    synchronized (commLogic)
                    {
                        if (controlAccelChanged)
                            motorControlDrive =
                                motorControlAccel * CONTROL_SPEED;
                        if (controlTurnChanged)
                            motorControlSteer =
                                motorControlTurn * CONTROL_SPEED;
                    }
                    break;
                }
            }
        }
        
        private int mrcPrevL, mrcPrevR;
    }
    
    //--------------------------------------------------------------------------
    
    private static final double MILLISEC_TO_SEC = 1e-3;    
    private final CommunicatorLogicImpl commLogic;
    private final Observer observer;
    
    private long prevTime, tMotorPosOk;
    private int mrcSumPrev, mrcDeltaP1, mrcDeltaP2, mrcDeltaP3;
    private double gyroOffset, gyroAngle;
    
    private double motorControlDrive;
    private double motorControlSteer;
    private double motorDiffTarget;
    private double motorControlAccel;
    private double motorControlTurn;
    private double motorPosition;
}
