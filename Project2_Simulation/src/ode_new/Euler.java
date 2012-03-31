package ode_new;

import linalg.Vector;

/**
 * The Euler method.
 */
public class Euler extends ODESolver
{
    public Euler(ODE ode, double dt)
    {
        super (ode, dt);
    }

    @Override
    public Vector next(double t, Vector x, Vector result)
    {
        x.add(ode().f(t, x).mulL(dt()));
        return result;
    }
}
