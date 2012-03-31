package visual.draw;

import org.lwjgl.opengl.GL11;
import org.lwjgl.util.Color;

public class Buoy extends Wheel
{
    public Buoy(double x, double z, Color color)
    {
        super(20, 0.1, 1.0, color);
        this.x = x;
        this.z = z;
        y = getD();
    }

    //--------------------------------------------------------------------------

    @Override
    public void draw()
    {
        GL11.glPushMatrix();
        GL11.glTranslated(x, y, z);
        GL11.glRotatef(90.0f, 0.0f, 0.0f, 1.0f);
        super.draw();
        GL11.glPopMatrix();
    }

    //--------------------------------------------------------------------------

    double x, y, z;
}
