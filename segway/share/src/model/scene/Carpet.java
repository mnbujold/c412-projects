package model.scene;

import geom3d.Brick;
import geom3d.Parallelepiped;
import geom3d.Point3D;

/**
 * Model of the table carpet.
 */
public class Carpet extends AbstractSceneModelObject
{
    public Carpet(double x, double y, double sizeX, double sizeY, double height)
    {
        bricks = new Parallelepiped[]{
                     new Brick(new Point3D(x, y, 0), sizeX, sizeY, height)
        };
        brickColors = new Color[]{new Color(30, 100, 30)};
        
        this.height = height;
        xMin = x; xMax = x + sizeX;
        yMin = y; yMax = y + sizeY;
    }
    
    public double height() { return height; }
    
    public double xMin() { return xMin; }
    public double xMax() { return xMax; }
    public double yMin() { return yMin; }
    public double yMax() { return yMax; }
    
    @Override
    public Parallelepiped[] parallelepipedObjects() { return bricks; }
    
    @Override
    public Color[] parallelepipedColors() { return brickColors; }

    //--------------------------------------------------------------------------
    
    private final double height, xMin, xMax, yMin, yMax;
    
    private final Parallelepiped[] bricks;
    private final Color[] brickColors;
}
