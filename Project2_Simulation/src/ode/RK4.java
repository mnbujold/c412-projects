package ode;

import Jama.Matrix;

/**
 * The 4th order Runge-Kutta method.
 * <ul>http://en.wikipedia.org/wiki/Runge%E2%80%93Kutta_methods</ul>
 */
public class RK4 extends ODESolver
{
    public RK4()
    {
        h = 0.0;
    }

    public RK4(ODE.RHS rhs, double stepSize)
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
        Matrix k1 = rhs().eval(x, t);
        Matrix k2 = rhs().eval(x.plus(k1.times(h/2.0)), t + h/2.0);
        Matrix k3 = rhs().eval(x.plus(k2.times(h/2.0)), t + h/2.0);
        Matrix k4 = rhs().eval(x.plus(k3.times(h)), t + h);
        return x.plus(k1.times(h/6.0))
                .plus(k2.times(h/3.0))
                .plus(k3.times(h/3.0))
                .plus(k4.times(h/6.0));
    }

    @Override
    public double stepSize()
    {
        return h;
    }

    //--------------------------------------------------------------------------

    private double h; // constant step size
}
