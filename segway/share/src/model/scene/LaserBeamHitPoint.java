package model.scene;

import model.scene.SceneModel.DistanceResult;
import geom3d.Brick;
import geom3d.Parallelepiped;
import geom3d.Point3D;

/***
 * Represents the hitting point of a laser beam.
 */
public class LaserBeamHitPoint extends AbstractSceneModelObject
{
    public LaserBeamHitPoint(LaserBeam laserBeam)
    {
        this.laserBeam = laserBeam;
        
        double s = 7.0;
        bricks = new Parallelepiped[]{new Brick(new Point3D(-s/2, -s/2, -s/2),
                                                s, s, s)};
        brickColors = new Color[]{new Color(255, 0, 0)};
    }
    
    //--------------------------------------------------------------------------
    
    /**
     * @return hit point in the scene by the related laser beam
     *         (null if nothing is hit)
     */
    public Point3D hitPoint()
    {
        DistanceResult result = laserBeam.result();
        return result.isHit() ? result.hitPoint() : null;
    }
    
    /** Enable/disable the beam hit point based on the beam's status. */
    public void update()
    {
        setEnabled(laserBeam.result().isHit());
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
    public Point3D translate(int di, int ti) { return hitPoint(); }
    
    //--------------------------------------------------------------------------
    
    private final LaserBeam laserBeam;
    private final Parallelepiped[] bricks;
    private final Color[] brickColors;
}
