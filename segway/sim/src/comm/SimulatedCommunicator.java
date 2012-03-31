package comm;

import run.SimulatedThread;
import simulator.Simulator;

/**
 * Simulated communicator.
 */
public final class SimulatedCommunicator
             extends SimulatedThread implements Communicator
{
    public SimulatedCommunicator(String name,
                                 String device,
                                 CommunicatorLogic logic,
                                 Simulator sim,
                                 Channel channel)
    {
        super (sim, name, new SimulatedCommunicatorLogic(logic));
        this.device = device;
        this.channel = channel;
    }
    
    //--------------------------------------------------------------------------

    @Override
    public String device() { return device; }
    
    @Override
    public synchronized boolean isConnected() { return (channel != null); }
    
    @Override
    public synchronized Channel channel() { return channel; }
    
    //--------------------------------------------------------------------------

    // TODO refactor this !!!
    
    private static class SimulatedCommunicatorLogic extends CommunicatorLogic
    {
        public SimulatedCommunicatorLogic(CommunicatorLogic logic)
        {
            nestedLogic = logic;
        }
        
        @Override
        public void initalize() throws Exception { nestedLogic.initalize(); }

        @Override
        public void logic() throws Exception { nestedLogic.logic(); }
        
        @Override
        public void run()
        {
            final SimulatedCommunicator comm = (SimulatedCommunicator)comm();
            nestedLogic.setThread(comm);
            
            double time;
            try
            {
                comm.observe();
                initalize();
                while (comm.isRunning())
                {
                    time = comm.nextTime();
                    logic();
                    if (time == comm.nextTime())
                        msDelay((int)(comm.sim().cfg().simDT()*1000.0));
                }
            }
            catch (Exception e) { e.printStackTrace(System.err); }
        }
        
        private CommunicatorLogic nestedLogic;
    }
    
    //--------------------------------------------------------------------------
    
    private final String device;
    private final Channel channel;
}
