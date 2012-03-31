package exercise;

import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Set;

import javax.swing.event.MouseInputAdapter;

import model.motion.MotionConfig;
import model.scene.SceneModel;
import run.PC;
import visual.View;

import comm.CommunicatorLogic;

import control.PCController;
import geom3d.Point3D;

/**
 * An autonomously navigating controller
 * based on a particle filtering localization.
 */
public class AutoNavPFLocPCController extends PCController
{
    /** Bluetooth name of the NXT robot. */
    public static final String ROBOT_NAME = "nxt-robot";
    
    //--------------------------------------------------------------------------
    
    public AutoNavPFLocPCController(PC pc,
                                    MotionConfig motionCfg,
                                    SceneModel scene,
                                    View view)
    {
        super (pc, motionCfg, scene, view);
        mouseListener = new MouseListenerImpl();
        commLogic = new CommunicatorLogicImpl();
        pc().createCommunicator(ROBOT_NAME, commLogic);
        
        pf = new ParticleFilterAlg(19, motionCfg, scene);
        scene.addParticleFilter(pf);
        
        // You can disable particle cloud visualization by
        // scene.particleCloud().setEnabled(false);
    }
    
    @Override
    public void initialize() throws Exception
    {
        super.initialize();
        pf.init();
        
        // showing the true state in simulation
        if (pc().isSimulated())
        {
            scene().update(pc().simDynState());
        }
        view().updateCanvas();
    }
    
    @Override
    public void control() throws Exception
    {
    	//This is the higher priority right now!!!
    	pf.next(/*need IR outputs, Gyro outputs, the change in wheel rotation*/);
        
        // showing the true state in simulation
        if (pc().isSimulated())
            scene().update(pc().simDynState());
        
        view().updateCanvas();
        pc().msDelay(50);
        //this is how we read from the robot channel??
        //commLogic.channel().readByte();
    }
    
    //--------------------------------------------------------------------------
    
    private class CommunicatorLogicImpl extends CommunicatorLogic
    {
        @Override
        public void initalize() throws Exception
        {
            super.initalize();
            maxKeyCodes = channel().readByte();
        }
        
        @Override
        public void logic() throws Exception
        {
            // TODO modify this to receive observations
            
            Set<Short> activeKeys = activeKeys();
            synchronized (activeKeys)
            {
                if (activeKeys.contains((short)KeyEvent.VK_ESCAPE))
                {
                    channel().writeByte((byte)-1);
                    terminate();
                }
                else
                {
                    byte size = (byte) activeKeys.size();
                    if (size > maxKeyCodes) size = maxKeyCodes;
                    
                    channel().writeByte(size);
                    for (short keycode : activeKeys)
                    {
                        channel().writeShort(keycode);
                        if (--size == 0) break;
                    }
                }
                channel().flush();
            }
            msDelay(100);
        }
        
        private byte maxKeyCodes;
    }
    
    //--------------------------------------------------------------------------
    
    private class MouseListenerImpl extends MouseInputAdapter
    {
        @Override
        public void mouseClicked(MouseEvent e)
        {
            view().getPoint(e.getX(), e.getY(), p);
            if (scene().isOnFloor(p.x(), p.y()))
            {
                p.setZ(scene().isOnCarpet(p.x(), p.y())
                       ? (1+scene().carpet().height()) : 1);
                scene().selectedPoint().setPosition(p);
            }
            else scene().selectedPoint().setPosition(null);
        }
        
        private Point3D p = new Point3D();
    }
    
    @Override
    public MouseListener mouseListener() { return mouseListener; }
    
    //--------------------------------------------------------------------------
    
    private final ParticleFilterAlg pf;
    private final CommunicatorLogicImpl commLogic;
    private final MouseListenerImpl mouseListener;
}
