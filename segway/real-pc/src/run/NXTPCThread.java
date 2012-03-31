package run;

import lejos.util.Delay;

/**
 * Thread on a real PC.
 */
public class NXTPCThread extends AbstractThread
{
    public NXTPCThread(String name,
                       ThreadLogic logic,
                       boolean runInNewThread)
    {
        super (name, logic);
        
        runner = new Runner();
        realThread = runInNewThread ? new java.lang.Thread(runner) : null;
        if (realThread != null) realThread.setDaemon(true);
    }
    
    //--------------------------------------------------------------------------
    
    @Override
    public final long currentTimeMillis() { return System.currentTimeMillis(); }

    @Override
    public final void msDelay(int millis) { Delay.msDelay(millis); }
    
    @Override
    public void start()
    { if (realThread == null) runner.run(); else realThread.start(); }
    
    @Override
    public Thread spawn(String name, ThreadLogic logic)
    { return new NXTPCThread(name, logic, true); }
    
    //--------------------------------------------------------------------------
    
    private class Runner implements Runnable
    {
        @Override
        public void run()
        {
            setIsRunning(true);
            logicObject().run();
            setIsRunning(false);
        }
    }
    
    //--------------------------------------------------------------------------
    
    private final Runner runner;
    private final java.lang.Thread realThread;
}
