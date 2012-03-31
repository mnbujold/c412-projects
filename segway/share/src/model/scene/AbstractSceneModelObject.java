package model.scene;

import geom3d.Cylinder;
import geom3d.HalfLine;
import geom3d.Parallelepiped;
import geom3d.Point3D;

/**
 * Common implementation of scene model objects.
 */
public abstract class AbstractSceneModelObject implements SceneModelObject
{
    public AbstractSceneModelObject()
    {
        isEnabled = true;
    }
    
    //--------------------------------------------------------------------------
    
    @Override
    public geom3d.Parallelogram[] parallelogramObjects() { return null; };
    
    @Override
    public Color[] parallelogramColors() { return null; }
    
    @Override
    public Parallelepiped[] parallelepipedObjects() { return null; }
    
    @Override
    public Color[] parallelepipedColors() { return null; }
    
    @Override
    public Cylinder[] cylinderObjects() { return null; }
    
    @Override
    public Color[] cylinderColors() { return null; }
    
    //--------------------------------------------------------------------------
    
    @Override
    public boolean isEnabled() { return isEnabled; }
    
    @Override
    public void setEnabled(boolean b) { isEnabled = b; }
    
    //--------------------------------------------------------------------------
    
    @Override
    public boolean canBeHit() { return true; }
    
    @Override
    public double hitAt(HalfLine ray, Point3D result)
    {
        double d = Double.POSITIVE_INFINITY;
        if (!canBeHit()) return d;
        
        // TODO support dynamic and cylinder objects
        assert (1 == numDraws() && 0 == numDynamicTransforms(0) &&
                null == cylinderObjects());
        
        double dist2sq;
        double x = 0.0, y = 0.0, z = 0.0;
        final Point3D rayP = ray.startPoint();
        if (null != parallelepipedObjects())
        {
            for (Parallelepiped brick : parallelepipedObjects())
            {
                if (null != brick.intersection(ray, result))
                {
                    dist2sq = rayP.distance2sq(result);
                    if (dist2sq < d)
                    {
                        d = dist2sq;
                        x = result.x();
                        y = result.y();
                        z = result.z();
                    }
                }
            }
        }
        if (!Double.isInfinite(d)) result.set(x, y, z);
        return Math.sqrt(d);
    }
    
    //--------------------------------------------------------------------------

    @Override
    public boolean isLighted() { return true; };
    
    @Override
    public int numDraws() { return 1; }
    
    @Override
    public int[] parallelogramIndices(int di) { return null; }
    
    @Override
    public int[] parallelepipedIndices(int di) { return null; }
    
    @Override
    public int[] cylinderIndices(int di) { return null; }
    
    @Override
    public int numDynamicTransforms(int di) { return 0; }

    @Override
    public Point3D scale(int di, int ti) { return null; }
    
    @Override
    public double pitch(int di, int ti) { return 0; }
    
    @Override
    public double yaw(int di, int ti) { return 0; }
    
    @Override
    public Point3D translate(int di, int ti) { return null; }
    
    //--------------------------------------------------------------------------
    
    private boolean isEnabled;
}
