package math;

import Jama.Matrix;

/**
 * The Euler method.
 * <ul>http://en.wikipedia.org/wiki/Euler_method</ul>
 */
public class Euler extends ODESolver
{
    public Euler(ODE.RHS rhs, double stepSize)
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
