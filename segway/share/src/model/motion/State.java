package model.motion;

import helper.Ratio;

import java.io.PrintStream;

import linalg.Vector;

/**
 * Segway state.
 * Positive angles are always counterclockwise.
 */
public class State
{
    /**
     * Create a new empty state.
     */
    public State()
    {
        cfg = null;
        time = 0.0;
        sVec = Vector.create(8);
    }
    
    /**
     * Create a new segway state.
     * @param cfg motion model configuration
     * @param time simulation time (sec)
     * @param x x-axis coordinate of the axle midpoint (m)
     * @param y y-axis coordinate of the axle midpoint (m)
     * @param pitch angle from z-axis to body center of mass point (rad)
     * @param roll total rotation of axle midpoint (rad)
     * @param yaw angle from x-axis to axle midpoint (rad)
     * @param dPitch pitch angular velocity (rad/sec)
     * @param dRoll roll angular velocity (rad/sec)
     * @param dYaw yaw angular velocity (rad/sec)
     */
    public State(MotionConfig cfg,
                 double time,
                 double x, double y,
                 double pitch, double roll, double yaw,
                 double dPitch, double dRoll, double dYaw)
    {
        this.cfg = cfg;
        this.time = time;
        
        sVec = Vector.create(8);
        sVec.set(VEC_IDX_PITCH, pitch);
        sVec.set(VEC_IDX_ROLL, roll);
        sVec.set(VEC_IDX_YAW, yaw);
        sVec.set(VEC_IDX_DPITCH, dPitch);
        sVec.set(VEC_IDX_DROLL, dRoll);
        sVec.set(VEC_IDX_DYAW, dYaw);
        sVec.set(VEC_IDX_X, x);
        sVec.set(VEC_IDX_Y, y);
        
        updateCache();
    }

    /** @return copied state (placed into "result") */
    public State copy(State result)
    {
        result.cfg = motionModelConfig();
        result.time = time();
        stateVec().copy(result.sVec);
        result.updateCache();
        return result;
    }
    
    /** @return copied state (placed into a new state) */
    public State copy()
    {
        return new State(motionModelConfig(),
                         time(),
                         x(), y(),
                         pitch(), roll(), yaw(),
                         dPitch(), dRoll(), dYaw());
    }
    
    //--------------------------------------------------------------------------
    
    /** @return motion model configuration */
    public MotionConfig motionModelConfig() { return cfg; }
    
    /** @return state space dimensionality (without time) */
    public int stateSpaceDimension() { return sVec.length(); }
    
    /** @return simulation time (sec) */
    public double time() { return time; }
    
    /** @return axle midpoint x-axis coordinate (m) */
    public double x() { return sVec.get(VEC_IDX_X); }
    
    /** @return axle midpoint y-axis coordinate (m) */
    public double y() { return sVec.get(VEC_IDX_Y); }
    
    /** @return angle from z-axis to body center of mass point (rad) */
    public double pitch() { return sVec.get(VEC_IDX_PITCH); }
    
    /** @return total rotation of axle midpoint (rad) */
    public double roll() { return sVec.get(VEC_IDX_ROLL); }
    
    /** @return angle from x-axis to axle midpoint (rad) */
    public double yaw() { return sVec.get(VEC_IDX_YAW); }
    
    /** @return pitch angular velocity (rad/sec) */
    public double dPitch() { return sVec.get(VEC_IDX_DPITCH); }
    
    /** @return roll angular velocity (rad/sec) */
    public double dRoll() { return sVec.get(VEC_IDX_DROLL); }
    
    /** @return yaw angular velocity (rad/sec) */
    public double dYaw() { return sVec.get(VEC_IDX_DYAW); }
    
    /** @return total rotation of left axle point (rad) */
    public double leftRoll() { return leftRoll; }
    
    /** @return total rotation of right axle point (rad) */
    public double rightRoll() { return rightRoll; }
    
    /** @return left roll angular velocity (rad/sec) */
    public double dLeftRoll() { return dLeftRoll; }
    
    /** @return right roll angular velocity (rad/sec) */
    public double dRightRoll() { return dRightRoll; }
    
    //--------------------------------------------------------------------------
    
    public void print(PrintStream p)
    {
        p.println("time.......: " + time() + " sec");
        p.println("x          : " + x() + " m");
        p.println("y..........: " + y() + " m");
        p.println("pitch      : " + pitch()*Ratio.RAD_TO_DEG + " deg");
        p.println("roll       : " + roll()*Ratio.RAD_TO_DEG + " deg");
        p.println("yaw........: " + yaw()*Ratio.RAD_TO_DEG + " deg");
        p.println("dPitch     : " + dPitch()*Ratio.RAD_TO_DEG + " deg/sec" );
        p.println("dRoll      : " + dRoll()*Ratio.RAD_TO_DEG + " deg/sec");
        p.println("dYaw.......: " + dYaw()*Ratio.RAD_TO_DEG + " deg/sec");
        p.println("leftRoll   : " + leftRoll()*Ratio.RAD_TO_DEG + " deg");
        p.println("rightRoll..: " + rightRoll()*Ratio.RAD_TO_DEG + " deg");
        p.println("dLeftRoll  : " + dLeftRoll()*Ratio.RAD_TO_DEG + " deg/sec");
        p.println("dRightRoll.: " + dRightRoll()*Ratio.RAD_TO_DEG + " deg/sec");
    }
    
    //--------------------------------------------------------------------------
    // Package private routines (only for internal use).

    Vector stateVec() { return sVec; }
    
    void incTime(double incr) { time += incr; }
    
    void updateCache()
    {
        double W = cfg.W, R = cfg.R;
        
        double rollOffset = W * yaw() / (2.0*R);
        leftRoll = roll() - rollOffset;
        rightRoll = roll() + rollOffset;
        
        double dRollOffset = W * dYaw() / (2.0*R);
        dLeftRoll = dRoll() - dRollOffset;
        dRightRoll = dRoll() + dRollOffset;
    }
    
    static final int VEC_IDX_X      = 0;
    static final int VEC_IDX_Y      = 1;
    static final int VEC_IDX_PITCH  = 2;
    static final int VEC_IDX_ROLL   = 3;
    static final int VEC_IDX_YAW    = 4;
    static final int VEC_IDX_DPITCH = 5;
    static final int VEC_IDX_DROLL  = 6;
    static final int VEC_IDX_DYAW   = 7;

    //--------------------------------------------------------------------------

    // motion model
    private MotionConfig cfg;
    
    // simulation time (sec)
    private double time;
    
    // state vector 
    private final Vector sVec;
    
    // left and right wheel total rotations (rad)
    private double leftRoll, rightRoll;
    
    // left and right wheel angular velocities (rad/sec)
    private double dLeftRoll, dRightRoll;
}
