package control;

/**
 * Representation of an action requested by a controller.
 */
public class Action
{
    public Action(double powerL, double powerR)
    {
        this(powerL, powerR, false);
    }

    public Action(double powerL, double powerR, boolean exit)
    {
        this.powerL = powerL;
        this.powerR = powerR;
        this.exit = exit;
    }

    //--------------------------------------------------------------------------

    /**
     * Returns the applied power on the left motor.
     */
    public double lPower()
    {
        return powerL;
    }

    /**
     * Returns the applied power on the right motor.
     */
    public double rPower()
    {
        return powerR;
    }

    /**
     * Returns the termination flag of the experiment.
     * If it is true, the experiment will be finished.
     */
    public boolean isExit()
    {
        return exit;
    }

    //--------------------------------------------------------------------------

    @Override
    public String toString()
    {
        return "A[l" + lPower() + ", r:" + rPower() + "]";
    }

    //--------------------------------------------------------------------------

    private final double powerL;
    private final double powerR;
    private final boolean exit;
}
