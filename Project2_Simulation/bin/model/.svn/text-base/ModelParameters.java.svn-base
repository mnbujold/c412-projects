package model;

import helper.Ratio;

public interface ModelParameters
{
    /**
     * Supported wheel types.
     */
    enum WheelType
    {
        NXT_2_0, // NXT 2.0 wheels
        RCX // RCX (large white) wheels
    }

    /**
     * Currently used wheel type.
     */
    WheelType wheelType = WheelType.RCX;

    /**
     * Wheel weights (kg).
     */
    double[] wheelWeight = {
                0.0165, // NXT_2_0
                0.0295  // RCX
    };

    /**
     * Wheel radius (m).
     */
    double[] wheelRadius = {
                0.0216, // NXT_2_0
                0.0408  // RCX
    };

    /**
     * Wheel width (m).
     */
    double[] wheelWidth = {
                0.022, // NXT_2_0
                0.015  // RCX
    };

    //--------------------------------------------------------------------------

    /**
     * Gravitational acceleration (cm/sec^2).
     */
    double g = 9.80665 * Ratio.M_TO_CM;

    /**
     * Wheel weight (kg).
     */
    double m = wheelWeight[wheelType.ordinal()];

    /**
     * Wheel radius (cm).
     */
    double R = wheelRadius[wheelType.ordinal()] * Ratio.M_TO_CM;

    /**
     * Wheel width (cm).
     */
    double Ww = wheelWidth[wheelType.ordinal()] * Ratio.M_TO_CM;

    /**
     * Wheel inertia moment (kg*cm^2).
     */
    double Jw = m * (R*R) / 2;

    /**
     * Body weight (kg).
     */
    double M = 0.549;

    /**
     * Body width (cm).
     */
    double W = 0.15 * Ratio.M_TO_CM;

    /**
     * Body depth (cm).
     */
    double D = 0.045 * Ratio.M_TO_CM;

    /**
     * Body height (cm).
     */
    double H = 0.158 * Ratio.M_TO_CM;

    /**
     * Distance of the center of mass from the wheel axle (cm).
     */
    double L = H / 2;

    /**
     * Body pitch inertia moment (kg*cm^2).
     */
    double Jpsi = M * (L*L) / 3;

    /**
     * Body yaw inertia moment (kg*cm^2).
     */
    double Jphi = M * (W*W + D*D) / 12;

    //--------------------------------------------------------------------------
    // Coming from<url>http://web.mac.com/ryo_watanabe/iWeb/Ryo%27s%20Holiday/NXT%20Motor.html</url>.

    /**
     * DC motor resistance (ohm). 
     */
    double Rm = 6.8562;

    /**
     * DC motor back electromotive force constant (V*sec/rad).
     */
    double Kb = 0.46839;

    /**
     * DC motor torque constant (N*cm/A).
     */
    double Kt = 0.31739 * Ratio.M_TO_CM;

    //--------------------------------------------------------------------------
    // Conveniently chosen at this moment (should be meaured instead).

    /**
     * DC motor inertia moment (kg*cm^2).
     */
    double Jm = 1e-5 * Ratio.M_TO_CM * Ratio.M_TO_CM;

    /**
     * Gear ratio.
     */
    double n = 1;

    /**
     * Friction coefficient between body and DC motor.
     */
    double fm = 0.0022;

    /**
     * Friction coefficient between wheel and floor (??).
     */
    double fw = 0.0;
}
