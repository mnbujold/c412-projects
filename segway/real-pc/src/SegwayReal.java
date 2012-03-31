import java.awt.event.KeyListener;
import java.awt.event.MouseListener;
import java.io.File;

import model.motion.MotionConfig;
import model.scene.SceneModel;
import model.sensor.DistanceSensorConfig;
import run.PC;
import run.RunConfig;
import visual.View;
import visual.ViewConfig;
import run.NXTPC;
import control.PCController;


/**
 * Main entry point of the segway program (real case).
 */
public class SegwayReal
{
    public static void main(String[] args)
    {
        int exitCode = 0;
        
        View view = null;
        try
        {
            SegwayReal segway = new SegwayReal();
            view = segway.view;
            segway.run();
        }
        catch (Exception e)
        {
            e.printStackTrace(System.err);
            exitCode = -1;
        }
        finally { if (view != null) view.dispose(); }
        
        System.exit(exitCode);
    }
    
    //--------------------------------------------------------------------------
    
    public SegwayReal()
    throws Exception
    {
        RunConfig runCfg = new RunConfig(new File("../share/cfg/run.cfg"));
        ViewConfig viewCfg = new ViewConfig(new File("../share/cfg/view.cfg"));
        MotionConfig motionCfg =
            new MotionConfig(new File("../share/cfg/motion.cfg"));

        File[] distCfgFile = runCfg.distSensorConfigs();
        DistanceSensorConfig[] distCfg =
            new DistanceSensorConfig[distCfgFile.length];
        for (int i = 0; i < distCfg.length; ++i)
            distCfg[i] = new DistanceSensorConfig(distCfgFile[i]);        
        
        sceneModel = new SceneModel(motionCfg, runCfg.mapFile(), distCfg);
        view = View.create(sceneModel, viewCfg);

        pc = new NXTPC();
        pcController = (PCController)
            Class.forName(runCfg.pcControllerClassName())
            .getConstructor(PC.class, MotionConfig.class,
                            SceneModel.class, View.class)
            .newInstance(pc, motionCfg, sceneModel, view);
        pc.setController(pcController);
        
        KeyListener pcKeyListener = pcController.keyListener();
        if (pcKeyListener != null) view.addKeyListener(pcKeyListener);
        
        MouseListener pcMouseListener = pcController.mouseListener();
        if (pcMouseListener != null) view.addMouseListener(pcMouseListener);
    }
    
    private void run()
    throws Exception
    {
        while (!view.isInitialized()) Thread.sleep(5);
        
        pc.start();
        while (!view.isCloseRequested() && !pcController.isTerminated())
        {
            try { Thread.sleep(500); }
            catch (InterruptedException e) { e.printStackTrace(System.err); }
        }
        pcController.terminate();
        Thread.sleep(2000);
    }
    
    //--------------------------------------------------------------------------
    
    private final NXTPC pc;
    private final PCController pcController;
    
    private final SceneModel sceneModel;
    private final View view;
}
