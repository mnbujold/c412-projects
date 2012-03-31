package visual.draw;

import java.nio.FloatBuffer;

import org.lwjgl.opengl.GL11;
import org.lwjgl.util.Color;

import visual.Helper;

public class WheelSign
{
    public WheelSign(double scale, Color color)
    {
        this.scale = scale;
        this.color = Helper.colorToFB(color);
    }

    //--------------------------------------------------------------------------

    public void draw()
    {
        GL11.glMaterial(GL11.GL_FRONT, GL11.GL_AMBIENT_AND_DIFFUSE, color);
        
        GL11.glBegin(GL11.GL_LINES);
        {
            GL11.glVertex3d(0.0, -scale, 0.0);
            GL11.glVertex3d(0.0, scale, 0.0);
            GL11.glVertex3d(0.0, -scale/2, -scale/4);
            GL11.glVertex3d(0.0, -scale/2, scale/4);
            GL11.glVertex3d(0.0, scale/2, 0.0);
            GL11.glVertex3d(0.0, -scale/2, -scale/4);
            GL11.glVertex3d(0.0, scale/2, 0.0);
            GL11.glVertex3d(0.0, -scale/2, scale/4);
        }
        GL11.glEnd();
    }

    //--------------------------------------------------------------------------

    private double scale;
    private FloatBuffer color;
}
