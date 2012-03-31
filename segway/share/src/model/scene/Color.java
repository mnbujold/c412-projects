package model.scene;

/**
 * Color representation of scene objects.
 */
public class Color
{
    public Color(float red, float green, float blue, float alpha)
    {
        assert (0 <= red && red <= 255);
        assert (0 <= green && green <= 255);
        assert (0 <= blue && blue <= 255);
        assert (0 <= alpha && alpha <= 255);
        r = red; g = green; b = blue; a = alpha;
    }
    
    public Color(float red, float green, float blue)
    {
        this (red, green, blue, 255);
    }
    
    public Color copy()
    {
        return new Color(red(), green(), blue(), alpha());
    }
    
    //--------------------------------------------------------------------------
    
    public float red() { return r; }
    public float green() { return g; }
    public float blue() { return b; }
    public float alpha() { return a; }
    
    //--------------------------------------------------------------------------
    
    private final float r, g, b, a;
}
