package helper;

import java.util.Random;

import linalg.Vector;

/**
 * Statistics helper functions.
 */
public abstract class Statistics
{
    /** @return random number sampled from U(min,max) */
    public static double uniform(Random rng, double min, double max)
    {
        return min + rng.nextDouble() * (max - min);
    }
    
    /** @return random number sampled from U(range(0),range(1)) */
    public static double uniform(Random rng, Vector range)
    {
        return uniform(rng, range.get(0), range.get(1));
    }
    
    //--------------------------------------------------------------------------
    
    /** @return random number sampled from N(mean,std^2) */
    public static double gaussian(Random rng, double mean, double std)
    {
        return mean + rng.nextGaussian() * std;
    }
}
