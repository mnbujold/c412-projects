package run;

import model.motion.State;
import model.sensor.DistanceState;
import model.sensor.GyroSensor.GyroState;

import comm.Communicator;
import comm.CommunicatorLogic;
import comm.NXTPCCommunicator;

import control.PCController;

/**
 * PC implementation for communicating with a real NXT robot.
 */
public class NXTPC extends NXTPCThread implements PC
{
    public NXTPC()
    {
        super ("nxt-pc", new NXTPCLogic(), true);
        comm = null;
    }
    
    /** Set the PC controller. */
    public void setController(PCController controller)
    {
        NXTPCLogic logic = (NXTPCLogic)logicObject();
        if (logic.controller != null)
            throw new IllegalStateException("Controller cannot be changed!");
        logic.controller = controller;
    }
    
    //--------------------------------------------------------------------------
    
    @Override
    public boolean isSimulated() { return false; }
    
    @Override
    public State simDynState() { return null; }
    
    @Override
    public GyroState simGyroState() { return null; }
    
    @Override
    public DistanceState[] simDistState() { return null; }
    
    //--------------------------------------------------------------------------
    
    @Override
    public void createCommunicator(String device, CommunicatorLogic logic)
    {
        comm = new NXTPCCommunicator(device, logic);
        comm.start();
    }
    
    @Override
    public Communicator comm() { return comm; }
    
    //--------------------------------------------------------------------------

    private static class NXTPCLogic extends ThreadLogic
    {
        @Override
        public void run()
        {
            try
            {
                controller.initialize();
                while (!controller.isTerminated()) controller.control();
            }
            catch (Exception e) { e.printStackTrace(System.err); }
        }
        
        private PCController controller = null;
    }
    
    //--------------------------------------------------------------------------
    
    
    private NXTPCCommunicator comm;
}
