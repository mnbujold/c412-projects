package visual.engine;

import geom3d.Point3D;
import visual.Constant;
import model.scene.Color;

/**
 * Visualization of geom3d.Parallelogram.
 * Its normal vector (for lighting) is set by u().xprod(v()).
 */
public class Parallelogram extends AbstractEngineObject
{
    public Parallelogram(geom3d.Parallelogram parallelogram, Color color)
    {
        this.parallelogram = parallelogram;
        this.color = color;
    }
    
    @Override
    Data[] quadData()
    {
        // normal vector to be used for lighting
        Point3D n = parallelogram.u().xprod(parallelogram.v()).normalize2();
        
        float[] vertices = new float[4 * Constant.VERTEX_FLOATSIZE];
        float[] normals = new float[4 * Constant.NORMAL_FLOATSIZE];
        int i = 0, j;
        for (Point3D p : parallelogram.points())
        {
            for (j = 0; j < n.length(); ++j)
            {
                vertices[i] = (float) p.get(j);
                normals[i] = (float) n.get(j);
                ++i;
            }
        }
        
        int[] indices = {0,1,2,3};
        
        return new Data[]{new Data(vertices, normals, color, indices)};
    }
    
    //--------------------------------------------------------------------------
    
    private final geom3d.Parallelogram parallelogram;
    private final Color color;
}
