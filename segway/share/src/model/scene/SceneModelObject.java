package model.scene;

import geom3d.Cylinder;
import geom3d.HalfLine;
import geom3d.Parallelepiped;
import geom3d.Parallelogram;
import geom3d.Point3D;

/**
 * Interface of objects of the scene model.
 */
public interface SceneModelObject
{
    Parallelogram[] parallelogramObjects();
    Color[] parallelogramColors();
    
    Parallelepiped[] parallelepipedObjects();
    Color[] parallelepipedColors();
    
    Cylinder[] cylinderObjects();
    Color[] cylinderColors();
    
    /** @return true when the object is enabled */
    boolean isEnabled();
    
    /** Enable/disable the object. */
    void setEnabled(boolean b);
    
    //--------------------------------------------------------------------------
    
    /** @return true if the object can be hit by a laser beam */
    boolean canBeHit();
    
    /**
     * @return distance to hitting point by the specified "ray"
     *         The hitting point closest to the source of "ray" is
     *         placed into "result" if there is one.
     */
    double hitAt(HalfLine ray, Point3D result);

    //--------------------------------------------------------------------------
    // The dynamic behavior can be defined by a series of transformations.
    // One transformation: < scale, rotate(pitch), rotate(yaw), translate >

    /** @return true if the object has lighting effect */
    boolean isLighted();
    
    /** @return number of draws of the object */
    int numDraws();

    /**
     * @param di draw index (0 <= di < numDraws())
     * @return parallelogram indices to be drawn or null for all
     */
    int[] parallelogramIndices(int di);

    /**
     * @param di draw index (0 <= di < numDraws())
     * @return parallelepiped indices to be drawn or null for all
     */
    int[] parallelepipedIndices(int di);
    
    /**
     * @param di draw index (0 <= di < numDraws())
     * @return cylinder indices to be drawn or null for all
     */
    int[] cylinderIndices(int di);
    
    /**
     * @param di draw index (0 <= di < numDraws())
     * @return number of dynamic transformations
     */
    int numDynamicTransforms(int di);

    /**
     * @param di draw index (0 <= di < numDraws())
     * @param ti transformation index (0 <= ti < numDynamicTransforms())
     * @return scaling along the xyz axises or null
     */
    Point3D scale(int di, int ti);
    
    /**
     * @param di draw index (0 <= di < numDraws())
     * @param ti transformation index (0 <= ti < numDynamicTransforms())
     * @return pitch angle (rad), rotation around z-axis (counterclockwise)
     */
    double pitch(int di, int ti);
    
    /**
     * @param di draw index (0 <= di < numDraws())
     * @param ti transformation index (0 <= ti < numDynamicTransforms())
     * @return yaw angle (rad), rotation around y-axis (counterclockwise)
     */
    double yaw(int di, int ti);
    
    /**
     * @param di draw index (0 <= di < numDraws())
     * @param ti transformation index (0 <= ti < numDynamicTransforms())
     * @return translation vector or null
     */
    Point3D translate(int di, int ti);    
}
