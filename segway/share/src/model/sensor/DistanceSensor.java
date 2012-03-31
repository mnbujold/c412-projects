package model.sensor;

import geom3d.Point3D;

import java.util.Random;

import model.scene.SceneModel;
import model.scene.SceneModelObject;
import model.scene.SceneModel.DistanceResult;

/**
 * Model of a distance sensor.
 */
public abstract class DistanceSensor
{
    public DistanceSensor(DistanceSensorConfig cfg, SceneModel scene)
    {
        this.cfg = cfg;
        this.scene = scene;
        rng = new Random();
        result = new DistanceResult();
        state = new DistanceState();
    }
    
    /** @return distance sensor configuration settings */
    public DistanceSensorConfig cfg() { return cfg; }
    
    /** @return random number generator */
    public Random rng() { return rng; }
    
    //--------------------------------------------------------------------------
    
    /**
     * Return the measured distance given the real one. It is equal to sample
     * from the X|Y="realDistance" conditional distribution.
     * 
     * @param realDistance real distance between the sensor's position
     *                     and the hit object (mm), or Double.POSITIVE_INFINITY
     * @param hitObj hit scene object or null (if nothing is hit)
     * @return distance measured by the sensor
     */
    public abstract double sample(double realDistance, SceneModelObject hitObj);
    
    /**
     * Return the "probability" of observing "sampleDistance" by the distance
     * sensor while the real distance is equal to "realDistance". It is equal to
     * the value of the pdf function of the X|Y distribution evaluated at
     * X = "sampleDistance" and Y="realDistance".
     * 
     * @param sampleDistance measured distance (mm)
     * @param realDistance real distance between the sensor's position
     *                     and the hit object (mm), or Double.POSITIVE_INFINITY
     * @param hitObj hit scene object or null (if nothing is hit)
     * @return pdf(sampleDistance|realDistance)
     */
    public abstract double pdf(double sampleDistance,
                               double realDistance,
                               SceneModelObject hitObj);
    
    //--------------------------------------------------------------------------

    /** Reset the sensor's state. */
    public void reset(long seed)
    {
        rng.setSeed(seed);
        sampledDistance = 0.0;
        state.realDistance = 0.0;
        state.hitObj = null;
    }    

    /**
     * @param t time (sec)
     * @param pos position of the axle midpoint of the segway (mm)
     * @param pitch pitch angle of the segway (rad)
     * @param yaw yaw angle of the segway (rad)
     */
    public void next(double t, Point3D pos, double pitch, double yaw)
    {
        scene.realDistance(cfg(), pos, pitch, yaw, result);
        state.realDistance = result.distance();
        state.hitObj = result.hitObject();
        
        sampledDistance = sample(state.realDistance(),
                                 (SceneModelObject)state.hitObj);
    }
    
    /** @return distance sensor measurement at the current state (mm) */
    public double read() { return sampledDistance; }

    //--------------------------------------------------------------------------
    
    /** @return the internal state of the distance sensor */
    public DistanceState state() { return state; }

    //--------------------------------------------------------------------------
    
    private double sampledDistance;
    private final DistanceState state;
    
    private final DistanceResult result;
    private final DistanceSensorConfig cfg;
    private final SceneModel scene;
    private final Random rng;
}
