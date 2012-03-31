package visual;

import geom3d.Point3D;

import java.awt.BorderLayout;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.nio.FloatBuffer;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.swing.JFrame;

import model.scene.SceneModel;

import org.lwjgl.BufferUtils;

import visual.engine.EngineCanvas;

/**
 * The main visualization window (singleton).
 */
public final class View extends JFrame
{
    private View(SceneModel sceneModel, ViewConfig viewConfig)
    throws Exception
    {
        setTitle("Segway");
        setLayout(new BorderLayout());
        
        closeRequested = new AtomicBoolean(false);        
        addWindowListener(new WindowAdapter()
        {
            @Override
            public void windowClosing(WindowEvent e)
            {
                dispose();
                closeRequested.set(true);
            }
        });
               
        canvas = new VisualCanvas(viewConfig, sceneModel);
        canvas.setSize(800, 600);
        canvas.setFocusable(false);
        add(canvas, BorderLayout.CENTER);
        
        setResizable(true);
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }
    
    private static View INSTANCE = null;
    private static final long serialVersionUID = 1L;
    
    public static View create(SceneModel sceneModel, ViewConfig options)
    throws Exception
    {
        INSTANCE = new View(sceneModel, options);
        return instance();
    }
    public static View instance() { return INSTANCE; }

    //--------------------------------------------------------------------------
    
    public boolean isCloseRequested() { return closeRequested.get(); }
    public float fps() { return canvas.fps(); }
    public EngineCanvas.Info info() { return canvas.info(); }
    public boolean isInitialized() { return canvas.isInitialized(); }
    public void updateCanvas() { canvas.repaint(); }
    
    //--------------------------------------------------------------------------
    
    @Override
    public synchronized void addMouseListener(MouseListener l)
    { canvas.addMouseListener(l); }
    
    //--------------------------------------------------------------------------
    
    /** @return the point (p) in the model view
     *          corresponds to (x,y) the window coordinates */
    public synchronized void getPoint(int x, int y, Point3D p)
    {
        canvas.getPoint(x, y, pfb);
        p.set(pfb.get(0), pfb.get(1), pfb.get(2));
    }
    
    //--------------------------------------------------------------------------
    
    private AtomicBoolean closeRequested;
    private final VisualCanvas canvas;
    
    private final FloatBuffer pfb = BufferUtils.createFloatBuffer(3);
}
