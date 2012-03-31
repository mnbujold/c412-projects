package math;

import Jama.Matrix;

/**
 * The RHS of ODE \dot{x1} = -x2, \dot{x2} = x1
 * with initial conditions x1(0) = x2(0) = 3/2.
 */
public class CircleODE implements ODE
{
    static class CircleRHS implements ODE.RHS
    {
        @Override
        public Matrix eval(Matrix x, double t)
        {
            return Helper.matrix2D(-x.get(1,0),
                                    x.get(0,0));
        }
    }
    static final CircleRHS rhs = new CircleRHS();

    //--------------------------------------------------------------------------

    public CircleODE(Matrix initialPosition)
    {
        init = initialPosition;
        dist = init.norm2();
        offset = Math.acos(init.get(0,0) / dist);
        if (init.get(1,0) < 0)
        {
            offset = -offset;
        }
    }

    public CircleODE()
    {
        this (Helper.matrix2D(1.5, 1.5));
    }

    //--------------------------------------------------------------------------

    @Override
    public Matrix init()
    {
        return init; 
    }

    @Override
    public RHS rhs()
    {
        return rhs;
    }

    @Override
    public Matrix solution(double t)
    {
        return Helper.matrix2D(Math.cos(t + offset),
                               Math.sin(t + offset)).times(dist);
    }

    //--------------------------------------------------------------------------

    private Matrix init = null;
    private double dist = 0.0;
    private double offset = 0.0;
}
