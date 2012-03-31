package control;

import java.util.Set;

import model.State;
import sensor.GyroDynamics;
import sensor.Observation;
import visual.Keyboard.Key;

/**
 * Robot controller.
 */
public abstract class Controller
{
    public Controller(boolean isNullState,
                      GyroDynamics.Parameters gyroParams)
    {
        this.isNullState = isNullState;
        this.gyroParams = gyroParams;
        this.sleep = 10;
    }

    //--------------------------------------------------------------------------

    /**
     * Shows whether the controller will receive <code>null</code> states.
     */
    public boolean isNullState()
    {
        return isNullState;
    }

    /**
     * The controller knows the gyroscope model parameters.
     * @return gyroscope parameters
     */
    public GyroDynamics.Parameters gyroParameters()
    {
        return gyroParams;
    }

    /**
     * Returns the elapsed time since the last step in seconds.
     * @return elapsed time since last step (sec)
     */
    public double dtSec()
    {
        return ((double)dt()) / 1000.0;
    }

    /**
     * Returns the elapsed time since the last step in milliseconds.
     * @return elapsed time since last step (msec)
     */
    public int dt() { return t - prevT; }

    //--------------------------------------------------------------------------

    /**
     * Resets the controller (for a new experiment).
     */
    public void reset()
    {
        prevT = 0;
        t = 0;
    }

    /**
     * Returns the control action for the robot.
     * @param obs sensor observations
     * @param state true state (can be null, see SegwaySim.ENABLE_NULL_STATE!)
     * @return control action
     */
    public abstract Action computeControl(Observation obs, State state);

    /**
     * Allows some remote control on the robot.
     * Called just before the Controller.step function.
     * @param keys pressed keys
     */
    public void keyControl(Set<Key> keys)
    {
    }

    /**
     * Performs a simulation step on the robot.
     * @param obs sensor observations
     * @param state true state (can be null, see SegwaySim.ENABLE_NULL_STATE!)
     * @return control action
     */
    public Action step(Observation obs, State state)
    {
        prevT = t;
        t = obs.time();
        
        Action action = computeControl(obs, state);
        return action;
    }

    /**
     * Called after the experiments.
     */
    public void cleanup()
    {
    }

    //--------------------------------------------------------------------------

    /** @return sleeping time until the next step (msec) */
    public int sleep() { return sleep; }

    /**
     * Sets the sleeping time until the next step (msec)
     * @param sleeping time until the next step (msec)
     */
    public void sleep(int sleep) { this.sleep = sleep; }

    //--------------------------------------------------------------------------

    /**
     * Indicates whether null or real state
     * will be provided to the control function.
     */
    private boolean isNullState;

    /**
     * Gyroscope model parameters.
     */
    private GyroDynamics.Parameters gyroParams;

    /**
     * Time of the current simulation step (msec).
     */
    private int t;

    /**
     * Time of the last simulation step (msec).
     */
    private int prevT;

    /**
     * Sleeping time until the next step (msec).
     */
    private int sleep;
}
