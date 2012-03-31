package visual;

import helper.Ratio;

import java.nio.FloatBuffer;

import model.ModelParameters;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.Color;
import org.lwjgl.util.glu.GLU;

import visual.draw.Buoy;
import visual.draw.Floor;
import visual.draw.Segway;

public class View
{
    /**
     * "Glue length" which is used when an object want to be drawn onto another.
     */
    private static final double GLUE = 0.01;

    /**
     * Meters to pixels scale.
     */
    private static final double SCALE_M = 10.0;

    /**
     * Centimeters to pixels scale.
     */
    private static final double SCALE_CM = SCALE_M / Ratio.M_TO_CM;

    //--------------------------------------------------------------------------

    public View()
    throws Exception
    {
        int width = 800;
        int height = 600;
        int freq = 60;
        int bpp = Display.getDisplayMode().getBitsPerPixel();
        
        displayModes = Display.getAvailableDisplayModes();
        org.lwjgl.util.Display.setDisplayMode(displayModes,
                                              new String[]{"width=" + width,
                                                           "height=" + height,
                                                           "freq=" + freq,
                                                           "bpp=" + bpp});
        Display.setTitle("Segway simulator");
        Display.setVSyncEnabled(true);
        Display.setFullscreen(false);
        Display.create();
        Display.setLocation(-1, -1);
        
        GL11.glViewport(0, 0, width, height);
        GL11.glMatrixMode(GL11.GL_PROJECTION);
        GL11.glLoadIdentity();
        GLU.gluPerspective(45.0f, ((float)width/(float)height), 0.1f, 100.0f);
        GL11.glMatrixMode(GL11.GL_MODELVIEW);
        GL11.glLoadIdentity();
        
        { // enable lighting
            GL11.glEnable(GL11.GL_LIGHTING);
            GL11.glEnable(GL11.GL_LIGHT0);
            
            FloatBuffer lightAmbient = BufferUtils.createFloatBuffer(4);
            FloatBuffer lightDiffuse = BufferUtils.createFloatBuffer(4);
            FloatBuffer lightSpecular = BufferUtils.createFloatBuffer(4);
            FloatBuffer lightPosition = BufferUtils.createFloatBuffer(4);
            
            lightAmbient.put(new float[]{0.2f, 0.2f, 0.2f, 1.0f}).flip();
            lightDiffuse.put(new float[]{0.8f, 0.8f, 0.8f, 1.0f}).flip();
            lightSpecular.put(new float[]{0.5f, 0.5f, 0.5f, 1.0f}).flip();
            lightPosition.put(new float[]{10.0f, 10.0f, 10.0f, 1.0f}).flip();
            
            GL11.glLight(GL11.GL_LIGHT0, GL11.GL_AMBIENT, lightAmbient);
            GL11.glLight(GL11.GL_LIGHT0, GL11.GL_DIFFUSE, lightDiffuse);
            GL11.glLight(GL11.GL_LIGHT0, GL11.GL_SPECULAR, lightSpecular);
            GL11.glLight(GL11.GL_LIGHT0, GL11.GL_POSITION, lightPosition);            
        }
        
        GL11.glShadeModel(GL11.GL_SMOOTH);
        GL11.glClearDepth(1.0f);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glDepthFunc(GL11.GL_LEQUAL);
        GL11.glHint(GL11.GL_PERSPECTIVE_CORRECTION_HINT, GL11.GL_NICEST);

        // Sky (background) color.
        {
            float r = 75;
            float g = 155;
            float b = 200;
            GL11.glClearColor(r/255.0f, g/255.0f, b/255.0f, 0.0f);
        }

        floor = new Floor(SCALE_CM, GLUE);
        segway = new Segway(SCALE_CM, GLUE);
        
        double d = 5.0 * ModelParameters.W * SCALE_CM;
        buoys = new Buoy[]{new Buoy(-d, 0.0, new Color(60, 250, 50)),
                           new Buoy( d, 0.0, new Color(60, 100, 50)),
                           new Buoy(0.0, -d, new Color(250, 50, 60)),
                           new Buoy(0.0,  d, new Color(100, 50, 60))};
    }

    public void update() { Display.update(); }
    public void destroy() { Display.destroy(); }

    public boolean isClosed() { return Display.isCloseRequested(); }
    public boolean isActive() { return Display.isActive(); }
    public boolean isVisible() { return Display.isVisible(); }
    public boolean isDirty() { return Display.isDirty(); }

    //--------------------------------------------------------------------------

    /**
     * Rendering the scene.
     * @param x x (plane) position of axle midpoint (m)
     * @param y y (plane) position of axle midpoint (m)
     * @param z z (elevation) position of axle midpoint
     * @param pitchAngle body pitch angle (deg)
     * @param yawAngle body yaw angle (deg)
     * @param lWheelAngle left wheel rotation angle (deg)
     * @param rWheelAngle right wheel rotation angle (deg)
     * @param camX camera x (plane) position (pixels)
     * @param camY camera y (plane) position (pixels)
     * @param camZ camera z (elevation) position (pixels)
     */
    public void render(double x,
                       double y,
                       double z,
                       float pitchAngle,
                       float yawAngle,
                       float lWheelAngle,
                       float rWheelAngle,
                       float camX,
                       float camY,
                       float camZ)
    {
        x *= SCALE_M;
        y *= -SCALE_M;
        z *= SCALE_M;
        
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
        
        GL11.glLoadIdentity();
        GLU.gluLookAt(camX, camZ, camY, // camera position
                      (float)x, 1.25f, (float)y, // look the robot
                      0.0f, 1.0f, 0.0f); // up
        floor.draw();
        for (Buoy buoy : buoys)
        {
            buoy.draw();
        }
        segway.draw(x, z, y,
                    pitchAngle, yawAngle,
                    lWheelAngle, rWheelAngle);
    }

    //--------------------------------------------------------------------------

    private Floor floor;
    private Segway segway;
    private Buoy buoys[];

    private DisplayMode[] displayModes;
}
