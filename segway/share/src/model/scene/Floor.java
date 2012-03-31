package model.scene;

import geom3d.Brick;
import geom3d.Parallelepiped;
import geom3d.Point3D;

/**
 * Model of the floor of the scene.
 */
public class Floor extends AbstractSceneModelObject
{
    public Floor(double width, double height, double thickness)
    {
        this.thickness = thickness;
        this.width = width;
        this.height = height;
        
        bricks = new Parallelepiped[]{
                     new Brick(new Point3D(0.0, 0.0, -thickness),
                               width, height, thickness)
        };
        colors = new Color[]{new Color(65, 61, 53)};
    }
    
    public double thickness() { return thickness; }
    public double width() { return width; }
    public double height() { return height; }
    
    @Override
    public Parallelepiped[] parallelepipedObjects() { return bricks; }

    @Override
    public Color[] parallelepipedColors() { return colors; }

    //--------------------------------------------------------------------------
    
    private final double thickness, width, height;
    
    private final Parallelepiped[] bricks;
    private final Color[] colors;
}
