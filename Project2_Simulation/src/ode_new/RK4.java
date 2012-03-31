package ode_new;

import linalg.Vector;

/**
 * The 4th order Runge-Kutta (RK4) method.
 */
public class RK4 extends ODESolver
{
    public RK4(ODE ode, double dt)
    {
        super (ode, dt);
    }
    
    @Override
    public Vector next(double t, Vector x, Vector result)
    {
        ODE ode = ode();
        double h = dt(), h2 = h/2.0, th2 = t + h2;
        
        // k1 = f(t, x) * h/2
        Vector k1 = ode.f(t, x).mulL(h2);
        // k2 = f(t+h/2, x+k1) * h/2
        Vector k2 = ode.f(th2, x.add(k1, result)).mulL(h2);
        // k3 = f(t+h/2, x+k2) * h
        Vector k3 = ode.f(th2, x.add(k2, result)).mulL(h);
        // k4 = f(t+h, x+k3)
        Vector k4 = ode.f(t+h, x.add(k3, result));
        
        // result = (k1 + 2*k2 + k3 + k4)/3
        for (int i = 0; i < result.length(); ++i)
            result.set(i, x.get(i) + (  k1.get(i)
                                      + k2.get(i) * 2.0
                                      + k3.get(i)
                                      + k4.get(i) * h2 ) / 3.0);
        return result;
    }
}
