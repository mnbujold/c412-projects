package visual.engine;

import java.awt.Dimension;
import java.io.PrintStream;
import java.lang.reflect.Field;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

import model.scene.Color;
import model.scene.SceneModel;

import org.lwjgl.BufferUtils;
import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.AWTGLCanvas;
import org.lwjgl.opengl.ContextCapabilities;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.lwjgl.opengl.GLContext;
import org.lwjgl.util.glu.GLU;

/**
 * Engine of the visualization scene canvas.
 */
public abstract class EngineCanvas extends AWTGLCanvas
{
    public EngineCanvas(SceneModel scene)
    throws LWJGLException
    {
        this.scene = scene;
        info = new AtomicReference<Info>(null);
        look = new Look();
        engineTools = new EngineTools(this);
        
        backgroundColor = new Color(0, 0, 0);
        isBackgroundColorChanged = true;
        hasWindowSizeModified = true;
    }
    
    SceneModel scene() { return scene; }
    int currentWidth() { return currentWidth; }
    int currentHeight() { return currentHeight; }
    
    //--------------------------------------------------------------------------
    
    public Color backgroundColor()
    {
        synchronized (backgroundColor) { return backgroundColor; }
    }
    
    public void setBackgroundColor(Color color)
    {
        synchronized (backgroundColor)
        {
            if (backgroundColor != color)
            {
                backgroundColor = color;
                isBackgroundColorChanged = true;
            }
        }
    }
    
    //--------------------------------------------------------------------------

    /** Apply the rotational camera changes. */
    public void rotateCamera(double pitch, double yaw, double distance)
    {
        synchronized (look)
        {
            look.pitch = (float)pitch;
            look.yaw = (float)yaw;
            look.distance = (float)distance;
            look.hasModified = true;
        }
    }

    /** Apply the translational camera changes. */
    public void translateCamera(double x, double y, double z)
    {
        synchronized (look)
        {
            look.centerX = (float)x;
            look.centerY = (float)y;
            look.centerZ = (float)z;
            look.hasModified = true;
        }
    }
    
    //--------------------------------------------------------------------------
    
    /** Reset frame/second ration measurement. */
    public void resetFps() { frames = new AtomicLong(-1); }
    
    /** @return frame/second ratio (with 2 digit decimal fragment precision) */
    public float fps()
    {
        long framesValue = frames.get();
        long elapsed = System.currentTimeMillis() - startTime;
        return (float)(framesValue*100000L/elapsed) / 100f;
    }
    
    //--------------------------------------------------------------------------
    
    @Override
    protected void initGL()
    {
        super.initGL();
        
        setMinimumSize(new Dimension(500, 500));
        currentWidth = currentHeight = 0;

        FloatBuffer lightPos = BufferUtils.createFloatBuffer(4);
        lightPos.put(new float[]{0f, 0f, 0f, 1f}).flip();
        GL11.glLight(GL11.GL_LIGHT0, GL11.GL_POSITION, lightPos);
        GL11.glEnable(GL11.GL_LIGHTING);
        GL11.glEnable(GL11.GL_LIGHT0);
        
        GL11.glEnable(GL11.GL_NORMALIZE);
        GL11.glShadeModel(GL11.GL_SMOOTH);
        
        // GL11.glEnable(GL11.GL_BLEND);
        // GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        
        GL11.glEnable(GL11.GL_CULL_FACE);
        GL11.glColorMaterial(GL11.GL_FRONT, GL11.GL_AMBIENT_AND_DIFFUSE);
        GL11.glEnable(GL11.GL_COLOR_MATERIAL);
        
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glDepthFunc(GL11.GL_LEQUAL);
        GL11.glClearDepth(1.0);
        GL11.glHint(GL11.GL_PERSPECTIVE_CORRECTION_HINT, GL11.GL_NICEST);
        
        Engine.initialize();
        try
        {
            initalizeObjects();
        }
        catch (Exception e)
        {
            e.printStackTrace(System.err);
            System.exit(-10);
        }
        Engine.closeRegistration();
        EngineFont.buildFonts();
        
        setVSyncEnabled(true);
        resetFps();
        
        info.set(new Info());
    }

    /** Create and register objects of the scene. */
    protected abstract void initalizeObjects() throws Exception;

    //--------------------------------------------------------------------------

    @Override
    protected void paintGL()
    {
        long framesValue = frames.incrementAndGet();
        if (framesValue <= 0)
        {
            framesValue = 0;
            startTime = System.currentTimeMillis();
        }
        
        try
        {
            if (getWidth() != currentWidth || getHeight() != currentHeight)
            {
                hasWindowSizeModified = true;
                currentWidth = getWidth();
                currentHeight = getHeight();
                GL11.glViewport(0, 0, currentWidth, currentHeight);
                GL11.glMatrixMode(GL11.GL_PROJECTION);
                GL11.glLoadIdentity();
                float ratio = ((float)currentWidth) / currentHeight;
                GLU.gluPerspective(45f, ratio, 100f, 5000f);
                GL11.glMatrixMode(GL11.GL_MODELVIEW);
            }

            synchronized (backgroundColor)
            {
                if (isBackgroundColorChanged)
                {
                    GL11.glClearColor(backgroundColor.red() / 255f,
                                      backgroundColor.green() / 255f,
                                      backgroundColor.blue() / 255f,
                                      1f);
                }
            }
            GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
            
            synchronized (look) // camera view setup
            {
                if (look.hasModified)
                {
                    GL11.glLoadIdentity();
                    GL11.glTranslatef(0f, 0f, -look.distance);
                    GL11.glRotatef(look.pitch, 1f, 0f, 0f);
                    GL11.glRotatef(look.yaw, 0f, 0f, 1f);
                    GL11.glTranslatef(-look.centerX,
                                      -look.centerY,
                                      -look.centerZ);
                    look.hasModified = false;
                }
            }
            GL11.glPushMatrix();
            
            Engine.enableVBOs();
            draw();
            Engine.disableVBOs();
            
            if (isPointPick)
            {
                modelview.clear();
                projection.clear();
                viewport.clear();
                z.clear();

                if (hasWindowSizeModified)
                {
                    GL11.glGetFloat(GL11.GL_PROJECTION_MATRIX, projection);
                    GL11.glGetInteger(GL11.GL_VIEWPORT, viewport);
                    hasWindowSizeModified = false;
                }
                GL11.glGetFloat(GL11.GL_MODELVIEW_MATRIX, modelview);
                winY = viewport.get(3) - winY; // height - winY
                
                GL11.glReadPixels(winX, winY, 1, 1, GL11.GL_DEPTH_COMPONENT,
                                  GL11.GL_FLOAT, z);
                
                GLU.gluUnProject(winX, winY, z.get(0), modelview,
                                 projection, viewport, pointToPick);
                
                isPointPick = false;
            }
            
            engineTools.draw();
            
            GL11.glPopMatrix();
            swapBuffers();            
            frames.set(framesValue + 1);
        }
        catch (LWJGLException e) { throw new RuntimeException(e); }
    }
    
    /** Draw the objects on the scene. */
    protected abstract void draw();
    
    void enableOrthoView()
    {
        GL11.glMatrixMode(GL11.GL_PROJECTION);
        GL11.glPushMatrix();
        GL11.glLoadIdentity();
        GL11.glOrtho(0, currentWidth, 0, currentHeight, -1, 1);
        GL11.glMatrixMode(GL11.GL_MODELVIEW);
        GL11.glPushMatrix();
        GL11.glLoadIdentity();
    }
    
    void disableOrthoView()
    {
        GL11.glMatrixMode(GL11.GL_PROJECTION);
        GL11.glPopMatrix();
        GL11.glMatrixMode(GL11.GL_MODELVIEW);
        GL11.glPopMatrix();
    }
    
    //--------------------------------------------------------------------------
    
    /** @return the point (p) in the model view
     *          corresponds to (x,y) the window coordinates */
    public void getPoint(int x, int y, FloatBuffer p)
    {
        winX = x; winY = y;
        pointToPick = p;
        isPointPick = true;
        paint(null);
    }
    
    //--------------------------------------------------------------------------
    
    /** Visual font representation with which one can write on the screen. */
    /* TODO imeplement fonts
    protected final static class VisualFont
    {
        public VisualFont(Font font, Color color)
        {
            this.font = null; //new TrueTypeFont(font, true);
        }
        
        public void drawText(float x, float y, String text)
        {
            font.drawString(x, y, text, 1, 1);
        }
        
        private TrueTypeFont font;
    }
    */
    
    /** Text drawings on the top of the rendered screen. */
    protected void drawTextOnScreen()
    {
    }
    
    //--------------------------------------------------------------------------
    
    public static class Info
    {
        public Info()
        {
            ContextCapabilities cc = GLContext.getCapabilities();
            if (cc != null)
            {
                info = new TreeMap<String, String>();
                for (Field field : cc.getClass().getFields())
                {
                    if (field.getType().isAssignableFrom(boolean.class))
                    {
                        try
                        {
                            info.put(field.getName(),
                                     "" + field.getBoolean(cc));
                        }
                        catch (IllegalAccessException e) {}
                    }
                }
                info.put("gl_max_element_vertices",
                         "" + GL12.GL_MAX_ELEMENTS_VERTICES);
                info.put("gl_max_element_indices",
                         "" + GL12.GL_MAX_ELEMENTS_INDICES);
            }
            else { info = null; }
        }
        
        public Map<String, String> data() { return info; }
        
        public void print() { print(System.out); }
        public void print(PrintStream ps)
        {
            for (Map.Entry<String, String> e : data().entrySet())
                ps.println(e.getKey() + " : " + e.getValue());
        };
        
        private final TreeMap<String, String> info;
    }
    
    /** @return true if the GL engine is initialized */
    public boolean isInitialized() { return null != info(); }
    
    /** @return engine information (null before initialization) */
    public Info info() { return info.get(); }
    
    //--------------------------------------------------------------------------

    private final SceneModel scene;
    private AtomicReference<Info> info;
    private final EngineTools engineTools;
    
    private long startTime;
    private AtomicLong frames;
    
    private boolean isBackgroundColorChanged;
    private Color backgroundColor;
    
    private static class Look
    {
        boolean hasModified = true;
        float pitch = 0f, yaw = 0f, distance = 0f;
        float centerX = 0f, centerY = 0f, centerZ = 0f;
    }
    private Look look;
    
    private int currentWidth, currentHeight;

    private int winX, winY;
    private boolean isPointPick = false;
    private boolean hasWindowSizeModified;
    private FloatBuffer pointToPick = null;
    private final IntBuffer viewport = BufferUtils.createIntBuffer(16);
    private final FloatBuffer z = BufferUtils.createFloatBuffer(1);
    private final FloatBuffer modelview = BufferUtils.createFloatBuffer(16);
    private final FloatBuffer projection = BufferUtils.createFloatBuffer(16);
    
    private static final long serialVersionUID = 1L;
}
