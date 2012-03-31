package sensor;

/**
 * The gyroscope dynamics model.
 */
public interface GyroDynamics
{
    /**
     * Gyroscope model parameters.
     */
    interface Parameters
    {
        /**
         * Returns the gyroscope bias.
         * @return gyroscope bias
         */
        double offset();

        /**
         * The gyroscope can have a zero-mean Gaussian noise on its output
         * with the standard deviation returned by this function. The standard
         * deviation is automatically scaled with sqrt(dt), where dt is the
         * simulation time step.
         * @return noise standard deviation
         */
        double std();

        /**
         * The a1 gyroscope ODE parameter.
         * @return a1 parameter
         */
        double a1();

        /**
         * The a2 gyroscope ODE parameter.
         * @return a2 parameter
         */
        double a2();

        /**
         * The b1 gyroscope parameter.
         * @return b1 parameter
         */
        double b1();
    }

    //--------------------------------------------------------------------------

    /**
     * Number of samples used to generate the real bias.
     * If this is set to 0, the "real" bias will be equal to offset().
     */
    int offsetN();
    
    /**
     * Resets to the initial state (for an experiment restart).
     */
    void reset();

    /**
     * Returns the gyroscope model parameters.
     * @return gyroscope parameters
     */
    Parameters parameters();

    /**
     * Returns the current unbiased and noise-free gyroscope reading
     * according to the ddot{y} + a1*dot{y} + a2*y = b1*dot{u} ODE.
     * @param avel true angular velocity (deg/sec)
     * @param dt elapsed time since the last reading (sec)
     * @return current (unbiased and noise-free) gyroscope reading (deg/sec)
     */
    double nextValue(double avel, double dt);
}
