package exercise;

import geom3d.Point3D;
import helper.Ratio;
import helper.Statistics;
import exercise.IRSensor;
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
        for (int i = 0; i < irSensor.length; ++i){
            irSensor[i] = new IRSensor(dCfg[i], scene);
        }
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
                //putParticleToInit2(pC.get(j));
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
        double range = 30; // mm
        Point3D loc = scene().fixedPoints().get(rng().nextInt(n)).position();
        p.setX(loc.x() + Statistics.uniform(rng(), -range, range));
        p.setY(loc.y() + Statistics.uniform(rng(), -range, range));
        p.setPitch(0); p.setThetaL(0); p.setThetaR(0);
    }
    
    void putParticleToInit2(Particle p)
    {
        p.setX(Statistics.uniform(rng(), 0, scene().floor().width()));
        p.setY(Statistics.uniform(rng(), 0, scene().floor().height()));
        p.setPitch(0); p.setThetaL(0); p.setThetaR(0);
    }
    
    //returns the highest weighted particle
    public Particle getHighestWeight(){
    	ParticleCloud cV = particles();
    	double max=-100000;
    	int highest=-1;
    	Particle cur;
    	for (int i = 0; i < cV.size(); ++i){
    		cur=cV.get(i);
    		if(cur.weight()>max){
    			max=cur.weight();
    			highest=i;
    		}
    	}
    	return cV.elementAt(highest);
    }
    

	public boolean lost() {
		ParticleCloud cV = particles();
		
		double maxY =-1;
		double maxX = -1;
		double minY =1000000;
		double minX =1000000;
		for (Particle p : cV){
			if(p.x()<minX)
				minX=p.x();
			else if (p.x()>maxX)
				maxX=p.x();
			if(p.y()<minY)
				minY=p.y();
			else if (p.y()>maxY)
				maxY=p.y();
		}
		if(maxY-minY>LOST_THRESH || maxX-minX>LOST_THRESH){
			//System.out.println((maxY-minY)+" "+(maxX-minX));
			return true;
		}
		return false;
	}
    
    public void next(double pitch, int dMrcL, int dMrcR, int[] ir)
    {
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
            
            pTo.setX(x);
            pTo.setY(y);
            
            // updating the weight
            
            w = pFrom.weight();
            pos.setX(x); pos.setY(y);
            if (scene().isOnFloor(x, y)){
            	for (j = 0; j < ir.length; ++j)
            	{
            		scene().realDistance(irSensor[j].cfg(),
                                     pos, psi, phi,
                                     distanceResult);
            		w *= irSensor[j].pdf(ir[j],
                                     distanceResult.distance(),
                                     distanceResult.hitObject());
            	}
            }
            else{
            	//add if it is inside a box
            	w=0;
            }
            
            if (w < MIN_WEIGHT){
            	if(rng().nextDouble()>RETHROW){
            		pTo.setX(pTo.x()+Statistics.gaussian(rng(), 0, X_STD));
            		pTo.setY(pTo.y()+Statistics.gaussian(rng(), 0, Y_STD));
            	}
            	w = MIN_WEIGHT;
            }
            wSum += w;
            pTo.setWeight(w);
        }
        
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
    
    //leftover filtering step: does not work
    /*
    public void next(double pitch, int dMrcL, int dMrcR, int[] ir) {
    //System.out.println("next");
     //add gaussian to this, and get from model!!
     //default mean=0
     //double delta_left=dMrcL + rng().nextGaussian()*VAR_THETA;
     //double delta_right=dMrcR + rng().nextGaussian()*VAR_THETA;
    
     //model for gyro
     //double pitch=0.0 + rng().nextGaussian()*VAR_PSI;
    

     //find out where to put wheel size
     //double r_w=1.0;//radius divided by axel
     //radius time added d_thetas divided by 2
    
     //we need the state parameter.
     //System.out.println();
    
     //double phi=radius*(delta_right-delta_left)/axel;
    
     ParticleCloud cV = particles();
     ParticleCloud cA = nextCloud();
    
     double weight=0.0;
     double sum_w=0.0;
     double phi=0.0;
     double yawA=0;
     Particle pFrom, pTo;
     for(int i = 0; i < cV.size(); ++i){
         double delta_left =
             Statistics.gaussian(rng(), dMrcL * Ratio.DEG_TO_RAD, STD_THETA);
         double delta_right =
             Statistics.gaussian(rng(), dMrcR * Ratio.DEG_TO_RAD, STD_THETA);

         double rt_2=delta_left+delta_right;
	     pFrom = cV.get(i);
	            pTo = cA.get(i);
	            
	            pTo.setThetaL(pFrom.thetaL()+delta_left);
	            pTo.setThetaR(pFrom.thetaR()+delta_right);
	            yawA=yaw(pTo);
	            
	            phi=r_w*(pFrom.thetaR()-pFrom.thetaL());
	            pTo.setX(pFrom.x()+halfR*rt_2*Math.cos(phi));
	            pTo.setY(pFrom.y()+halfR*rt_2*Math.sin(phi));
	            position.setX(pTo.x());
	            position.setY(pTo.y());
	    
	     weight=pFrom.weight();
	     //System.out.print(weight+" ");
	     //check intersection of IR ray and scene.
	     for(int j=0; j<irSensor.length; j++){
	                scene().realDistance(irSensor[j].cfg(),
	                        position, pitch, phi,
	                        distanceResult);
	                double p=irSensor[j].pdf(ir[j],
                            distanceResult.distance(),
                            distanceResult.hitObject());
	                //System.out.print(weight+" "+p+", ");
	                weight*=p;
	                //weight*=irSensor[j].pdf(ir[j],
	                                     //distanceResult.distance(),
		                                     //distanceResult.hitObject());
	     }
	     if (weight < MIN_WEIGHT) 
	    	 weight = MIN_WEIGHT;
	     sum_w += weight;
	     //System.out.print(weight+" | ");
	     pTo.setWeight(weight);
	 }
	    
	    
	 double effR = 0.0;
	 double estW = 0.0;
	 Particle estP = null;
	 for (Particle p : cA){
		 weight = p.weight() / sum_w;
		 p.setWeight(weight);
		 // searching the maximum likelihood particle 
	  	 if (weight > estW){
	    		 estP = p;
	    		 estW = weight;
	  	 }
	  	 //System.out.print(weight+" ");
	     effR += weight * weight;
	 }
	 //System.out.println();
	     
     int N = cA.size();
     if (1.0 < effR * EFF_RATIO * N)
     {
         double threshold = 0.5/N;
    	 //double threshold=0;
         double onePerN = 1.0/N;
         ParticleCloud cB = nextCloud();
         
         int i = 0;
         Particle p;
         weight = cA.get(0).weight();
         for (int j = 0; j < N; ++j)
         {
             while (weight < threshold && i < N-1) weight += cA.get(++i).weight();
             threshold += onePerN;
             
             p = cB.get(j);
             p.set(cA.get(i));
             p.setWeight(onePerN);
         }
         setCloudAndEstimate(cB, estP);
     }
     else setCloudAndEstimate(cA, estP);
        
     //setCloudAndEstimate(cA, estP);
     
     //ParticleCloud cB = nextCloud();
    
        // Recommended work flow:
        // cV = particles() -> filtering process -> cA = nextCloud()
        // optionally use cB = nextCloud(), i.e. for re-sampling
        // compute the robot position estimate -> estP
        // setCloudAndEstimate(cA or cB, estP)
        /*
	ParticleCloud cV = particles();
	ParticleCloud cA = nextCloud();
	for (Particle p : cA)
	{
	p.set(cV.get(rng().nextInt(cV.size())));
	randomize(p, p.weight());
	}
	Particle estP = cA.get(rng().nextInt(cA.size()));
	setCloudAndEstimate(cA, estP);
    }*/
    
    private final double RETHROW=0.995;
    private final double LOST_THRESH=200;
    private final double X_STD=30;
    private final double Y_STD=X_STD;
    
    private final DistanceSensor[] irSensor;
    private final Point3D pos;
    private DistanceResult distanceResult;
    private final double halfR;


}


