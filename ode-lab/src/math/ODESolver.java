package math;

import Jama.Matrix;

/**
 * Interface for numerical solver of an ODE (ordinary differential equation).
 */
public abstract class ODESolver
{
    public ODESolver(ODE.RHS rhs)
    {
        this.rhs = rhs;
    }

    public ODE.RHS rhs() { return rhs; }

    //--------------------------------------------------------------------------

    /**
     * Computes the next state at state x and time t.
     */
    public abstract Matrix next(Matrix x, double t);
    
    /**
     * Returns the step size used for the last computation.
     */
    public abstract double stepSize();

    //--------------------------------------------------------------------------

    private final ODE.RHS rhs;
}
