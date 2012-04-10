package model.scene;

import geom3d.Brick;
import geom3d.Parallelepiped;
import geom3d.Point3D;
import helper.Ratio;

/**
 * Model of a point with changing position.
 */
public class DynamicPoint extends AbstractSceneModelObject
{
    public DynamicPoint(SceneModel scene)
    {
        this.scene = scene;
        position = new Point3D(-1, -1, -1);
        setEnabled(false);
        
        double w = 15, l = 70;
        bricks = new Parallelepiped[]{new Brick(new Point3D(-l/2, -w/2, 0),
                                                l, w, 1)
                                          .rotateZ(45 * Ratio.DEG_TO_RAD),
                                      new Brick(new Point3D(-w/2, -l/2, 0),
                                                w, l, 1)
                                          .rotateZ(45 * Ratio.DEG_TO_RAD)};
        
        brickColors = new Color[]{new Color(200, 25, 25),
                                  new Color(200, 25, 25)};
    }
    
    /** @return current position of the point */
    public Point3D position() { return position; }
    
    /**
     * Set the current position of the point.
     * @param position new position to be used or null to disable the point
     */
    public void setPosition(Point3D position)
    {
        synchronized (scene) // synchronize with visualization
        {
            if (position != null)
            {
                setEnabled(true);
                position.copy(this.position);
            }
            else setEnabled(false);
        }
    }
    
    //--------------------------------------------------------------------------
    
    @Override
    public Parallelepiped[] parallelepipedObjects() { return bricks; }
    
    @Override
    public Color[] parallelepipedColors() { return brickColors; }
    
    @Override
    public boolean canBeHit() { return false; }
    
    @Override
    public int numDynamicTransforms(int di) { return 1; }
    
    @Override
    public Point3D translate(int di, int ti) { return position; }
    
    //--------------------------------------------------------------------------
    
    private final Point3D position;
    private final SceneModel scene;
    private final Parallelepiped[] bricks;
    private final Color[] brickColors;
}
