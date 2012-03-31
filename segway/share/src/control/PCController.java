package control;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseListener;
import java.util.HashSet;
import java.util.Set;

import model.motion.MotionConfig;
import model.scene.SceneModel;
import run.PC;
import visual.View;

/**
 * Shared implementation of a PC controller.
 */
public abstract class PCController extends Controller
{
    public PCController(PC pc,
                        MotionConfig motionCfg,
                        SceneModel scene,
                        View view)
    {
        this.pc = pc;
        this.motionCfg = motionCfg;
        this.scene = scene;
        this.view = view;
        
        activeKeys = new HashSet<Short>();
        keyListener = new KeyListenerImpl();
    }
    
    /** @return interface to the "controlled pc" */
    public PC pc() { return pc; }
    
    /** @return the configuration of the motion model */
    public MotionConfig motionCfg() { return motionCfg; }
    
    /** @return the model of the scene */
    public SceneModel scene() { return scene; }
    
    /** @return the visualization of the scene */
    public View view() { return view; }
    
    /** @return active key codes */
    public Set<Short> activeKeys() { return activeKeys; }
    
    /** @return key listener of the PC controller */
    public KeyListener keyListener() { return keyListener; }
    
    /** @return mouse listener of the PC controller */
    public MouseListener mouseListener() { return null; }
    
    //--------------------------------------------------------------------------
    
    private class KeyListenerImpl implements KeyListener
    {
        @Override
        public void keyPressed(KeyEvent e)
        {
            synchronized (activeKeys)
            { activeKeys.add((short)e.getKeyCode()); }
        }
        
        @Override
        public void keyReleased(KeyEvent e)
        {
            synchronized (activeKeys)
            { activeKeys.remove((short)e.getKeyCode()); }
        }
        
        @Override
        public void keyTyped(KeyEvent e)
        {}
    }
    
    //--------------------------------------------------------------------------
    
    private final PC pc;
    private final HashSet<Short> activeKeys;
    private final KeyListenerImpl keyListener;
    
    private final MotionConfig motionCfg;
    private final SceneModel scene;
    private final View view;
}
