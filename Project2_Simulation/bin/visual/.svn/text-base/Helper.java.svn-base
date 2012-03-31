package visual;

import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.util.Color;

public class Helper
{
    public static FloatBuffer createFlippedFB(float[] floats)
    {
        FloatBuffer fb = BufferUtils.createFloatBuffer(floats.length);
        fb.put(floats).flip();
        return fb;
    }

    public static FloatBuffer colorToFB(Color color)
    {
        float[] floats = new float[]{
                ((float)color.getRed()) / 255.0f,
                ((float)color.getGreen()) / 255.0f,
                ((float)color.getBlue()) / 255.0f,
                ((float)color.getAlpha()) / 255.0f
        };
        return createFlippedFB(floats);
    }
}
