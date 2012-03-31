package visual;

import helper.Ratio;

import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.util.LinkedList;

import javax.swing.event.MouseInputAdapter;

import model.scene.SceneModel;
import model.scene.SceneModelObject;
import visual.ViewConfig.CameraLookAt;
import visual.engine.EngineCanvas;

/**
 * Canvas of the visualization scene.
 * 
 * Only one can be created at the same time
 * as the engine does not support shared contexts!
 */
class VisualCanvas extends EngineCanvas
{
    VisualCanvas(ViewConfig cfg, SceneModel scene)
    throws Exception
    {
        this.cfg = cfg;
        this.scene = scene;
        visualObjects = new LinkedList<VisualObject>();
        
        MouseInputAdapter mouseListener = new CanvasMouseListener(this);
        addMouseListener(mouseListener);
        addMouseMotionListener(mouseListener);
        addMouseWheelListener(mouseListener);

        // TODO implement fonts
        //timeFont = new VisualFont(new Font("Monospace", Font.BOLD, 24),
        //                          new Color(250, 250, 250));
        
        cameraTableCenter();
        setBackgroundColor(scene.background());
        hasCameraTrModified = hasCameraRotModified = true;
    }
    
    //--------------------------------------------------------------------------

    @Override
    protected void initalizeObjects()
    throws Exception
    {
        VisualObject vo;
        for (SceneModelObject smo : scene.objects())
        {
            vo = new VisualObject(smo);
            visualObjects.addLast(vo);
            vo.register();
        }
    };
    
    @Override
    protected void draw()
    {
        applyCameraSettings();
        setBackgroundColor(scene.background());
        synchronized (scene)
        {
            for (VisualObject vo : visualObjects) vo.draw();
        }
    }
    
    @Override
    protected void drawTextOnScreen()
    {
        super.drawTextOnScreen();
        
        //if (scene.showElapsedTime())
        //{
            // TODO implement fonts
            // timeFont.drawText(100, 50,
            //                  "" + (int)(scene.elapsedTime()*10)/10.0 + "s");
        //}
    }
    
    //--------------------------------------------------------------------------
    
    private static enum Op { NONE, TRANSLATE, ROTATE, ZOOM };
    
    private class CanvasMouseListener extends MouseInputAdapter
    {
        public CanvasMouseListener(VisualCanvas canvas)
        {
            this.canvas = canvas;
            op = Op.NONE;
        }
        
        @Override
        public void mousePressed(MouseEvent e)
        {
            if (e.getComponent() != canvas) return;
            switch (e.getButton())
            {
                case MouseEvent.BUTTON1 :
                    op = Op.ROTATE;
                    break;
                case MouseEvent.BUTTON3 :
                    if (cfg.cameraLookAt() == CameraLookAt.FREE)
                        op = Op.TRANSLATE;                    
                    break;
                default :
                    return;
            }
            x = e.getX(); y = e.getY();
        }
        
        @Override
        public void mouseDragged(MouseEvent e)
        {
            if (op == Op.NONE) return;
            
            double dx = e.getX() - x;
            double dy = e.getY() - y;
            
            switch (op)
            {
                case ROTATE :
                {
                    hasCameraRotModified = true;
                    cfg.setCameraPitch(cfg.cameraPitch()
                                       + dy * cfg.cameraChgRatePitch());
                    cfg.setCameraYaw(cfg.cameraYaw()
                                     + dx * cfg.cameraChgRateYaw());
                    break;
                }
                case TRANSLATE :
                {
                    hasCameraTrModified = true;
                    double yaw = cfg.cameraYaw() * Ratio.DEG_TO_RAD;
                    double cosYaw = Math.cos(yaw);
                    double sinYaw = Math.sin(yaw);
                    cfg.setCameraX(cfg.cameraX() + cfg.cameraCghRateX()
                                                 * (dy*sinYaw - dx*cosYaw));
                    cfg.setCameraY(cfg.cameraY() + cfg.cameraCghRateY()
                                                 * (dx*sinYaw + dy*cosYaw));
                    break;
                }
            }
            
            x = e.getX(); y = e.getY();
            repaint();
        }
        
        @Override
        public void mouseReleased(MouseEvent e)
        {
            if (op != Op.NONE) op = Op.NONE;
        }
        
        @Override
        public void mouseWheelMoved(MouseWheelEvent e)
        {
            hasCameraRotModified = true;
            cfg.setCameraDistance(cfg.cameraDistance()
                                  + e.getWheelRotation()
                                      * cfg.cameraChgRateDistance());
            repaint();
        }
        
        private Op op;
        private VisualCanvas canvas;
        private double x, y;
    }

    private void applyCameraSettings()
    {
        if (hasCameraRotModified)
        {
            hasCameraRotModified = false;
            rotateCamera(cfg.cameraPitch(),
                         cfg.cameraYaw(),
                         cfg.cameraDistance());
        }
        if (hasCameraTrModified)
        {
            hasCameraTrModified = false;
            translateCamera(cfg.cameraX(), cfg.cameraY(), cfg.cameraZ());
        }
    }
    
    private void cameraTableCenter()
    {
        cfg.setCameraX(scene.floor().width()/2);
        cfg.setCameraY(scene.floor().height()/2);
        cfg.setCameraZ(scene.carpet() != null ? scene.carpet().height() : 0);
    }
    
    //--------------------------------------------------------------------------

    private boolean hasCameraTrModified, hasCameraRotModified;
    
    private final ViewConfig cfg;
    private final SceneModel scene;
    private final LinkedList<VisualObject> visualObjects;
    // private final VisualFont timeFont; TODO implement fonts
    
    private static final long serialVersionUID = 1L;
}
