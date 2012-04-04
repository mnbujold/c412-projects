package model.scene;

import java.util.LinkedList;

public final class Drawer extends AbstractSceneModelObject
{
    /**
     * @param scene owner scene
     * @param z elevation of the drawing from the floor
     */
    public Drawer(SceneModel scene, double z)
    {
        this.scene = scene;
        this.z = (float)z;
        width = 2f;
        
        sLines = lines1; sRects = rects1;
        dLines = lines2; dRects = rects2;
    }
    
    //--------------------------------------------------------------------------
    
    public float z() { return z; }
    public void setZ(float z) { this.z = z; }
    
    /** @return line and empty rectangle boundary width */
    public float width() { return width; }
    public void setWidth(float width) { this.width = width; }
    
    public LinkedList<Line> shownLines() { return sLines; }
    public LinkedList<Rect> shownRects() { return sRects; }
    
    //--------------------------------------------------------------------------
    
    public synchronized void clear()
    {
        unusedLines.addAll(dLines); dLines.clear();
        unusedRects.addAll(dRects); dRects.clear();        
    }
    
    /**
     * Draw a line between (x1,y1) and (x2,y2) with the specified color.
     */
    public synchronized void drawLine(double x1, double y1,
                                      double x2, double y2,
                                      Color color)
    {
        Line line =
            unusedLines.isEmpty() ? new Line() : unusedLines.pollFirst();
        line.set(x1, y1, x2, y2, color);
        dLines.add(line);
    }

    /**
     * Draw a rectangle span by the (x1,y1) and (x2,y2) points with the
     * specified color. If isFilled is true, the rectangle is drawn filled,
     * otherwise it is drawn empty.
     */
    public synchronized void drawRect(double x1, double y1,
                                      double x2, double y2,
                                      Color color, boolean isFilled)
    {
        Rect rect =
            unusedRects.isEmpty() ? new Rect() : unusedRects.pollFirst();
        rect.set(x1, y1, x2, y2, color, isFilled);
        dRects.add(rect);
    }
    
    public synchronized void swapBuffers()
    {
        LinkedList<Line> tLines = sLines;
        LinkedList<Rect> tRects = sRects;
        
        synchronized (scene) // synchronizing with current visualization
        {
            sLines = dLines; sRects = dRects;
            dLines = tLines; dRects = tRects;
        }
    }
    
    //--------------------------------------------------------------------------
    
    public final static class Line
    {
        private float x1, y1, x2, y2;
        private Color color;

        public float x1() { return x1; }
        public float y1() { return y1; }
        public float x2() { return x2; }
        public float y2() { return y2; }
        public Color color() { return color; }
        
        private void set(double x1, double y1, double x2, double y2,
                         Color color)
        {
            this.x1 = (float)x1; this.y1 = (float)y1;
            this.x2 = (float)x2; this.y2 = (float)y2;
            this.color = color;
        }
    }
    
    public final static class Rect
    {
        private float x1, y1, x2, y2;
        private Color color;
        private boolean isFilled;
        
        public float x1() { return x1; }
        public float y1() { return y1; }
        public float x2() { return x2; }
        public float y2() { return y2; }
        public Color color() { return color; }
        public boolean isFilled() { return isFilled; }
        
        private void set(double x1, double y1, double x2, double y2,
                         Color color, boolean isFilled)
        {
            this.x1 = (float)x1; this.y1 = (float)y1;
            this.x2 = (float)x2; this.y2 = (float)y2;
            this.color = color; this.isFilled = isFilled;
        }
    }
    
    //--------------------------------------------------------------------------
    
    @Override
    public boolean canBeHit() { return false; }
    
    @Override
    public boolean isLighted() { return false; }
    
    // Its rendering is specially handled in VisualObject.
    
    //--------------------------------------------------------------------------
    
    private float z, width;
    private LinkedList<Line> sLines, dLines;
    private LinkedList<Rect> sRects, dRects;
    
    private final LinkedList<Line> lines1 = new LinkedList<Line>();
    private final LinkedList<Rect> rects1 = new LinkedList<Rect>();
    
    private final LinkedList<Line> lines2 = new LinkedList<Line>();
    private final LinkedList<Rect> rects2 = new LinkedList<Rect>();
    
    private final LinkedList<Line> unusedLines = new LinkedList<Line>();
    private final LinkedList<Rect> unusedRects = new LinkedList<Rect>();
    
    private final SceneModel scene;
}
