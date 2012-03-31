package visual.engine;

/**
 * Proxy interface of the visualization engine to the public.
 */
public abstract class Engine
{
    /**
     * Initialize the engine.
     * Should be called before any registration happens.
     */
    public static final void initialize()
    { BufferManager.instance().init(); }
    
    /**
     * Close engine object registration.
     * Should be called before any drawing attempt happens.
     */
    public static final void closeRegistration()
    { BufferManager.instance().closeRegistration(); }
    
    /**
     * Enable VBO buffers for drawing.
     */
    public static final void enableVBOs()
    { BufferManager.instance().enableVBOs(); }
    
    /**
     * Disable drawing VBO buffers.
     */
    public static final void disableVBOs()
    { BufferManager.instance().disableVBOs(); }
}
