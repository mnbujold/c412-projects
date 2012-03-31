package model.sensor;

import geom3d.Point3D;
import helper.Config;
import helper.MissingConfigException;
import helper.Ratio;

import java.io.File;
import java.io.IOException;

/**
 * Distance sensor configuration.
 */
public class DistanceSensorConfig extends Config
{
    public DistanceSensorConfig(File file)
    throws IOException, MissingConfigException, ClassNotFoundException
    {
        super (file);
        
        c = getStringConfig("class");
        
        pos = new Point3D(getDoubleConfig("x"),
                          getDoubleConfig("y"),
                          getDoubleConfig("z"));
        
        pitch = getDoubleConfig("pitch") * Ratio.DEG_TO_RAD;
        yaw = getDoubleConfig("yaw") * Ratio.DEG_TO_RAD;

        axis = Point3D.unitX().rotateY(pitch).rotateZ(yaw);
        
        maxValue = getDoubleConfig("max-value");
    }
    
    //--------------------------------------------------------------------------

    /** @return distance sensor class */
    public String sensorClass() { return c; }
    
    /** @return relative position (mm) to the axle midpoint */
    public Point3D position() { return pos; }
    
    /** @return orientation of the distance measuring ray (unit vector) */
    public Point3D axis() { return axis; }
    
    /** @return pitch angle (rad) rotated around the sensor's position */
    public double pitch() { return pitch; }
    
    /** @return yaw angle (rad) rotated around the sensor's position */
    public double yaw() { return yaw; }
    
    /** @return maximum value which can be measured by the sensor */
    public double maxValue() { return maxValue; }
    
    //--------------------------------------------------------------------------
    
    private final String c;
    private final Point3D pos, axis;
    private final double pitch, yaw;
    private final double maxValue;
}
