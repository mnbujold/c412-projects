package solution;

import geom3d.Point3D;

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

/**
 * An autonomously navigating controller
 * based on a particle filtering localization.
 */
public class AutoNavPFLocPCController extends PCController
{
    /** Bluetooth name of the NXT robot. */
    public static final String ROBOT_NAME = "suzy";
    
    //--------------------------------------------------------------------------
    
    public AutoNavPFLocPCController(PC pc,
                                    MotionConfig motionCfg,
                                    SceneModel scene,
                                    View view)
    {
        super (pc, motionCfg, scene, view);
        mouseListener = new MouseListenerImpl();
        commLogic = new CommunicatorLogicImpl();
        dist = null;
        
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
            scene().update(pc().simDynState());
        view().updateCanvas();
        
        pc().createCommunicator(ROBOT_NAME, commLogic);
    }
    
    @Override
    public void control() throws Exception
    {
        double pitch = 0;
        int dMrcL = 0, dMrcR = 0;
        boolean isNewData = false;
        synchronized (commLogic)
        {
            isNewData = commLogic.isNewData;
            if (isNewData)
            {
                dMrcL = commLogic.dMrcL;
                dMrcR = commLogic.dMrcR;
                commLogic.dMrcL = commLogic.dMrcR = 0;
                
                if (dist == null) dist = new int[commLogic.dist.length];
                for (int i = 0; i < dist.length; ++i)
                    dist[i] = commLogic.dist[i];
                
                pitch = commLogic.pitch;
                System.out.println("pitch: " + commLogic.pitch);
                //pitch = pc().simDynState().pitch(); // TODO !!!
            }
        }
        
        if (isNewData)
        {
            //System.out.println("obs: " + pitch + " , (" + dMrcL + " , " + dMrcR
            //                           + ") , [" + dist[0] + "," + dist[1]
            //                                    + "," + dist[2] + "]");
            pf.next(pitch, dMrcL, dMrcR, dist);
            
            // showing the true state in simulation
            if (pc().isSimulated())
                scene().update(pc().simDynState());
        }
        view().updateCanvas();
        pc().msDelay(200);
    }
    
    //--------------------------------------------------------------------------
    
    private class CommunicatorLogicImpl extends CommunicatorLogic
    {
        public CommunicatorLogicImpl() { dist = null; }
        
        @Override
        public void initalize() throws Exception
        {
            super.initalize();
            System.out.println("reading...");
            maxKeyCodes = channel().readByte();
            System.out.println("maxKeyCodes: " + maxKeyCodes);
            int len = channel().readByte();
            System.out.println("len = " + len);
            synchronized (this)
            {
                dist = new short[len];
                distTmp = new short[len];
                dMrcL = dMrcR = 0;
                pitch = 0.0;
                isNewData = false;
            }
            isControlRound = true;
        }
        
        @Override
        public void logic() throws Exception
        {
            if (isControlRound)
            {
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
                }
                channel().flush();                
            }
            else
            {
                channel().writeByte((byte)-2);
                channel().flush();
                
                float pitch = channel().readFloat();
                short dL = channel().readShort();
                short dR = channel().readShort();
                for (int i = 0; i < distTmp.length; ++i)
                    distTmp[i] = channel().readShort();
                
                synchronized (this)
                {
                    this.pitch = pitch;
                    dMrcL += dL;
                    dMrcR += dR;
                    for (int i = 0; i < dist.length; ++i)
                        dist[i] = distTmp[i];
                    isNewData = true;
                }
            }
            isControlRound = !isControlRound;
            msDelay(50);
        }
        
        private short[] dist, distTmp;
        private int dMrcL, dMrcR;
        private double pitch;
        private boolean isControlRound, isNewData;
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
    
    private int[] dist;
    
    private final ParticleFilterAlg pf;
    private final CommunicatorLogicImpl commLogic;
    private final MouseListenerImpl mouseListener;
}
