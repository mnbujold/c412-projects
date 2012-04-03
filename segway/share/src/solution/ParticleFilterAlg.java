package solution;

import geom3d.Point3D;
import helper.Ratio;
import solution.IRSensor;
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
        distanceResult = new DistanceResult();
        position = new Point3D(225, 225, mcfg.R * Ratio.M_TO_MM);
        
        DistanceSensorConfig[] dCfg = scene.distCfg();
        irSensor = new IRSensor[dCfg.length];
        for (int i = 0; i < irSensor.length; ++i)
            irSensor[i] = new IRSensor(dCfg[i], scene);
    }
    
    //--------------------------------------------------------------------------
    private static final double VAR_PSI = 0.01;//for pitch
    private static final double VAR_THETA = 0.05;//for wheels
    private static final double EFF_RATIO = 0.2;
    private static final double MIN_WEIGHT = 1e-10;
    
    //--------------------------------------------------------------------------
    
    // TODO Implement the algorithm according to your taste.
    // The following functions are just examples (without too much sense)!

    /** Initialization. */
    public void init(/* parameters */)
    {
    	//start by no starting estimations?
        int N = 2000; // max number of particles in the clouds
        for (int i = 0; i < clouds().length; ++i)
        {
            ParticleCloud pC = (clouds()[i] = new ParticleCloud(N));
            double sumW = 0.0;
            for (int j = 0; j < N; ++j)
            {
                double w = (0 == i) ? rng().nextDouble()
                                    : clouds()[0].get(j).weight();
                pC.set(j, randomize(new Particle(), w));
                sumW += w;
            }
            for (int j = 0; j < N; ++j)
            {
                Particle p = pC.get(i);
                //sets all with equal weight
                p.setWeight(p.weight() / sumW);
            }
        }
        initViewedCloud(clouds()[0]);
    }
    
    /** Perform a filtering step. 
     * @param mcfg current estimation of the state*/
    public void next(double pitch, int dMrcL, int dMrcR, int[] ir) {
    	
    	//add gaussian to this, and get from model!!
    	//default mean=0
    	double delta_left=1.0 + rng().nextGaussian()*VAR_THETA;
    	double delta_right=1.0 + rng().nextGaussian()*VAR_THETA;
    	
    	//model for gyro
    	//double pitch=0.0 + rng().nextGaussian()*VAR_PSI;
    	

    	//find out where to put wheel size
    	double r_w=1.0;//radius divided by axel
    	//radius time added d_thetas divided by 2
    	double rt_2=delta_left+delta_right;
    	
    	//we need the state parameter.
    	System.out.println();
    	
    	//double phi=radius*(delta_right-delta_left)/axel;
    	
    	ParticleCloud cV = particles();
    	ParticleCloud cA = nextCloud();
    	
    	double weight=0.0;
    	double sum_w=0.0;
    	double phi=0.0;
    	Particle pFrom, pTo;
    	for(int i = 0; i < cV.size(); ++i){
    		pFrom = cV.get(i);
            pTo = cA.get(i);
            
            pTo.setThetaL(pFrom.thetaL()+delta_left);
            pTo.setThetaR(pFrom.thetaR()+delta_right);
    		phi=r_w*(pFrom.thetaR()-pFrom.thetaL());
    		pTo.setX(pFrom.x()+rt_2*Math.cos(phi));
    		pTo.setY(pFrom.y()+rt_2*Math.sin(phi));
    		position.setX(pTo.x());
    		position.setY(pTo.y());
    		
    		weight=pFrom.weight();
    		//check intersection of IR ray and scene.
    		for(int j=0; j<2/*irSensor.length*/; j++){
                scene().realDistance(irSensor[j].cfg(),
                        position, pitch, phi,
                        distanceResult);
                weight*=irSensor[j].pdf(ir[j],
                                     distanceResult.distance(),
                                     distanceResult.hitObject());
    		}
    		if (weight < MIN_WEIGHT) weight = MIN_WEIGHT;
            sum_w += weight;
            pTo.setWeight(weight);
    	}
    	
    	
    	//double effR = 0.0;
    	double estW = 0.0;
        Particle estP = null;
        for (Particle p : cA)
        {
            weight = p.weight() / sum_w;
            p.setWeight(weight);
            if (weight > estW) // searching the maximum likelihood particle
            {
                estP = p;
                estW = weight;
            }
            //effR += w * w;
        }
    	
    	setCloudAndEstimate(cA, estP);
    	
        // Recommended work flow:
        //      cV = particles() -> filtering process -> cA = nextCloud()
        //      optionally use cB = nextCloud(), i.e. for re-sampling
        //      compute the robot position estimate -> estP
        //      setCloudAndEstimate(cA or cB, estP)
        /*
        ParticleCloud cV = particles();
        ParticleCloud cA = nextCloud();
        for (Particle p : cA)
        {
            p.set(cV.get(rng().nextInt(cV.size())));
            randomize(p, p.weight());
        }
        Particle estP = cA.get(rng().nextInt(cA.size()));
        setCloudAndEstimate(cA, estP);*/
    }
    private final DistanceSensor[] irSensor;
    private final Point3D position;
    private DistanceResult distanceResult;

}


