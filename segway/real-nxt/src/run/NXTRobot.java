package run;

import lejos.nxt.BasicMotorPort;
import lejos.nxt.LCD;
import lejos.nxt.MotorPort;
import lejos.nxt.NXTRegulatedMotor;
import lejos.nxt.SensorPort;
import lejos.nxt.Sound;
import lejos.nxt.addon.GyroSensor;
import lejos.nxt.addon.OpticalDistanceSensor;
import lejos.util.Delay;
import model.motion.State;
import model.sensor.DistanceState;
import model.sensor.GyroSensor.GyroState;

import comm.Communicator;
import comm.CommunicatorLogic;
import comm.NXTRobotCommunicator;
import control.RobotController;

/**
 * Access to a NXT Segway robot.
 */
public final class NXTRobot extends NXTRobotThread implements Robot
{
    // Configuration:
    
    private static final MotorPort LEFT_MOTOR = MotorPort.C;
    private static final MotorPort RIGHT_MOTOR = MotorPort.B;
    private static final SensorPort GYRO_PORT = SensorPort.S4;
    
    private static final SensorPort[] IR_PORTS = { SensorPort.S1, SensorPort.S2, SensorPort.S3 };
    
    private static RobotController CREATE_CONTROLLER(Robot robot)
    {
        return new exercise.AutoNavPFLocRobotController(robot);
        //return new solution.AutoNavPFLocRobotController(robot);
    }
    
    /** Counting amount (with beeps) before controlling. */
    private static final int BEEP_COUNT = 3;
    
    //--------------------------------------------------------------------------

    public static void main(String[] args)
    {
        NXTRobot robot = new NXTRobot(LEFT_MOTOR, RIGHT_MOTOR, GYRO_PORT);
        robot.setController(CREATE_CONTROLLER(robot));
        robot.start();
    }

    @SuppressWarnings("unused")
    private static class StopRegulator extends NXTRegulatedMotor
    {
        public StopRegulator(MotorPort mp) { super(mp); }
        static
        {
            while (!cont.isAlive()) Delay.msDelay(100);
            cont.interrupt(); // stop the LEJOS speed regulator thread
        }
    }

    //--------------------------------------------------------------------------
    
    public NXTRobot(MotorPort leftMotor,
                    MotorPort rightMotor,
                    SensorPort gyroPort)
    {
        super ("nxt-robot", new NXTRobotLogic(), false);
        
        this.leftMotor = leftMotor;
        this.rightMotor = rightMotor;
        gyro = new GyroSensor(gyroPort);
        comm = null;
        
        distance = new int[IR_PORTS.length];
        ir = new OpticalDistanceSensor[IR_PORTS.length];
        for (int i = 0; i < IR_PORTS.length; ++i)
        {
            distance[i] = 0;
            ir[i] = new OpticalDistanceSensor(IR_PORTS[i]);
        }
        
        leftMotor.setPWMMode(BasicMotorPort.FLOAT);
        rightMotor.setPWMMode(BasicMotorPort.FLOAT);
        
        controlLeftMotor(0);
        controlRightMotor(0);
    }
    
    /** Set the robot controller. */
    public void setController(RobotController controller)
    {
        NXTRobotLogic logic = (NXTRobotLogic)logicObject();
        if (logic.controller != null)
            throw new IllegalStateException("Controller cannot be changed!");
        logic.controller = controller;
    }
    
    //--------------------------------------------------------------------------
    
    @Override
    public final void resetLeftRotationCounter()
    { leftMotor.resetTachoCount(); }
    
    @Override
    public final void resetRightRotationCounter()
    { rightMotor.resetTachoCount(); }
    
    @Override
    public final int leftRotationCounter()
    { return leftMotor.getTachoCount(); }
    
    @Override
    public final int rightRotationCounter()
    { return rightMotor.getTachoCount(); }
    
    //--------------------------------------------------------------------------
    
    @Override
    public final int readGyro() { return gyro.readValue(); }

    @Override
    public int[] readDistances()
    {
        for (int i = 0; i < IR_PORTS.length; ++i)
            distance[i] = ir[i].getDistance();
        return distance;
    }
    
    //--------------------------------------------------------------------------
    
    @Override
    public void controlLeftMotor(int power)
    { leftMotor.controlMotor(power, BasicMotorPort.FORWARD); }
    
    @Override
    public void controlRightMotor(int power)
    { rightMotor.controlMotor(power, BasicMotorPort.FORWARD); }
    
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
    public void createCommunicator(CommunicatorLogic logic)
    {
        comm = new NXTRobotCommunicator(logic);
        comm.start();
    };
    
    @Override
    public Communicator comm() { return comm; }
    
    //--------------------------------------------------------------------------
    
    private static final class NXTRobotLogic extends ThreadLogic
    {
        @Override
        public void run()
        {
            try
            {
                LCD.clear();
                LCD.drawString("NXTRobot", 0, 0);
                LCD.drawString("Initialization", 0, 3);
                LCD.refresh();
                
                msDelay(1000);
                controller.initialize();
                
                LCD.clear();
                LCD.drawString("NXTRobot", 0, 0);
                for (int c = BEEP_COUNT; c > 0; --c)
                {
                    LCD.drawString("Controlling in", 0, 3);
                    LCD.drawString("  " + c + " seconds...", 0, 4);
                    LCD.refresh();
                    Sound.playTone(440, 100);
                    msDelay(1000);
                }
                LCD.clear();
                LCD.drawString("NXTRobot", 0, 0);
                LCD.drawString("Controlling...", 0, 3);
                LCD.refresh();
                Sound.playTone(440, 100);
                msDelay(1000);
                
                while (!controller.isTerminated()) controller.control();
                msDelay(2000);
            }
            catch (Exception e) {}
        }
        
        private RobotController controller = null;
    }
    
    //--------------------------------------------------------------------------
    
    private NXTRobotCommunicator comm;
    
    private final MotorPort leftMotor, rightMotor;
    private final GyroSensor gyro;
    private final OpticalDistanceSensor[] ir;
    private final int[] distance;
}
