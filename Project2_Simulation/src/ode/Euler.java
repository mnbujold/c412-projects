package ode;

import Jama.Matrix;

/**
 * The Euler method.
 * <ul>http://en.wikipedia.org/wiki/Euler_method</ul>
 */
public class Euler extends ODESolver
{
    public Euler()
    {
        h = 0.0;
    }

    public Euler(ODE.RHS rhs, double stepSize)
    {
        super(rhs);
        h = stepSize;
    }

    public void init(ODE.RHS rhs, double stepSize)
    {
        super.init(rhs);
        h = stepSize;
    }

    //--------------------------------------------------------------------------

    @Override
    public Matrix next(Matrix x, double t)
    {
        
        return x.plus(rhs().eval(x, t).times(h));
    }

    @Override
    public double stepSize()
    {
        return h;
    }

    //--------------------------------------------------------------------------

    private double h; // constant step size
}
