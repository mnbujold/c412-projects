package visual;

import geom3d.Point3D;
import helper.Ratio;
import model.scene.Color;
import model.scene.SceneModelObject;

import org.lwjgl.opengl.GL11;

import visual.engine.Cylinder;
import visual.engine.Parallelepiped;
import visual.engine.Parallelogram;

/**
 * Representation of a visualization object.
 */
public class VisualObject
{
    /** Number of discretization points along a visualized circle. */
    private static final int CIRCLE_POINTS = 36;
    
    //--------------------------------------------------------------------------
    
    public VisualObject(SceneModelObject sceneObject)
    {
        this.sceneObject = sceneObject;
        isRegistered = false;
    }
    
    SceneModelObject sceneObject() { return sceneObject; }
    
    //--------------------------------------------------------------------------
    
    /** @return true if the object is registered to the drawing engine */
    boolean isRegistered() { return isRegistered; }
    
    /** Register the object to the drawing engine. */    
    void register()
    {
        geom3d.Parallelogram[] pObjs = sceneObject().parallelogramObjects();
        if (pObjs != null)
        {
            Color[] pColors = sceneObject().parallelogramColors();
            assert (pObjs.length == pColors.length);
            parallelogramObjects = new Parallelogram[pObjs.length];
            for (int i = 0; i < parallelogramObjects.length; ++i)
            {
                parallelogramObjects[i] =
                    new Parallelogram(pObjs[i], pColors[i]);
                parallelogramObjects[i].register();
            }
        }
        else parallelogramObjects = null;
        
        geom3d.Parallelepiped[] ppObjs = sceneObject().parallelepipedObjects();
        if (ppObjs != null)
        {
            Color[] ppColors = sceneObject().parallelepipedColors();
            assert (ppObjs.length == ppColors.length);
            parallelepipedObjects = new Parallelepiped[ppObjs.length];
            for (int i = 0; i < parallelepipedObjects.length; ++i)
            {
                parallelepipedObjects[i] =
                    new Parallelepiped(ppObjs[i], ppColors[i]);
                parallelepipedObjects[i].register();
            }
        }
        else parallelepipedObjects = null;
        
        geom3d.Cylinder[] cObjs = sceneObject().cylinderObjects();
        if (cObjs != null)
        {
            Color[] cColors = sceneObject.cylinderColors();
            assert (cObjs.length == cColors.length);
            cylinderObjects = new Cylinder[cObjs.length];
            for (int i = 0; i < cObjs.length; ++i)
            {
                cylinderObjects[i] =
                    new Cylinder(cObjs[i], CIRCLE_POINTS, cColors[i]);
                cylinderObjects[i].register();
            }
        }
        else cylinderObjects = null;
    }
    
    /** Draw all the engine objects of this visual object. */
    void draw()
    {
        if (!sceneObject().isEnabled()) return;
        
        if (!sceneObject().isLighted()) GL11.glDisable(GL11.GL_LIGHTING);
        
        int di, ti, tn;
        int[] indices;
        final int dn = sceneObject().numDraws();
        for (di = 0; di < dn; ++di)
        {
            tn = sceneObject().numDynamicTransforms(di);
            if (0 < tn)
            {
                Point3D p;
                double pitch, yaw;
                
                GL11.glPushMatrix();            
                for (ti = tn-1; ti >= 0; --ti)
                {
                    p = sceneObject().translate(di, ti);
                    if (p != null)
                        GL11.glTranslated(p.x(), p.y(), p.z());
                    
                    yaw = sceneObject().yaw(di, ti);
                    if (yaw != 0.0)
                        GL11.glRotated(yaw * Ratio.RAD_TO_DEG, 0, 0, 1);
                    
                    pitch = sceneObject().pitch(di, ti);
                    if (pitch != 0.0)
                        GL11.glRotated(pitch * Ratio.RAD_TO_DEG, 0, 1, 0);
                    
                    p = sceneObject().scale(di, ti);
                    if (p != null)
                        GL11.glScaled(p.x(), p.y(), p.z());
                }
            }
            
            if (parallelogramObjects != null)
            {
                indices = sceneObject().parallelogramIndices(di);
                if (indices == null)
                    for (Parallelogram eo : parallelogramObjects) eo.draw();
                else
                    for (int i : indices) parallelogramObjects[i].draw();
            }
            
            if (parallelepipedObjects != null)
            {
                indices = sceneObject().parallelepipedIndices(di);
                if (indices == null)
                    for (Parallelepiped eo : parallelepipedObjects) eo.draw();
                else
                    for (int i : indices) parallelepipedObjects[i].draw();
            }
            
            if (cylinderObjects != null)
            {
                indices = sceneObject().cylinderIndices(di);
                if (indices == null)
                    for (Cylinder eo : cylinderObjects) eo.draw();
                else
                    for (int i : indices) cylinderObjects[i].draw();
            }
            
            if (0 < tn) GL11.glPopMatrix();
        }
        
        if (!sceneObject().isLighted()) GL11.glEnable(GL11.GL_LIGHTING);
    }

    //--------------------------------------------------------------------------

    private final SceneModelObject sceneObject;

    private boolean isRegistered;
    private Parallelogram[] parallelogramObjects;
    private Parallelepiped[] parallelepipedObjects;
    private Cylinder[] cylinderObjects;
}
