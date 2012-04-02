package solution;

import geom3d.Point3D;
import helper.Ratio;
import helper.Statistics;
import localize.Particle;
import localize.ParticleCloud;
import localize.ParticleFilter;
import model.motion.MotionConfig;
import model.scene.SceneModel;
import model.scene.SceneModel.DistanceResult;
import model.sensor.DistanceSensor;
import model.sensor.DistanceSensorConfig;

/**
 * The particle filter algorithm.
 */
public class ParticleFilterAlg extends ParticleFilter
{
    public ParticleFilterAlg(long seed, MotionConfig mcfg, SceneModel scene)
    {
        // create 3 particle clouds (1 for visualization, 2 for working)
        super (seed, mcfg, scene, 3);
        halfR = mcfg.R * Ratio.M_TO_MM / 2.0;
        distanceResult = new DistanceResult();
        pos = new Point3D(0, 0, mcfg.R * Ratio.M_TO_MM);
        
        DistanceSensorConfig[] dCfg = scene.distCfg();
        irSensor = new IRSensor[dCfg.length];
        for (int i = 0; i < irSensor.length; ++i)
            irSensor[i] = new IRSensor(dCfg[i], scene);
    }
    
    //--------------------------------------------------------------------------

    private static final double STD_PSI = 0.01;
    private static final double STD_THETA = 0.1;
    private static final double EFF_RATIO = 0.2;
    private static final double MIN_WEIGHT = 1e-10;
    
    //--------------------------------------------------------------------------
    
    /** Initialization. */
    public void init(/* parameters */)
    {
        int N = 1000; // max number of particles in the clouds
        for (int i = 0; i < clouds().length; ++i)
        {
            ParticleCloud pC = (clouds()[i] = new ParticleCloud(N));
            double sumW = 0.0;
            for (int j = 0; j < N; ++j)
            {
                double w = (0 == i) ? rng().nextDouble()
                                    : clouds()[0].get(j).weight();
                pC.set(j, randomize(new Particle(), w));
                putParticleToInit1(pC.get(j));
                sumW += w;
            }
            for (int j = 0; j < N; ++j)
            {
                Particle p = pC.get(i);
                p.setWeight(p.weight() / sumW);
            }
        }
        initViewedCloud(clouds()[0]);
    }
    
    void putParticleToInit1(Particle p)
    {
        final int n = scene().fixedPoints().size();
        double range = 15; // mm
        Point3D loc = scene().fixedPoints().get(rng().nextInt(n)).position();
        p.setX(loc.x() + Statistics.uniform(rng(), -range, range));
        p.setY(loc.y() + Statistics.uniform(rng(), -range, range));
        p.setPitch(0); p.setThetaL(0); p.setThetaR(0);
    }
    
    /** Perform a filtering step. */
    public void next(double pitch, int dMrcL, int dMrcR, int[] ir)
    {
        // Recommended work flow:
        //      cV = particles() -> filtering process -> cA = nextCloud()
        //      optionally use cB = nextCloud(), i.e. for re-sampling
        //      compute the robot position estimate -> estP
        //      setCloudAndEstimate(cA or cB, estP)
        
        //System.out.println("--------------------");
        
        ParticleCloud cV = particles();
        ParticleCloud cA = nextCloud();
        
        int i, j;
        Particle pFrom, pTo;
        double psi, phi, dThetaL, dThetaR, dThetaSum, x ,y, w, wSum = 0;
        for (i = 0; i < cV.size(); ++i)
        {
            pFrom = cV.get(i);
            pTo = cA.get(i);

            // sampling based on the motion model
            
            psi = Statistics.gaussian(rng(), pitch, STD_PSI);
            dThetaL =
                Statistics.gaussian(rng(), dMrcL * Ratio.DEG_TO_RAD, STD_THETA);
            dThetaR =
                Statistics.gaussian(rng(), dMrcR * Ratio.DEG_TO_RAD, STD_THETA);
            
            pTo.setPitch(psi);            
            pTo.setThetaL(pFrom.thetaL() + dThetaL);
            pTo.setThetaR(pFrom.thetaR() + dThetaR);
            
            phi = yaw(pTo);
            dThetaSum = dThetaL + dThetaR;
            x = pFrom.x() + halfR * dThetaSum * Math.cos(phi);
            y = pFrom.y() + halfR * dThetaSum * Math.sin(phi);
            //System.out.println("xy: " + x + " , " + y);
            
            pTo.setX(x);
            pTo.setY(y);
            
            // updating the weight
            
            w = pFrom.weight();
            pos.setX(x); pos.setY(y);
            for (j = 0; j < ir.length; ++j)
            {
                scene().realDistance(irSensor[j].cfg(),
                                     pos, psi, phi,
                                     distanceResult);
                w *= irSensor[j].pdf(ir[j],
                                     distanceResult.distance(),
                                     distanceResult.hitObject());
            }
            if (w < MIN_WEIGHT) w = MIN_WEIGHT;
            wSum += w;
            pTo.setWeight(w);
        }
        
        assert (wSum != 0); // TODO
        
        // normalizing and computing the position estimate particle
        
        double effR = 0.0, estW = 0.0;
        Particle estP = null;
        for (Particle p : cA)
        {
            w = p.weight() / wSum;
            p.setWeight(w);
            if (w > estW) // searching the maximum likelihood particle
            {
                estP = p;
                estW = w;
            }
            effR += w * w;
        }
        
        // re-sampling (if necessary) and activating the updated particle cloud
        
        int N = cA.size();
        if (1.0 < effR * EFF_RATIO * N)
        {
            double threshold = 0.5/N, onePerN = 1.0/N;
            ParticleCloud cB = nextCloud();
            
            i = 0;
            Particle p;
            w = cA.get(0).weight();
            for (j = 0; j < N; ++j)
            {
                while (w < threshold && i < N-1) w += cA.get(++i).weight();
                threshold += onePerN;
                
                p = cB.get(j);
                p.set(cA.get(i));
                p.setWeight(onePerN);
            }
            setCloudAndEstimate(cB, estP);
        }
        else setCloudAndEstimate(cA, estP);
    }
    
    //--------------------------------------------------------------------------

    private DistanceResult distanceResult;
    private final DistanceSensor[] irSensor;
    private final Point3D pos;
    private final double halfR;
}
