package ode_new;

import helper.VectorCache;
import linalg.Vector;

/**
 * An abstract ODE implementation which
 * provides a rotating cache to store the temporary computation results.
 */
public abstract class CachedODE implements ODE
{
    /**
     * Create the ODE cache.
     * @param cacheSize size of the cache into which the temporary calculation
     *        should fit (eg.: should be set to 4 for RK4)
     * @param xDim dimension of the ODE state (x)
     */
    public CachedODE(int cacheSize, int xDim)
    {
        cache = new VectorCache(cacheSize, xDim);
    }
    
    /**
     * @return the next cache vector
     *         into which the temporary calculation result can be stored
     */
    protected Vector nextCachedVector() { return cache.next(); }
    
    //--------------------------------------------------------------------------
    
    private final VectorCache cache;
}
