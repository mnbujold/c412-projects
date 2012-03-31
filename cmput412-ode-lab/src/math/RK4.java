package math;

import Jama.Matrix;

/**
 * The 4th order Runge-Kutta method.
 * <ul>http://en.wikipedia.org/wiki/Runge%E2%80%93Kutta_methods</ul>
 */
public class RK4 extends ODESolver
{
    public RK4(ODE.RHS rhs, double stepSize)
    {
        super(rhs);
        h = stepSize;
    }

    //--------------------------------------------------------------------------

    @Override
    public Matrix next(Matrix x, double t)
    {
        // TODO implement me!
        return null;
    }

    @Override
    public double stepSize()
    {
        return h;
    }

    //--------------------------------------------------------------------------

    private final double h; // constant step size
}
