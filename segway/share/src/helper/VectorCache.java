package helper;

import linalg.Vector;

/**
 * A simple rotating vector caching array.
 */
public class VectorCache
{
    public VectorCache(int cacheSize, int vectorDim)
    {
        cacheIdx = 0;
        cache = new Vector[cacheSize];
        for (int i = 0; i < cache.length; ++i)
            cache[i] = Vector.zero(vectorDim);
    }
    
    //--------------------------------------------------------------------------
    
    /** @return next vector in the cache */
    public Vector next()
    {
        Vector v = cache[cacheIdx];
        if (cache.length <= ++cacheIdx) cacheIdx = 0;
        return v;
    }
    
    //--------------------------------------------------------------------------
    
    private int cacheIdx;
    private final Vector[] cache;
}
