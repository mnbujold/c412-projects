package model;

import helper.MatrixHelper;
import helper.NumberHelper;
import helper.Ratio;

import java.io.PrintStream;

import Jama.Matrix;

public class State
{
    /**
     * Creates a new state.
     */
    public State()
    {
        this(0.0, 0.0, 0.0, 0.0, 0.0);
    }

    /**
     * Creates a new state.
     * @param px x coordinate of the axle midpoint (m)
     * @param py y coordinate of the axle midpoint (m)
     * @param psi body pitch angle (rad)
     * @param theta axle rotation angle (rad)
     * @param phi body yaw angle (rad)
     */
    public State(double px, double py, double psi, double theta, double phi)
    {
        this(px, py, psi, theta, phi, 0.0, 0.0, 0.0);
    }

    /**
     * Creates a new state.
     * @param px x coordinate of the axle midpoint (m)
     * @param py y coordinate of the axle midpoint (m)
     * @param psi body pitch angle (rad)
     * @param theta axle midpoint angle (rad)
     * @param phi body yaw angle (rad)
     * @param dotPsi body pitch angular velocity (rad/sec)
     * @param dotTheta axle midpoint angular velocity (rad/sec)
     * @param dotPhi body yaw angular velocity (rad/sec)
     */
    public State(double px, double py,
                 double psi, double theta, double phi,
                 double dotPsi, double dotTheta, double dotPhi)
    {
        resetTime();
        set(px, py, psi, theta, phi, dotPsi, dotTheta, dotPhi);
    }

    //--------------------------------------------------------------------------

    public double getTime() { return time; }
    public void resetTime() { time = 0.0; }
    public void incTime(double incr) { time += incr; }

    //--------------------------------------------------------------------------

    /** @return plane x position (m) */
    public double getX() { return x.get(6, 0) / Ratio.M_TO_CM; }

    /** @return plane y position (m) */
    public double getY() { return x.get(7, 0) / Ratio.M_TO_CM; }


    /** @return body pitch angle (rad) */
    public double getPsi() { return x.get(0, 0); }

    /** @return body yaw angle (rad) */
    public double getPhi() { return x.get(2, 0); }

    /** @return axle midpoint rotation angle (rad) */
    public double getTheta() { return x.get(1, 0); }

    /** @return left wheel rotation angle (rad) */
    public double getThetaL() { return thetaL; }

    /** @return right wheel rotation angle (rad) */
    public double getThetaR() { return thetaR; }


    /** @return body pitch angular velocity (rad/sec) */
    public double getDotPsi() { return x.get(3, 0); }

    /** @return body yaw angular velocity (rad/sec) */
    public double getDotPhi() { return x.get(5, 0); }

    /** @return axle midpoint rotation angular velocity (rad/sec) */
    public double getDotTheta() { return x.get(4, 0); }

    /** @return left wheel rotation angular velocity (rad/sec) */
    public double getDotThetaL() { return dotThetaL; }

    /** @return right wheel rotation angular velocity (rad/sec) */
    public double getDotThetaR() { return dotThetaR; }


    /** @return body pitch angle (deg) */
    public double getPsiDeg() { return Ratio.RAD_TO_DEG * getPsi(); }

    /** @return body yaw angle (deg) */
    public double getPhiDeg() { return Ratio.RAD_TO_DEG * getPhi(); }

    /** @return axle midpoint rotation angle (deg) */
    public double getThetaDeg() { return Ratio.RAD_TO_DEG * getTheta(); }

    /** @return left wheel rotation angle (deg) */
    public double getThetaLDeg() { return Ratio.RAD_TO_DEG * getThetaL(); }

    /** @return right wheel rotation angle (deg) */
    public double getThetaRDeg() { return Ratio.RAD_TO_DEG * getThetaR(); }    


    /** @return body pitch angular velocity (deg/sec) */
    public double getDotPsiDegSec() { return Ratio.RAD_TO_DEG * getDotPsi(); }

    /** @return body yaw angular velocity (deg/sec) */
    public double getDotPhiDegSec() { return Ratio.RAD_TO_DEG * getDotPhi(); }

    /** @return axle midpoint rotation angular velocity (deg/sec) */
    public double getDotThetaDegSec() { return Ratio.RAD_TO_DEG * getDotTheta(); }

    /** @return left wheel rotation angular velocity (deg/sec) */
    public double getDotThetaLDegSec() { return Ratio.RAD_TO_DEG * getDotThetaL(); }

    /** @return right wheel rotation angular velocity (deg/sec) */
    public double getDotThetaRDegSec() { return Ratio.RAD_TO_DEG * getDotThetaR(); }


    /** @return state matrix */
    public Matrix getMatrix() { return x; }

    //--------------------------------------------------------------------------

    @Override
    public String toString()
    {
        final int PREC = 1;
        return "S[t:" + getTime()
             + ", psi:" + NumberHelper.keepPrecision(getPsiDeg(), PREC)
             + ", theta:" + NumberHelper.keepPrecision(getThetaDeg(), PREC)
             + ", phi:" + NumberHelper.keepPrecision(getPhiDeg(), PREC)
             + ", psi-dot:" + NumberHelper.keepPrecision(getDotPsiDegSec(), PREC)
             + ", theta-dot:" + NumberHelper.keepPrecision(getDotThetaDegSec(), PREC)
             + ", phi-dot:" + NumberHelper.keepPrecision(getDotPhiDegSec(), PREC)
             + ", pos-x:" + NumberHelper.keepPrecision(getX(), PREC+8)
             + ", pos-y:" + NumberHelper.keepPrecision(getY(), PREC+8)
             + "]";
    }

    public void print(PrintStream p)
    {
        p.println("time      : " + getTime());
        p.println("psi       : " + getPsiDeg());
        p.println("theta     : " + getThetaDeg());
        p.println("phi       : " + getPhiDeg());
        p.println("psi-dot   : " + getDotPsiDegSec());
        p.println("theta-dot : " + getDotThetaDegSec());
        p.println("phi-dot   : " + getDotPhiDegSec());
        p.println("pos x     : " + getX());
        p.println("pos y     : " + getY());
    }

    //--------------------------------------------------------------------------

    void set(double px, double py,
             double psi, double theta, double phi,
             double dotPsi, double dotTheta, double dotPhi)
    {
        setMatrix(MatrixHelper.matrixD(new double[]{psi,
                                              theta,
                                              phi,
                                              dotPsi,
                                              dotTheta,
                                              dotPhi,
                                              px,
                                              py}));
    }

    void setMatrix(Matrix x)
    {
        this.x = x;
        
        double theta = x.get(1, 0);
        double phi = x.get(2, 0);
        double dotTheta = x.get(4, 0);
        double dotPhi = x.get(5, 0);
        
        double Wp2Rphi = W * phi / twoR;
        thetaL = theta - Wp2Rphi;
        thetaR = theta + Wp2Rphi;
        
        double Wp2RdotPhi = W * dotPhi / twoR;
        dotThetaL = dotTheta - Wp2RdotPhi;
        dotThetaR = dotTheta + Wp2RdotPhi;
    }

    //--------------------------------------------------------------------------

    private static final double W = ModelParameters.W;
    private static final double R = ModelParameters.R;
    private static final double twoR = 2.0 * R;

    //--------------------------------------------------------------------------

    /**
     * Simulation time (sec).
     */
    private double time;

    /**
     * State vector: [psi,theta,phi,dot{psi},dot{theta},dot{phi},px,py]^T.
     * The px,py coordinates are stored in centimeter for stability reasons.
     */
    private Matrix x;

    /**
     * Left and right wheel angles.
     */
    private double thetaL, thetaR;

    /**
     * Left and right wheel angular velocities.
     */
    private double dotThetaL, dotThetaR;
}
