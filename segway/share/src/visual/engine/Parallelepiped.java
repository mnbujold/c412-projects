package visual.engine;

import geom3d.Parallelogram;
import geom3d.Point3D;
import model.scene.Color;
import visual.Constant;

/**
 * Visualization of geom3d.Parallelepiped.
 */
public class Parallelepiped extends AbstractEngineObject
{
    public Parallelepiped(geom3d.Parallelepiped parallelepiped, Color color)
    {
        this.parallelepiped = parallelepiped;
        this.color = color;
    }

    //--------------------------------------------------------------------------
    
    @Override
    Data[] quadData()
    {
        float[] vertices = new float[24 * Constant.VERTEX_FLOATSIZE];
        float[] normals = new float[24 * Constant.NORMAL_FLOATSIZE];
        int i = 0, j, si = 0;
        for (Parallelogram side : parallelepiped.sides())
        {
            Point3D n = parallelepiped.normal(si);
            for (Point3D p : side.points())
            {
                for (j = 0; j < n.length(); ++j)
                {
                    vertices[i] = (float) p.get(j);
                    normals[i] = (float) n.get(j);
                    ++i;
                }
            }
            ++si;
        }
        
        int[] indices = {0,1,2,3,
                         4,5,6,7,
                         8,9,10,11,
                         12,13,14,15,
                         16,17,18,19,
                         20,21,22,23};
        
        return new Data[]{new Data(vertices, normals, color, indices)};
    }
    
    //--------------------------------------------------------------------------

    private final geom3d.Parallelepiped parallelepiped;
    private final Color color;
}
