package model.scene;

import geom3d.Brick;
import geom3d.Parallelepiped;
import geom3d.Point3D;

/**
 * Model of a segway (without wheels).
 */
public class Segway extends AbstractSceneModelObject
{
    public Segway(double wheelRadius, double wheelWidth,
                  double bodyWidth, double bodyHeight, double bodyDepth)
    {
        double frontBodyDepth = 0.75 * bodyDepth;
        double backBodyDepth = 0.25 * bodyDepth;
        double lcdWidth = 0.7 * bodyWidth;
        double lcdHeight = 0.3 * bodyHeight;
        double lcdZ = 0.55 * bodyHeight;
        
        bricks = new Parallelepiped[]{
                     // body front
                     new Brick(new Point3D(-bodyDepth/2 + backBodyDepth,
                                         -bodyWidth/2,
                                         0.0),
                               frontBodyDepth, bodyWidth, bodyHeight),
                     // body back
                     new Brick(new Point3D(-bodyDepth/2,
                                         -bodyWidth/2,
                                         0.0),
                               backBodyDepth, bodyWidth, bodyHeight),
                     // LCD
                     new Brick(new Point3D(bodyDepth/2,
                                         -lcdWidth/2,
                                         lcdZ),
                               0.5, lcdWidth, lcdHeight)
        };
        brickColors = new Color[]{new Color(200, 200, 200)   // body front
                                , new Color(50, 50, 50)      // body back
                                , new Color(175, 175, 225)}; // LCD
        
        this.wheelRadius = wheelRadius;
        this.wheelWidth = wheelWidth;
        this.bodyWidth = bodyWidth;
        
        position = new Point3D();
        pitch = 0.0;
        yaw = 0.0;
    }
    
    //--------------------------------------------------------------------------
    
    public double wheelRadius() { return wheelRadius; }
    public double wheelWidth() { return wheelWidth; }
    public double bodyWidth() { return bodyWidth; }

    public Point3D position() { return position; }
    public double pitch() { return pitch; }
    public double yaw() { return yaw; }
    
    /** Set current position (by axle midpoint), pitch and yaw angles (rad). */
    public void update(Point3D position, double pitch, double yaw)
    {
        this.position.set(position.x(),
                          position.y(),
                          position.z() + wheelRadius);
        this.pitch = pitch;
        this.yaw = yaw;
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
    public double pitch(int di, int ti) { return pitch; }
    
    @Override
    public double yaw(int di, int ti) { return yaw; }
    
    @Override
    public Point3D translate(int di, int ti) { return position; }
    
    //--------------------------------------------------------------------------

    private final Parallelepiped[] bricks;
    private final Color[] brickColors;
    
    private final double wheelRadius, wheelWidth, bodyWidth;
    
    private final Point3D position;
    private double pitch, yaw;
}
