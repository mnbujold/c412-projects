package model.scene;

import geom3d.Brick;
import geom3d.Parallelepiped;
import geom3d.Point3D;

/**
 * Model of walls around the floor.
 */
public class Walls extends AbstractSceneModelObject
{
    public Walls(Floor floor, double height, double thickness)
    {
        this.height = height;
        this.thickness = thickness;
        double t = thickness();
        double w = floor.width();
        double h = floor.height();
        
        bricks = new Parallelepiped[]{
                     new Brick(new Point3D(-t, -t, -t), w+2*t, t, t+height),
                     new Brick(new Point3D(-t, h, -t),  w+2*t, t, t+height),
                     new Brick(new Point3D(-t, 0, -t),  t,     h, t+height),
                     new Brick(new Point3D(w, 0, -t),   t,     h, t+height)
        };
        colors = new Color[]{new Color(65, 61, 53)
                           , new Color(65, 61, 53)
                           , new Color(65, 61, 53)
                           , new Color(65, 61, 53)};
    }
    
    public double height() { return height; }
    public double thickness() { return thickness; }
    
    @Override
    public Parallelepiped[] parallelepipedObjects() { return bricks; }

    @Override
    public Color[] parallelepipedColors() { return colors; }

    //--------------------------------------------------------------------------

    private final double height, thickness;
    
    private final Parallelepiped[] bricks;
    private final Color[] colors;
}
