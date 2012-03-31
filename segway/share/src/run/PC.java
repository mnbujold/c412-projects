package run;

import comm.CommunicatorLogic;

/**
 * Interface of a real/simulated PC.
 */
public interface PC extends RobotPC
{
    /** Create and run a communicator thread. */
    void createCommunicator(String device, CommunicatorLogic logic);
}
