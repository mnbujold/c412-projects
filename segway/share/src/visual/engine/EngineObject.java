package visual.engine;

import geom3d.Point3D;


/**
 * Interface of visualization engine classes. 
 */
public interface EngineObject
{
    boolean isRegistered();
    void register();    
    void draw();
    
    //--------------------------------------------------------------------------
    
    /** Specifies the order of rotation along the x,y and z axis. */
    enum RotationOrder { XYZ, XZY, YXZ, YZX, ZXY, ZYX }
    
    /** @return order of rotation along the x,y and z axis */
    RotationOrder rotationOrder();
    
    /** Set the rotation order along the x,y and z axis. */
    void setRotationOrder(RotationOrder ro);

    /** @return rotation angle (rad) along the x-axis */
    float rotationX();
    
    /** Set rotation angle (rad) along the x-axis. */
    void setRotationX(float angle);
    
    /** @return rotation angle (rad) along the y-axis */
    float rotationY();
    
    /** Set rotation angle (rad) along the y-axis. */
    void setRotationY(float angle);
    
    /** @return rotation angle (rad) along the z-axis */
    float rotationZ();
    
    /** Set rotation angle (rad) along the z-axis. */
    void setRotationZ(float angle);
    
    //--------------------------------------------------------------------------
    
    /** @return translation vector */
    Point3D translation();
    
    /** Set a translation vector. */
    void setTranslation(Point3D v);
}
