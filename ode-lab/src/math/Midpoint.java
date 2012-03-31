package math;

import Jama.Matrix;

/**
 * The Midpoint method.
 * <ul>http://en.wikipedia.org/wiki/Midpoint_method</ul>
 */
public class Midpoint extends ODESolver
{
    public Midpoint(ODE.RHS rhs, double stepSize)
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
