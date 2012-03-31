package ode;

import Jama.Matrix;

/**
 * Interface of an ODE (ordinary differential equation).
 */
public interface ODE
{
    /**
     * Evaluating the right hand side (RHS) of the ODE at state x and time t.
     */
    interface RHS
    {
        Matrix eval(Matrix x, double t);
    }

    //--------------------------------------------------------------------------

    /**
     * Returns the initial condition of the ODE.
     */
    Matrix init();

    /**
     * Returns the right hand side evaluator of the ODE.
     */
    RHS rhs();

    /**
     * Returns the solution of the ODE at time t.
     */
    Matrix solution(double t);
}
