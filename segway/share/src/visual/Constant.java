package visual;

/**
 * General visualization constants.
 */
public interface Constant
{
    /** Switch using OpenGL Vertex Buffer Object (VBO) on/off. TODO */
    static final boolean USE_VBO = true;

    //--------------------------------------------------------------------------
    
    static final int SHORT_BYTESIZE = 2;
    static final int INT_BYTESIZE = 4;
    static final int FLOAT_BYTESIZE = 4;
    
    static final int VERTEX_FLOATSIZE = 3;
    static final int VERTEX_BYTESIZE = VERTEX_FLOATSIZE * FLOAT_BYTESIZE;
    
    static final int NORMAL_FLOATSIZE = 3;
    static final int NORMAL_BYTESIZE = NORMAL_FLOATSIZE * FLOAT_BYTESIZE;
    
    static final int COLOR_FLOATSIZE = 4;
    static final int COLOR_BYTESIZE = COLOR_FLOATSIZE * FLOAT_BYTESIZE;
    
    static final int VNC_FLOATSIZE = VERTEX_FLOATSIZE
                                   + NORMAL_FLOATSIZE
                                   + COLOR_FLOATSIZE;
    static final int VNC_BYTESIZE = VERTEX_BYTESIZE
                                  + NORMAL_BYTESIZE
                                  + COLOR_BYTESIZE;
}
