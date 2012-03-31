package run;

import lejos.util.Delay;

/**
 * Thread on the NXT robot.
 */
public class NXTRobotThread extends AbstractThread
{
    public NXTRobotThread(String name,
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
    public synchronized void start()
    { if (realThread == null) runner.run(); else realThread.start(); }

    @Override
    public Thread spawn(String name, ThreadLogic logic)
    { return new NXTRobotThread(name, logic, true); }
    
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
