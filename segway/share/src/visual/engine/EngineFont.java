package visual.engine;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.LinkedList;

import org.lwjgl.opengl.GL11;

/**
 * Visualization of AWT fonts.
 * Fonts has to be created during the GL initialization phase.
 */
public class EngineFont
{
    private EngineFont(Font font)
    {
        assert (fonts != null);
        this.font = font;
        fonts.add(this);
    }
    
    /** @return the AWT representation of the font */
    public Font awt() { return font; }

    //--------------------------------------------------------------------------
    
    public void glPrint(String text)
    {
        // TODO
        // advance widths ?
        GL11.glPushAttrib(GL11.GL_LIST_BIT);
        GL11.glListBase(base);
        // GL11.glCallLists(bytes);
        GL11.glPopAttrib();
    }
    
    //--------------------------------------------------------------------------

    private void build(char[] chars)
    {
        // determining font metrics
        
        int maxAscent, maxDescent, maxAdvance;
        {
            BufferedImage image =
                new BufferedImage(1, 1, BufferedImage.TYPE_4BYTE_ABGR);
            Graphics2D g = (Graphics2D) image.getGraphics();
            g.setFont(font);
            FontMetrics metrics = g.getFontMetrics();
            maxAscent = metrics.getMaxAscent();
            maxDescent = metrics.getMaxDescent();
            maxAdvance = metrics.getMaxAdvance();
        }
        
        int width = maxAdvance, height = maxAscent + maxDescent;
        assert (0 != (width % 2) && 0 != (height % 2));
        isAlongWidth = (width >= height);
        
        // drawing all the characters to an image
        
        BufferedImage image;
        if (isAlongWidth)
        {
            image = new BufferedImage(width * chars.length, height,
                                      BufferedImage.TYPE_4BYTE_ABGR);
            Graphics2D g = (Graphics2D) image.getGraphics();
            g.setFont(font);
            g.setColor(OPAQUE_WHITE);
            g.setBackground(TRANSPARENT_BLACK);
            for (int i = 0; i < chars.length; ++i)
                g.drawString("" + chars[i], i * width, maxAscent);
        }
        else
        {
            image = new BufferedImage(width, height * chars.length,
                                      BufferedImage.TYPE_4BYTE_ABGR);
            Graphics2D g = (Graphics2D) image.getGraphics();
            g.setFont(font);
            g.setColor(OPAQUE_WHITE);
            g.setBackground(TRANSPARENT_BLACK);
            for (int i = 0; i < chars.length; ++i)
                g.drawString("" + chars[i], 0, i * height);
        }
        
        // creating the font texture
        
        ByteBuffer buffer = ByteBuffer.allocateDirect(
                                image.getWidth() * image.getHeight() * 4);
        buffer.order(ByteOrder.nativeOrder());
        buffer.put(((DataBufferByte)image.getData().getDataBuffer()).getData());
        buffer.flip();
        
        textureId = GL11.glGenTextures();
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureId);
        
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D,
                             GL11.GL_TEXTURE_MIN_FILTER,
                             GL11.GL_LINEAR);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D,
                             GL11.GL_TEXTURE_MAG_FILTER,
                             GL11.GL_LINEAR);
        
        GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA,
                          image.getWidth(), image.getHeight(), 0,
                          GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, buffer);
        
        // generating the display lists
        
        base = GL11.glGenLists(256);
        
        float d = 1f / chars.length;
        if (isAlongWidth)
        {
            for (int i = 0; i < chars.length; ++i)
            {
                float w = i * d;
                GL11.glNewList(base + i, GL11.GL_COMPILE);
                GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureId);
                GL11.glBegin(GL11.GL_QUADS);
                    GL11.glTexCoord2f(w+d, 1f);
                    GL11.glVertex2f(0.5f, 0.5f);
                    GL11.glTexCoord2f(w, 1f);
                    GL11.glVertex2f(-0.5f, 0.5f);
                    GL11.glTexCoord2f(w, 0f);
                    GL11.glVertex2f(-0.5f, -0.5f);
                    GL11.glTexCoord2f(w+d, 0f);
                    GL11.glVertex2f(0.5f, -0.5f);
                GL11.glEnd();
                // TODO glTranslate by ascent !
                GL11.glEndList();
            }
        }
        else
        {
            for (int i = 0; i < chars.length; ++i)
            {
                float h = i * d;
                GL11.glNewList(base + i, GL11.GL_COMPILE);
                GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureId);
                GL11.glBegin(GL11.GL_QUADS);
                    GL11.glTexCoord2f(1f, h+d);
                    GL11.glVertex2f(0.5f, 0.5f);
                    GL11.glTexCoord2f(0f, h+d);
                    GL11.glVertex2f(-0.5f, 0.5f);
                    GL11.glTexCoord2f(0f, h);
                    GL11.glVertex2f(-0.5f, -0.5f);
                    GL11.glTexCoord2f(1f, h);
                    GL11.glVertex2f(0.5f, -0.5f);
                GL11.glEnd();
                GL11.glEndList();
            }
        }
    }
    
    //--------------------------------------------------------------------------
    
    private final Font font;
    private int base, textureId;
    private boolean isAlongWidth;
    
    //--------------------------------------------------------------------------
    
    static LinkedList<EngineFont> fonts = new LinkedList<EngineFont>();
    
    static final char[] ASCII_CHARS = new char[256];
    static final Color OPAQUE_WHITE = new Color(0xFFFFFFFF, true);
    static final Color TRANSPARENT_BLACK = new Color(0x00000000, true);
    
    static void buildFonts()
    {
        assert (fonts != null);
        
        for (int i = 0; i < ASCII_CHARS.length; ++i) ASCII_CHARS[i] = (char)i; 
        for (EngineFont ef : fonts) ef.build(ASCII_CHARS);
        fonts = null;
    }
}
