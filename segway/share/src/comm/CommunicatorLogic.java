package comm;

import run.Thread;
import run.ThreadLogic;

/**
 * Shared implementation of the logic of a communicator thread.
 */
public abstract class CommunicatorLogic extends ThreadLogic
{
    /** @return communicator thread */
    public final Communicator comm() { return comm; }
    
    /** @return communication channel */
    public final Channel channel() { return comm().channel(); }

    //--------------------------------------------------------------------------
    
    /** Initialization (just before running). */
    public void initalize() throws Exception {}
    
    /** Implement the logic (run in an infinite cycle). */
    public abstract void logic() throws Exception;
    
    @Override
    public void run()
    {
        try
        {
            initalize();
            while (comm().isConnected()) logic();
        }
        catch (Exception e) {}
    }
    
    //--------------------------------------------------------------------------
    
    @Override
    public void setThread(Thread thread)
    {
        if (thread instanceof Communicator)
        {
            super.setThread(thread);
            if (comm != null)
                throw new IllegalStateException(
                          "Communicator thread cannot be changed!");
            comm = (Communicator)thread;
        }
        else throw new IllegalStateException(
                       "Only communicator thread can be set!");
    }
    
    //--------------------------------------------------------------------------

    private Communicator comm;
}
