package visual.engine;

import geom3d.Point3D;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.ARBVertexBufferObject;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.lwjgl.opengl.GLContext;

import visual.Constant;
import visual.engine.AbstractEngineObject.Data;

/**
 * Manager of drawing buffers. (singleton).
 */
final class BufferManager
{
    private BufferManager()
    {
        vboVncId = vboIdxId = 0;
    }

    private static final BufferManager INSTANCE = new BufferManager();
    
    static BufferManager instance() { return INSTANCE; }
        
    //--------------------------------------------------------------------------
    // proxied functions by the Engine class
    
    void init()
    {
        if (0 != vboVncId || 0 != vboIdxId)
        {
            System.err.println("BufferManager already initialized!");
            System.exit(-101);
        }
        
        if (!GLContext.getCapabilities().GL_ARB_vertex_buffer_object)
        {
            System.err.println("OpenGL: VBO is not supported!");
            System.exit(-102);
        }
        
        int vncSize = Constant.VNC_FLOATSIZE;
        vncBuffer = BufferUtils.createFloatBuffer(
                        GL12.GL_MAX_ELEMENTS_VERTICES * vncSize);
        vboVncId = ARBVertexBufferObject.glGenBuffersARB();
        ARBVertexBufferObject.glBindBufferARB(
            ARBVertexBufferObject.GL_ARRAY_BUFFER_ARB, vboVncId);
        
        idxBuffer = BufferUtils.createIntBuffer(
                        GL12.GL_MAX_ELEMENTS_INDICES);
        idxType = GL11.GL_UNSIGNED_INT; // TODO adjust this to byte/short/int!
        idxByteSize = Constant.INT_BYTESIZE;
        vboIdxId = ARBVertexBufferObject.glGenBuffersARB();
        ARBVertexBufferObject.glBindBufferARB(
                ARBVertexBufferObject.GL_ELEMENT_ARRAY_BUFFER_ARB, vboIdxId);
        
        numVertices = 0;
    }
    
    void closeRegistration()
    {
        if (vncBuffer == null || idxBuffer == null)
        {
            System.err.println("Uninitialized BufferManager!");
            System.exit(-103);
        }
        
        vncBuffer.flip();
        if (0 != vboVncId)
        {
            ARBVertexBufferObject.glBufferDataARB(
                ARBVertexBufferObject.GL_ARRAY_BUFFER_ARB,
                vncBuffer,
                ARBVertexBufferObject.GL_STATIC_DRAW_ARB);
            
            vncBuffer = null;
            ARBVertexBufferObject.glBindBufferARB(
                ARBVertexBufferObject.GL_ARRAY_BUFFER_ARB, 0);
        }

        idxBuffer.flip();
        if (0 != vboIdxId)
        {
            ARBVertexBufferObject.glBufferDataARB(
                ARBVertexBufferObject.GL_ELEMENT_ARRAY_BUFFER_ARB,
                idxBuffer,
                ARBVertexBufferObject.GL_STATIC_DRAW_ARB);
            
            idxBuffer = null;
            ARBVertexBufferObject.glBindBufferARB(
                ARBVertexBufferObject.GL_ELEMENT_ARRAY_BUFFER_ARB, 0);
        }
    }

    void enableVBOs()
    {
        ARBVertexBufferObject.glBindBufferARB(
            ARBVertexBufferObject.GL_ARRAY_BUFFER_ARB, vboVncId);

        GL11.glInterleavedArrays(GL11.GL_C4F_N3F_V3F, 0, 0);
        
        ARBVertexBufferObject.glBindBufferARB(
            ARBVertexBufferObject.GL_ELEMENT_ARRAY_BUFFER_ARB, vboIdxId);
        
        GL11.glEnableClientState(GL11.GL_INDEX_ARRAY);
    }
    
    void disableVBOs()
    {
        GL11.glDisableClientState(GL11.GL_VERTEX_ARRAY);
        GL11.glDisableClientState(GL11.GL_NORMAL_ARRAY);
        GL11.glDisableClientState(GL11.GL_COLOR_ARRAY);
        
        ARBVertexBufferObject.glBindBufferARB(
                ARBVertexBufferObject.GL_ARRAY_BUFFER_ARB, 0);
        
        GL11.glDisableClientState(GL11.GL_INDEX_ARRAY);
        
        ARBVertexBufferObject.glBindBufferARB(
                ARBVertexBufferObject.GL_ELEMENT_ARRAY_BUFFER_ARB, 0);
    }
    
    //--------------------------------------------------------------------------

    /** Drawing buffer information for engine object data. */
    static final class Info
    {
        Info(int mode, int vncStart, int vncEnd, long idxOffset, int idxCount)
        {
            this.mode = mode;
            this.vncStart = vncStart;
            this.vncEnd = vncEnd;
            this.idxOffset = idxOffset;
            this.idxCount = idxCount;
        }
        
        final int mode; // GL_QUADS, GL_LINE, GL_POLYGON, ...
        final int vncStart;
        final int vncEnd;
        final long idxOffset;
        final int idxCount;
        
        @Override
        public String toString()
        {
            return "bInfo{" + mode
                      + "," + vncStart + ":" + vncEnd
                      + "," + idxOffset + "(" + idxCount
                      + ")}";
        }
    }

    /** Register an engine object into the drawing buffers. */
    Info[] register(AbstractEngineObject obj)
    {
        Data[] triangles = obj.triangleData();
        Data[] quads = obj.quadData();
        Data[] polygons = obj.polygonData();
        
        int numInfos = (triangles == null ? 0 : triangles.length)
                     + (quads == null ? 0 : quads.length)
                     + (polygons == null ? 0 : polygons.length);
        Info[] infos = new Info[numInfos];
        
        int idxInfo = 0;
        if (triangles != null)
            for (Data data : triangles)
                infos[idxInfo++] = registerData(GL11.GL_TRIANGLES, obj, data);
        if (quads != null)
            for (Data data : quads)
                infos[idxInfo++] = registerData(GL11.GL_QUADS, obj, data);
        if (polygons != null)
            for (Data data : polygons)
                infos[idxInfo++] = registerData(GL11.GL_POLYGON, obj, data);
        
        return infos;
    }

    /** Draw the appropriate buffered graphicsj information. */
    void draw(Info bInfo)
    {
        GL12.glDrawRangeElements(bInfo.mode,
                                 bInfo.vncStart,
                                 bInfo.vncEnd,
                                 bInfo.idxCount,
                                 idxType,
                                 bInfo.idxOffset);
    }
    
    //--------------------------------------------------------------------------

    /** Register engine object data into the drawing buffers. */
    private Info registerData(int mode, AbstractEngineObject obj, Data data)
    {
        int dataNumVertices = data.vertices.length / 3;
        Info info = new Info(mode,
                             numVertices,
                             numVertices + dataNumVertices - 1,
                             idxBuffer.position() * idxByteSize,
                             data.indices.length);
        
        Point3D p = new Point3D();
        float[] buffer = new float[3];
        float[] vertexVector, normalVector;
        float[] colorVector = AbstractEngineObject.colorToFloat(data.color);
        for (int i = 0; i < data.vertices.length; i += 3)
        {
            putVector(vncBuffer, colorVector);
            
            normalVector = rotate(getVector(buffer, data.normals, i), obj, p);
            putVector(vncBuffer, normalVector);
            
            vertexVector = translate(rotate(getVector(buffer, data.vertices, i),
                                            obj, p),
                                     obj);
            putVector(vncBuffer, vertexVector);            
        }

        for (int i = 0; i < data.indices.length; ++i)
            idxBuffer.put(data.indices[i] + numVertices);
        
        numVertices += dataNumVertices;
        return info;
    }
    
    /**
     * Get a 3d vector from "buffer" at "offset" and put it into "vector"
     * @return "vector"
     */
    private static float[] getVector(float[] vector, float[] buffer, int offset)
    {
        for (int i = 0, j = offset; i < 3; ++i, ++j) vector[i] = buffer[j];
        return vector;
    }

    /**
     * Translate "vector" according to the specifications in "obj".
     * The "p" parameter is used as a temporary working buffer.
     * @return translated "vector"
     */
    private static float[] translate(float[] vector,
                                     AbstractEngineObject obj)
    {
        Point3D tr = obj.translation();
        for (int i = 0; i < vector.length; ++i) vector[i] += tr.get(i);
        return vector;
    }

    /**
     * Rotate "vector" according to the specifications in "obj".
     * The "p" parameter is used as a temporary working buffer.
     * @return rotates "vector"
     */
    private static float[] rotate(float[] vector,
                                  AbstractEngineObject obj,
                                  Point3D p)
    {
        p.setX(vector[0]);
        p.setY(vector[1]);
        p.setZ(vector[2]);
        
        switch (obj.rotationOrder())
        {
            case XYZ :
                p.rotateX(obj.rotationX());
                p.rotateY(obj.rotationY());
                p.rotateZ(obj.rotationZ());
                break;
            case XZY :
                p.rotateX(obj.rotationX());
                p.rotateZ(obj.rotationZ());
                p.rotateY(obj.rotationY());
                break;
            case YXZ :
                p.rotateY(obj.rotationY());
                p.rotateX(obj.rotationX());
                p.rotateZ(obj.rotationZ());
                break;
            case YZX :
                p.rotateY(obj.rotationY());
                p.rotateZ(obj.rotationZ());
                p.rotateX(obj.rotationX());
                break;
            case ZXY :
                p.rotateZ(obj.rotationZ());
                p.rotateX(obj.rotationX());
                p.rotateY(obj.rotationY());
                break;
            case ZYX :
                p.rotateZ(obj.rotationZ());
                p.rotateY(obj.rotationY());
                p.rotateX(obj.rotationX());
                break;
            default :
                throw new IllegalArgumentException(
                          "Unsupported rotation order: " + obj.rotationOrder());
        }
        
        vector[0] = (float) p.x();
        vector[1] = (float) p.y();
        vector[2] = (float) p.z();
        return vector;
    }

    /**
     * Put "vector" into "buffer".
     */
    private static void putVector(FloatBuffer buffer, float[] vector)
    {
        for (float f : vector) buffer.put(f);
    }
    
    //--------------------------------------------------------------------------
    
    private int numVertices;
    
    private int idxType, idxByteSize;
    private IntBuffer idxBuffer;
    private FloatBuffer vncBuffer;
    private int vboVncId, vboIdxId;
}
