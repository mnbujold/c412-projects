package exercise;

import model.scene.SceneModel;
import model.scene.SceneModelObject;
import model.sensor.DistanceSensor;
import model.sensor.DistanceSensorConfig;

/**
 * Model of an infrared sensor.
 * 
 * Use the rng() method to access the random number generator.
 */
public class IRSensor extends DistanceSensor
{
    public IRSensor(DistanceSensorConfig cfg, SceneModel scene)
    {
        super (cfg, scene);
    }
    
    @Override
    public double sample(double realDistance, SceneModelObject hitObj)
    {
        // TODO modify according to your IR sensor model
        return realDistance;
    }

    @Override
    public double pdf(double sampleDistance,
                      double realDistance,
                      SceneModelObject hitObj)
    {
        // TODO modify according to your IR sensor model
        return (sampleDistance == realDistance) ? 1.0 : 0.0;
    }
}
