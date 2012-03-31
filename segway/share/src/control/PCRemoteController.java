package control;

import java.awt.event.KeyEvent;
import java.util.Set;

import model.motion.MotionConfig;
import model.scene.SceneModel;
import run.PC;
import visual.View;

import comm.CommunicatorLogic;


public class PCRemoteController extends PCController
{
    /** Bluetooth name of the NXT robot. */
    public static final String ROBOT_NAME = "suzy";
    
    //--------------------------------------------------------------------------
    
    public PCRemoteController(PC pc,
                              MotionConfig motionCfg,
                              SceneModel scene,
                              View view)
    {
        super (pc, motionCfg, scene, view);
        commLogic = new CommunicatorLogicImpl();
        pc().createCommunicator(ROBOT_NAME, commLogic);
    }
    
    @Override
    public void initialize() throws Exception
    {
        super.initialize();
        show();
    }
    
    @Override
    public void control() throws Exception
    {
        show();
        pc().msDelay(10);
    }
    
    private void show()
    {
        if (pc().isSimulated())
        {
            // visualize the simulation state
            scene().update(pc().simDynState());
            view().updateCanvas();
        }
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
    
    private final CommunicatorLogicImpl commLogic;
}
