package run;

import model.motion.State;
import model.sensor.DistanceState;
import model.sensor.GyroSensor.GyroState;
import simulator.Simulator;

import comm.Communicator;
import comm.SimulatedChannel;
import comm.SimulatedCommunicator;

/**
 * Simulated thread implementing the shared features
 * of the Robot and PC interfaces. 
 */
public class SimulatedRobotPC extends SimulatedThread implements RobotPC
{
    public SimulatedRobotPC(Simulator sim,
                            SimulatedChannel channel,
                            String name,
                            ThreadLogic logic)
    {
        super (sim, name, logic);
        this.channel = channel;
        comm = null;
        
        dynState = sim.state().copy();
        gyroState = sim.gyro().state().copy(new GyroState());
        distState = new DistanceState[sim.dist().length];
        for (int i = 0; i < distState.length; ++i)
            distState[i] = sim.dist()[i].state().copy(new DistanceState());
    }
    
    //--------------------------------------------------------------------------
   
    @Override
    public Communicator comm() { return comm; }
    
    protected void setCommunicator(SimulatedCommunicator comm)
    { this.comm = comm; }
    
    protected SimulatedChannel channel() { return channel; }
    
    //--------------------------------------------------------------------------
    
    @Override
    protected void observe()
    {
        synchronized (sim())
        {
            super.observe();
            sim().state().copy(dynState);
            sim().gyro().state().copy(gyroState);
            for (int i = 0; i < distState.length; ++i)
                sim().dist()[i].state().copy(distState[i]);
        }
    }
    
    @Override
    public final boolean isSimulated() { return true; }
    
    @Override
    public final State simDynState() { return dynState; }
    
    @Override
    public GyroState simGyroState() { return gyroState; }
    
    @Override
    public DistanceState[] simDistState() { return distState; }
    
    //--------------------------------------------------------------------------
    
    private final SimulatedChannel channel;
    private SimulatedCommunicator comm;
    
    // These states do not have to be protected by synchronization
    // as a (robot/pc) controller runs exclusively with the simulator.
    
    private final State dynState;
    private final GyroState gyroState;
    private final DistanceState[] distState;
}
