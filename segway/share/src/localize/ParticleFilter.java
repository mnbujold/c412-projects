package localize;

import helper.Statistics;

import java.util.Random;

import model.motion.MotionConfig;
import model.scene.SceneModel;

/**
 * Tools and general interface for the particle filter algorithm.
 */
public abstract class ParticleFilter
{
    public ParticleFilter(long seed,
                          MotionConfig mcfg,
                          SceneModel scene,
                          int numClouds)
    {
        rng = new Random(seed);
        this.mcfg = mcfg;
        this.scene = scene;

        cloudIdx = 0;
        clouds = new ParticleCloud[numClouds];
        viewedEstimate = new Particle();
    }
    
    /** Initializes the visualized particle cloud. */
    public void initViewedCloud(ParticleCloud cloud)
    {
        viewedCloud = cloud;
    }
    
    //--------------------------------------------------------------------------
    
    /** @return random number generator */
    protected Random rng() { return rng; }
    
    /** @return motion model configuration */
    protected MotionConfig mcfg() { return mcfg; }
    
    /** @return scene model */
    protected SceneModel scene() { return scene; }
    
    /** @return all particle clouds */
    protected ParticleCloud[] clouds() { return clouds; }
    
    //--------------------------------------------------------------------------
    // General helper functions.
    
    /** @return body yaw angle (rad) */
    public double yaw(Particle p)
    {
        return (p.thetaR() - p.thetaL()) * mcfg.R / mcfg.W;
    }
    
    /** Set the "p" particle's attributes according to the provided values. */
    public Particle set(Particle p,
                        double weight, double x, double y,
                        double pitch, double yaw)
    {
        double thetaR = mcfg.W / (2.0 * mcfg.R) * yaw;
        p.set(weight, x, y, pitch, -thetaR, thetaR);
        return p;
    }
    
    /** Randomly initialize a particle. */
    public Particle randomize(Particle p, double weight)
    {
        return
           set(p, weight,
                  Statistics.uniform(rng, 0.0, scene.floor().width()),  // x
                  Statistics.uniform(rng, 0.0, scene.floor().height()), // y
                  Statistics.uniform(rng, -0.1, 0.1),                   // pitch
                  Statistics.uniform(rng, -Math.PI, Math.PI));          // yaw
    }

    //--------------------------------------------------------------------------
    // Visualization related helper functions.
    
    /** @return the visualized particle cloud */
    public ParticleCloud particles() { return viewedCloud; }

    /** @return estimate of the robot's position and orientation */
    public Particle estimate() { return viewedEstimate; }
    
    /** @return the "next" non-visualized particle cloud */
    protected ParticleCloud nextCloud()
    {
        ParticleCloud p = clouds[cloudIdx];
        cloudIdx = (cloudIdx + 1) % clouds.length;
        return (viewedCloud == p) ? nextCloud() : p;
    }

    /** Set the visualized particle cloud. */
    protected void setCloudAndEstimate(ParticleCloud cloud, Particle estimate)
    {
        // synchronization with the visualization
        synchronized (scene)
        {
            viewedCloud = cloud;
            viewedEstimate.set(estimate);
        }        
    }
    
    //--------------------------------------------------------------------------
    
    private int cloudIdx;
    private final ParticleCloud[] clouds;
    private ParticleCloud viewedCloud;
    private final Particle viewedEstimate;
    
    private final Random rng;
    private final MotionConfig mcfg;
    private final SceneModel scene;
}
