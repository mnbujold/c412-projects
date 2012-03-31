package sensor;

import java.util.Random;

import sensor.GyroDynamics.Parameters;

/**
 * Simulated gyroscope sensor
 * which can measure by one degree per second accuracy.
 */
public class GyroSensor
{
    public GyroSensor(GyroDynamics dyn, Random rng)
    {
        this.dyn = dyn;
        this.rng = rng;
        offset = 0.0;
        reset();
    }

    //--------------------------------------------------------------------------

    /**
     * Returns the gyroscope offset used for
     * shifting the gyroscope measurements in the "reality of the simulator".
     * @return real gyroscope offset
     */
    public double realOffset()
    {
        return offset;
    }
    
    /**
     * Puts the sensor into an initial state (for an experiment restart).
     */
    public void reset()
    {
        dyn.reset();
        Parameters params = dyn.parameters();

        // generating the real bias
        offset = 0.0;
        if (0 < dyn.offsetN())
        {
            for (int i = 0; i < dyn.offsetN(); ++i)
                offset += generateNoise(0.005);
            offset /= dyn.offsetN();
        }
        offset += params.offset();

        System.out.println("mean gyroscope offset : " + params.offset());
        System.out.println("real gyroscope offset : " + offset);
    }

    /**
     * Returns the current gyroscope reading
     * shifted with an offset and perturbed by some noise.
     * @param avel true angular velocity (deg/sec)
     * @param dt elapsed time since the last reading (sec)
     * @return current (biased and noisy) gyroscope reading (deg/sec)
     */
    public int nextValue(double avel, double dt)
    {
        double value = dyn.nextValue(avel, dt);
        return (int)Math.round(value + offset + generateNoise(dt));
    }

    //--------------------------------------------------------------------------
    
    /**
     * Generates gyroscope measurement noise.
     */
    private double generateNoise(double dt)
    {
        Parameters params = dyn.parameters();
        return rng.nextGaussian() * params.std() * Math.sqrt(dt);
    }
    
    //--------------------------------------------------------------------------

    private Random rng;
    private double offset;
    private GyroDynamics dyn;
}
