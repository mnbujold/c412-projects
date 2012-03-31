package visual.draw;

import java.nio.FloatBuffer;

import model.ModelParameters;

import org.lwjgl.opengl.GL11;
import org.lwjgl.util.Color;

import visual.Helper;

public class Floor
{
    public Floor(double scale, double glue)
    {
        floorColor = Helper.colorToFB(new Color(168, 146, 103));
        axisColor = Helper.colorToFB(new Color(180, 170, 120));
        signColor = Helper.colorToFB(new Color(175, 202, 123));
        
        radius = 2.0 * ModelParameters.W * scale;
        this.glue = glue;
    }

    //--------------------------------------------------------------------------

    public void draw()
    {
        GL11.glPushMatrix();
        
        GL11.glMaterial(GL11.GL_FRONT, GL11.GL_AMBIENT_AND_DIFFUSE, floorColor);
        GL11.glBegin(GL11.GL_QUADS);
        {
            GL11.glNormal3d(0.0, 1.0, 0.0);
            GL11.glVertex3d(-MAX, 0.0f,  MAX);
            GL11.glVertex3d(-MAX, 0.0f, -MAX);
            GL11.glVertex3d( MAX, 0.0f, -MAX);
            GL11.glVertex3d( MAX, 0.0f,  MAX);
        }
        GL11.glEnd();
        
        GL11.glMaterial(GL11.GL_FRONT, GL11.GL_AMBIENT_AND_DIFFUSE, axisColor);
        GL11.glBegin(GL11.GL_LINES);
        {
            GL11.glVertex3d(0.0, 0.0, -MAX);
            GL11.glVertex3d(0.0, 0.0,  MAX);
            GL11.glVertex3d( MAX, 0.0, 0.0);
            GL11.glVertex3d(-MAX, 0.0, 0.0);
        }
        GL11.glEnd();
        
        GL11.glMaterial(GL11.GL_FRONT, GL11.GL_AMBIENT_AND_DIFFUSE, signColor);
        GL11.glBegin(GL11.GL_LINE_LOOP);
        {
            final double frac = 2.0 * Math.PI / SIGN_POINTS;
            for (int i = 0; i < SIGN_POINTS; ++i)
            {
                GL11.glVertex3d(radius * Math.cos(i * frac),
                                glue,
                                radius * Math.sin(i * frac));
            }
        }
        GL11.glEnd();
        GL11.glBegin(GL11.GL_LINES);
        {
            double l = radius * 0.1;
            GL11.glVertex3d( l, glue, 0.0);
            GL11.glVertex3d(-l, glue, 0.0);
            GL11.glVertex3d(0.0, glue,  l);
            GL11.glVertex3d(0.0, glue, -l);
        }
        GL11.glEnd();
        
        GL11.glPopMatrix();
    }

    //--------------------------------------------------------------------------

    private static final int SIGN_POINTS = 100;
    private static final double MAX = 100.0;

    private double glue;
    private double radius;

    private FloatBuffer floorColor;
    private FloatBuffer axisColor;
    private FloatBuffer signColor;
}
