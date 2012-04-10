package exercise;

import helper.VectorCache;
import linalg.Vector;
import model.sensor.GyroSensor;
import model.sensor.GyroSensorConfig;
import ode.ODE;
import ode.ODESolver;
import ode.RK4;

/**
* Model of a HiTechnic gyroscope.
* http://www.hitechnic.com/cgi-bin/commerce.cgi?preadd=action&key=NGY1044
*/
public class HiTechnicGyroSensor extends GyroSensor
{
    public HiTechnicGyroSensor(GyroSensorConfig cfg)
    {
        super (cfg);
        ode = new GyroODE(4);
        odeSolver = new RK4(ode, 0.0);
    }
    
    public double bias(double t)
    {
        return cfg().B;
    }
    
    public double noise(double t, double u)
    {
        return rng().nextGaussian() * cfg().std;
    }
    
    @Override
    public void next(double t, Vector xt,
                     double dotU,
                     double dt, Vector xtdt)
    {
        ode.updateDotU(dotU);
        odeSolver.setDt(dt);
        odeSolver.next(t, xt, xtdt);
    }

    //--------------------------------------------------------------------------
    
    private final class GyroODE implements ODE
    {
        public GyroODE(int cacheSize)
        {
            vectorCache = new VectorCache(cacheSize, 2);
            dotU = 0.0;
        }
        
        @Override
        public Vector f(double t, Vector x)
        {
            Vector result = vectorCache.next();
            result.set(0, x.get(1));
            result.set(1, -cfg().a2*x.get(0) -cfg().a1*x.get(1) +cfg().b1*dotU);
            return result; // E*w + F*dotU
        }
        
        void updateDotU(double dotU) { this.dotU = dotU; }
        
        private double dotU;
        private final VectorCache vectorCache;
    }
    
    //--------------------------------------------------------------------------
    
    private final GyroODE ode;
    private final ODESolver odeSolver;
}