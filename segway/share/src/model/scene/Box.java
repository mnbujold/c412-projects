package model.scene;

import geom3d.Brick;
import geom3d.Parallelepiped;
import geom3d.Point3D;

/**
 * Model of a box.
 */
public class Box extends AbstractSceneModelObject
{
	// Position, Size x, size y, height, pitch (dont worry about), yaw
    public Box(Point3D position,
               double dX, double dY, double dZ,
               double pitch, double yaw)
    {
        Brick brick = new Brick(new Point3D(0, 0, 0), dX, dY, dZ);
        brick.rotateY(pitch);
        brick.rotateZ(yaw);
        brick.translate(position);
        
        bricks = new Parallelepiped[]{brick};
        brickColors = new Color[]{new Color(150, 125, 85)};
         
    }
    
    @Override
    public Parallelepiped[] parallelepipedObjects() { return bricks; }
    
    @Override
    public Color[] parallelepipedColors() { return brickColors; }

    //--------------------------------------------------------------------------
    
    private final Parallelepiped[] bricks;
    private final Color[] brickColors;
    
}
