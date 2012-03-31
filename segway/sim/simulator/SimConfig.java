package simulator;

import helper.Config;
import helper.MissingConfigException;
import helper.Ratio;

import java.io.File;
import java.io.IOException;

import linalg.Vector;

/**
 * Simulation related configuration.
 */
public class SimConfig extends Config
{
    public SimConfig(File file)
    throws IOException, MissingConfigException
    {
        super (file);

        seed = getLongConfig("seed");
        
        initPitchRange = getVectorConfig("init-pitch-range")
                         .mulL(Ratio.DEG_TO_RAD);
        initPitchVelRange = getVectorConfig("init-dPitch-range")
                            .mulL(Ratio.DEG_TO_RAD);
        
        initYawRange = getVectorConfig("init-yaw-range")
                       .mulL(Ratio.DEG_TO_RAD);
        initYawVelRange = getVectorConfig("init-dYaw-range")
                          .mulL(Ratio.DEG_TO_RAD);
        
        Vector posRange = getVectorConfig("init-pos-range");
        initXPosDevRange = Vector.create(new double[]{posRange.get(0),
                                                   posRange.get(1)});
        initYPosDevRange = Vector.create(new double[]{posRange.get(2),
                                                   posRange.get(3)});
        
        time0 = getBooleanConfig("sim-time0");
        simDT = getDoubleConfig("sim-dt");
        timeRatio = getDoubleConfig("time-ratio");
    }
    
    //--------------------------------------------------------------------------

    /** @return simulation seed */
    public long seed() { return seed; }
    
    /** @return initial pitch (uniformly distributed) range (rad) */
    public Vector initPitchRange() { return initPitchRange; }
    
    /** @return initial pitch velocity (uniformly distributed) range (rad/sec) */
    public Vector initPitchVelRange() { return initPitchVelRange; }
    
    /** @return initial yaw (uniformly distributed) range (rad) */
    public Vector initYawRange() { return initYawRange; }
    
    /** @return initial yaw velocity (uniformly distributed) range (rad/sec) */
    public Vector initYawVelRange() { return initYawVelRange; }
    
    /** @return initial x position deviation (uniformly distributed) range (m) */
    public Vector initXPositionDevRange() { return initXPosDevRange; }
    
    /** @return initial y position deviation (uniformly distributed) range (m) */
    public Vector initYPositionDevRange() { return initYPosDevRange; }

    /** @return true if the simulation time starts from zero,
     *          false if the simulation time reseted to the true current time */
    public boolean isTime0() { return time0; }
    
    /** @return simulation time step (sec) */
    public double simDT() { return simDT; }

    /** @return real time / simulation time ratio */
    public double timeRatio() { return timeRatio; }
    
    //--------------------------------------------------------------------------
    
    private final long seed;
    private final double simDT;
    private final Vector initPitchRange, initYawRange;
    private final Vector initPitchVelRange, initYawVelRange;
    private final Vector initXPosDevRange, initYPosDevRange;
    private final boolean time0;
    private final double timeRatio;
}
