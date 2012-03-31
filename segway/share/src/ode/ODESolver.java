package ode;

import linalg.Vector;

/**
 * General implementation of a numerical solver
 * of ordinary differential equations (ODEs).
 */
public abstract class ODESolver
{
    public ODESolver(ODE ode, double dt)
    {
        this.ode = ode;
        this.dt = dt;
    }

    //--------------------------------------------------------------------------

    public ODE ode() { return ode; }
    public void setOde(ODE ode) { this.ode = ode; }
    
    public double dt() { return dt; }
    public void setDt(double dt) { this.dt = dt; }
    
    //--------------------------------------------------------------------------
    
    /** @return state of the ODE at time t+dt (placed into "result" != "x") */
    public abstract Vector next(double t, Vector x, Vector result);
    
    /** @return state of the ODE at time t+dt (placed into a new vector) */
    public Vector next(double t, Vector x)
    { return next(t, x, Vector.create(x.length())); }
    
    //--------------------------------------------------------------------------
    
    private ODE ode;
    private double dt; // step size
}
