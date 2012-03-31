package comm;

import run.NXTRobotThread;
import lejos.nxt.comm.BTConnection;
import lejos.nxt.comm.Bluetooth;
import lejos.util.Delay;

/**
 * NXT robot communicator.
 */
public final class NXTRobotCommunicator
             extends NXTRobotThread implements Communicator
{
    public NXTRobotCommunicator(CommunicatorLogic logic)
    {
        super ("nxt-robot-comm(" + Bluetooth.getFriendlyName() + ")",
               new NXTRobotCommunicatorLogic(logic), true);
        
        channel = null;
    }
    
    //--------------------------------------------------------------------------

    @Override
    public String device() { return Bluetooth.getFriendlyName(); }
    
    @Override
    public synchronized boolean isConnected()
    { return (channel != null); }
    
    @Override
    public synchronized Channel channel() { return channel; }
    
    //--------------------------------------------------------------------------
    
    // TODO refactor this !!!
    
    private static class NXTRobotCommunicatorLogic extends CommunicatorLogic
    {
        public NXTRobotCommunicatorLogic(CommunicatorLogic logic)
        { nestedLogic = logic; }
        
        @Override
        public void initalize() throws Exception { nestedLogic.initalize(); }
        
        @Override
        public void logic() throws Exception { nestedLogic.logic(); }
        
        @Override
        public void run()
        {
            final NXTRobotCommunicator comm = (NXTRobotCommunicator)comm();
            nestedLogic.setThread(comm);
            
            BTConnection btc = null;
            NXTRobotChannel channel = null;
            while (true)
            {
                btc = Bluetooth.waitForConnection();
                Delay.msDelay(200);
                try
                {
                    channel = new NXTRobotChannel(btc);
                    synchronized (comm) { comm.channel = channel; }
                    initalize();
                    while (comm.isRunning()) logic();
                }
                catch (Exception e) {}
                finally
                {
                    channel().close();
                    synchronized (comm) { comm.channel = null; }
                    btc.close();
                }
                Delay.msDelay(1000);
            }
        }
        
        private final CommunicatorLogic nestedLogic;
    }
    
    //--------------------------------------------------------------------------
    
    private Channel channel;
}
