package model.scene;

import geom3d.Brick;
import geom3d.Parallelepiped;
import geom3d.Point3D;
import helper.Ratio;

/**
 * Model of a point with fixed position.
 */
public class FixedPoint extends AbstractSceneModelObject
{
    public FixedPoint(Point3D position)
    {
        this.position = position;
        
        double w = 15, l = 70;
        bricks = new Parallelepiped[]{new Brick(new Point3D(-l/2, -w/2, 0),
                                                l, w, 1)
                                          .rotateZ(45 * Ratio.DEG_TO_RAD)
                                          .translate(position),
                                      new Brick(new Point3D(-w/2, -l/2, 0),
                                                w, l, 1)
                                          .rotateZ(45 * Ratio.DEG_TO_RAD)
                                          .translate(position)};
        
        brickColors = new Color[]{new Color(150, 125, 85),
                                  new Color(150, 125, 85)};
    }
    
    public Point3D position() { return position; }
    
    //--------------------------------------------------------------------------
    
    @Override
    public Parallelepiped[] parallelepipedObjects() { return bricks; }
    
    @Override
    public Color[] parallelepipedColors() { return brickColors; }
    
    @Override
    public boolean canBeHit() { return false; }
    
    //--------------------------------------------------------------------------
    
    private final Point3D position;
    private final Parallelepiped[] bricks;
    private final Color[] brickColors;
}
