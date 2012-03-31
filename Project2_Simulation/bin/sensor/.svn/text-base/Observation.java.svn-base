package sensor;

/**
 * Representation of sensor observations.
 */
public class Observation
{
    public Observation(int time,
                       int gyroValue,
                       int lWheelRotCtr,
                       int rWheelRotCtr)
    {
        this.time = time;
        this.gyroValue = gyroValue;
        this.lWheelRotCtr = lWheelRotCtr;
        this.rWheelRotCtr = rWheelRotCtr;
    }

    //--------------------------------------------------------------------------

    /**
     * Simulation time (milliseconds).
     */
    public int time()
    {
        return time;
    }

    /**
     * Sensed gyroscope value (deg/sec).
     * One degree/second accuracy.
     */
    public int gyroValue()
    {
        return gyroValue;
    }

    /**
     * Left wheel rotation counter (deg) with one degree accuracy.
     */
    public int lWheelRotCtr()
    {
        return lWheelRotCtr;
    }

    /**
     * Right wheel rotation counter (deg) with one degree accuracy.
     */
    public int rWheelRotCtr()
    {
        return rWheelRotCtr;
    }

    //--------------------------------------------------------------------------

    @Override
    public String toString()
    {
        return "O[t:" + time + ", g:" + gyroValue +
               ", l:" + lWheelRotCtr + ", r:" + rWheelRotCtr + "]";
    }

    //--------------------------------------------------------------------------

    private final int time;
    private final int gyroValue;
    private final int lWheelRotCtr;
    private final int rWheelRotCtr;
}
