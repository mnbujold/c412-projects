package run;

/**
 * An abstract runnable class which can access the abstract thread routines.
 */
public abstract class ThreadLogic implements Runnable
{
    public ThreadLogic()
    {
        thread = null; // set later through setThread
    }
    
    //--------------------------------------------------------------------------
    
    /** @return the runner thread */
    public final Thread thread() { return thread; }
    
    /** Set the runner thread (for internal use only). */
    public void setThread(Thread thread)
    {
        if (this.thread != null)
            throw new IllegalStateException("Runner thread cannot be changed!");
        this.thread = thread;
    }

    //--------------------------------------------------------------------------
    
    /** Current time in milliseconds. */
    public final long currentTimeMillis()
    { return thread().currentTimeMillis(); }
    
    /** Sleep the control thread for "millis" milliseconds. */
    public final void msDelay(int millis)
    { thread().msDelay(millis); }

    //--------------------------------------------------------------------------
    
    private Thread thread;
}
