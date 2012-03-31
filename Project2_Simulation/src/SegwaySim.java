import helper.TextFileHelper;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Locale;
import java.util.Random;

import model.ModelParameters;
import model.MotionDynamics;
import model.State;
import sensor.GyroDynamics;
import sensor.GyroSensor;
import sensor.Observation;
import student.GyroDynamicsImpl;
import student.HTWayController;
import visual.Keyboard;
import visual.View;
import control.Action;
import control.Controller;

/**
 * Segway simulator based on
 * <url>http://www.mathworks.com/matlabcentral/fileexchange/19147</url>,
 * but extended and modified to be more accurate.
 */
public class SegwaySim
{
    /**
     * Switches between the TA and the student package.
     * If this is true, the TA package is used, otherwise the student one.
     */
    final boolean TA_MODE = false;

    /**
     * Seed of the random number generator.
     * If this is non-positive, the seed is determined automatically.
     */
    final long RNG_SEED = 0;

    /**
     * Simulation sampling frequency in seconds.
     */
    final double SIM_SAMPLING_TIME = 0.01;

    /**
     * Minimum controller sampling time in seconds. 
     */
    final double MIN_CONTROL_SAMPLING_TIME = Math.max(0.01, SIM_SAMPLING_TIME);

    /**
     * Optionally, one can put a zero mean Gaussian noise to the sampling time
     * (of the controller) to make it a bit more realistic. Then this value 
     * specify the variance of the sampling time noise distribution. One can 
     * switch this functionality off by setting this value non-positive.
     */
    final double SAMPLING_VAR = 0.001;

    /**
     * The controller can access the true state which is good for statistical
     * or experimental purposes. However, it is also useful to be sure that
     * the controller is not cheating. So this switch can enable/disable this 
     * feature. When this is disabled, only <code>null</code> is provided 
     * instead of the real state.
     */
    final boolean ENABLE_NULL_STATE = false;

    /**
     * Initial x position of the axle midpoint in meters.
     */
    final double INIT_X = 0.0;

    /**
     * Initial y position of the axle midpoint in meters.
     */
    final double INIT_Y = 0.0;

    /**
     * The initial left and right pushing power is drawn from a zero mean
     * Gaussian distribution. Here, one can specify its variance.
     */
    final double INIT_PUSH_VAR = 25;

    /**
     * Allowed power range to be applied.
     */
    final double POWER_MIN = -100.0;
    final double POWER_MAX = +100.0;

    /**
     * If <code>true</code>, the experiment finishes
     * when the robot hits the ground.
     */
    final boolean FINISH_ON_GROUND_HIT = false;

    /**
     * Sleep time per step after visualization refreshment in milliseconds.
     * Useful to slow down the visualization.
     */
    final int SLEEP = 9;

    /**
     * Enable/disable logging on standard output.
     */
    final boolean TEXT_LOG = false;

    /**
     * Enable/disable logging states,observations and actions into files.
     * The log files are always overwritten without confirmation!
     */
    final boolean FILE_LOG = false;

    /**
     * Log file prefix.
     * The true states are logged into <prefix>-states.log.
     * Observations and actions are logged into <prefix>-control.log.
     */
    final String LOGFILE_PREFIX = "segsim";

    /**
     * Directory path of the log files.
     */
    final String LOGFILE_DIR = "data" + File.separator;

    /**
     * The amount with which the camera positions are changed in a round.
     */
    final float CAM_CHG = 0.1f;

    /**
     * Default camera position.
     */
    final float CAM_DEF_X = 4.4f;
    final float CAM_DEF_Y = 4.3f;
    final float CAM_DEF_Z = 1.7f;

    /**
     * Default locale setting.
     */
    final Locale DEF_LOCALE = Locale.CANADA;

    
    GyroDynamics gyroDyn = null;
    //--------------------------------------------------------------------------

    public static void main(String[] args)
    {
        try
        {
            new SegwaySim().run();
        }
        catch (Exception e)
        {
            e.printStackTrace(System.err);
            System.exit(-1);
        }
        System.exit(0);
    }

    //--------------------------------------------------------------------------

    /**
     * Creates all necessary objects.
     */
    public SegwaySim()
    throws Exception
    {
        Locale.setDefault(DEF_LOCALE);
        rng = (RNG_SEED > 0) ? new Random(RNG_SEED) : new Random();
        
        motionDyn = new MotionDynamics();
//        GyroDynamics gyroDyn = null;
        if (TA_MODE)
        {
            try
            {
                gyroDyn = (GyroDynamics)Class.forName("ta.TaGyroDynamics")
                                             .getConstructor().newInstance();
                controller = (Controller)Class.forName("ta.HTWayJ")
                                 .getConstructor(boolean.class,
                                                 GyroDynamics.Parameters.class)
                                 .newInstance(ENABLE_NULL_STATE,
                                              gyroDyn.parameters());
            }
            catch (Exception e)
            {
                e.printStackTrace(System.err);
                System.exit(-1);
            }
        }
        else
        {
            gyroDyn = new GyroDynamicsImpl();
            controller = new HTWayController(ENABLE_NULL_STATE,
                                             gyroDyn.parameters());
        }
        gyroSensor = new GyroSensor(gyroDyn, rng);
        
        view = new View();
        textFileHelper = new TextFileHelper();
    }

    /**
     * Responsible for running experiment(s).
     */
    private void run()
    throws Exception
    {
        camX = CAM_DEF_X;
        camY = CAM_DEF_Y;
        camZ = CAM_DEF_Z;
        
        reset();
        try
        {
            // experiment START {
            finished = false;
            while (!finished)
            {
                view.update();
               
                if (view.isClosed())
                {
                    finished = true;
                }
                else
                {
                    logic();
                    if (view.isVisible()) { render(); }
                }
                
                try { Thread.sleep(SLEEP); }
                catch (InterruptedException e) {}
            }
            controller.cleanup();
            textFileHelper.closeTextFiles();
            // experiment FINISH }
        }
        catch (Exception e)
        {
            controller.cleanup();
            textFileHelper.closeTextFiles();
            throw e;
        }
        finally
        {
        	HTWayController tmpCtl = (HTWayController)controller;
//        	System.out.println("-------t_array--------");
//        	System.out.println(Arrays.toString(tmpGyro.t_array));
//        	System.out.println("-------u_array--------");
//        	System.out.println(Arrays.toString(tmpGyro.u_array));
//        	System.out.println("-------y_array--------");
//        	System.out.println(Arrays.toString(tmpGyro.y_array));
        	FileWriter fw = new FileWriter("output.csv");
            PrintWriter pw = new PrintWriter(fw);
            pw.print("t_array");
            pw.print(",");
            pw.print("u_dot_array");
            pw.print(",");
            pw.print("u_array");
            pw.print(",");
            pw.println("y_array");
            
            for(int i =0;i<60*1000/10;i++){
            	pw.print(tmpCtl.t_array[i]);
            	pw.print(",");
            	pw.print(tmpCtl.u_dot_array[i]);
            	pw.print(",");
            	pw.print(tmpCtl.u_array[i]);
            	pw.print(",");
                pw.println(tmpCtl.y_array[i]);
            }
        	pw.flush();
        	pw.close();
        	fw.close();
        	view.destroy();
        }
    }

    /**
     * The logic of one step of an experiment.
     */
    private void logic()
    throws IOException
    {
        if (Keyboard.isKeyDown(Keyboard.Key.ESCAPE))
        {
            finished = true;
            return;
        }
        
        writeLog("---------- step " + (++step) + " ----------");
        
        // simulating the motion
        double deltaT = SIM_SAMPLING_TIME;
        if (SIM_SAMPLING_TIME > controlDelay)
        {
            deltaT = controlDelay;
            controlDelay = 0.0;
        }
        else { controlDelay -= deltaT; }
        
        motionDyn.setStepSize(deltaT);
        motionDyn.step(powerL, powerR);
        State s = motionDyn.getState();
        
        writeLog("state: " + s);
        logState(s);
        handleHitGround();
        
        // sensing
        int gyroValue = gyroSensor.nextValue(s.getDotPsiDegSec(), deltaT);
        
        // controlling
        if (0.0 >= controlDelay)
        {
            Observation obs =new Observation((int)(s.getTime() * 1000.0),
                                             gyroValue,
                                             (int)Math.round(s.getThetaLDeg()),
                                             (int)Math.round(s.getThetaRDeg()));
            writeLog("observation: " + obs);
            
            State ss = ENABLE_NULL_STATE ? null : s;
            controller.keyControl(Keyboard.downKeys());
            Action action = controller.step(obs, ss);
            logControl(obs, action);
            if (null != action)
            {
                writeLog("action: " + action);
                powerL = action.lPower();
                powerR = action.rPower();
                if (action.isExit())
                {
                    finished = true;
                    return;
                }
            }
            powerL = Math.max(POWER_MIN, Math.min(POWER_MAX, powerL));
            powerR = Math.max(POWER_MIN, Math.min(POWER_MAX, powerR));
            
            controlDelay = ((double)controller.sleep()) / 1000.0;
            if (0.0 < SAMPLING_VAR)
            {
                controlDelay += rng.nextGaussian() * SAMPLING_VAR;
                controlDelay = Math.max(MIN_CONTROL_SAMPLING_TIME,
                                        controlDelay);
            }
        }
        
        handleCamera();
    }

    /**
     * Resets for a new experiment.
     */
    private void reset()
    throws IOException
    {
        motionDyn.set(INIT_X, INIT_Y, 0.0, 0.0, 0.0);
        
        gyroSensor.reset();
        controller.reset();
        
        // initial pushing
        powerL = rng.nextGaussian() * INIT_PUSH_VAR;
        powerR = rng.nextGaussian() * INIT_PUSH_VAR;
        
        step = 0;
        controlDelay = MIN_CONTROL_SAMPLING_TIME;
        writeLog("reset: " + motionDyn.getState());
        openLogFiles();
    }

    /**
     * Put a limit on the pitch angle to handle when the robot hits the ground.
     */
    private void handleHitGround()
    {
        final double maxPsi =
            Math.PI - Math.acos(ModelParameters.R / ModelParameters.H);
        
        State s = motionDyn.getState();
        if (maxPsi < Math.abs(s.getPsi()))
        {
            double psi = Math.signum(s.getPsi()) * maxPsi;
            motionDyn.set(s.getX(),
                          s.getY(),
                          psi,
                          s.getTheta(),
                          s.getPhi(),
                          0.0,
                          0.0,
                          s.getDotPhi());
            
            if (FINISH_ON_GROUND_HIT) { finished = true; }
        }
    }

    /**
     * Refresh the view.
     */
    private void render()
    {
        State s = motionDyn.getState();
        view.render(s.getX(), // x position of axle midpoint
                    s.getY(), // y position of axle midpoint
                    0.0, // z position (elevation) of axle midpoint
                    (float)s.getPsiDeg(), // pitch angle
                    (float)s.getPhiDeg(), // yaw angle
                    (float)s.getThetaLDeg(), // left wheel angle
                    (float)s.getThetaRDeg(), // right wheel angle
                    camX, // camera x position
                    camY, // camera y position
                    camZ); // camera z position (elevation)
    }

    /**
     * Repositions the camera according to the pressed camera keys.
     */
    private void handleCamera()
    {        
        // camera movements
        boolean camPosChanged = false;
        if (Keyboard.isKeyDown(Keyboard.Key.Q))
        {
            camX += CAM_CHG;
            camPosChanged = true;
        }
        else if (Keyboard.isKeyDown(Keyboard.Key.A))
        {
            camX -= CAM_CHG;
            camPosChanged = true;
        }
        if (Keyboard.isKeyDown(Keyboard.Key.W))
        {
            camY += CAM_CHG;
            camPosChanged = true;
        }
        else if (Keyboard.isKeyDown(Keyboard.Key.S))
        {
            camY -= CAM_CHG;
            camPosChanged = true;
        }
        if (Keyboard.isKeyDown(Keyboard.Key.E))
        {
            camZ += CAM_CHG;
            camPosChanged = true;
        }
        else if (Keyboard.isKeyDown(Keyboard.Key.D))
        {
            camZ -= CAM_CHG;
            camPosChanged = true;
        }
        if (camPosChanged) { logCamPosition(); }        
    }

    //--------------------------------------------------------------------------

    /**
     * Logs the new camera position to the standard output.
     */
    private void logCamPosition()
    {
        writeLog("cam at (" + camX + "," + camY + "," + camZ + ")");
    }

    private void writeLog(String msg)
    {
        if (TEXT_LOG)
        {
            System.out.println(msg);
        }
    }

    private void openLogFiles()
    throws IOException
    {
        logStateFile = null;
        logControlFile = null;
        if (FILE_LOG)
        {
            logStateFile = textFileHelper.openTextFile(
                                LOGFILE_DIR + LOGFILE_PREFIX + "-states.log");
            logStateFile.write("# time(sec)" +
            		            " psi(deg)" +
            		            " theta(deg)" +
            		            " phi(deg)" +
            		            " psi-dot(deg/sec)" +
            		            " theta-dot(deg/sec)" +
            		            " phi-dot(deg/sec)" +
            		            " pos-x(m)" +
            		            " pos-y(m)" +
            		            "\n");
            
            logControlFile = textFileHelper.openTextFile(
                                LOGFILE_DIR + LOGFILE_PREFIX + "-control.log");
            logControlFile.write("# time(sec)" +
                                  " gyro-value(deg/sec)" +
                                  " left-wheel-rc(deg)" +
                                  " right-wheel-rc(deg)" +
                                  " left-power" +
                                  " right-power" +
            		              "\n");
        }
    }

    private void logState(State state)
    throws IOException
    {
        if (FILE_LOG)
        {
            logStateFile.write("" + state.getTime()
                            + " " + state.getPsiDeg()
                            + " " + state.getThetaDeg()
                            + " " + state.getPhiDeg()
                            + " " + state.getDotPsiDegSec()
                            + " " + state.getDotThetaDegSec()
                            + " " + state.getDotPhiDegSec()
                            + " " + state.getDotPhiDegSec()
                            + " " + state.getX()
                            + " " + state.getY()
                            + "\n");
        }
    }

    private void logControl(Observation obs, Action action)
    throws IOException
    {
        if (FILE_LOG)
        {
            double sec = ((double)obs.time()) / 1000.0;
            double gOffset = controller.gyroParameters().offset();
            logControlFile.write("" + sec
                              + " " + (obs.gyroValue() - gOffset)
                              + " " + obs.lWheelRotCtr()
                              + " " + obs.rWheelRotCtr()
                              + " " + action.lPower()
                              + " " + action.rPower()
                              + "\n");
        }
    }

    //--------------------------------------------------------------------------

    private Random rng;
    private int step;
    private double controlDelay;

    private float camX, camY, camZ;
    private View view;    
    private boolean finished;

    private double powerL;
    private double powerR;

    private Controller controller;
    private GyroSensor gyroSensor;
    private MotionDynamics motionDyn;

    private TextFileHelper textFileHelper;
    private BufferedWriter logStateFile;
    private BufferedWriter logControlFile;
}
