package run;

/**
 * Abstract interface of a Thread.
 */
public interface Thread
{
    /** @return identification name */
    String name();
    
    /** @return logic object run by the thread */
    ThreadLogic logicObject();
    
    //--------------------------------------------------------------------------
    
    /** Current time in milliseconds. */
    long currentTimeMillis();
    
    /** Sleep the control thread for "millis" milliseconds. */
    void msDelay(int millis);
    
    //--------------------------------------------------------------------------
    
    /** @return true if the entity's thread is running */
    boolean isRunning();
    
    /** Start the entity's thread. */
    void start();
    
    //--------------------------------------------------------------------------
    
    /** Create a new thread running the specified object. */
    Thread spawn(String name, ThreadLogic logic);
}
