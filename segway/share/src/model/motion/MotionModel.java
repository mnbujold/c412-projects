package model.motion;

import linalg.Vector;
import ode.CachedODE;
import ode.ODESolver;
import ode.RK4;

public class MotionModel
{
    /**
     * Create a new motion model.
     * @param cfg motion model configuration
     * @param dt time step size (sec)
     */
    public MotionModel(MotionConfig cfg, double dt)
    {
        this.cfg = cfg;
        
        maxPitch = Math.PI - Math.acos(cfg.R / cfg.H);
        state = new State(cfg, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0);

        ode = new MotionModelODE(4);
        odeSolver = new RK4(ode, dt);
        odeTemp = Vector.zero(state.stateSpaceDimension());
    }
    
    /** @return motion model configuration */
    public MotionConfig motionModelConfig() { return cfg; }
  
    //--------------------------------------------------------------------------
    
    /** @return simulation time step size */
    public double dt() { return odeSolver.dt(); }
    
    /** Set the simulation time step size. */
    public void setDt(double dt) { odeSolver.setDt(dt); }
    
    /** @return current simulation state */
    public State state() { return state; }
    
    /** @return absolute ground hitting pitch angle (rad) */
    public double maxPitch() { return maxPitch; }
    
    /** @return true if the body hit the ground */
    public boolean isGroundHit() { return maxPitch <= Math.abs(state.pitch()); }
    
    /** Reset the motion model. */
    public void setState(State state) { state.copy(this.state); }
    
    /**
     * @return step the motion model into the next state
     *         by constantly applying the provided motor powers
     */
    public State step(double leftPower, double rightPower)
    {
        if (!isGroundHit())
        {
            ode.updatePowers(leftPower, rightPower);
            Vector sv = state.stateVec();
            odeSolver.next(state.time(), sv.copy(odeTemp), sv);
        }
        else
        {
            // freeze any movement when the robot body hits the ground
            Vector v = state.stateVec();
            v.set(State.VEC_IDX_DPITCH, 0.0);
            v.set(State.VEC_IDX_DYAW,   0.0);
            v.set(State.VEC_IDX_DROLL,  0.0);
        }
        
        state.incTime(odeSolver.dt());
        state.updateCache();        
        return state;
    }
    
    //--------------------------------------------------------------------------

    private final class MotionModelODE extends CachedODE
    {
        public MotionModelODE(int cacheSize)
        {
            super (cacheSize, state.stateSpaceDimension());
            
            double g = cfg.g,
                   m = cfg.m, Jw = cfg.Jw,
                   M = cfg.M, W = cfg.W, L = cfg.L,
                   Jpsi = cfg.Jpsi, Jphi = cfg.Jphi,
                   B = cfg.B;
            
            K = cfg.K;
            B2 = 2.0*B;
            R = cfg.R;
            MLR = M*L*R;
            RRMLL2 = 2*R*R*M*L*L;
            MLL = M*L*L;
            MgL = M*g*L;
            WWB = W*W*B;
            RWK = R*W*K;
            
            H11 = MLL + Jpsi;
            H22 = (2*m + M)*R*R + 2*Jw;
            H11MH22 = H11*H22;
            
            ht = m*R*R*W*W + W*W*Jw + 2*R*R*Jphi;
            
            psi0 = cfg.psi0;
            
            vl = vr = 0.0;
        }
        
        @Override
        public Vector f(double t, Vector x)
        {
            double psi = x.get(State.VEC_IDX_PITCH) - psi0;
            double phi = x.get(State.VEC_IDX_YAW);
            double dPsi = x.get(State.VEC_IDX_DPITCH);
            double dTheta = x.get(State.VEC_IDX_DROLL);
            double dPhi = x.get(State.VEC_IDX_DYAW);

            double sinPsi = Math.sin(psi);
            double cosPsi = Math.cos(psi);
            double sinPhi = Math.sin(phi);
            double cosPhi = Math.cos(phi);
            
            double KMvlPvr = K*(vl + vr);
            double dPsiSdTheta = dPsi - dTheta;

            double H12 = MLR*cosPsi;
            double h = RRMLL2*sinPsi*sinPsi + ht;
            
            double fGv1 = MLL*dPhi*dPhi*sinPsi*cosPsi
                        + MgL*sinPsi
                        - B2*dPsiSdTheta
                        - KMvlPvr
                 , fGv2 = MLR*dPsi*dPsi*sinPsi
                        + B2*dPsiSdTheta
                        + KMvlPvr
                 , fGv3 = -RRMLL2*dPhi*dPsi*sinPsi*cosPsi
                        - WWB*dPhi
                        + RWK*(vr - vl);
            
            double detH = H11MH22 - H12*H12;
            
            Vector result = nextCachedVector();
            result.set(State.VEC_IDX_PITCH,  dPsi);
            result.set(State.VEC_IDX_ROLL,   dTheta);
            result.set(State.VEC_IDX_YAW,    dPhi);
            result.set(State.VEC_IDX_DPITCH, (H22*fGv1 - H12*fGv2) / detH);
            result.set(State.VEC_IDX_DROLL,  (H11*fGv2 - H12*fGv1) / detH);
            result.set(State.VEC_IDX_DYAW,   fGv3 / h);
            result.set(State.VEC_IDX_X,      R * dTheta * cosPhi);
            result.set(State.VEC_IDX_Y,      R * dTheta * sinPhi);
            
            assert (!result.hasNaN());
            return result;
        }
        
        void updatePowers(double leftV, double rightV)
        {
            vl = regulatePower(leftV);
            vr = regulatePower(rightV);
        }
        
        double regulatePower(double power)
        {
            if (cfg.powerNoEffectLow <= power &&
                power <= cfg.powerNoEffectHigh) return 0.0;
            if (cfg.powerMin > power) return cfg.powerMin;
            if (cfg.powerMax < power) return cfg.powerMax;
            return power;
        }
        
        private final double K, R, MLR, RRMLL2, MLL, MgL, WWB, RWK, B2;
        private final double H11, H22, H11MH22, ht;
        private final double psi0;
        
        private double vl, vr;
    }
    
    //--------------------------------------------------------------------------
    
    private final double maxPitch;
    
    private final MotionConfig cfg;
    private final State state;
    private final Vector odeTemp;
    
    private final MotionModelODE ode;
    private final ODESolver odeSolver;
}
