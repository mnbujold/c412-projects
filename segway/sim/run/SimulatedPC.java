package run;

import simulator.Simulator;

import comm.CommunicatorLogic;
import comm.SimulatedChannel;
import comm.SimulatedCommunicator;

import control.PCController;

/**
 * Simulated computer side.
 */
public final class SimulatedPC extends SimulatedRobotPC implements PC
{
    public SimulatedPC(Simulator sim, SimulatedChannel channel)
    {
        super (sim, channel, "sim-pc", new SimulatedPCLogic());        
        SimulatedPCLogic logic = (SimulatedPCLogic)logicObject();
        logic.pc = this;
    }
    
    /** Set the PC controller. */
    public void setController(PCController controller)
    {
        SimulatedPCLogic logic = (SimulatedPCLogic)logicObject();
        if (logic.controller != null)
            throw new IllegalStateException("Controller cannot be changed!");
        logic.controller = controller;
    }

    //--------------------------------------------------------------------------
    
    private static class SimulatedPCLogic extends ThreadLogic
    {
        @Override
        public void run()
        {
            try
            {
                controller.initialize();                
                while (pc.isRunning() && !controller.isTerminated())
                    controller.control();
            }
            catch (Exception e) { e.printStackTrace(System.err); }
        }
        
        private PCController controller = null;
        private SimulatedPC pc = null;
    }
    
    //--------------------------------------------------------------------------
    
    @Override
    public void createCommunicator(String device, CommunicatorLogic logic)
    {
        SimulatedCommunicator comm = new SimulatedCommunicator("sim-pc-comm",
                                                               device,
                                                               logic,
                                                               sim(),
                                                               channel());
        setCommunicator(comm);
        channel().setThread(comm);
        comm.start();
    }
}
