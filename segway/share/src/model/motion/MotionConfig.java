package model.motion;

import helper.Config;
import helper.MissingConfigException;
import helper.Ratio;

import java.io.File;
import java.io.IOException;

/**
 * Motion model related configuration.
 */
public class MotionConfig extends Config
{
    public MotionConfig(File file)
    throws IOException, MissingConfigException
    {
        super (file);

        g = getDoubleConfig("g");

        wheel = getStringConfig("wheel");
        m = getDoubleConfig("m-" + wheel);
        R = getDoubleConfig("R-" + wheel);
        w = getDoubleConfig("w-" + wheel);        
        Jw = getDoubleConfig("Jw-" + wheel, 0.5*m*(R*R));
        
        M = getDoubleConfig("M");
        W = getDoubleConfig("W");
        D = getDoubleConfig("D");
        H = getDoubleConfig("H");
        L = getDoubleConfig("L", H/2.0);
        Jpsi = getDoubleConfig("Jpsi", M*(H*H+D*D)/12.0 + M*L*L);
        Jphi = getDoubleConfig("Jphi", M*(W*W+D*D)/12.0);
        psi0 = getDoubleConfig("psi0") * Ratio.DEG_TO_RAD;
        
        B = getDoubleConfig("B");
        K = getDoubleConfig("K");
        
        powerMin = getDoubleConfig("power-min");
        powerMax = getDoubleConfig("power-max");
        powerNoEffectLow = getDoubleConfig("power-no-effect-low");
        powerNoEffectHigh = getDoubleConfig("power-no-effect-high");
    }
    
    //--------------------------------------------------------------------------

    /** Gravitational acceleration (m/s^2). */
    public final double g;

    //- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - --
    
    /** Wheel type string. */
    public final String wheel;
    
    /** Wheel weight (kg). */
    public final double m;
    
    /** Wheel radius (m). */
    public final double R;
    
    /** Wheel width (m). */
    public final double w;

    /** Wheel inertia moment (kg*m^2). */
    public final double Jw;
    
    //- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - --
    
    /** Body weight (kg). */
    public final double M;
    
    /** Body width (m). */
    public final double W;
    
    /** Body depth (m). */
    public final double D;
    
    /** Body height (m). */
    public final double H;

    /** Center of mass distance from the wheel axle (m). */
    public final double L;
    
    /** Body pitch inertia moment (kg*m^2). */
    public final double Jpsi;
    
    /** Body yaw inertia moment (kg*m^2). */
    public final double Jphi;
    
    /** Pitch angle at equilibrium (rad). */
    public final double psi0;
    
    //- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - --
    
    /** DC motor damping coefficient (Nm*s/rad). */
    public final double B;
    
    /** DC motor power coefficient (Nm/V). */
    public final double K;
    
    //- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - --
    
    /** Applicable power range (V). */
    public final double powerMin, powerMax;
    
    /** Power no-effect range (V). */
    public final double powerNoEffectLow, powerNoEffectHigh;
}
