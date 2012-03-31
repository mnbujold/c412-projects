package localize;

/**
 * Representation of a single particle.
 * 
 * The left/right wheel roll angles are not the same as the rotation
 * counter measurements of the robot. The relationship is
 * 
 *      thetaL/R = rotationCounterL/R + pitch .
 */
public class Particle
{
    public Particle()
    {
        x = y = 0.0;
        pitch = thetaL = thetaR = 0.0;
        weight = 0.0;
    }
    
    //--------------------------------------------------------------------------

    /** @return normalized weight
     *          (should be between 0.0 and 1.0 for visualization) */
    public double weight() { return weight; }
    
    /** @return axle midpoint x position (mm) */
    public double x() { return x; }
    
    /** @return axle midpoint y position (mm) */
    public double y() { return y; }
    
    /** @return body pitch angle (rad) */
    public double pitch() { return pitch; }
    
    /** @return left wheel roll angle (rad) */
    public double thetaL() { return thetaL; }
    
    /** @return right wheel roll angle (rad) */
    public double thetaR() { return thetaR; }
    
    //--------------------------------------------------------------------------
    
    public void setWeight(double weight) { this.weight = weight; }
    public void setX(double x) { this.x = x; }
    public void setY(double y) { this.y = y; }
    public void setPitch(double pitch) { this.pitch = pitch; }
    public void setThetaL(double thetaL) { this.thetaL = thetaL; }
    public void setThetaR(double thetaR) { this.thetaR = thetaR; }
    
    public void set(double weight, double x, double y,
                    double pitch, double thetaL, double thetaR)
    {
        this.weight = weight;
        this.x = x; this.y = y;
        this.pitch = pitch; this.thetaL = thetaL; this.thetaR = thetaR;
    }
    
    public void set(Particle p)
    {
        set(p.weight(), p.x(), p.y(), p.pitch(), p.thetaL(), p.thetaR());
    }
    
    //--------------------------------------------------------------------------
    
    private double weight;
    private double x, y; // mm
    private double pitch, thetaL, thetaR; // rad
}
