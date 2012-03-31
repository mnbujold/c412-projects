package model.sensor;

import java.util.Random;

import linalg.Vector;

/**
 * Model of a rate gyroscope.
 */
public abstract class GyroSensor
{
    public GyroSensor(GyroSensorConfig cfg)
    {
        this.cfg = cfg;
        rng = new Random();
        
        state = new GyroState();
        wTemp = Vector.zero(2);
    }
    
    /** @return random number generator */
    public Random rng() { return rng; }
    
    /** @return gyroscope configuration */
    public GyroSensorConfig cfg() { return cfg; }
    
    //--------------------------------------------------------------------------
    
    /** @return the bias at time t */
    public abstract double bias(double t);
    
    /** @return the noise at time t for input dotU */
    public abstract double noise(double t, double dotU);
    
    /**
     * Update the gyroscope state (without applying the bias and the noise).
     * @param t current time (sec)
     * @param xt current state at time t
     * @param dotU current input
     * @param dt time step (sec)
     * @param xtdt next state at time t+dt (to be computed)
     */
    public abstract void next(double t, Vector xt,
                              double dotU,
                              double dt, Vector xtdt);
    
    //--------------------------------------------------------------------------
    
    /** Reset the gyroscope state. */
    public void reset(long seed)
    {
        rng.setSeed(seed);
        
        state.w.setToZero();
        state.bias = bias(0);
        state.noise = noise(0.0, 0.0);
        gyroValue = state.bias() + state.noise();
    }
    
    /**
     * Update the gyroscope state for time t+dt
     * (applying the bias and the noise).
     */
    public void next(double t, double dotU, double dt)
    {
        next(t, state.w, dotU, dt, wTemp);
        
        state.w.set(0, wTemp.get(0));
        state.w.set(1, wTemp.get(1));
        state.bias = bias(t);
        state.noise = noise(t, dotU);
        
        gyroValue = state.y() + state.bias() + state.noise();
    }
    
    /** @return gyroscope reading at the current state */
    public double read() { return gyroValue; }

    //--------------------------------------------------------------------------
    
    /** Internal state representation of the gyroscope. */
    public static class GyroState
    {
        /** @return gyroscope angle (deg) */
        public double y() { return w.get(0); }
        
        /** @return gyroscope angular velocity (deg/sec) */
        public double dy() { return w.get(1); }
        
        /** @return current gyroscope bias (deg/sec) */
        public double bias() { return bias; }
        
        /** @return current noise (deg/sec) */
        public double noise() { return noise; }
        
        /** @return copied gyroscope sensor state (placed into "result") */
        public GyroState copy(GyroState result)
        {
            result.w.set(0, w.get(0));
            result.w.set(1, w.get(1));
            result.bias = bias;
            result.noise = noise;
            return result;
        }
        
        private Vector w = Vector.create(2);
        private double bias;
        private double noise;
    }
    
    /** @return the internal state of the gyroscope sensor */
    public GyroState state() { return state; }
    
    //--------------------------------------------------------------------------
    
    
    //--------------------------------------------------------------------------
    
    private double gyroValue;
    
    private final GyroSensorConfig cfg;
    private final Random rng;
    
    private final GyroState state;
    private final Vector wTemp;
}
