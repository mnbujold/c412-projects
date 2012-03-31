package run;

/**
 * Common implementation parts of threads.
 */
public abstract class AbstractThread implements Thread
{
    public AbstractThread(String name, ThreadLogic logic)
    {
        this.name = name;
        this.logic = logic;
        logic.setThread(this);
        isRunning = false;
    }
    
    //--------------------------------------------------------------------------
    
    @Override
    public final String name() { return name; }
    
    @Override
    public final ThreadLogic logicObject() { return logic; }
    
    //--------------------------------------------------------------------------
    
    @Override
    public final boolean isRunning()
    { synchronized (mutex) { return isRunning; } }
    
    /** Set the running flag to the specified value. */
    protected void setIsRunning(boolean b)
    { synchronized (mutex) { isRunning = b; } }
    
    //--------------------------------------------------------------------------
    
    private boolean isRunning;
    
    private final String name;
    private final ThreadLogic logic;
    
    private final Boolean mutex = true;
}
