package visual.draw;

import java.nio.FloatBuffer;

import org.lwjgl.opengl.GL11;
import org.lwjgl.util.Color;

import visual.Helper;

public class Wheel
{
    public Wheel(int numPoints, double radius, double depth, Color color)
    {
        this.radius = radius;
        this.d = depth / 2.0;
        this.color = Helper.colorToFB(color);

        double aincr = 2.0 * Math.PI / (numPoints - 1.0);
        ys = new double[numPoints];
        zs = new double[numPoints];
        double angle = 0.0;
        for (int i = 0; i < numPoints; ++i)
        {
            ys[i] = Math.sin(angle) * radius;
            zs[i] = Math.cos(angle) * radius;
            angle += aincr;            
        }
    }

    public double getD() { return d; }
    public double getRadius() { return radius; }

    //--------------------------------------------------------------------------

    public void draw()
    {
        
        GL11.glMaterial(GL11.GL_FRONT, GL11.GL_AMBIENT_AND_DIFFUSE, color);
        
        drawSide(+1.0);
        drawSide(-1.0);
        
        GL11.glBegin(GL11.GL_QUADS);
        {
            int len = ys.length;
            for (int i = 1; i < len; ++i)
            {
                GL11.glNormal3d(0.0, (ys[i-1]+ys[i])/2, (zs[i-1]+zs[i])/2);
                GL11.glVertex3d(-d, ys[i-1], zs[i-1]);
                GL11.glVertex3d(d, ys[i-1], zs[i-1]);
                GL11.glVertex3d(d, ys[i], zs[i]);
                GL11.glVertex3d(-d, ys[i], zs[i]);
            }
            GL11.glNormal3d(0.0, (ys[len-1]+ys[0])/2, (zs[len-1]+zs[0])/2);
            GL11.glVertex3d(-d, ys[len-1], zs[len-1]);
            GL11.glVertex3d(d, ys[len-1], zs[len-1]);
            GL11.glVertex3d(d, ys[0], zs[0]);
            GL11.glVertex3d(-d, ys[0], zs[0]);
        }
        GL11.glEnd();
    }

    //--------------------------------------------------------------------------

    private void drawSide(double sign)
    {
        GL11.glBegin(GL11.GL_POLYGON);
        {
            GL11.glNormal3d(sign, 0.0, 0.0);
            for (int i = 0; i < ys.length; ++i)
            {
                GL11.glVertex3d(sign * d, ys[i], zs[i]);
            }
        }
        GL11.glEnd();        
    }

    //--------------------------------------------------------------------------

    private double[] ys;
    private double[] zs;

    private double radius;
    private double d; // depth/2

    private FloatBuffer color;
}
