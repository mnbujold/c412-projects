package run;

import simulator.Simulator;

/**
 * Simulated thread.
 */
public class SimulatedThread extends AbstractThread
{
    public SimulatedThread(Simulator sim,
                           String name,
                           ThreadLogic logic)
    {
        super (name, logic);
        this.sim = sim;
        
        realThread = new java.lang.Thread(new Runner());
        realThread.setDaemon(true);
        
        nextTime = currentTime = (long)(sim.time() * 1000.0);
    }
    
    //--------------------------------------------------------------------------
    
    protected final Simulator sim() { return sim; }
    
    /** @return next simulation time when the entity should be notified */
    public final double nextTime()
    { synchronized (nextTimeMutex) { return nextTime; } }
    
    private final void setNextTime(double nextTime)
    {
        synchronized (nextTimeMutex) { this.nextTime = nextTime; }
        synchronized (this) { notifyAll(); }
    }

    /** @return true if the entity is active (e.g. not waiting) */
    public final boolean isEnabled()
    { synchronized (nextTimeMutex) { return (0.0 <= nextTime); } }
    
    /** Set the active status of the entity. */
    public final void setEnabled(boolean b)
    {
        if (b) synchronized (sim())
               {
                   observe();
                   setNextTime(sim().time());
               }
        else setNextTime(-1.0);
    }
    
    protected void observe()
    { synchronized (sim()) { currentTime = (long)(sim().time() * 1000.0); } }
    
    //--------------------------------------------------------------------------
    
    @Override
    public final long currentTimeMillis() { return currentTime; }
    
    @Override
    public final void msDelay(int millis)
    {
        double nextTime = sim().time() + millis/1000.0;
        setNextTime(nextTime);
        
        while (isRunning())
        {
            synchronized (sim())
            {
                if (sim().time() >= nextTime) break;
                
                try { sim().wait(); }
                catch (InterruptedException e)
                { e.printStackTrace(System.err); }
            }
        }
        observe();
    }
    
    @Override
    protected void setIsRunning(boolean b)
    {
        super.setIsRunning(b);
        synchronized (this) { notifyAll(); }
    }
    
    @Override
    public void start() { realThread.start(); }
    
    @Override
    public Thread spawn(String name, ThreadLogic logic)
    { return new SimulatedThread(sim(), name, logic); }

    //--------------------------------------------------------------------------

    private class Runner implements Runnable
    {
        @Override
        public void run()
        {
            setIsRunning(true);
            sim.registerThread(SimulatedThread.this);
            logicObject().run();
            setIsRunning(false);
        }
    }
    
    //--------------------------------------------------------------------------
    
    // The currentTime does not have to be protected by synchronization
    // as a (robot/pc) controller runs exclusively with the simulator
    // guarded by nextTime (which is hence protected).
    
    private long currentTime;
    private double nextTime;
    
    private final Simulator sim;
    private final java.lang.Thread realThread;
    
    private final Boolean nextTimeMutex = true;
}
