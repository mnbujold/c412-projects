package ode;

import linalg.Vector;

/**
 * Interface of an ordinary differential equation (ODE)
 * in the form dx/dt = f(t, x).
 */
public interface ODE
{
    /**
     * Compute the right hand side of the ODE.
     * The "x" vector should be left unchanged and the returned vector
     * should be changeable by ODE solvers without any interference.
     * @return evaluated ODE right hand side at time "t" and state "x"
     */
    Vector f(double t, Vector x);
}
