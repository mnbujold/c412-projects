package model.scene;

import geom3d.Brick;
import geom3d.Cylinder;
import geom3d.Parallelepiped;
import geom3d.Point3D;

/**
 * Model of a segway wheel (with an arrow sign on it).
 */
public class Wheel extends AbstractSceneModelObject
{
    public Wheel(Segway segway, double sign)
    {
        this.segway = segway;
        
        double wheelRadius = segway.wheelRadius();
        double wheelWidth = segway.wheelWidth();
        double bodyWidth = segway.bodyWidth();
        double wheelY = sign*bodyWidth/2;// + wheelWidth/2);
        
        double signLength = 2*wheelRadius/3;
        double signWidth = wheelRadius/6;
        double signY = wheelY + sign*wheelWidth;
        
        bricks = new Parallelepiped[]{
                     // sign upper line
                     new Brick(new Point3D(-signLength/2,
                                         signY,
                                         signLength/2),
                               signLength, sign, -signWidth),
                     // sign lower line
                     new Brick(new Point3D(signLength/2-signWidth,
                                         signY,
                                         signLength/2 - signWidth),
                               signWidth, sign, signWidth - signLength)
        };
        brickColors = new Color[]{new Color(150, 150, 150),
                                  new Color(150, 150, 150)};
        
        cylinders = new Cylinder[]{
                        new Cylinder(new Point3D(0.0, wheelY, 0.0),
                                     Point3D.unitY().mulL(sign*wheelWidth),
                                     wheelRadius)
        };
        cylinderColors = new Color[]{new Color(30, 30, 30)};
    }

    //--------------------------------------------------------------------------
    
    /** Set current wheel angle (rad). */
    public void update(double theta)
    {
        this.theta = theta;
    }
    
    //--------------------------------------------------------------------------
    
    @Override
    public Parallelepiped[] parallelepipedObjects() { return bricks; }
    
    @Override
    public Color[] parallelepipedColors() { return brickColors; }

    @Override
    public Cylinder[] cylinderObjects() { return cylinders; }
    
    @Override
    public Color[] cylinderColors() { return cylinderColors; }
    
    @Override
    public boolean canBeHit() { return false; }

    @Override
    public int numDynamicTransforms(int di) { return 1; }
    
    @Override
    public double pitch(int di, int ti) { return theta; }

    @Override
    public double yaw(int di, int ti) { return segway.yaw(); }
    
    @Override
    public Point3D translate(int di, int ti) { return segway.position(); }
    
    //--------------------------------------------------------------------------
    
    private double theta;
    private Segway segway;
    
    private final Parallelepiped[] bricks;
    private final Color[] brickColors;
    
    private final Cylinder[] cylinders;
    private final Color[] cylinderColors;
}
