package comm;

import java.io.IOException;

import run.NXTPCThread;

import lejos.pc.comm.NXTConnector;
import lejos.util.Delay;

/**
 * NXT PC communicator.
 */
public final class NXTPCCommunicator extends NXTPCThread implements Communicator
{
    public NXTPCCommunicator(String device, CommunicatorLogic logic)
    {
        super ("nxt-pc-comm(" + device + ")",
               new NXTPCCommunicatorLogic(logic), true);
        
        this.device = device;
        channel = null;
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
    
    private static class NXTPCCommunicatorLogic extends CommunicatorLogic
    {
        public NXTPCCommunicatorLogic(CommunicatorLogic logic)
        { nestedLogic = logic; }
        
        @Override
        public void initalize() throws Exception { nestedLogic.initalize(); }
        
        @Override
        public void logic() throws Exception { nestedLogic.logic(); }
        
        @Override
        public void run()
        {
            final NXTPCCommunicator comm = (NXTPCCommunicator)comm();
            nestedLogic.setThread(comm);
            
            NXTConnector conn = new NXTConnector();
            NXTPCChannel channel = null;
            try
            {
                System.out.println("Connecting to " + comm.device() + "...");
                if (conn.connectTo("btspp://" + comm.device()))
                {
                    System.out.println("Connected to " + comm.device() + ".");
                    Delay.msDelay(50);
                    channel = new NXTPCChannel(conn);
                    synchronized (comm) { comm.channel = channel; }
                    System.out.println("Channel is ready.");
                    initalize();
                    while (comm.isRunning()) logic();
                }
                else { System.err.println("Failed to connect to "
                                          + comm.device() + "!"); }
            }
            catch (IOException e) {}
            catch (Exception e) { e.printStackTrace(System.err); }
            finally
            {
                channel().close();
                synchronized (comm) { comm.channel = null; }
                System.out.println("Channel is closed.");
                try { conn.close(); } catch (IOException e) {}
            }
            Delay.msDelay(1000);
        }
        
        private final CommunicatorLogic nestedLogic;
    }
    
    //--------------------------------------------------------------------------
    
    private final String device;
    private Channel channel;
}
