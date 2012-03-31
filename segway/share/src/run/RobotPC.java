package run;

import model.motion.State;
import model.sensor.DistanceState;
import model.sensor.GyroSensor.GyroState;

import comm.Communicator;

/**
 * Shared interface content of the Robot and PC interfaces.
 */
public interface RobotPC extends Thread
{
    /** @return the communicator or null if has not been created */
    Communicator comm();
    
    //--------------------------------------------------------------------------
    
    /** @return true if the robot is simulated */
    boolean isSimulated();
    
    /** @return dynamics state (only for simulation), null otherwise */
    State simDynState();
    
    /** @return gyroscope state (only for simulation), null otherwise */
    GyroState simGyroState();

    /** @return distance sensor states (only for simulation), null otherwise */
    DistanceState[] simDistState();
}
