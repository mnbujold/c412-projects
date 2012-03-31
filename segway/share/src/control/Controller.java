package control;

/**
 * A shared implementation of a general controller.
 */
public abstract class Controller
{
    public Controller()
    {
        isTerminated = false;
    }
    
    /** @return true if the PC controller is terminated */
    public boolean isTerminated()
    { synchronized (isTerminated) { return isTerminated; } }

    //--------------------------------------------------------------------------
    
    /** Initialization. */
    public void initialize() throws Exception
    { setIsTerminated(false); }
    
    /** One control step. */
    abstract public void control() throws Exception;
    
    /** Terminate the controller. */
    public void terminate()
    { setIsTerminated(true); }
    
    //--------------------------------------------------------------------------
    
    private void setIsTerminated(boolean b)
    { synchronized (isTerminated) { isTerminated = b; } }
    
    //--------------------------------------------------------------------------
    
    private Boolean isTerminated;
}
