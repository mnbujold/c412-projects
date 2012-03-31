package control;

import run.Robot;


/**
 * Abstract superclass of segway controllers. 
 */
public abstract class RobotController extends Controller
{
    public RobotController(Robot robot)
    {
        this.robot = robot;
    }
    
    /** @return interface to the controlled robot */
    public Robot robot() { return robot; }
    
    //--------------------------------------------------------------------------
    
    /** @return power enforced into the [-100,100] range */
    protected final int limitPower(int power)
    {
        if (power > 100) return 100;
        if (power < -100) return -100;
        return power;
    }
    
    //--------------------------------------------------------------------------
    
    private final Robot robot;
}
