package localize;

import java.util.Vector;

/**
 * Type of particle clouds.
 */
public class ParticleCloud extends Vector<Particle>
{
    public ParticleCloud(int capacity)
    {
        super (capacity);
        setSize(capacity);
    }
    
    //--------------------------------------------------------------------------
    
    private static final long serialVersionUID = 1L;
}
