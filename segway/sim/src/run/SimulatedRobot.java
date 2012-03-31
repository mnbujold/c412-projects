package run;

import simulator.Simulator;

import comm.CommunicatorLogic;
import comm.SimulatedChannel;
import comm.SimulatedCommunicator;

import control.RobotController;

/**
 * Simulated NXT Segway robot side.
 */
public class SimulatedRobot extends SimulatedRobotPC implements Robot
{
    public SimulatedRobot(Simulator sim, SimulatedChannel channel)
    {
        super (sim, channel, "sim-nxt-robot", new SimulatedRobotLogic());
        SimulatedRobotLogic logic = (SimulatedRobotLogic)logicObject();
        logic.robot = this;
        
        leftPower = rightPower = 0;
        leftRotCtr = rightRotCtr = 0;
        
        distance = new int[sim.dist().length];
        for (int i = 0; i < distance.length; ++i) distance[i] = 0;
    }
    
    /** Set the robot controller. */
    public void setController(RobotController controller)
    {
        SimulatedRobotLogic logic = (SimulatedRobotLogic)logicObject();
        if (logic.controller != null)
            throw new IllegalStateException("Controller cannot be changed!");
        logic.controller = controller;
    }
    
    //--------------------------------------------------------------------------

    @Override
    public final void resetLeftRotationCounter()
    { resetLeftRotCtr = leftRotCtr; }
    
    @Override
    public final void resetRightRotationCounter()
    { resetRightRotCtr = rightRotCtr; }
    
    @Override
    public final int leftRotationCounter()
    { return leftRotCtr - resetLeftRotCtr; }

    @Override
    public final int rightRotationCounter()
    { return rightRotCtr - resetRightRotCtr; }

    //--------------------------------------------------------------------------
    
    @Override
    public final int readGyro() { return gyro; }

    @Override
    public int[] readDistances() { return distance; }
    
    //--------------------------------------------------------------------------
    
    /** @return applied power on the left motor (milliV) */
    public final int leftPower() { return leftPower; }
    
    /** @return applied power on the right motor (milliV) */
    public final int rightPower() { return rightPower; }
    
    @Override
    public final void controlLeftMotor(int power) { leftPower = power; }
    
    @Override
    public final void controlRightMotor(int power) { rightPower = power; }
    
    //--------------------------------------------------------------------------

    private static class SimulatedRobotLogic extends ThreadLogic
    {
        @Override
        public void run()
        {
            try
            {
                robot.observe();
                controller.initialize();
                
                robot.sim().standUpRobot();
                
                robot.observe();
                while (robot.isRunning() && !controller.isTerminated())
                    controller.control();
            }
            catch (Exception e) { e.printStackTrace(System.err); }
        }
        
        private RobotController controller = null;
        private SimulatedRobot robot = null;
    }

    //--------------------------------------------------------------------------
    
    @Override
    protected void observe()
    {
        synchronized (sim())
        {
            super.observe();        
            gyro = (int)(sim().readGyro());
            
            leftRotCtr = (int)(sim().readLeftRotationCounter());
            rightRotCtr = (int)(sim().readRightRotationCounter());
            
            for (int i = 0; i < distance.length; ++i)
                distance[i] = (int)(sim().readDistance(i));
        }
    }
    
    //--------------------------------------------------------------------------
    
    @Override
    public void createCommunicator(CommunicatorLogic logic)
    {
        SimulatedCommunicator comm = new SimulatedCommunicator("sim-robot-comm",
                                                               "sim-robot",
                                                               logic,
                                                               sim(),
                                                               channel());
        setCommunicator(comm);
        channel().setThread(comm);
        comm.start();
    }
    
    //--------------------------------------------------------------------------
        
    private int leftPower, rightPower; // milliV
    private int leftRotCtr, rightRotCtr; // deg
    private int resetLeftRotCtr, resetRightRotCtr; // deg
    private int gyro; // deg/sec
    private int[] distance; // mm
}
