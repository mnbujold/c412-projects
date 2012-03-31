package model.scene;

import geom3d.Brick;
import geom3d.Parallelepiped;
import geom3d.Point3D;
import model.scene.SceneModel.DistanceResult;
import model.sensor.DistanceSensorConfig;

/**
 * Model of a laser beam emitted by an IR sensor.
 */
public class LaserBeam extends AbstractSceneModelObject
{
    public LaserBeam(DistanceSensorConfig cfg, Segway segway, SceneModel scene)
    {
        this.cfg = cfg;
        this.segway = segway;
        this.scene = scene;
        
        result = new DistanceResult();
        scale = new Point3D(1.0, 1.0, 1.0);
        
        double s = 3.0;
        bricks = new Parallelepiped[]{new Brick(new Point3D(0.0, -s/2, -s/2),
                                                1.0, s, s)};
        brickColors = new Color[]{new Color(140, 20, 20)};
    }

    //--------------------------------------------------------------------------

    /** @return the result of the last distance measurement update */
    public DistanceResult result() { return result; }

    /**
     * Update the beam ray based on the segway's position and orientation.
     */
    public void update()
    {
        scene.realDistance(cfg,
                           segway.position(),
                           segway.pitch(),
                           segway.yaw(),
                           result);
        
        scale.setX(result.distance());
    }
    
    //--------------------------------------------------------------------------
    
    @Override
    public Parallelepiped[] parallelepipedObjects() { return bricks; }

    @Override
    public Color[] parallelepipedColors() { return brickColors; }
    
    @Override
    public boolean canBeHit() { return false; }
    
    //--------------------------------------------------------------------------

    @Override
    public int numDynamicTransforms(int di) { return 2; }

    @Override
    public Point3D scale(int di, int ti) { return (0 == ti) ? scale : null; }
    
    @Override
    public double pitch(int di, int ti)
    { return (0 == ti) ? cfg.pitch() : segway.pitch(); }
    
    @Override
    public double yaw(int di, int ti)
    { return (0 == ti) ? cfg.yaw() : segway.yaw(); }

    @Override
    public Point3D translate(int di, int ti)
    { return (0 == ti) ? cfg.position() : segway.position(); }
    
    //--------------------------------------------------------------------------
    
    private final DistanceResult result;
    private final DistanceSensorConfig cfg;
    private final Segway segway;
    private final SceneModel scene;
    private final Point3D scale;
    
    private final Parallelepiped[] bricks;
    private final Color[] brickColors;
}
