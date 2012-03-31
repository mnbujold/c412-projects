package student;

import linalg.Vector;
import sensor.GyroDynamics;
//import Jama.Matrix;
import ode_new.*;

/**
 * TODO Implement the noise-free gyroscope dynamics.
 */
public class GyroDynamicsImpl implements GyroDynamics
{
//    double lastY;
//    double lastYdot;
//	public double[] t_array = new double[60*1000/5];
//	public double[] u_dot_array = new double[60*1000/5];
//	public double[] y_array = new double[60*1000/5];
	
//	//added for observer
//	public double[] est_u_array = new double[60*1000/5];
//	public double[] est_u_dot_array = new double[60*1000/5];
//	public double[] est_y_array = new double[60*1000/5];
//	public double[] est_y_dot_array = new double[60*1000/5];
	
	public GyroDynamicsImpl() {
//		lastY=0;
//		lastYdot=0;
//		t_array[0]=0;
//	    u_dot_array[0]=0;
//	    y_array[0]=0;
	    
//	    //set state at 0 for t=0 in observer
//	    est_u_array[0]=0;
//	    est_u_dot_array[0]=0;
//	    est_y_array[0]=0;
//	    est_y_dot_array[0]=0;
	}

	@Override
    public int offsetN()
    {
        return 100;
    }
    
    @Override
    public void reset()
    {
        // TODO Implement this!
    }
    
    class GyroODE extends CachedODE{
    	static final int xDim = 2;
    	
    	 public GyroODE()
         {
             // 4 results of the right hand side calculation will be cached
             // which is enough for Euler, Midpoint and RK4 too
             super (4, xDim);
         }
    	 
    	 private double udot;
    	 
		public void setUdot(double udot) {
			this.udot = udot;
		}

        GyroParameters gp = new GyroParameters();

		@Override
		public Vector f(double t, Vector x) {
			// TODO Auto-generated method stub
			Vector result = nextCachedVector();
            // the calculation is just some random illustration
            result.set(0, x.get(1));
			result.set(1,gp.b1()*udot-gp.a1()*x.get(1)-gp.a2()*x.get(0));
			return result;
		}
    	
    }
    
    class GyroParameters implements GyroDynamics.Parameters{

		@Override
		public double offset() {
			// TODO Auto-generated method stub
			return 100;
		}

		@Override
		public double std() {
			// TODO Auto-generated method stub
			return 1;
		}

		@Override
		public double a1() {
			// TODO Auto-generated method stub
			return 77.5;
		}

		@Override
		public double a2() {
			// TODO Auto-generated method stub
			return 1500;
		}

		@Override
		public double b1() {
			// TODO Auto-generated method stub
			return 1590;
		}
    	
    }
    
    @Override
    public Parameters parameters()
    {
    	return new GyroParameters();
    }

    GyroODE ode = new GyroODE();
    RK4 rk4 = new RK4(ode, 0);
    Vector x = Vector.zero(2);
    Vector result = Vector.create(2);
    double t = 0.0;
    int index =0;
    
    @Override
    public double nextValue(double avel, double dt)
    {
    	
//    	System.out.println(t);
        ode.setUdot(avel);
        rk4.setDt(dt); // you can change dt
        rk4.next(t, x, result); // result = x(t+dt)
        result.copy(x);
//        x.set(0, result.get(0));
//        x.set(1, result.get(1));
        t+=dt;
//        if(index<60*1000/5-1){
//        index++;
//        t_array[index]=t;
//        u_dot_array[index]=avel;
//        y_array[index]=result.get(0);
//                
//        }else{
//        	System.out.println("enough");
//        }
        return result.get(0);
    	

    }
    
    
}
