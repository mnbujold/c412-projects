package visual.engine;

import geom3d.Point3D;
import model.scene.Color;
import visual.engine.BufferManager.Info;

/**
 * Common implementation of engine objects.
 */
public abstract class AbstractEngineObject implements EngineObject
{
    public AbstractEngineObject()
    {
        bufferInfo = null;
        
        rotationOrder = RotationOrder.XYZ;
        angleX = angleY = angleZ = 0f;
        v = ORIGIN;
    }

    //--------------------------------------------------------------------------

    @Override
    public boolean isRegistered() { return bufferInfo != null; }
    
    @Override
    public void register()
    {
        bufferInfo = BufferManager.instance().register(this);
        // free resources (should not be referenced after registration)
        v = ORIGIN;
    }
    
    @Override
    public void draw()
    {
        assert (isRegistered());
        BufferManager buffMan = BufferManager.instance();
        for (Info bInfo : bufferInfo) buffMan.draw(bInfo);
    }

    //--------------------------------------------------------------------------
    
    @Override
    public RotationOrder rotationOrder() { return rotationOrder; }
    
    @Override
    public void setRotationOrder(RotationOrder ro) { rotationOrder = ro; }

    @Override
    public float rotationX() { return angleX; }
    
    @Override
    public void setRotationX(float angle) { angleX = angle; }
    
    @Override
    public float rotationY() { return angleY; }
    
    @Override
    public void setRotationY(float angle) { angleY = angle; }
    
    @Override
    public float rotationZ() { return angleZ; }
    
    @Override
    public void setRotationZ(float angle) { angleZ = angle; }    

    @Override
    public Point3D translation() { return v; }
    
    @Override
    public void setTranslation(Point3D v) { this.v = v; }
    
    //--------------------------------------------------------------------------

    /** Registration data representation of engine objects. */
    static class Data
    {
        public Data(float[] vertices,
                    float[] normals,
                    Color color,
                    int[] indices)
        {
            assert (vertices.length == normals.length);
            this.vertices = vertices;
            this.normals = normals;
            this.color = color;
            this.indices = indices;
        }
        
        final float[] vertices;
        final float[] normals;
        final Color color;
        final int[] indices;
    }
    
    Data[] triangleData() { return null; }
    Data[] quadData() { return null; }
    Data[] polygonData() { return null; }

    //--------------------------------------------------------------------------
    
    /** Convert a color to its float array representation. */
    static float[] colorToFloat(Color color)
    {
        return new float[]{color.red() / 255f,
                           color.green() / 255f,
                           color.blue() / 255f,
                           color.alpha() / 255f};
    }
    
    //--------------------------------------------------------------------------
    
    private Info[] bufferInfo;

    private RotationOrder rotationOrder;
    private float angleX, angleY, angleZ;
    private Point3D v;
    
    private static final Point3D ORIGIN = Point3D.origin();
}
