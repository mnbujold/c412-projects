package comm;

import run.Thread;

/**
 * Interface of a communicator thread.
 */
public interface Communicator extends Thread
{
    /**
     * @return name of the device
     *         which the communicator is connected to/from (PC side/robot side)
     */
    String device();
    
    /** @return true if the communicator is connected and the channel is ready */
    boolean isConnected();

    /** @return communication channel */
    Channel channel();
}
