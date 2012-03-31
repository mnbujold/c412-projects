package visual.draw;

import java.nio.FloatBuffer;

import org.lwjgl.opengl.GL11;
import org.lwjgl.util.Color;

import visual.Helper;

public class Brick
{
    public Brick(double width, double height, double depth, Color color)
    {
        this.w = width / 2.0;
        this.h = height / 2.0;
        this.d = depth / 2.0;
        this.color = Helper.colorToFB(color);
    }

    public double getW() { return w; }
    public double getH() { return h; }
    public double getD() { return d; }

    //--------------------------------------------------------------------------

    public void draw()
    {
        GL11.glMaterial(GL11.GL_FRONT, GL11.GL_AMBIENT_AND_DIFFUSE, color);
        GL11.glBegin(GL11.GL_QUADS);
        {
            // front face
            GL11.glNormal3d(0.0, 0.0, 1.0);
            GL11.glVertex3d(-w, -h, d);
            GL11.glVertex3d(w, -h, d);
            GL11.glVertex3d(w, h, d);
            GL11.glVertex3d(-w, h, d);
            
            // back face
            GL11.glNormal3d(0.0, 0.0, -1.0);
            GL11.glVertex3d(-w, -h, -d);
            GL11.glVertex3d(w, -h, -d);
            GL11.glVertex3d(w, h, -d);
            GL11.glVertex3d(-w, h, -d);
            
            // top face
            GL11.glNormal3d(0.0, 1.0, 0.0);
            GL11.glVertex3d(-w, h, -d);
            GL11.glVertex3d(-w, h, d);
            GL11.glVertex3d(w, h, d);
            GL11.glVertex3d(w, h, -d);
            
            // bottom face
            GL11.glNormal3d(0.0, -1.0, 0.0);
            GL11.glVertex3d(-w, -h, -d);
            GL11.glVertex3d(w, -h, -d);
            GL11.glVertex3d(w, -h, d);
            GL11.glVertex3d(-w, -h, d);
            
            // right face
            GL11.glNormal3d(1.0, 0.0, 0.0);
            GL11.glVertex3d(w, -h, -d);
            GL11.glVertex3d(w, h, -d);
            GL11.glVertex3d(w, h, d);
            GL11.glVertex3d(w, -h, d);
            
            // left face
            GL11.glNormal3d(-1.0, 0.0, 0.0);
            GL11.glVertex3d(-w, -h, -d);
            GL11.glVertex3d(-w, -h, d);
            GL11.glVertex3d(-w, h, d);
            GL11.glVertex3d(-w, h, -d);
        }
        GL11.glEnd();
    }

    //--------------------------------------------------------------------------

    private double w; // width/2
    private double h; // height/2
    private double d; // depth/2
    
    private FloatBuffer color;
}
