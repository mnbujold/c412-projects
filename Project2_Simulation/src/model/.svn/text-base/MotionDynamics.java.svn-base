package model;

import helper.MatrixHelper;
import helper.Ratio;
import ode.ODE;
import ode.RK4;
import Jama.Matrix;

public class MotionDynamics implements ModelParameters
{
    public MotionDynamics()
    {
        s = new State();
        odeRHS = new DynRHS();
        odeSolver = new RK4(odeRHS, stepSize);
    }

    /**
     * Initializes the state of motion dynamics.
     * @param px x coordinate of the axle midpoint (m)
     * @param py y coordinate of the axle midpoint (m)
     * @param psi body pitch angle (rad)
     * @param theta axle rotation angle (rad)
     * @param phi body yaw angle (rad)
     */
    public void set(double px, double py, double psi, double theta, double phi)
    {
        set(px, py, psi, theta, phi, 0.0, 0.0, 0.0);
    }

    /**
     * Initializes the state of motion dynamics.
     * @param px x coordinate of the axle midpoint (m)
     * @param py y coordinate of the axle midpoint (m)
     * @param psi body pitch angle (rad)
     * @param theta axle midpoint angle (rad)
     * @param phi body yaw angle (rad)
     * @param dotPsi body pitch angular velocity (rad/sec)
     * @param dotTheta axle midpoint angular velocity (rad/sec)
     * @param dotPhi body yaw angular velocity (rad/sec)
     */
    public void set(double px, double py,
                    double psi, double theta, double phi,
                    double dotPsi, double dotTheta, double dotPhi)
    {
        px *= Ratio.M_TO_CM;
        py *= Ratio.M_TO_CM;
        s.set(px, py, psi, theta, phi, dotPsi, dotTheta, dotPhi);
    }

    //--------------------------------------------------------------------------

    public State getState()
    {
        return s;
    }

    public double getStepSize()
    {
        return stepSize;
    }

    public void setStepSize(double stepSize)
    {
        this.stepSize = stepSize;
    }

    public void step(double powerL, double powerR)
    {
        double dt = getStepSize();
        odeRHS.updateV(powerL, powerR);
        odeSolver.init(odeRHS, dt);
        s.incTime(dt);
        s.setMatrix(odeSolver.next(s.getMatrix(), 0.0));
    }

    //--------------------------------------------------------------------------

    private static class DynRHS implements ODE.RHS
    {
        static final double MLR = M*L*R;
        static final double MLL = M*L*L;
        static final double MgL = M*g*L;
        static final double a = (2.0*m + M)*R*R + 2.0*Jw + 2.0*n*n*Jm;
        static final double c = MLL + Jpsi + 2.0*n*n*Jm;
        static final double ac = a*c;
        static final double alpha = n*Kt/Rm;
        static final double beta = n*Kt*Kb/Rm + fm;
        static final double e1 = 0.5*m*W*W + Jphi + W*W*(Jw + n*n*Jm)/(2.0*R*R);
        static final double e2 = W*alpha/(2.0*R);
        static final double e3 = W*W*(beta + fw)/(2.0*R*R);
        static final double e4 = 2.0*(beta + fw);
        
        @Override
        public Matrix eval(Matrix x, double t)
        {
            double x1 = x.get(0, 0);
            double x3 = x.get(2, 0);
            double x4 = x.get(3, 0);
            double x5 = x.get(4, 0);
            double x6 = x.get(5, 0);

            double sinx1 = Math.sin(x1);
            double cosx1 = Math.cos(x1);
            double sinx3 = Math.sin(x3);
            double cosx3 = Math.cos(x3);
            
            double b = MLR*cosx1 - 2.0*n*n*Jm;
            double d = e1 + MLL*sinx1*sinx1;
            double vv = v1 + v2;
            
            double f4 = MLR*x4*x4*sinx1 + alpha*vv - e4*x5 + 2.0*beta*x4;
            double f5 = MgL*sinx1 + MLL*x6*x6*sinx1*cosx1
                        - alpha*vv + 2.0*beta*(x5 - x4);
            double f6 = -2.0*MLL*x4*x6*sinx1*cosx1 + e2*(v2 - v1) - e3*x6;
            
            double det = b*b - ac;
            return MatrixHelper.matrixD(new double[]{x4,
                                        x5,
                                        x6,
                                        (f4*b - f5*a) / det,
                                        (f5*b - f4*c) / det,
                                        f6/d,
                                        R * x5 * cosx3,
                                        R * x5 * sinx3});
        }

        public void updateV(double v1, double v2)
        {
            this.v1 = v1;
            this.v2 = v2;
        }

        private double v1, v2;
    }

    //--------------------------------------------------------------------------

    /**
     * Current simulation state.
     */
    private State s;

    /**
     * Simulation step size (sec).
     */
    private double stepSize = 0.01;

    /**
     * Right hand side of the motion ODE.
     */
    private DynRHS odeRHS;

    /**
     * The ODE solver method used for the simulation.
     */
    private RK4 odeSolver;
}
