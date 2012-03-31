import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

import math.CircleODE;
import math.Euler;
import math.Midpoint;
import math.ODE;
import math.ODESolver;
import math.RK4;
import Jama.Matrix;

/**
 * Comparison of numerical ODE solvers.
 */
public class OdeMain
{
    static final String dataFilePrefix = "data/result-";
    static final boolean writeData = true;
    static final boolean writeLog = false;

    static final double endTime = 50.0;
    static final ODE ode = new CircleODE();

    static final double[] stepSizes = { 0.25    // Euler
                                      , 0.25    // Midpoint
                                      , 0.25 }; // RK4

    static final ODESolver[] solvers = { new Euler(ode.rhs(), stepSizes[0])
                                       , new Midpoint(ode.rhs(), stepSizes[1])
                                       , new RK4(ode.rhs(), stepSizes[2]) };
    
    //--------------------------------------------------------------------------

    public static void main(String[] args)
    {
        for (ODESolver solver : solvers)
        {
            String method = solver.getClass().getSimpleName();
            System.out.println("Method: " + method);
            
            String fn = dataFilePrefix + method;
            BufferedWriter out = null;
            try
            {
                if (writeData)
                {
                    out = new BufferedWriter(new FileWriter(fn));
                }
                
                int step = 0;
                double t = 0.0;
                Matrix x = ode.init();
                Matrix nextX = x;
                Matrix trueX = x;
                double gerr = 0.0; // global error
                double lerr = 0.0; // local error
                if (writeData)
                {
                    out.write("# step time computedX trueX error(norm2)\n");
                    out.write("# state dimension: "
                            + x.getRowDimension() + "\n");
                }
                long startTS = System.currentTimeMillis();
                while (true)
                {
                    // reporting
                    if (writeData)
                    {
                        out.write("" + step
                                + " " + t
                                + fmtX(nextX)
                                + fmtX(trueX)
                                + " " + gerr
                                + " " + lerr
                                + "\n");
                    }
                    if (t >= endTime) { break; }
                    
                    // computation
                    ++step;
                    nextX = solver.next(x, t);
                    if (null == nextX) break; // skip this solver
                    
                    double h = solver.stepSize();
                    t += h;
                    trueX = ode.solution(t);
                    
                    // error calculation
                    gerr = nextX.minus(trueX).norm2();
                    ODE tmpODE = new CircleODE(x);
                    Matrix tmpX = tmpODE.solution(h);
                    lerr = nextX.minus(tmpX).norm2();
                    
                    // logging
                    if (writeLog)
                    {
                        System.out.println("----------");
                        System.out.println("x     :" + fmtX(x));
                        System.out.println("h     : " + h);
                        System.out.println("nextX :" + fmtX(nextX));
                        System.out.println("trueX :" + fmtX(trueX));
                        System.out.println("tmpX  :" + fmtX(tmpX));
                        System.out.println("gerr  : " + gerr);
                        System.out.println("lerr  : " + lerr);
                    }
                    
                    x = nextX;
                }
                long endTS = System.currentTimeMillis();
                System.out.println("  elapsed: " + (endTS - startTS) + "ms");
            }
            catch (IOException e)
            {
                e.printStackTrace(System.err);
            }
            finally
            {
                if (writeData) { close(out); }
            }
        }
    }

    //--------------------------------------------------------------------------

    static String fmtX(Matrix x)
    {
        String s = "";
        for (int i = 0; i < x.getRowDimension(); ++i)
        {
            s += " " + x.get(i, 0);
        }
        return s;
    }

    static void close(BufferedWriter bw)
    {
        try
        {
            bw.close();
        }
        catch (IOException e)
        {
            e.printStackTrace(System.err);
        }        
    }
}
