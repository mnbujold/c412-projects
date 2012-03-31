package model.sensor;


/**
 * Internal state representation of the distance sensor.
 */
public class DistanceState
{
    double realDistance = 0.0;
    Object hitObj = null;
    
    /** @return real distance traveled by the ray (mm) */
    public double realDistance() { return realDistance; }
    
    /** @return hit scene object (or null if nothing is hit) */
    public Object hitObject() { return hitObj; }
    
    /** @return copied distance sensor state (placed into "result") */
    public DistanceState copy(DistanceState result)
    {
        result.realDistance = realDistance;
        result.hitObj = hitObj;
        return result;
    }
}
