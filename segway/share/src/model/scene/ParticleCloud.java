package model.scene;

import geom3d.Parallelogram;
import geom3d.Point3D;
import localize.Particle;
import localize.ParticleFilter;

/**
 * Model of a particle cloud.
 */
public class ParticleCloud extends AbstractSceneModelObject
{
    public ParticleCloud(ParticleFilter pf)
    {
        this.pf = pf;
        
        double s = 5; // particle rectangle scale
        double e = 5; // elevation (mm)
        Parallelogram rect = new Parallelogram(new Point3D(-s/2, -s/2, e),
                                               Point3D.unitX().mulL(s),
                                               Point3D.unitY().mulL(s));
        colorN = 150; // color gradient size
        rects = new Parallelogram[colorN];
        for (int i = 0; i < rects.length; ++i) rects[i] = rect;
        
        rectColors = new Color[colorN];
        float darkR = 25, darkG = 150, darkB = 150;
        float lightR = 240, lightG = 250, lightB = 125;
        for (int i = 0; i < rectColors.length; ++i)
            rectColors[i] = new Color(colorAvg(darkR, lightR, i, colorN),
                                      colorAvg(darkG, lightG, i, colorN),
                                      colorAvg(darkB, lightB, i, colorN));
        pIndices = new int[1];
    }
    
    private float colorAvg(float low, float high, int i, int n)
    { return ((n-1-i)*low + i*high) / (n-1); }
    
    //--------------------------------------------------------------------------
    
    @Override
    public Parallelogram[] parallelogramObjects() { return rects; }
    
    @Override
    public Color[] parallelogramColors() { return rectColors; }
    
    @Override
    public boolean canBeHit() { return false; }
    
    @Override
    public int numDraws()
    {
        pc = pf.particles();
        return pc == null ? 0 : pc.size();
    }

    @Override
    public int[] parallelogramIndices(int di)
    {
        pIndices[0] = (int)(p.weight() * colorN) % colorN;
        return pIndices;
    }
    
    @Override
    public int numDynamicTransforms(int di)
    {
        p = pc.get(di);
        return 1;
    }
    
    @Override
    public Point3D translate(int di, int ti) { return pp; }

    //--------------------------------------------------------------------------
    
    private final class ParticlePoint extends Point3D
    {
        @Override
        public double x() { return p.x(); }
        
        @Override
        public double y() { return p.y(); }
    }
    private ParticlePoint pp = new ParticlePoint();
    
    //--------------------------------------------------------------------------
    
    private localize.ParticleCloud pc;
    private Particle p;
    
    private final int colorN;
    private final int[] pIndices;
    private final ParticleFilter pf;
    private final Parallelogram[] rects;
    private final Color[] rectColors;
}
